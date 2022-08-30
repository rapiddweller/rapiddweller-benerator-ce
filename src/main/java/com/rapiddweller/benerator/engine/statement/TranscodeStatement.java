/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.composite.GenerationStepSupport;
import com.rapiddweller.benerator.composite.GenerationStep;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.factory.GenerationStepFactory;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.jdbacl.identity.IdentityModel;
import com.rapiddweller.jdbacl.identity.IdentityProvider;
import com.rapiddweller.jdbacl.identity.KeyMapper;
import com.rapiddweller.jdbacl.identity.NoIdentity;
import com.rapiddweller.jdbacl.model.DBTable;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

/**
 * {@link Statement} that transcodes a database table.<br/><br/>
 * Created: 08.09.2010 16:23:56
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class TranscodeStatement extends SequentialStatement implements CascadeParent {

  private static final Logger logger = LoggerFactory.getLogger(TranscodeStatement.class);

  final Expression<ComplexTypeDescriptor> typeExpression;
  final Expression<AbstractDBSystem> sourceEx;
  final Expression<String> selectorEx;
  final Expression<AbstractDBSystem> targetEx;
  final Expression<Long> pageSizeEx;
  final Expression<ErrorHandler> errorHandlerEx;
  final TranscodingTaskStatement parent;

  AbstractDBSystem source;
  private AbstractDBSystem target;

  private Entity currentEntity;

  public TranscodeStatement(MutatingTypeExpression typeExpression, TranscodingTaskStatement parent,
                            Expression<AbstractDBSystem> sourceEx, Expression<String> selectorEx, Expression<AbstractDBSystem> targetEx,
                            Expression<Long> pageSizeEx, Expression<ErrorHandler> errorHandlerEx) {
    this.typeExpression = cache(typeExpression);
    this.parent = parent;
    this.sourceEx = sourceEx;
    this.selectorEx = selectorEx;
    this.targetEx = targetEx;
    this.pageSizeEx = pageSizeEx;
    this.errorHandlerEx = errorHandlerEx;
    this.currentEntity = null;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    AbstractDBSystem target = targetEx.evaluate(context);
    Long pageSize = ExpressionUtil.evaluate(pageSizeEx, context);
    if (pageSize == null) {
      pageSize = 1L;
    }
    transcodeTable(getSource(context), target, pageSize, context);
    return true;
  }

  @Override
  public KeyMapper getKeyMapper() {
    return parent.getKeyMapper();
  }

  @Override
  public IdentityProvider getIdentityProvider() {
    return parent.getIdentityProvider();
  }

  @Override
  public Entity currentEntity() {
    return currentEntity;
  }

  @Override
  public ComplexTypeDescriptor getType(AbstractDBSystem db, BeneratorContext context) {
    return typeExpression.evaluate(context);
  }

  @Override
  public AbstractDBSystem getSource(BeneratorContext context) {
    if (source == null) {
      source = sourceEx.evaluate(context);
    }
    return source;
  }

  @Override
  public AbstractDBSystem getTarget(BeneratorContext context) {
    if (target == null) {
      target = targetEx.evaluate(context);
    }
    return target;
  }

  @Override
  public boolean needsNkMapping(String tableName) {
    return parent.needsNkMapping(tableName);
  }

  // helper methods --------------------------------------------------------------------------------------------------

  private void transcodeTable(AbstractDBSystem source, AbstractDBSystem target, long pageSize, BeneratorContext context) {
    KeyMapper mapper = getKeyMapper();
    ComplexTypeDescriptor type = typeExpression.evaluate(context);
    IdentityModel identity = getIdentityProvider().getIdentity(type.getName(), false);
    String tableName = type.getName();
    logger.info("Starting transcoding of {} from {} to {}", tableName, source.getId(), target.getId());

    // iterate rows
    String selector = ExpressionUtil.evaluate(selectorEx, context);
    DataSource<Entity> iterable = source.queryEntities(tableName, selector, context);
    List<GenerationStep<Entity>> generationSteps =
        GenerationStepFactory.createMutatingGenerationSteps(type, true, Uniqueness.NONE, context);
    try (GenerationStepSupport<Entity> cavs = new GenerationStepSupport<>(tableName, generationSteps)) {
      cavs.init(context);
      DataIterator<Entity> iterator = iterable.iterator();
      mapper.registerSource(source.getId(), source.getConnection());
      long rowCount = 0;
      DataContainer<Entity> container = new DataContainer<>();
      while ((container = iterator.next(container)) != null) {
        Entity sourceEntity = container.getData();
        Object sourcePK = sourceEntity.idComponentValues();
        boolean mapNk = parent.needsNkMapping(tableName);
        String nk = null;
        if (mapNk) {
          nk = mapper.getNaturalKey(source.getId(), identity, sourcePK);
        }
        Entity targetEntity = new Entity(sourceEntity);
        cavs.apply(targetEntity, context);
        Object targetPK = targetEntity.idComponentValues();
        transcodeForeignKeys(targetEntity, source, context);
        mapper.store(source.getId(), identity, nk, sourcePK, targetPK);
        target.store(targetEntity);
        logger.debug("transcoded {} to {}", sourceEntity, targetEntity);
        cascade(sourceEntity, context);
        rowCount++;
        if (rowCount % pageSize == 0) {
          target.flush();
        }
      }
      target.flush();
      logger.info("Finished transcoding {} rows of table {}", source.countEntities(tableName), tableName);
    }
  }

  private void cascade(Entity sourceEntity, BeneratorContext context) {
    this.currentEntity = sourceEntity;
    executeSubStatements(context);
    this.currentEntity = null;
  }

  private void transcodeForeignKeys(Entity entity, AbstractDBSystem source, Context context) {
    ComplexTypeDescriptor tableDescriptor = entity.descriptor();
    for (InstanceDescriptor component : tableDescriptor.getParts()) {
      if (component instanceof ReferenceDescriptor) {
        ReferenceDescriptor fk = (ReferenceDescriptor) component;
        String refereeTableName = fk.getTargetType();
        Object sourceRef = entity.get(fk.getName());
        if (sourceRef != null) {
          IdentityProvider identityProvider = parent.getIdentityProvider();
          IdentityModel sourceIdentity = identityProvider.getIdentity(refereeTableName, false);
          if (sourceIdentity == null) {
            DBTable refereeTable = source.getDbMetaData().getTable(refereeTableName);
            sourceIdentity = new NoIdentity(refereeTable.getName());
            identityProvider.registerIdentity(sourceIdentity, refereeTableName);
          }

          boolean needsNkMapping = parent.needsNkMapping(refereeTableName);
          if (sourceIdentity instanceof NoIdentity && needsNkMapping) {
            throw BeneratorExceptionFactory.getInstance().configurationError("No identity defined for table " + refereeTableName);
          }
          KeyMapper mapper = parent.getKeyMapper();
          Object targetRef;
          if (needsNkMapping) {
            String sourceRefNK = mapper.getNaturalKey(source.getId(), sourceIdentity, sourceRef);
            targetRef = mapper.getTargetPK(sourceIdentity, sourceRefNK);
          } else {
            targetRef = mapper.getTargetPK(source.getId(), sourceIdentity, sourceRef);
          }
          if (targetRef == null) {
            String message = "No mapping found for " + source.getId() + '.' + refereeTableName + "#" + sourceRef +
                " referred in " + entity.type() + "(" + fk.getName() + "). " +
                "Probably has not been in the result set of the former '" + refereeTableName + "' nk query.";
            getErrorHandler(context).handleError(message);
          }
          entity.setComponent(fk.getName(), targetRef);
        }
      }
    }
  }


  //  @Override
  //  public void merge(DBSystem source, DBSystem target, int pageSize, KeyMapper mapper, Context context) {
  //    String activity = "Merging " + name + " from " + source.getId() + " to " + target.getId();
  //    startActivity(activity);
  //    HeavyweightIterator<Object[]> nkIterator = createNkPkIterator(source, mapper, context);
  //    Set<String> sourceNKs = new HashSet<String>();
  //    try {
  //      while (nkIterator.hasNext()) {
  //        Object[] row = nkIterator.next();
  //        String nk = String.valueOf(row[0]);
  //        sourceNKs.add(nk);
  //        Object sourceId = extractPK(row);
  //        Entity sourceEntity = source.queryEntityById(name, sourceId);
  //        Object targetId = mapper.getTargetPK(this, nk);
  //        if (targetId == null) {
  //          handleNKNotFound(nk, name, source, target);
  //          continue;
  //        } else {
  //          Entity targetEntity = target.queryEntityById(name, targetId);
  //          String message = checkEquivalence(sourceEntity, targetEntity, source, nk, mapper);
  //          if (message != null) {
  //            handleNonEquivalence(message, source.getId(), sourceEntity);
  //          }
  //        }
  //        mapper.store(source, this, nk, sourceId, targetId);
  //      }
  //    } finally {
  //      IOUtil.close(nkIterator);
  //    }
  //    target.flush();
  //
  //    endActivity(activity, source.countEntities(name));
  //  }


}
