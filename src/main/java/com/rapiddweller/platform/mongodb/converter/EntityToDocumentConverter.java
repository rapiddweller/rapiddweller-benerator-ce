package com.rapiddweller.platform.mongodb.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import com.rapiddweller.model.data.Entity;
import org.bson.Document;
import org.bson.types.Decimal128;

import java.lang.reflect.Array;
import java.util.*;

public class EntityToDocumentConverter extends ThreadSafeConverter<Entity, Document> {

    public EntityToDocumentConverter() {
        super(Entity.class, Document.class);
    }

    @Override
    public Document convert(Entity entity) throws ConversionException {
        Document document = new Document();
        for (Map.Entry<String, Object> component : entity.getComponents().entrySet()) {
            Object value = component.getValue();
            if (value instanceof Entity) {
                // Cast field with key=="$numberLong"
                Entity subEntity = (Entity) component.getValue();
                if (subEntity.get("$numberLong") != null) {
                    value = Long.parseLong((String) subEntity.get("$numberLong"));
                } else if (subEntity.get("$numberInt") != null) {
                    value = Integer.parseInt((String) subEntity.get("$numberInt"));
                } else if (subEntity.get("$numberDecimal") != null) {
                    value = Decimal128.parse((String) subEntity.get("$numberDecimal"));
                } else if (subEntity.get("$date") != null) {
                    value = new Date(Long.parseLong((String) subEntity.get("$date")) * 1000);
                } else {
                    value = convert((Entity) value);
                }
            }
            else if (value instanceof List)
                value = convertList((List<?>) value);
            else if (value instanceof Set)
                value = convertSet((Set<?>) value);
            else if (value != null && value.getClass().isArray())
                value = convertArray(value);
            document.put(component.getKey(), value);
        }
        return document;
    }

    private Object convertList(List<?> list) {
        List<Object> result = new ArrayList<>();
        for (Object element : list) {
            if (element instanceof Entity)
                element = convert((Entity) element);
            result.add(element);
        }
        return result;
    }

    private Set<?> convertSet(Set<?> set) {
        Set<Object> result = new HashSet<>();
        for (Object element : set) {
            if (element instanceof Entity)
                element = convert((Entity) element);
            result.add(element);
        }
        return result;
    }

    private Object[] convertArray(Object array) {
        int length = Array.getLength(array);
        Object[] result = new Object[length];
        for (int i = 0; i < length; i++) {
            Object element = Array.get(array, i);
            if (element instanceof Entity)
                element = convert((Entity) element);
            result[i] = element;
        }
        return result;
    }


}
