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

import com.rapiddweller.benerator.composite.ComponentAndVariableSupport;
import com.rapiddweller.benerator.composite.GeneratorComponent;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.ComplexTypeGeneratorFactory;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.SyntaxError;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.jdbacl.identity.IdentityModel;
import com.rapiddweller.jdbacl.identity.IdentityProvider;
import com.rapiddweller.jdbacl.identity.KeyMapper;
import com.rapiddweller.jdbacl.identity.NoIdentity;
import com.rapiddweller.jdbacl.model.DBForeignKeyConstraint;
import com.rapiddweller.jdbacl.model.DBTable;
import com.rapiddweller.jdbacl.model.Database;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.platform.db.DBSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;

import static java.io.StreamTokenizer.TT_WORD;

/**
 * Cascades the 'transcode' operation to all entities configured to be related
 * to the currently transcoded entity.<br/><br/>
 * Created: 18.04.2011 07:14:34
 *
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class CascadeStatement extends SequentialStatement implements CascadeParent {

  private static final Logger LOGGER = LogManager.getLogger(CascadeStatement.class);

  private static final String REF_SYNTAX_MESSAGE = "Expected Syntax: table(column1, column2, ...)";

  private final CascadeParent parent;
  private final Reference ref;
  private Entity currentEntity;
  /**
   * The Type expression.
   */
  final MutatingTypeExpression typeExpression;
  /**
   * The Type.
   */
  ComplexTypeDescriptor type;

  /**
   * Instantiates a new Cascade statement.
   *
   * @param ref            the ref
   * @param typeExpression the type expression
   * @param parent         the parent
   */
  public CascadeStatement(String ref, MutatingTypeExpression typeExpression, CascadeParent parent) {
    this.typeExpression = typeExpression;
    this.ref = Reference.parse(ref);
    this.parent = parent;
    this.currentEntity = null;
  }

  @Override
  public boolean execute(BeneratorContext context) {
    DBSystem source = getSource(context);
    getType(source, context);
    IdentityModel identity = parent.getIdentityProvider().getIdentity(type.getName(), false);
    String tableName = type.getName();
    LOGGER.debug("Cascading transcode from " + parent.currentEntity().type() + " to " + tableName);

    // iterate rows
    List<GeneratorComponent<Entity>> generatorComponents =
        ComplexTypeGeneratorFactory.createMutatingGeneratorComponents(type, Uniqueness.NONE, context);
    ComponentAndVariableSupport<Entity> cavs = new ComponentAndVariableSupport<>(tableName,
        generatorComponents, context);
    cavs.init(context);

    DataIterator<Entity> iterator = ref.resolveReferences(parent.currentEntity(), source, context);
    DataContainer<Entity> container = new DataContainer<>();
    while ((container = iterator.next(container)) != null) {
      mutateAndTranscodeEntity(container.getData(), identity, cavs, context);
    }
    IOUtil.close(iterator);
    return true;
  }

  @Override
  public DBSystem getSource(BeneratorContext context) {
    return parent.getSource(context);
  }

  @Override
  public Entity currentEntity() {
    return currentEntity;
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
  public boolean needsNkMapping(String type) {
    return parent.needsNkMapping(type);
  }

  @Override
  public DBSystem getTarget(BeneratorContext context) {
    return parent.getTarget(context);
  }

  @Override
  public ComplexTypeDescriptor getType(DBSystem db, BeneratorContext context) {
    if (type == null) {
      String parentType = parent.getType(db, context).getName();
      typeExpression.setTypeName(ref.getTargetTableName(parentType, db, context));
      type = typeExpression.evaluate(context);
    }
    return type;
  }

  // implementation --------------------------------------------------------------------------------------------------

  private void mutateAndTranscodeEntity(Entity sourceEntity, IdentityModel identity, ComponentAndVariableSupport<Entity> cavs,
                                        BeneratorContext context) {
    Object sourcePK = sourceEntity.idComponentValues();
    boolean mapNk = parent.needsNkMapping(sourceEntity.type());
    String nk = null;
    KeyMapper mapper = getKeyMapper();
    DBSystem source = getSource(context);
    if (mapNk) {
      nk = mapper.getNaturalKey(source.getId(), identity, sourcePK);
    }
    Entity targetEntity = new Entity(sourceEntity);
    cavs.apply(targetEntity, context);
    Object targetPK = targetEntity.idComponentValues();
    transcodeForeignKeys(targetEntity, source, context);
    mapper.store(source.getId(), identity, nk, sourcePK, targetPK);
    getTarget(context).store(targetEntity);
    LOGGER.debug("transcoded {} to {}", sourceEntity, targetEntity);
    cascade(sourceEntity, context);
  }

  private void transcodeForeignKeys(Entity entity, DBSystem source, Context context) {
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
            throw new ConfigurationError("No identity defined for table " + refereeTableName);
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

  private void cascade(Entity sourceEntity, BeneratorContext context) {
    this.currentEntity = sourceEntity;
    executeSubStatements(context);
    this.currentEntity = null;
  }

  /**
   * The type Reference.
   */
  public static class Reference {

    private final String refererTableName;
    private final String[] columnNames;

    private DBForeignKeyConstraint fk;
    private Database database;
    private DBTable refererTable;
    private DBTable refereeTable;
    private DBTable targetTable;

    /**
     * Instantiates a new Reference.
     *
     * @param refererTableName the referer table name
     * @param columnNames      the column names
     */
    public Reference(String refererTableName, String[] columnNames) {
      this.refererTableName = refererTableName;
      this.columnNames = columnNames;
    }

    /**
     * Gets target table name.
     *
     * @param parentTable the parent table
     * @param db          the db
     * @param context     the context
     * @return the target table name
     */
    public String getTargetTableName(String parentTable, DBSystem db, BeneratorContext context) {
      if (!parentTable.equals(refererTableName)) {
        return refererTableName;
      } else {
        initIfNecessary(parentTable, db, context);
        return targetTable.getName();
      }
    }

    /**
     * Resolve references data iterator.
     *
     * @param currentEntity the current entity
     * @param db            the db
     * @param context       the context
     * @return the data iterator
     */
    public DataIterator<Entity> resolveReferences(Entity currentEntity, DBSystem db, BeneratorContext context) {
      initIfNecessary(currentEntity.type(), db, context);
      DBTable parentTable = database.getTable(currentEntity.type());
      if (parentTable.equals(refereeTable)) {
        return resolveToManyReference(currentEntity, fk, db, context); // including self-recursion
      } else if (parentTable.equals(refererTable)) {
        return resolveToOneReference(currentEntity, fk, db, context);
      } else {
        throw new ConfigurationError("Table '" + parentTable + "' does not relate to the foreign key " +
            refererTableName + '(' + ArrayFormat.format(columnNames) + ')');
      }
    }

    private void initIfNecessary(String parentTable, DBSystem db, BeneratorContext context) {
      if (this.database != null) {
        return;
      }
      this.database = db.getDbMetaData();
      this.refererTable = this.database.getTable(refererTableName);
      this.fk = refererTable.getForeignKeyConstraint(columnNames);
      this.refereeTable = fk.getRefereeTable();
      this.targetTable = (parentTable.equalsIgnoreCase(refereeTable.getName()) ? refererTable : refereeTable);
    }

    /**
     * Resolve to many reference data iterator.
     *
     * @param fromEntity the from entity
     * @param fk         the fk
     * @param db         the db
     * @param context    the context
     * @return the data iterator
     */
    DataIterator<Entity> resolveToManyReference(
        Entity fromEntity, DBForeignKeyConstraint fk, DBSystem db, BeneratorContext context) {
      StringBuilder selector = new StringBuilder();
      String[] refererColumnNames = fk.getColumnNames();
      String[] refereeColumnNames = fk.getRefereeColumnNames();
      for (int i = 0; i < refererColumnNames.length; i++) {
        if (selector.length() > 0) {
          selector.append(" and ");
        }
        Object refereeColumnValue = fromEntity.get(refereeColumnNames[i]);
        selector.append(refererColumnNames[i]).append('=').append(db.getDialect().formatValue(refereeColumnValue));
      }
      return db.queryEntities(fk.getTable().getName(), selector.toString(), context).iterator();
    }

    /**
     * Resolve to one reference data iterator.
     *
     * @param fromEntity the from entity
     * @param fk         the fk
     * @param db         the db
     * @param context    the context
     * @return the data iterator
     */
    DataIterator<Entity> resolveToOneReference(
        Entity fromEntity, DBForeignKeyConstraint fk, DBSystem db, BeneratorContext context) {
      StringBuilder selector = new StringBuilder();
      String[] refererColumnNames = fk.getColumnNames();
      String[] refereeColumnNames = fk.getRefereeColumnNames();
      for (int i = 0; i < refererColumnNames.length; i++) {
        if (selector.length() > 0) {
          selector.append(" and ");
        }
        Object refererColumnValue = fromEntity.get(refererColumnNames[i]);
        selector.append(refereeColumnNames[i]).append('=').append(db.getDialect().formatValue(refererColumnValue));
      }
      return db.queryEntities(fk.getRefereeTable().getName(), selector.toString(), context).iterator();
    }

    /**
     * Parse reference.
     *
     * @param refSpec the ref spec
     * @return the reference
     */
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    static Reference parse(String refSpec) {
      StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(refSpec));
      tokenizer.wordChars('_', '_');
      try {
        // parse table name
        int token = tokenizer.nextToken();
        if (token != TT_WORD) {
          throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
        }
        // this must be at this position!
        String tableName = tokenizer.sval;

        // parse column names
        if ((token = tokenizer.nextToken()) != '(') {
          throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
        }
        ArrayBuilder<String> columnNames = new ArrayBuilder<>(String.class);
        do {
          if ((token = tokenizer.nextToken()) != TT_WORD) {
            throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
          }
          columnNames.add(tokenizer.sval);
          token = tokenizer.nextToken();
          if (token != ',' && token != ')') {
            throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
          }
        } while (token == ',');
        if (token != ')') {
          throw new SyntaxError("reference definition must end with ')'", refSpec);
        }
        return new Reference(tableName, columnNames.toArray());
      } catch (IOException e) {
        throw new SyntaxError(REF_SYNTAX_MESSAGE, refSpec);
      }
    }

  }

}
