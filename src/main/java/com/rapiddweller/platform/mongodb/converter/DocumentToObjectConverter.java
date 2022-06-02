package com.rapiddweller.platform.mongodb.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import org.bson.Document;
import org.bson.types.Decimal128;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DocumentToObjectConverter extends ThreadSafeConverter<Document, Object> {

    private final boolean simplifying;

    public DocumentToObjectConverter(boolean simplifying) {
        super(Document.class, Object.class);
        this.simplifying = simplifying;
    }

    @Override
    public Object convert(Document document) throws ConversionException {
        Object[] converted = convertToArray(document);
        if (this.simplifying && converted.length <= 1) {
            return converted[0];
        }
        return converted;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    private Object[] convertToArray(Document document) {
        List<Object> results = new ArrayList<>();
        for (Object value: document.values()) {
            if (value instanceof Document) {
                results.add(convertToArray((Document) value));
            }
            else if (value instanceof Collection) {
                results.add(convertCollection((Collection<?>) value));
            }
            else if (value.getClass().isArray()) {
                results.add(convertCollection(List.of(value)));
            }
            else {
                results.add(parseValue(value));
            }
        }
        return results.toArray();
    }

    private Object[] convertCollection(Collection<?> collection) {
        List<Object> results = new ArrayList<>();
        for (Object val : collection) {
            if (val instanceof Document) {
                results.add(convertToArray((Document) val));
            } else if (val instanceof Collection) {
                results.add(convertCollection((Collection<?>) val));
            } else {
                results.add(parseValue(val));
            }
        }
        return results.toArray();
    }

    private Object parseValue(Object typeValue) {
        if (typeValue instanceof Decimal128) {
            return ((Decimal128) typeValue).doubleValue();
        }
        return typeValue;
    }
}
