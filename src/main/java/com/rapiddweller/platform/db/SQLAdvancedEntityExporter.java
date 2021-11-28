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

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.consumer.TextFileExporter;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.jdbacl.DatabaseDialect;
import com.rapiddweller.model.data.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;
import java.util.Set;

import static com.rapiddweller.jdbacl.SQLUtil.appendCatSchTabToBuilder;

/**
 * Exports Entities to a SQL file based on a Database Connection.<br/><br/>
 * Created: 28.11.2021 09:43:59
 *
 * @author Alexander Kell
 * @since 2.0.0
 */
public class SQLAdvancedEntityExporter extends TextFileExporter {

  private static final Logger logger = LoggerFactory.getLogger(SQLAdvancedEntityExporter.class);

  // defaults --------------------------------------------------------------------------------------------------------

  private static final String DEFAULT_URI = "export.sql";

  // attributes ------------------------------------------------------------------------------------------------------

  private DatabaseDialect dialect = null;
  private String targetDb;
  private boolean quoteTableNames;

  // constructors ----------------------------------------------------------------------------------------------------

  public SQLAdvancedEntityExporter() {
    this(null, DEFAULT_URI);
  }

  public SQLAdvancedEntityExporter(String targetDb) {
    this(targetDb, DEFAULT_URI);
  }

  public SQLAdvancedEntityExporter(String targetDb, String uri) {
    this(targetDb, uri, null, null);
  }

  public SQLAdvancedEntityExporter(String targetDb, String uri, String lineSeparator,
                                   String encoding) {
    super(uri, encoding, lineSeparator);
    setTargetDb(targetDb);
  }

  public void setDialect(DatabaseDialect dialect) {
    this.dialect = dialect;
  }

  public void setTargetDb(String targetDb) {
    this.targetDb = targetDb;
  }

  // Callback methods for parent class functionality -----------------------------------------------------------------

  @Override
  protected void startConsumingImpl(Object object) {
    DatabaseDialect dialectTmp;
    logger.debug("exporting {}", object);
    if (!(object instanceof Entity)) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("Expected Entity");
    }
    Entity entity = (Entity) object;
    if (targetDb != null && entity.descriptor().getProvider().getDataModel().getDescriptorProvider(targetDb) instanceof DefaultDBSystem) {
      dialectTmp = ((DefaultDBSystem) entity.descriptor().getProvider().getDataModel().getDescriptorProvider(targetDb)).getDialect();
      setDialect(dialectTmp);
      this.quoteTableNames = dialectTmp.quoteTableNames;
    } else if (entity.descriptor instanceof LazyTableComplexTypeDescriptor) {
      dialectTmp = ((LazyTableComplexTypeDescriptor) entity.descriptor).db.getDialect();
      setDialect(dialectTmp);
      this.quoteTableNames = dialectTmp.quoteTableNames;
    } else if (entity.descriptor().getParent() instanceof LazyTableComplexTypeDescriptor) {
      dialectTmp = ((LazyTableComplexTypeDescriptor) ((Entity) object).descriptor().getParent()).db.getDialect();
      setDialect(dialectTmp);
      this.quoteTableNames = dialectTmp.quoteTableNames;
    }
    if (dialect == null) {
      throw BeneratorExceptionFactory.getInstance().configurationError(
          "'dialect' not set in " + getClass().getSimpleName());
    }
    String sql = createSQLInsert(entity);

    printer.println(sql);
  }

  @Override
  protected void postInitPrinter(Object object) {
    // nothing special to do
  }

  String createSQLInsert(Entity entity) {
    String table = entity.type();
    StringBuilder builder = new StringBuilder("insert into ");
    enrichCatSchTab(entity, table, builder);
    builder.append(" (");
    Set<Entry<String, Object>> components = enrichColumns(entity, builder);
    builder.append(") values (");
    enrichValues(builder, components);
    builder.append(");");
    String sql = builder.toString();
    logger.debug("built SQL statement: {}", sql);
    return sql;
  }

  private void enrichCatSchTab(Entity entity, String table, StringBuilder builder) {
    if (entity.descriptor().getProvider().getDataModel().getDescriptorProvider(targetDb) instanceof DefaultDBSystem) {
      String catalog = ((DefaultDBSystem) entity.descriptor().getProvider().getDataModel().getDescriptorProvider(targetDb)).getCatalog();
      String schema = ((DefaultDBSystem) entity.descriptor().getProvider().getDataModel().getDescriptorProvider(targetDb)).getSchema();
      appendCatSchTabToBuilder(catalog, schema, table, builder, this.dialect);
    } else if (entity.descriptor instanceof LazyTableComplexTypeDescriptor) {
      String catalog = ((LazyTableComplexTypeDescriptor) entity.descriptor).db.getCatalog();
      String schema = ((LazyTableComplexTypeDescriptor) entity.descriptor).db.getSchema();
      appendCatSchTabToBuilder(catalog, schema, table, builder, this.dialect);
    } else if (entity.descriptor().getParent() instanceof LazyTableComplexTypeDescriptor) {
      String catalog = ((LazyTableComplexTypeDescriptor) entity.descriptor().getParent()).db.getCatalog();
      String schema = ((LazyTableComplexTypeDescriptor) entity.descriptor().getParent()).db.getSchema();
      appendCatSchTabToBuilder(catalog, schema, table, builder, this.dialect);
    } else {
      builder.append(table);
    }
  }

  private void enrichValues(StringBuilder builder, Set<Entry<String, Object>> components) {
    boolean first;
    first = true;
    for (Entry<String, Object> entry : components) {
      if (first) {
        first = false;
      } else {
        builder.append(", ");
      }
      Object value = entry.getValue();

      builder.append(dialect.formatValue(value));
    }
  }

  private Set<Entry<String, Object>> enrichColumns(Entity entity, StringBuilder builder) {
    String columnName;
    Set<Entry<String, Object>> components =
        entity.getComponents().entrySet();
    boolean first = true;
    for (Entry<String, Object> entry : components) {
      if (first) {
        first = false;
      } else {
        builder.append(", ");
      }
      columnName = entry.getKey();
      if (this.quoteTableNames) {
        builder.append('"');
      }
      builder.append(columnName);
      if (this.quoteTableNames) {
        builder.append('"');
      }
    }
    return components;
  }

}
