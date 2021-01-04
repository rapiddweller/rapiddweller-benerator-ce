package com.rapiddweller.platform.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import com.rapiddweller.common.Capitalization;
import org.junit.Test;

public class ScriptedEntityExporterTest {
    @Test
    public void testConstructor() {
        ScriptedEntityExporter actualScriptedEntityExporter = new ScriptedEntityExporter();
        assertEquals(System. getProperty("file.encoding"), actualScriptedEntityExporter.getEncoding());
        assertNull(actualScriptedEntityExporter.getFooterScript());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss", actualScriptedEntityExporter.getDateTimePattern());
        assertEquals("\n", actualScriptedEntityExporter.getLineSeparator());
        assertEquals("", actualScriptedEntityExporter.getNullString());
        assertNull(actualScriptedEntityExporter.getHeaderScript());
        assertEquals("yyyy-MM-dd", actualScriptedEntityExporter.getDatePattern());
        assertFalse(actualScriptedEntityExporter.isAppend());
        assertEquals("HH:mm:ss", actualScriptedEntityExporter.getTimePattern());
        assertNull(actualScriptedEntityExporter.getPartScript());
        assertEquals(Capitalization.mixed, actualScriptedEntityExporter.getTimestampCapitalization());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.", actualScriptedEntityExporter.getTimestampPattern());
        assertEquals("ScriptedEntityExporter[export.txt]", actualScriptedEntityExporter.toString());
        assertEquals(Capitalization.mixed, actualScriptedEntityExporter.getDateCapitalization());
    }

    @Test
    public void testConstructor2() {
        ScriptedEntityExporter actualScriptedEntityExporter = new ScriptedEntityExporter("Uri", "Part Script");
        assertNull(actualScriptedEntityExporter.getFooterScript());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss", actualScriptedEntityExporter.getDateTimePattern());
        assertEquals("\n", actualScriptedEntityExporter.getLineSeparator());
        assertEquals("", actualScriptedEntityExporter.getNullString());
        assertNull(actualScriptedEntityExporter.getHeaderScript());
        assertEquals("yyyy-MM-dd", actualScriptedEntityExporter.getDatePattern());
        assertFalse(actualScriptedEntityExporter.isAppend());
        assertEquals("HH:mm:ss", actualScriptedEntityExporter.getTimePattern());
        assertEquals("Part Script", actualScriptedEntityExporter.getPartScript());
        assertEquals(Capitalization.mixed, actualScriptedEntityExporter.getTimestampCapitalization());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.", actualScriptedEntityExporter.getTimestampPattern());
        assertEquals("ScriptedEntityExporter[Uri]", actualScriptedEntityExporter.toString());
        assertEquals(Capitalization.mixed, actualScriptedEntityExporter.getDateCapitalization());
    }

    @Test
    public void testConstructor3() {
        ScriptedEntityExporter actualScriptedEntityExporter = new ScriptedEntityExporter("Uri", "UTF-8", "Header Script",
                "Part Script", "Footer Script");
        assertEquals("UTF-8", actualScriptedEntityExporter.getEncoding());
        assertEquals("Footer Script", actualScriptedEntityExporter.getFooterScript());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss", actualScriptedEntityExporter.getDateTimePattern());
        assertEquals("\n", actualScriptedEntityExporter.getLineSeparator());
        assertEquals("", actualScriptedEntityExporter.getNullString());
        assertEquals("Header Script", actualScriptedEntityExporter.getHeaderScript());
        assertEquals("yyyy-MM-dd", actualScriptedEntityExporter.getDatePattern());
        assertFalse(actualScriptedEntityExporter.isAppend());
        assertEquals("HH:mm:ss", actualScriptedEntityExporter.getTimePattern());
        assertEquals("Part Script", actualScriptedEntityExporter.getPartScript());
        assertEquals(Capitalization.mixed, actualScriptedEntityExporter.getTimestampCapitalization());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.", actualScriptedEntityExporter.getTimestampPattern());
        assertEquals("ScriptedEntityExporter[Uri]", actualScriptedEntityExporter.toString());
        assertEquals(Capitalization.mixed, actualScriptedEntityExporter.getDateCapitalization());
    }

    @Test
    public void testSetHeaderScript() {
        ScriptedEntityExporter scriptedEntityExporter = new ScriptedEntityExporter();
        scriptedEntityExporter.setHeaderScript("Header Script");
        assertEquals("Header Script", scriptedEntityExporter.getHeaderScript());
    }

    @Test
    public void testSetPartScript() {
        ScriptedEntityExporter scriptedEntityExporter = new ScriptedEntityExporter();
        scriptedEntityExporter.setPartScript("Part Script");
        assertEquals("Part Script", scriptedEntityExporter.getPartScript());
    }

    @Test
    public void testSetFooterScript() {
        ScriptedEntityExporter scriptedEntityExporter = new ScriptedEntityExporter();
        scriptedEntityExporter.setFooterScript("Footer Script");
        assertEquals("Footer Script", scriptedEntityExporter.getFooterScript());
    }
}

