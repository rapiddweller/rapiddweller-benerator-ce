package com.rapiddweller.platform.xml;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The type Xml entity exporter test.
 */
public class XMLEntityExporterTest {
  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    assertEquals("export.xml", (new XMLEntityExporter()).getUri());
    assertEquals("Uri", (new XMLEntityExporter("Uri")).getUri());
    assertEquals("Uri", (new XMLEntityExporter("Uri", "UTF-8")).getUri());
  }

  /**
   * Test get uri.
   */
  @Test
  public void testGetUri() {
    assertEquals("export.xml", (new XMLEntityExporter()).getUri());
  }

  /**
   * Test set uri.
   */
  @Test
  public void testSetUri() {
    XMLEntityExporter xmlEntityExporter = new XMLEntityExporter();
    xmlEntityExporter.setUri("Uri");
    assertEquals("Uri", xmlEntityExporter.getUri());
  }

  /**
   * Test to string.
   */
  @Test
  public void testToString() {
    assertEquals("XMLEntityExporter[export.xml]", (new XMLEntityExporter()).toString());
  }
}

