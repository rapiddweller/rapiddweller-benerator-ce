package com.rapiddweller.platform.nosql.mongo.converter;


import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;


public class DocumentToEntityConverter extends ThreadSafeConverter<Document, Entity> {

    private final ComplexTypeDescriptor typeDescriptor;

    public DocumentToEntityConverter(ComplexTypeDescriptor typeDescriptor) {
        super(Document.class, Entity.class);
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public Entity convert(Document document) throws ConversionException {
        Entity entity = new Entity(typeDescriptor);
        document.keySet().stream()
                .filter(e -> !e.equals("_id"))
                .forEach(e -> setComponent(e, document, entity));
        return entity;
    }

    private void setComponent(String componentName, Document document, Entity entity) {
        entity.setComponent(componentName, convertComponent(document.get(componentName)));
    }

    private Object convertComponent(Object component) {
        if (Objects.isNull(component)) {
            return null;
        }
        if (component instanceof Document) {
            return convert((Document) component);
        }
        if (component instanceof Collection) {
            return convertCollection((Collection<?>) component);
        }
        return component;
    }

    private Collection<?>  convertCollection(Collection<?> collection) {
        return collection.stream()
                .map(this::convertComponent)
                .collect(Collectors.toList());
    }


}
