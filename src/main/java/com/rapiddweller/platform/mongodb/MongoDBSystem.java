package com.rapiddweller.platform.mongodb;

import com.mongodb.client.model.Filters;
import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.consumer.NoConsumer;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.storage.StorageSystemInserter;
import com.rapiddweller.benerator.storage.StorageSystemUpdater;
import com.rapiddweller.benerator.util.DeprecationLogger;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.util.ConvertingDataSource;
import com.rapiddweller.model.data.*;
import com.rapiddweller.platform.mongodb.exception.NullEntityTypeException;
import com.rapiddweller.platform.mongodb.converter.DocumentToObjectConverter;
import com.rapiddweller.platform.mongodb.converter.DocumentIdToEntityConverter;
import com.rapiddweller.platform.mongodb.converter.DocumentToEntityConverter;
import com.rapiddweller.platform.mongodb.client.MongoDBClient;
import com.rapiddweller.platform.mongodb.client.MongoDBClientProvider;
import com.rapiddweller.platform.mongodb.datasource.MongoDBDataSource;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

import static com.rapiddweller.platform.mongodb.MongoDBUtils.convertEntityToDocument;
import static java.lang.Boolean.*;
import static java.lang.String.format;

public class MongoDBSystem extends CustomStorageSystem {

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.getInstance();

  private final MongoDBClientProvider mongoDBClientProvider;
  private final MongoDBClient mongoDBClient;
  private final Map<String, List<String>> entityPaths = new HashMap<>();

  public MongoDBSystem(DataModel dataModel, String id, String host, Integer port, String database, String user,
                       String password, String authenticationDatabase, String authMechanism, Boolean clean) {
    super(id, dataModel);
    if (authenticationDatabase == null) {
      authenticationDatabase = "admin";
    }
    if (authMechanism == null) {
      authMechanism = "SCRAM-SHA-1";
    }
    mongoDBClientProvider = new MongoDBClientProvider(host, port, database, user, password, authenticationDatabase, authMechanism);
    mongoDBClient = mongoDBClientProvider.createMongoDBClient();
    if (TRUE.equals(clean)) {
      mongoDBClient.cleanDatabase();
    }
  }

  public MongoDBSystem(String id, String environmentName, String systemName, BeneratorContext context) {
    super(id, context.getDataModel());
    if (environmentName != null) {
      if (systemName == null) {
        systemName = "db";
        DeprecationLogger.warn("Observed a <Database> definition with an 'environment', but without 'system' setting. " +
            "If you are using the old definition file format, please upgrade to " +
            "the new environment definition file format introduced in Benerator 3.0.0 and specify a 'system' name. " +
            "The old format is supported for backwards compatibility, but will be dropped in a future release");
      }
      SystemRef def = context.getEnvironmentSystem(environmentName, systemName);
      if (!"db".equals(def.getType())) {
        throw BeneratorExceptionFactory.getInstance().configurationError(
            "Not a database definition: '" + systemName + "' in environment '" + environmentName + "'");
      }
      try {
        mongoDBClientProvider = new MongoDBClientProvider(
            getOrElseThrowConfigurationError(def, "host"),
            Integer.parseInt(getOrElseThrowConfigurationError(def, "port")),
            getOrElseThrowConfigurationError(def, "database"),
            getOrElseThrowConfigurationError(def, "user"),
            getOrElseThrowConfigurationError(def, "password"),
            getOrElseReturnDefault(def, "authenticationDatabase", "admin"),
            getOrElseReturnDefault(def, "authenticationMechanism", "SCRAM-SHA-1")
        );
        mongoDBClient = mongoDBClientProvider.createMongoDBClient();
        if (TRUE.equals(parseBoolean(Optional.ofNullable(def.getProperty("clean")).orElse("false")))) {
          mongoDBClient.cleanDatabase();
        }
      } catch (ConfigurationError ex) {
        throw EXCEPTION_FACTORY.configurationError(format(
            "Cannot initiate Mongodb with id %s, because there is a missing attribute.", id), ex);
      }
    } else {
      throw EXCEPTION_FACTORY.configurationError(format(
          "Cannot initiate Mongodb with id %s, because the given environment is null.", id));
    }
  }

  private static String getOrElseThrowConfigurationError(SystemRef systemRef, String key) {
    return Optional.ofNullable(systemRef.getProperty(key))
        .orElseThrow(() -> EXCEPTION_FACTORY.configurationError(format(
            "No environment variable %s in environment %s found.", key, systemRef.getName())));
  }

  private static String getOrElseReturnEmpty(SystemRef systemRef, String key) {
    return Optional.ofNullable(systemRef.getProperty(key)).orElse("");
  }

  private static String getOrElseReturnDefault(SystemRef systemRef, String key, String defaultValue) {
    return Optional.ofNullable(systemRef.getProperty(key)).orElse(defaultValue);
  }

  @Override
  public DataSource<Entity> queryEntities(String collection, String query, Context context) {
    MongoDBDataSource mongoDBDataSource = new MongoDBDataSource(mongoDBClientProvider, collection, query, context);
    ComplexTypeDescriptor descriptor = (ComplexTypeDescriptor) getTypeDescriptor(collection);
    if (descriptor == null) {
      DataModel dm = new DataModel();
      DescriptorProvider dp = new DefaultDescriptorProvider("default", dm);
      descriptor = new ComplexTypeDescriptor(collection, dp);
    }
    DocumentToEntityConverter documentToEntityConverter = new DocumentToEntityConverter(descriptor);
    return new ConvertingDataSource<>(mongoDBDataSource, documentToEntityConverter);
  }

  @Override
  public DataSource<Object> queryEntityIds(String entity, String query, Context context) {
    String collection = getCollection(entity);
    MongoDBDataSource mongoDBDataSource = new MongoDBDataSource(mongoDBClientProvider, collection, query, context);
    DocumentIdToEntityConverter documentIdToEntityConverter = createDocumentIdToEntityConverter(collection);
    return new ConvertingDataSource<>(mongoDBDataSource, documentIdToEntityConverter);
  }

  private DocumentIdToEntityConverter createDocumentIdToEntityConverter(String entity) {
    String idName = MongoDBUtils.getIdName((ComplexTypeDescriptor) getTypeDescriptor(entity));
    List<String> path = getIdFieldPath(entity);
    path.add(idName);
    return new DocumentIdToEntityConverter(path);
  }

  private String getCollection(String entity) {
    return entityPaths.get(entity).get(0);
  }

  private List<String> getIdFieldPath(String entity) {
    if (entityPaths.get(entity).size() == 1) {
      return new ArrayList<>();
    }
    return new ArrayList<>(entityPaths.get(entity).subList(1, entityPaths.get(entity).size()));
  }

  @Override
  public DataSource<Object> query(String query, boolean simplify, Context context) {
    MongoDBDataSource mongoDBDataSource = new MongoDBDataSource(mongoDBClientProvider, null, query, context);
    DocumentToObjectConverter documentConverter = new DocumentToObjectConverter(simplify);
    return new ConvertingDataSource<>(mongoDBDataSource, documentConverter);
  }

  @Override
  public void store(Entity entity) {
    String entityType = getEntityType(entity);
    addTypeDescriptor(entity.descriptor);
    mongoDBClient.insertDocument(entityType, convertEntityToDocument(entity));
  }

  @Override
  public void update(Entity entity) {
    String entityType = getEntityType(entity);
    addTypeDescriptor(entity.descriptor);
    mongoDBClient.replaceDocument(entityType, Filters.eq("_id", entity.get("_id")), convertEntityToDocument(entity));
  }

  private String getEntityType(Entity entity) {
    return Optional.of(entity.type()).orElseThrow(() -> new NullEntityTypeException(entity));
  }

  @Override
  public TypeDescriptor getTypeDescriptor(String typeName) {
    String fullTypeName = Strings.join(entityPaths.get(typeName), '.');
    return this.descriptorProvider.getTypeDescriptor(fullTypeName);
  }

  @Override
  public void addTypeDescriptor(TypeDescriptor typeDescriptor) {
    if (typeDescriptor instanceof ComplexTypeDescriptor && isUnknownType(typeDescriptor)) {
      updateDescriptorProvider((ComplexTypeDescriptor) typeDescriptor);
    }
  }

  private void updateDescriptorProvider(ComplexTypeDescriptor typeDescriptor) {
    if (Objects.nonNull(typeDescriptor)) {
      List<String> path = new ArrayList<>(List.of(typeDescriptor.getName()));
      typeDescriptor.getParts().stream()
          .filter(this::isNestedPartDescriptor)
          .map(e -> (ComplexTypeDescriptor) e.getLocalType())
          .forEach(e -> updateDescriptorProvider(e, typeDescriptor.getName(), path));
      descriptorProvider.addTypeDescriptor(typeDescriptor);
      updatePaths(typeDescriptor.getName(), path);
    }
  }

  private void updateDescriptorProvider(ComplexTypeDescriptor typeDescriptor, String parentName, List<String> path) {
    String childName = getChildName(typeDescriptor, parentName);
    List<String> currentPath = new ArrayList<>(path);
    currentPath.add(childName);
    typeDescriptor.getParts().stream()
        .filter(this::isNestedPartDescriptor)
        .map(e -> (ComplexTypeDescriptor) e.getLocalType())
        .forEach(e -> updateDescriptorProvider(e, typeDescriptor.getName(), currentPath));
    if (isUnknownType(typeDescriptor)) {
      descriptorProvider.addTypeDescriptor(typeDescriptor);
    }
    updatePaths(childName, currentPath);
  }

  private boolean isNestedPartDescriptor(InstanceDescriptor partDescriptor) {
    return partDescriptor instanceof PartDescriptor && partDescriptor.getLocalType() instanceof ComplexTypeDescriptor;
  }

  private String getChildName(ComplexTypeDescriptor typeDescriptor, String parentName) {
    return typeDescriptor.getName().replaceFirst(parentName + ".", "");
  }

  private void updatePaths(String path, List<String> pathAsList) {
    entityPaths.put(path, pathAsList);
  }

  private boolean isUnknownType(TypeDescriptor typeDescriptor) {
    return !Objects.nonNull(descriptorProvider.getTypeDescriptor(typeDescriptor.getName()));
  }

  @Override
  public void flush() {
    // not needed
  }

  @Override
  public void close() {
    mongoDBClient.close();
  }

  public Consumer inserter(String target) {
    var targetType = (ComplexTypeDescriptor) getTypeDescriptor(target);
    targetType = targetType == null ? new ComplexTypeDescriptor(target, descriptorProvider) : targetType;
    return new StorageSystemInserter(this, targetType);
  }

  public Consumer deleter(String target) {
    mongoDBClient.getDatabase(this.mongoDBClientProvider.getDatabase()).getCollection(target).drop();
    return new NoConsumer();
  }

}
