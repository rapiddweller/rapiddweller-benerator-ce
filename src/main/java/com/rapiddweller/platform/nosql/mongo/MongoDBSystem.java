package com.rapiddweller.platform.nosql.mongo;

import com.mongodb.client.model.Filters;
import com.rapiddweller.common.Context;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.util.ConvertingDataSource;
import com.rapiddweller.model.data.*;
import com.rapiddweller.platform.nosql.CustomStorageSystem;
import com.rapiddweller.platform.nosql.exception.NullEntityTypeException;
import com.rapiddweller.platform.nosql.mongo.converter.DocumentToObjectConverter;
import com.rapiddweller.platform.nosql.mongo.converter.DocumentIdToEntityConverter;
import com.rapiddweller.platform.nosql.mongo.converter.DocumentToEntityConverter;
import com.rapiddweller.platform.nosql.mongo.client.MongoDBClient;
import com.rapiddweller.platform.nosql.mongo.client.MongoDBClientProvider;
import com.rapiddweller.platform.nosql.mongo.datasource.MongoDBDataSource;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

import static com.rapiddweller.platform.nosql.mongo.MongoDBUtils.convertEntityToDocument;

public class MongoDBSystem extends CustomStorageSystem {

    private MongoDBClientProvider mongoDBClientProvider;
    private MongoDBClient mongoDBClient;
    private final Map<String, List<String>> entityPaths = new HashMap<>();

    public MongoDBSystem(String id, DataModel dataModel, Map<String, String> params) {
        super(id, dataModel, params);
    }

    protected void init(Map<String, String> params) {
        mongoDBClientProvider = new MongoDBClientProvider(
                params.get("host"),
                Integer.parseInt(params.get("port")),
                params.get("database"),
                params.get("user"),
                params.getOrDefault("password", ""));
        mongoDBClient = mongoDBClientProvider.createMongoDBClient();
    }

    @Override
    public DataSource<Entity> queryEntities(String collection, String query, Context context) {
        MongoDBDataSource mongoDBDataSource = new MongoDBDataSource(mongoDBClientProvider, collection, query, context);
        DocumentToEntityConverter documentToEntityConverter = new DocumentToEntityConverter((ComplexTypeDescriptor) getTypeDescriptor(collection));
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
        String idName = MongoDBUtils.getIdName(entity.descriptor);
        mongoDBClient.replaceDocument(entityType, Filters.eq(idName, entity.get(idName)), convertEntityToDocument(entity));
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

    @Override
    public void flush() {
        // not needed
    }

    @Override
    public void close() {
        mongoDBClient.close();
    }

    @Override
    protected void clean() {
        mongoDBClient.cleanDatabase();
    }


}
