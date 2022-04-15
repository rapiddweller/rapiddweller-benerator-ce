package com.rapiddweller.platform.nosql;

import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.TypeDescriptor;

import java.util.Map;
import java.util.Objects;

/**
 * Abstract Implementation of a custom storage system. Extend this class for custom storage systems.
 * These custom storage systems can be used with <storage-system>.
 * See {@link com.rapiddweller.platform.nosql.mongo.MongoDBSystem} for reference implementation.
 */
public abstract class CustomStorageSystem extends AbstractStorageSystem {

    protected final String id;
    protected final DefaultDescriptorProvider descriptorProvider;

    protected CustomStorageSystem(String id, DataModel dataModel, Map<String, String> params) {
        this.dataModel = dataModel;
        this.id = id;
        this.descriptorProvider = new DefaultDescriptorProvider(id, dataModel);
        init(params);
        if (Boolean.parseBoolean(params.getOrDefault("clean", "false"))) {
            clean();
        }
    }

    public void addTypeDescriptor(TypeDescriptor typeDescriptor) {
        this.descriptorProvider.addTypeDescriptor(typeDescriptor);
    }

    @Override
    public TypeDescriptor[] getTypeDescriptors() {
        return this.descriptorProvider.getTypeDescriptors();
    }

    @Override
    public TypeDescriptor getTypeDescriptor(String typeName) {
        return this.descriptorProvider.getTypeDescriptor(typeName);
    }

    @Override
    public String getId() {
        return this.id;
    }

    protected boolean isUnknownType(TypeDescriptor typeDescriptor) {
        return !Objects.nonNull(descriptorProvider.getTypeDescriptor(typeDescriptor.getName()));
    }

    protected abstract void init(Map<String, String> params);

    protected abstract void clean();
}
