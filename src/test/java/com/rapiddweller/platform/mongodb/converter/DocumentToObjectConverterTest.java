package com.rapiddweller.platform.mongodb.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.rapiddweller.common.ConversionException;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.junit.Ignore;
import org.junit.Test;

public class DocumentToObjectConverterTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link DocumentToObjectConverter#DocumentToObjectConverter(boolean)}
     *   <li>{@link DocumentToObjectConverter#toString()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        assertEquals("DocumentToObjectConverter", (new DocumentToObjectConverter(true)).toString());
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);
        documentToObjectConverter.convert(new Document());
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert2() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(false);
        assertEquals(0, ((Object[]) documentToObjectConverter.convert(new Document())).length);
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert3() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        Document document = new Document();
        document.append("Key", "Value");
        assertEquals("Value", documentToObjectConverter.convert(document));
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert4() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        Document document = new Document();
        document.append("Key", new Document());
        assertEquals(0, ((Object[]) documentToObjectConverter.convert(document)).length);
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert5() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        Document document = new Document();
        document.append("Key", new ArrayList<>());
        assertEquals(0, ((Object[]) documentToObjectConverter.convert(document)).length);
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert6() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        Document document = new Document();
        document.append("Key", Decimal128.parse("42"));
        assertEquals(42.0, documentToObjectConverter.convert(document));
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert7() throws ConversionException {

        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        Document document = new Document();
        document.append("Key", null);
        assertNull(documentToObjectConverter.convert(document));
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert8() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        Document document = new Document();
        document.append("codec", "Value");
        document.append("Key", "Value");
        assertEquals(2, ((Object[]) documentToObjectConverter.convert(document)).length);
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert9() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        ArrayList<Object> objectList = new ArrayList<>();
        objectList.add("42");

        Document document = new Document();
        document.append("Key", objectList);
        assertEquals(1, ((Object[]) documentToObjectConverter.convert(document)).length);
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert10() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        ArrayList<Object> objectList = new ArrayList<>();
        objectList.add(new Document());

        Document document = new Document();
        document.append("Key", objectList);
        assertEquals(1, ((Object[]) documentToObjectConverter.convert(document)).length);
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert11() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        ArrayList<Object> objectList = new ArrayList<>();
        objectList.add(new ArrayList<>());

        Document document = new Document();
        document.append("Key", objectList);
        assertEquals(1, ((Object[]) documentToObjectConverter.convert(document)).length);
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert12() throws ConversionException {
        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        Document document = new Document();
        document.append("Key", Decimal128.parse("+infinity"));
        documentToObjectConverter.convert(document);
    }

    /**
     * Method under test: {@link DocumentToObjectConverter#convert(Document)}
     */
    @Test
    public void testConvert13() throws ConversionException {

        DocumentToObjectConverter documentToObjectConverter = new DocumentToObjectConverter(true);

        Document document = new Document();
        document.append("Key", Decimal128.parse("-inf"));
        documentToObjectConverter.convert(document);
    }
}

