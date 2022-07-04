package com.rapiddweller.platform.mongodb.converter;

import com.rapiddweller.common.ConversionException;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class DocumentIdToEntityConverterTest {
    /**
     * Method under test: {@link DocumentIdToEntityConverter#DocumentIdToEntityConverter(List)}
     */
    @Test
    public void testConstructor() {
        DocumentIdToEntityConverter actualDocumentIdToEntityConverter = new DocumentIdToEntityConverter(new ArrayList<>());
        Class<Document> expectedSourceType = Document.class;
        assertSame(expectedSourceType, actualDocumentIdToEntityConverter.getSourceType());
        assertTrue(actualDocumentIdToEntityConverter.isThreadSafe());
        assertTrue(actualDocumentIdToEntityConverter.isParallelizable());
        Class<Object> expectedTargetType = Object.class;
        assertSame(expectedTargetType, actualDocumentIdToEntityConverter.getTargetType());
    }

    /**
     * Method under test: {@link DocumentIdToEntityConverter#convert(Document)}
     */
    @Test
    public void testConvert() throws ConversionException {


        ArrayList<String> idFieldPath = new ArrayList<>();
        idFieldPath.add("1");
        idFieldPath.add("2");
        idFieldPath.add("3");

        DocumentIdToEntityConverter documentIdToEntityConverter = new DocumentIdToEntityConverter(idFieldPath);
        assertEquals("Test", documentIdToEntityConverter.convert(new Document("1", "Test")));
    }

    /**
     * Method under test: {@link DocumentIdToEntityConverter#convert(Document)}
     */
    @Test
    public void testConvert2() throws ConversionException {
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("foo");
        DocumentIdToEntityConverter documentIdToEntityConverter = new DocumentIdToEntityConverter(stringList);
        assertNull(documentIdToEntityConverter.convert(new Document()));
    }
}

