package com.rapiddweller.platform.nosql.mongo.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DocumentIdToEntityConverter extends ThreadSafeConverter<Document, Object> {

    private final List<String> idFieldPath;

    public DocumentIdToEntityConverter(List<String> idFieldPath) {
        super(Document.class, Object.class);
        this.idFieldPath = idFieldPath;
    }

    @Override
    public Object convert(Document document) throws ConversionException {
        return getId(document, new ArrayList<>(idFieldPath));
    }

    private Object getId(Document document, List<String> path) {
        Object value = document.get(path.remove(0));
        return value instanceof Document ? getId((Document) value, path) : value;
    }

}
