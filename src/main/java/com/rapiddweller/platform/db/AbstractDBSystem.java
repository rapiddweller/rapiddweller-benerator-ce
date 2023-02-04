/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.benerator.storage.StorageSystemInserter;
import com.rapiddweller.benerator.util.DeprecationLogger;
import com.rapiddweller.common.ArrayFormat;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.exception.ConnectFailedException;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.ImportFailedException;
import com.rapiddweller.common.LoggerEscalator;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.collection.OrderedNameMap;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.script.ScriptSpec;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.util.ConvertingDataSource;
import com.rapiddweller.jdbacl.ColumnInfo;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.jdbacl.DatabaseDialect;
import com.rapiddweller.jdbacl.DatabaseDialectManager;
import com.rapiddweller.jdbacl.ResultSetConverter;
import com.rapiddweller.jdbacl.SQLUtil;
import com.rapiddweller.jdbacl.dialect.OracleDialect;
import com.rapiddweller.jdbacl.model.DBCatalog;
import com.rapiddweller.jdbacl.model.DBColumn;
import com.rapiddweller.jdbacl.model.DBDataType;
import com.rapiddweller.jdbacl.model.DBForeignKeyConstraint;
import com.rapiddweller.jdbacl.model.DBMetaDataImporter;
import com.rapiddweller.jdbacl.model.DBPrimaryKeyConstraint;
import com.rapiddweller.jdbacl.model.DBSchema;
import com.rapiddweller.jdbacl.model.DBTable;
import com.rapiddweller.jdbacl.model.DBUniqueConstraint;
import com.rapiddweller.jdbacl.model.Database;
import com.rapiddweller.jdbacl.model.cache.CachingDBImporter;
import com.rapiddweller.jdbacl.model.jdbc.JDBCDBImporter;
import com.rapiddweller.jdbacl.model.jdbc.JDBCMetaDataUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.Mode;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.TypeMapper;
import com.rapiddweller.platform.db.postgres.JSONPGObject;
import com.rapiddweller.platform.db.postgres.PGgeometry;
import com.rapiddweller.script.PrimitiveType;
import com.rapiddweller.script.expression.ConstantExpression;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.rapiddweller.jdbacl.SQLUtil.createCatSchTabString;

/**
 * Abstract class that serves as parent for classes which connect to databases using JDBC.<br/><br/>
 * Created: 07.01.2013 08:11:25
 *
 * @author Volker Bergmann
 * @since 0.8.0
 */
public abstract class AbstractDBSystem extends AbstractStorageSystem implements ConnectionProvider {

  private static final int DEFAULT_FETCH_SIZE = 100;
  private static final VersionNumber MIN_ORACLE_VERSION = VersionNumber.valueOf("10" + ".2.0.4");
      // little trick to satisfy SonarCube which thinks this is an IP address
  private static final TypeDescriptor[] EMPTY_TYPE_DESCRIPTOR_ARRAY = new TypeDescriptor[0];
  public static final String FROM = " from ";
  public static final String SELECT = "select ";
  public static final String WHERE = " where ";

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  protected static LoggerEscalator escalator = new LoggerEscalator();
  private final TypeMapper driverTypeMapper;
  private final AtomicInteger invalidationCount;
  protected boolean batch;
  protected boolean readOnly;
  protected volatile Database database;
  protected DBMetaDataImporter importer;
  protected Map<String, DBTable> tables;
  protected DatabaseDialect dialect;
  private String id;
  private String environment;
  private String system;
  private String folder;
  private String url;
  private String user;
  private String password;
  private String driver;
  private String catalogName;
  private String schemaName;
  private String includeTables;
  private String excludeTables;
  private boolean metaCache;
  private boolean lazy;
  private boolean acceptUnknownColumnTypes;
  protected int fetchSize;
  private OrderedNameMap<TypeDescriptor> typeDescriptors;
  protected boolean dynamicQuerySupported;
  private boolean connectedBefore;

  protected AbstractDBSystem(String id, String url, String driver, String user, String password, DataModel dataModel) {
    this(id, dataModel);
    setUrl(url);
    setUser(user);
    setPassword(password);
    setDriver(driver);
    checkOracleDriverVersion(driver);
  }

  protected AbstractDBSystem(String id, String environmentName, String systemName, BeneratorContext context) {
    this(id, context.getDataModel());
    setEnvironment(environmentName);
    this.system = systemName;
    this.folder = context.getContextUri();
    logger.debug("Reading environment data for '{}'", environmentName);
    if (this.environment != null) {
      if (systemName == null) {
        systemName = "db";
        DeprecationLogger.warn("Observed a <Database> definition with an 'environment', but without 'system' setting. " +
            "If you are using the old definition file format, please upgrade to " +
            "the new environment definition file format introduced in Benerator 3.0.0 and specify a 'system' name. " +
            "The old format is supported for backwards compatibility, but will be dropped in a future release");
      }
      SystemRef def = context.getEnvironmentSystem(environment, systemName);
      if (def == null || !"db".equals(def.getType())) {
        throw BeneratorExceptionFactory.getInstance().configurationError(
            "Not a database definition: '" + systemName + "' in environment '" + environmentName + "'");
      }
      for (Map.Entry<String, String> entry : def.getProperties().entrySet()) {
        BeanUtil.setPropertyValue(this, entry.getKey(), entry.getValue(), true, true);
      }
    }
  }

  private AbstractDBSystem(String id, DataModel dataModel) {
    setId(id);
    setDataModel(dataModel);
    setSchema(null);
    setIncludeTables(".*");
    setExcludeTables(null);
    setFetchSize(DEFAULT_FETCH_SIZE);
    setMetaCache(false);
    setBatch(false);
    setReadOnly(false);
    setLazy(true);
    setDynamicQuerySupported(true);
    this.typeDescriptors = null;
    this.driverTypeMapper = driverTypeMapper();
    this.connectedBefore = false;
    this.invalidationCount = new AtomicInteger();
  }


  static String decimalGranularity(int scale) {
    if (scale == 0) {
      return "1";
    }
    return "0." + "0".repeat(Math.max(0, scale - 1)) + 1;
  }

  private static TypeMapper driverTypeMapper() {
    return new TypeMapper(
        "byte", Byte.class,
        "short", Short.class,
        "int", Integer.class,
        "big_integer", Long.class,
        "float", Float.class,
        "double", Double.class,
        "big_decimal", BigDecimal.class,

        "boolean", Boolean.class,
        "char", Character.class,
        "date", java.sql.Date.class,
        "timestamp", java.sql.Timestamp.class,

        "string", java.sql.Clob.class,
        "string", String.class,

        "binary", Blob.class,
        "binary", byte[].class
    );
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEnvironment() {
    return (environment != null ? environment : user);
  }

  private void setEnvironment(String environment) {
    this.environment = StringUtil.emptyToNull(environment);
  }

  public String getSystem() {
    return extractSystemNameFromUrl();
  }

  private String extractSystemNameFromUrl() {
    // jdbc url always starts with jdbc: followed by the system name and a colon
    if (url != null && url.startsWith("jdbc:")) {
      int colon = url.indexOf(':', 5);
      if (colon > 0) {
        return url.substring(5, colon);
      }
    }
    return "unknown";
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = StringUtil.emptyToNull(password);
  }

  public String getCatalog() {
    return catalogName;
  }

  public void setCatalog(String catalog) {
    this.catalogName = catalog;
  }

  public String getSchema() {
    return schemaName;
  }

  public void setSchema(String schema) {
    this.schemaName = StringUtil.emptyToNull(StringUtil.trim(schema));
  }

  public void setSystem(String system) {
    this.system = system;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public void setTableFilter(String tableFilter) {
    setIncludeTables(tableFilter);
  }

  public String getIncludeTables() {
    return includeTables;
  }

  public void setIncludeTables(String includeTables) {
    this.includeTables = includeTables;
  }

  public String getExcludeTables() {
    return excludeTables;
  }

  public void setExcludeTables(String excludeTables) {
    this.excludeTables = excludeTables;
  }

  public boolean isMetaCache() {
    return metaCache;
  }

  public void setMetaCache(boolean metaCache) {
    this.metaCache = metaCache;
  }

  public boolean isBatch() {
    return batch;
  }

  public void setBatch(boolean batch) {
    this.batch = batch;
  }

  public int getFetchSize() {
    return fetchSize;
  }

  public void setFetchSize(int fetchSize) {
    this.fetchSize = fetchSize;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public boolean isLazy() {
    return lazy;
  }

  public void setLazy(boolean lazy) {
    this.lazy = lazy;
  }


  // DescriptorProvider interface ------------------------------------------------------------------------------------

  public void setDynamicQuerySupported(boolean dynamicQuerySupported) {
    this.dynamicQuerySupported = dynamicQuerySupported;
  }

  public void setAcceptUnknownColumnTypes(boolean acceptUnknownColumnTypes) {
    this.acceptUnknownColumnTypes = acceptUnknownColumnTypes;
  }

  // StorageSystem interface -----------------------------------------------------------------------------------------

  @Override
  public TypeDescriptor[] getTypeDescriptors() {
    logger.debug("getTypeDescriptors()");
    fetchMetadataIfNecessary();
    if (typeDescriptors == null) {
      return EMPTY_TYPE_DESCRIPTOR_ARRAY;
    } else {
      return CollectionUtil.toArray(typeDescriptors.values(), TypeDescriptor.class);
    }
  }

  @Override
  public TypeDescriptor getTypeDescriptor(String tableName) {
    logger.debug("getTypeDescriptor({})", tableName);
    fetchMetadataIfNecessary();
    return typeDescriptors.get(tableName);
  }

  @Override
  public void store(Entity entity) {
    if (readOnly) {
      throw BeneratorExceptionFactory.getInstance().illegalAccess(
          "Tried to insert rows into table '" + entity.type() + "' though database '" + id + "' is read-only");
    }
    logger.debug("Storing {}", entity);
    persistOrUpdate(entity, true);
  }

  @Override
  public void update(Entity entity) {
    if (readOnly) {
      throw BeneratorExceptionFactory.getInstance().illegalAccess(
          "Tried to update table '" + entity.type() + "' though database '" + id + "' is read-only");
    }
    logger.debug("Updating {}", entity);
    persistOrUpdate(entity, false);
  }

  @Override
  public void close() {
    IOUtil.close(importer);
  }

  public Entity queryEntityById(String tableName, Object id) {
    try {
      logger.debug("queryEntityById({}, {})", tableName, id);
      ComplexTypeDescriptor descriptor = (ComplexTypeDescriptor) getTypeDescriptor(tableName);
      PreparedStatement query = getSelectByPKStatement(descriptor);
      query.setObject(1, id); // TODO support composite keys
      ResultSet resultSet = query.executeQuery();
      if (resultSet.next()) {
        return ResultSet2EntityConverter.convert(resultSet, descriptor);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw BeneratorExceptionFactory.getInstance().dbQueryFailed("Error querying " + tableName, e);
    }
  }

  public long countEntities(String tableName) {
    logger.debug("countEntities({})", tableName);
    String query = "select count(*) from " +
        createCatSchTabString(catalogName, schemaName, tableName,
            dialect);
    return DBUtil.queryLong(query, getConnection());
  }

  @Override
  @SuppressWarnings({"null", "checkstyle:VariableDeclarationUsageDistance"})
  public DataSource<Entity> queryEntities(String type, String selector, Context context) {
    logger.debug("queryEntities({})", type);
    String tableSpec = createCatSchTabString(catalogName, schemaName, type, getDialect());
    String sql = renderSelectorQuery(tableSpec, "*", selector);
    DataSource<ResultSet> source = createQuery(sql, context);
    return new EntityResultSetDataSource(source, (ComplexTypeDescriptor) getTypeDescriptor(type));
  }

  @Override
  public DataSource<?> queryEntityIds(String tableName, String selector, Context context) {
    logger.debug("queryEntityIds({}, {})", tableName, selector);
    String tableSpec = createCatSchTabString(catalogName, schemaName, tableName, getDialect());
    // find out pk columns
    DBTable table = getTable(tableName);
    String[] pkColumnNames = table.getPKColumnNames();
    if (pkColumnNames.length == 0) {
      throw BeneratorExceptionFactory.getInstance().configurationError(
          "Cannot create reference to table " + tableName + " since it does not define a primary key");
    }
    String colsSpec = ArrayFormat.format(pkColumnNames);
    String query = renderSelectorQuery(tableSpec, colsSpec, selector);
    return query(query, true, context);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public DataSource<?> query(String query, boolean simplify, Context context) {
    logger.debug("query({})", query);
    DataSource<ResultSet> resultSetIterable = createQuery(query, context);
    ResultSetConverter converter = new ResultSetConverter(Object.class, simplify);
    return new ConvertingDataSource<>(resultSetIterable, converter);
  }

  // database-specific interface -------------------------------------------------------------------------------------

  public Consumer inserter() {
    return new StorageSystemInserter(this);
  }

  public Consumer inserter(String tableName) {
    return new StorageSystemInserter(this, (ComplexTypeDescriptor) getTypeDescriptor(tableName));
  }

  protected abstract PreparedStatement getSelectByPKStatement(ComplexTypeDescriptor descriptor);

  public boolean tableExists(String tableName) {
    logger.debug("tableExists({})", tableName);
    return (getTypeDescriptor(tableName) != null);
  }

  public void createSequence(String name) throws SQLException {
    getDialect().createSequence(name, 1, getConnection());
  }

  public void dropSequence(String name) {
    execute(getDialect().renderDropSequence(name));
  }

  @Override
  public Object execute(String sql) {
    try {
      DBUtil.executeUpdate(sql, getConnection());
      if (SQLUtil.mutatesStructure(sql)) {
        invalidate();
      }
      return null;
    } catch (SQLException e) {
      throw BeneratorExceptionFactory.getInstance().operationFailed("Failed to execute SQL: " + sql, e);
    }
  }

  public long nextSequenceValue(String sequenceName) {
    return DBUtil.queryLong(getDialect().renderFetchSequenceValue(sequenceName), getConnection());
  }

  public void setSequenceValue(String sequenceName, long value) throws SQLException {
    getDialect().setNextSequenceValue(sequenceName, value, getConnection());
  }

  protected Connection createConnection() {
    try {
      Connection connection = DBUtil.connect(url, driver, user, password, readOnly);
      if (!connectedBefore) {
        DBUtil.logMetaData(connection);
        connectedBefore = true;
      }
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      connection.setAutoCommit(false);
      return connection;
    } catch (ConnectFailedException e) {
      throw BeneratorExceptionFactory.getInstance().connectFailed("Connecting the database failed. URL: " + url, e);
    } catch (SQLException e) {
      throw BeneratorExceptionFactory.getInstance().serviceFailed("Turning off auto-commit failed", e);
    }
  }

  public void invalidate() {
    database = null;
    typeDescriptors = null;
    tables = null;
    if (importer instanceof CachingDBImporter) {
      ((CachingDBImporter) importer).invalidate();
    } else {
      CachingDBImporter.deleteCacheFile(url, user, catalogName, schemaName);
    }
    invalidationCount.incrementAndGet();
  }

  public int invalidationCount() {
    return invalidationCount.get();
  }

  public DatabaseDialect getDialect() {
    if (dialect == null) {
      try {
        DatabaseMetaData metaData = getConnection().getMetaData();
        String productName = metaData.getDatabaseProductName();
        VersionNumber productVersion
            = VersionNumber.valueOf(metaData.getDatabaseMajorVersion() + "." + metaData.getDatabaseMinorVersion());
        dialect = DatabaseDialectManager.getDialectForProduct(productName, productVersion);
      } catch (SQLException e) {
        throw BeneratorExceptionFactory.getInstance().configurationError("Database meta data access failed", e);
      }
    }
    return dialect;
  }

  public String getDbType() {
    return getDialect().getDbType();
  }

  // private helpers ------------------------------------------------------------------------------

  static String renderSelectorQuery(String tableSpec, String colsSpec, String selector) {
    // check for script
    ScriptSpec spec = ScriptUtil.parseSpec(selector);
    // construct SQL query
    String sql;
    String specText = spec.getText();
    if (!spec.isScript()) {
      sql = renderStaticSelectorQuery(tableSpec, colsSpec, specText);
    } else if (spec.isTemplateScript()) {
      sql = renderTemplateSelectorQuery(tableSpec, colsSpec, spec, specText);
    } else if (specText.startsWith("'")) {
      sql = renderSingleQuotedSelectorQuery(tableSpec, colsSpec, spec, specText, "'select", "'select ", " where ' + ");
    } else {
      throw BeneratorExceptionFactory.getInstance().programmerUnsupported(
          "Script engine not supported in <database> select or subSelect attributes: " + spec.getEngineId());
    }
    return sql;
  }

  private static String renderSingleQuotedSelectorQuery(String tableSpec, String colsSpec, ScriptSpec spec, String specText, String s, String s2,
                                                        String s3) {
    String sql;
    if (specText.toLowerCase().startsWith(s)) {
      // SELECTOR is expected to be a complete and valid SQL query
      sql = specText;
    } else {
      // SELECTOR is expected to be the argument of a WHERE clause
      sql = s2 + colsSpec + FROM + tableSpec + s3 + specText;
    }
    sql = "{" + spec.getEngineId() + ":" + sql + "}";
    return sql;
  }

  private static String renderTemplateSelectorQuery(String tableSpec, String colsSpec, ScriptSpec spec, String specText) {
    String sql;// SELECTOR is a template expression
    if (StringUtil.startsWithIgnoreCase(specText, SELECT)) {
      // SELECTOR is expected to be a complete and valid SQL query
      sql = specText;
    } else {
      // SELECTOR is expected to be the argument of a WHERE clause
      sql = SELECT + colsSpec + FROM + tableSpec + WHERE + specText;
    }
    sql = "{" + spec.getEngineId() + ":" + sql + "}";
    return sql;
  }

  private static String renderStaticSelectorQuery(String tableSpec, String colsSpec, String specText) {
    String sql;
    // SELECTOR is static text
    if (StringUtil.startsWithIgnoreCase(specText, SELECT)) {
      // SELECTOR is expected to be a complete and valid SQL query
      sql = specText;
    } else {
      // SELECTOR is expected to be the static argument of a WHERE clause
      sql = SELECT + colsSpec + FROM + tableSpec;
      if (!StringUtil.isEmpty(specText)) {
        sql += " WHERE " + specText;
      }
    }
    return sql;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + user + '@' + url + ']';
  }

  private void checkOracleDriverVersion(String driver) {
    if (driver != null && driver.contains("oracle")) {
      try (Connection connection = createConnection()) {
        DatabaseMetaData metaData = connection.getMetaData();
        VersionNumber driverVersion = VersionNumber.valueOf(metaData.getDriverVersion());
        if (driverVersion.compareTo(MIN_ORACLE_VERSION) < 0) {
          logger.warn("Your Oracle driver has a bug in metadata support. Please update to 10.2.0.4 or newer. " +
              "You can use that driver for accessing an Oracle 9 server as well.");
        }
      } catch (SQLException e) {
        throw BeneratorExceptionFactory.getInstance().configurationError("Error getting database meta data", e);
      }
    }
  }

  protected DataSource<ResultSet> createQuery(String query, Context context) {
    return new QueryDataSource(this, query, fetchSize, (dynamicQuerySupported ? context : null));
  }

  protected abstract PreparedStatement getStatement(
      ComplexTypeDescriptor descriptor, boolean insert, List<ColumnInfo> columnInfos);

  private void persistOrUpdate(Entity entity, boolean insert) {
    fetchMetadataIfNecessary();
    List<ColumnInfo> writeColumnInfos = getWriteColumnInfos(entity, insert);
    try {
      String tableName = entity.type();
      PreparedStatement statement = getStatement(entity.descriptor(), insert, writeColumnInfos);
      for (int i = 0; i < writeColumnInfos.size(); i++) {
        ColumnInfo info = writeColumnInfos.get(i);
        Object jdbcValue = entity.getComponent(info.name);
        if (info.type != null) {
          jdbcValue = AnyConverter.convert(jdbcValue, info.type);
        }
        handleOracleType(tableName, statement, i, info, jdbcValue);
      }
      if (batch) {
        statement.addBatch();
      } else {
        int rowCount = statement.executeUpdate();
        if (rowCount == 0) {
          throw BeneratorExceptionFactory.getInstance().illegalArgument(
              "Update failed because, since there is no database entry with the PK of " + entity);
        }
      }
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().serviceFailed("Error in persisting " + entity, e);
    }
  }

  private void handleOracleType(String tableName, PreparedStatement statement, int i, ColumnInfo info, Object jdbcValue) {
    try {
      boolean criticalOracleType =
          (dialect instanceof OracleDialect && (info.sqlType == Types.NCLOB || info.sqlType == Types.OTHER));
      if (jdbcValue != null || criticalOracleType) { // Oracle is not able to perform setNull() on NCLOBs and NVARCHAR2
        statement.setObject(i + 1, jdbcValue);
      } else {
        statement.setNull(i + 1, info.sqlType);
      }
    } catch (SQLException e) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument(
          "error setting column " + tableName + '.' + info.name, e);
    }
  }


  // meta data handling ----------------------------------------------------------------------------------------------

  public Database getDbMetaData() {
    return fetchMetadataIfNecessary();
  }

  protected Database fetchMetadataIfNecessary() {
    if (this.database != null) {
      return this.database;
    } else {
      return fetchMetaData();
    }
  }

  public Database fetchMetaData() {
    try {
      getDialect(); // make sure dialect is initialized
      this.database = haveMetaDataImporter().importDatabase();
      if (lazy) {
        logger.info("Fetching table details and ordering tables by dependency");
      } else {
        logger.info("Ordering tables by dependency");
      }
      this.tables = new HashMap<>();
      this.typeDescriptors = OrderedNameMap.createCaseIgnorantMap();
      List<DBTable> orderedTables = DBUtil.dependencyOrderedTables(database);
      for (DBTable table : orderedTables) {
        parseTable(table);
      }
      return this.database;
    } catch (ConnectFailedException e) {
      throw BeneratorExceptionFactory.getInstance().connectFailed("Database not available. ", e);
    } catch (ImportFailedException e) {
      throw BeneratorExceptionFactory.getInstance().serviceFailed(
          "Unexpected failure of database meta data import. ", e);
    }
  }

  private DBMetaDataImporter haveMetaDataImporter() {
    if (importer == null) {
      // create JDBC DB importer
      importer = JDBCMetaDataUtil.createJDBCDBImporter(getConnection(), user,
          catalogName, schemaName, includeTables, excludeTables);
    }
    // apply 'metaCache' setting
    if (metaCache) {
      if (importer instanceof JDBCDBImporter) { // newly created or 'metaCache' setting was changed (for example in unit test)
        importer = new CachingDBImporter(url, (JDBCDBImporter) importer);
      }
    } else {
      if (importer instanceof CachingDBImporter) { // 'metaCache' setting was changed (for example in unit test)
        importer = ((CachingDBImporter) importer).getRealImporter();
      }
    }
    return importer;
  }

  private void parseTable(DBTable table) {
    logger.debug("Parsing table {}", table);
    String tableName = table.getName();
    tables.put(tableName.toUpperCase(), table);
    ComplexTypeDescriptor complexType;
    if (lazy) {
      complexType = new LazyTableComplexTypeDescriptor(table, this);
    } else {
      complexType = mapTableToComplexTypeDescriptor(table,
          new ComplexTypeDescriptor(tableName, this));
    }
    typeDescriptors.put(tableName, complexType);
  }

  public ComplexTypeDescriptor mapTableToComplexTypeDescriptor(
      DBTable table, ComplexTypeDescriptor complexType) {
    // process primary keys
    DBPrimaryKeyConstraint pkConstraint = table.getPrimaryKeyConstraint();
    if (pkConstraint != null) {
      String[] pkColumnNames = pkConstraint.getColumnNames();
      if (pkColumnNames.length == 1) { // TODO support composite primary keys
        String columnName = pkColumnNames[0];
        DBColumn column = table.getColumn(columnName);
        table.getColumn(columnName);
        String abstractType = JdbcMetaTypeMapper.abstractType(column.getType(), acceptUnknownColumnTypes);
        IdDescriptor idDescriptor = new IdDescriptor(columnName, this, abstractType);
        complexType.setComponent(idDescriptor);
      }
    }

    // process foreign keys
    for (DBForeignKeyConstraint constraint : table.getForeignKeyConstraints()) {
      String[] foreignKeyColumnNames = constraint.getForeignKeyColumnNames();
      if (foreignKeyColumnNames.length == 1) {
        String fkColumnName = foreignKeyColumnNames[0];
        DBTable targetTable = constraint.getRefereeTable();
        DBColumn fkColumn = constraint.getTable().getColumn(fkColumnName);
        DBDataType concreteType = fkColumn.getType();
        String abstractType = JdbcMetaTypeMapper.abstractType(concreteType, acceptUnknownColumnTypes);
        ReferenceDescriptor descriptor = new ReferenceDescriptor(
            fkColumnName, this, abstractType, targetTable.getName(), constraint.getRefereeColumnNames()[0]);
        descriptor.getLocalType(false).setSource(id);
        descriptor.setMinCount(new ConstantExpression<>(1L));
        descriptor.setMaxCount(new ConstantExpression<>(1L));
        boolean nullable = fkColumn.isNullable();
        descriptor.setNullable(nullable);
        complexType.setComponent(descriptor); // overwrite possible id descriptor for foreign keys
        if (logger.isDebugEnabled()) {
          logger.debug("Parsed reference {}.{}", table.getName(), descriptor);
        }
      } else {
        // TODO handle composite keys
      }
    }
    // process normal columns
    for (DBColumn column : table.getColumns()) {
      try {
        logger.debug("parsing column: {}", column);
        String columnName = column.getName();
        if (complexType.getComponent(columnName) != null) {
          continue; // skip columns that were already parsed (fk)
        }
        String columnId = table.getName() + '.' + columnName;
        if (column.isVersionColumn()) {
          logger.debug("Leaving out version column {}", columnId);
          continue;
        }
        DBDataType columnType = column.getType();
        String type = JdbcMetaTypeMapper.abstractType(columnType, acceptUnknownColumnTypes);
        String defaultValue = column.getDefaultValue();
        SimpleTypeDescriptor typeDescriptor = new SimpleTypeDescriptor(columnId, this, type);
        if (defaultValue != null) {
          typeDescriptor.setDetailValue("constant", defaultValue);
        }
        if (column.getSize() != null) {
          Integer size = column.getSize();
          if (size < 2147483647) {
            typeDescriptor.setMaxLength(size);
          }
        }
        if (column.getFractionDigits() != null) {
          if ("timestamp".equals(type)) {
            typeDescriptor.setGranularity("1970-01-02");
          } else {
            typeDescriptor.setGranularity(
                decimalGranularity(column.getFractionDigits()));
          }
        }
        PartDescriptor descriptor = new PartDescriptor(columnName, this);
        descriptor.setLocalType(typeDescriptor);
        descriptor.setMinCount(new ConstantExpression<>(1L));
        descriptor.setMaxCount(new ConstantExpression<>(1L));
        descriptor.setNullable(column.getNotNullConstraint() == null);
        List<DBUniqueConstraint> ukConstraints = column.getUkConstraints();
        for (DBUniqueConstraint constraint : ukConstraints) {
          if (constraint.getColumnNames().length == 1) {
            descriptor.setUnique(true);
          } else {
            logger.debug(
                "Automated uniqueness assurance on multiple columns is not provided yet: {}", constraint);
            // TODO support uniqueness constraints on combination of columns
          }
        }
        logger.debug("parsed attribute {}: {}", columnId, descriptor);
        complexType.setComponent(descriptor);
      } catch (Exception e) {
        throw BeneratorExceptionFactory.getInstance().configurationError(
            "Error processing column " + column.getName() + " of table " + table.getName(), e);
      }
    }
    return complexType;
  }

  public List<ColumnInfo> getWriteColumnInfos(Entity entity, boolean insert) {
    String tableName = entity.type();
    DBTable table;
    if (entity.descriptor instanceof LazyTableComplexTypeDescriptor) {
      table = getTable(this.schemaName, tableName);
    } else {
      table = getTable(tableName);
    }
    List<String> pkColumnNames = CollectionUtil.toList(table.getPKColumnNames());
    ComplexTypeDescriptor typeDescriptor = (ComplexTypeDescriptor) getTypeDescriptor(tableName);
    Collection<ComponentDescriptor> componentDescriptors = typeDescriptor.getComponents();
    List<ColumnInfo> pkInfos = new ArrayList<>(componentDescriptors.size());
    List<ColumnInfo> normalInfos = new ArrayList<>(componentDescriptors.size());
    ComplexTypeDescriptor entityDescriptor = entity.descriptor();
    for (ComponentDescriptor colDescriptor : componentDescriptors) {
      String columnName = colDescriptor.getName();
      if (!ignoreColumn(colDescriptor, entityDescriptor, columnName)) {
        ColumnInfo info = getWriteColumnInfo(entity, entityDescriptor, table, colDescriptor);
        if (pkColumnNames.contains(columnName)) {
          pkInfos.add(info);
        } else {
          normalInfos.add(info);
        }
      }
    }
    if (insert) {
      pkInfos.addAll(normalInfos);
      return pkInfos;
    } else {
      normalInfos.addAll(pkInfos);
      return normalInfos;
    }
  }

  private boolean ignoreColumn(ComponentDescriptor colDescriptor, ComplexTypeDescriptor entityDescriptor, String columnName) {
    if (colDescriptor.getMode() == Mode.ignored) {
      return true;
    }
    ComponentDescriptor attrDescriptor = entityDescriptor.getComponent(columnName);
    return (attrDescriptor != null && attrDescriptor.getMode() == Mode.ignored);
  }

  private ColumnInfo getWriteColumnInfo(Entity entity, ComplexTypeDescriptor entityDescriptor,
                                        DBTable table, ComponentDescriptor dbCompDescriptor) {
    String name = dbCompDescriptor.getName();
    DBColumn column = table.getColumn(name);
    DBDataType columnType = column.getType();
    Class<?> typeToWrite;
    int sqlType = columnType.getJdbcType();
    if ("UUID".equals(columnType.getName())) { // Special treatment for Postgres UUID types
      typeToWrite = UUID.class;
    } else if ("JSON".equals(columnType.getName())) { // Special treatment for Postgres JSON type
      typeToWrite = JSONPGObject.class;
    } else if ("GEOMETRY".equals(columnType.getName())) {
        typeToWrite = PGgeometry.class;
    }
    else {
      SimpleTypeDescriptor type = (SimpleTypeDescriptor) dbCompDescriptor.getTypeDescriptor();
      PrimitiveType primitiveType = type.getPrimitiveType();
      if (primitiveType == null) {
        if (!acceptUnknownColumnTypes) {
          throw BeneratorExceptionFactory.getInstance().configurationError(
              "Column type of " + entityDescriptor.getName() + "." + dbCompDescriptor.getName() +
                  " unknown: " + type.getName());
        } else if (entity.get(type.getName()) instanceof String) {
          primitiveType = PrimitiveType.STRING;
        } else {
          primitiveType = PrimitiveType.OBJECT;
        }
      }
      String primitiveTypeName = primitiveType.getName();
      typeToWrite = driverTypeMapper.concreteType(primitiveTypeName);
    }
    // TODO akell v3.0.0 wrong entity information when table with same name exists in different schema and is part of context.
    // should work like this: <database id="db" .../>
    // => <generate type="db:tablename" />
    return new ColumnInfo(name, sqlType, typeToWrite);
  }

  public DBTable getTable(String tableName) {
    fetchMetadataIfNecessary();
    DBTable table = findTableInConfiguredCatalogAndSchema(schemaName, tableName);
    if (table != null) {
      return table;
    } else {
      table = findAnyTableOfName(tableName);
      if (table != null) {
        logger.warn("Table '{}' not found in the expected catalog or schema." +
                "I have taken it from catalog '{}' and schema '{}' instead. " +
                "You better make sure this is right and fix the configuration",
            tableName, table.getCatalog(), table.getSchema());
        return table;
      }
    }
    throw BeneratorExceptionFactory.getInstance().objectNotFound("Table " + tableName);
  }

  public DBTable getTable(String schemaName, String tableName) {
    fetchMetadataIfNecessary();
    DBTable table = findTableInConfiguredCatalogAndSchema(schemaName, tableName);
    if (table != null) {
      return table;
    } else {
      table = findAnyTableOfName(tableName);
      if (table != null) {
        logger.info("Table '{}' not found " +
                "in the expected catalog or schema." +
                "I have taken it from catalog '{}' and schema '{}' instead. " +
                "You better make sure this is right and fix the configuration",
            tableName, table.getCatalog(), table.getSchema());
        return table;
      }
    }
    throw BeneratorExceptionFactory.getInstance().objectNotFound("Table " + tableName);
  }

  private DBTable findAnyTableOfName(String tableName) {
    for (DBCatalog catalog : database.getCatalogs()) {
      for (DBSchema schema : catalog.getSchemas()) {
        DBTable table = schema.getTable(tableName);
        if (table != null) {
          return table;
        }
      }
    }
    return null;
  }

  private DBTable findTableInConfiguredCatalogAndSchema(String tableName) {
    DBCatalog catalog = database.getCatalog(catalogName);
    DBSchema dbSchema;
    if (catalog == null) {
      escalator.escalate("No catalog set for database, trying to get schema directly", this,
          "findTableInConfiguredCatalogAndSchema()");
      return database.getCatalog("benerator").getSchema(schemaName).getTable(tableName);
    } else {
      dbSchema = catalog.getSchema(schemaName);
    }
    if (dbSchema != null) {
      return dbSchema.getTable(tableName);
    }
    return null;
  }

  private DBTable findTableInConfiguredCatalogAndSchema(String schemaName, String tableName) {
    DBSchema dbSchema;
    DBCatalog catalog = database.getCatalog(catalogName);
    if (catalog == null) {
      escalator.escalate("No catalog set for database, trying to get schema directly", this,
          "findTableInConfiguredCatalogAndSchema()");
      dbSchema = database.getSchema(schemaName);
    } else {
      dbSchema = catalog.getSchema(schemaName);
    }
    if (dbSchema != null) {
      return dbSchema.getTable(tableName);
    }
    return null;
  }

  public abstract void commit();

}
