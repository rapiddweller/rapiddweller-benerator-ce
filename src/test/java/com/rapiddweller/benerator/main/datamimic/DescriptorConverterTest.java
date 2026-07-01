/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import com.rapiddweller.common.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the Benerator-&gt;DATAMIMIC converter emits native DATAMIMIC vocabulary and reports what it
 * cannot map, using the self-contained {@code simple/numbers.ben.xml} demo.
 */
public class DescriptorConverterTest {

  @Test
  public void convertsNumbersDemoToNativeDatamimic() throws Exception {
    File in = new File("src/demo/resources/demo/simple/numbers.ben.xml");
    File out = File.createTempFile("numbers", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);

    Document doc = XMLUtil.parse(out.getAbsolutePath());
    Element root = doc.getDocumentElement();
    assertEquals("setup", root.getTagName()); // XML namespace stripped

    Element gen = (Element) root.getElementsByTagName("generate").item(0);
    assertEquals("numbers", gen.getAttribute("name")); // Benerator type -> DATAMIMIC name
    assertEquals("", gen.getAttribute("target"));      // consumer -> empty target

    NodeList keys = doc.getElementsByTagName("key");   // <attribute> -> <key>
    assertTrue("expected the 8 attributes as keys", keys.getLength() >= 8);

    boolean intKey = false;
    boolean floatGenKey = false;
    for (int i = 0; i < keys.getLength(); i++) {
      Element k = (Element) keys.item(i);
      if ("int_max_10".equals(k.getAttribute("name"))) {
        intKey = "int".equals(k.getAttribute("type"));
      }
      if ("double_001".equals(k.getAttribute("name"))) {
        // Benerator double + min/max/granularity -> DATAMIMIC float + folded FloatGenerator.
        floatGenKey = "float".equals(k.getAttribute("type"))
            && k.getAttribute("generator").equals("FloatGenerator(min=0.0, max=10.0, granularity=0.01)");
      }
    }
    assertTrue("int type mapped", intKey);
    assertTrue("numeric range folded into FloatGenerator", floatGenKey);

    String rep = report.format();
    assertTrue("consumer flagged", rep.contains("consumer"));
    assertTrue("unmapped maxLength flagged", rep.contains("maxLength"));
  }
}
