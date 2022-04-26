package com.rapiddweller.platform.nosql;

import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.TypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Implementation of a custom storage system. Extend this class for custom storage systems.
 * These custom storage systems can be used with <storage-system>.
 * See {@link com.rapiddweller.platform.nosql.mongo.MongoDBSystem} for reference implementation.
 */
public abstract class CustomStorageSystem extends AbstractStorageSystem {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final String id;
    protected final DefaultDescriptorProvider descriptorProvider;

    protected CustomStorageSystem(String id, DataModel dataModel) {
        this.id = id;
        this.dataModel = dataModel;
        this.descriptorProvider = new DefaultDescriptorProvider(id, dataModel);
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

}
