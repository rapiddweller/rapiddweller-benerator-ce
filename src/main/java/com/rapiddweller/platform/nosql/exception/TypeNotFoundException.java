package com.rapiddweller.platform.nosql.exception;

import com.rapiddweller.model.data.TypeDescriptor;

public class TypeNotFoundException extends RuntimeException {

    public TypeNotFoundException(TypeDescriptor type) {
        super(String.format("Could not find type %s.", type));
    }
}
