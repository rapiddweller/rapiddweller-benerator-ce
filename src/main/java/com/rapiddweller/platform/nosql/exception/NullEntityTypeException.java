package com.rapiddweller.platform.nosql.exception;

import com.rapiddweller.model.data.Entity;

public class NullEntityTypeException extends RuntimeException {

    public NullEntityTypeException(Entity entity) {
        super("Trying to persist an entity without type: " + entity);
    }
}
