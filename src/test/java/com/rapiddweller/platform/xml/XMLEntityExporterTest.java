package com.rapiddweller.platform.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XMLEntityExporterTest {
    @Test
    public void testConstructor() {
        assertEquals("export.xml", (new XMLEntityExporter()).getUri());
        assertEquals("Uri", (new XMLEntityExporter("Uri")).getUri());
        assertEquals("Uri", (new XMLEntityExporter("Uri", "UTF-8")).getUri());
    }

    @Test
    public void testGetUri() {
        assertEquals("export.xml", (new XMLEntityExporter()).getUri());
    }

    @Test
    public void testSetUri() {
        XMLEntityExporter xmlEntityExporter = new XMLEntityExporter();
        xmlEntityExporter.setUri("Uri");
        assertEquals("Uri", xmlEntityExporter.getUri());
    }

    @Test
    public void testToString() {
        assertEquals("XMLEntityExporter[export.xml]", (new XMLEntityExporter()).toString());
    }
}

