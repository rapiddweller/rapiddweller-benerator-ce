package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;
import org.bson.types.ObjectId;

public class MongoDBObjectIdGenerator extends ThreadSafeNonNullGenerator<ObjectId> {

    @Override
    public Class<ObjectId> getGeneratedType() {
        return ObjectId.class;
    }

    @Override
    public ObjectId generate() {
        ObjectId objectId = new ObjectId();
        return objectId;
    }
}
