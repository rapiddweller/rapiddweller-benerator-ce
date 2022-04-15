package com.rapiddweller.platform.nosql.mongo;

import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.platform.nosql.exception.TypeNotFoundException;
import com.rapiddweller.platform.nosql.mongo.converter.EntityToDocumentConverter;
import org.bson.Document;

public class MongoDBUtils {

    private static final EntityToDocumentConverter ENTITY_TO_DOCUMENT_CONVERTER = new EntityToDocumentConverter();

    private MongoDBUtils() {}

    public static Document toDocument(String rawQuery) {
        return rawQuery == null ? new Document() : Document.parse("{" + rawQuery + "}");
    }

    public static String getIdName(ComplexTypeDescriptor typeDescriptor) {
        return typeDescriptor.getParts().stream()
                .filter(IdDescriptor.class::isInstance)
                .findFirst()
                .orElseThrow(() -> new TypeNotFoundException(typeDescriptor))
                .getName();
    }

    public static Document convertEntityToDocument(Entity entity) {
        return ENTITY_TO_DOCUMENT_CONVERTER.convert(entity);
    }
}
