/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import com.rapiddweller.common.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    assertEquals("LogExporter", gen.getAttribute("target")); // consumer LoggingConsumer -> DATAMIMIC LogExporter

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
    assertTrue("unmapped maxLength flagged", rep.contains("maxLength"));
  }

  @Test
  public void convertsShopReferencesAndSources() throws Exception {
    File in = new File("src/demo/resources/demo/shop/shop-hsqlmem.ben.xml");
    File out = File.createTempFile("shop", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    // <iterate source=.. consumer="db"> stays <iterate source=.. target="db"> (clarity kept)
    boolean iterateKept = false;
    NodeList iters = doc.getElementsByTagName("iterate");
    for (int i = 0; i < iters.getLength(); i++) {
      Element it = (Element) iters.item(i);
      if ("shop.dbunit.xml".equals(it.getAttribute("source")) && "db".equals(it.getAttribute("target"))) {
        iterateKept = true;
      }
    }
    assertTrue("iterate stays <iterate> with source + target=db", iterateKept);

    // FK reference: Benerator targetType -> DATAMIMIC sourceType, with a defaulted sourceKey.
    Element catRef = first(doc, "reference", "name", "category_id");
    assertNotNull("category_id kept as <reference>", catRef);
    assertEquals("db_category", catRef.getAttribute("sourceType"));
    assertEquals("id", catRef.getAttribute("sourceKey"));

    // constant reference is a fixed value -> emitted as <key>
    Element roleKey = first(doc, "key", "name", "role_id");
    assertNotNull("role_id constant reference -> <key>", roleKey);
    assertEquals("customer", roleKey.getAttribute("constant"));

    // <database> mapped structurally; the connection is flagged for manual setup.
    assertNotNull("database element mapped", first(doc, "database", "id", "db"));
    assertTrue("dbms/connection flagged", report.format().contains("dbms"));
  }

  @Test
  public void convertsConditionsExecuteDatabaseInclude() throws Exception {
    File in = new File("src/test/resources/com/rapiddweller/benerator/main/datamimic/phase_c.ben.xml");
    File out = File.createTempFile("phasec", ".xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    // <database url=.. driver="org.postgresql.Driver"> -> dbms derived
    Element db = first(doc, "database", "id", "db");
    assertNotNull("database mapped", db);
    assertEquals("postgresql", db.getAttribute("dbms"));

    // <include uri> kept natively
    assertEquals(1, doc.getElementsByTagName("include").getLength());

    // <execute uri=.. type="sql"> -> <execute uri target> (type dropped, DATAMIMIC infers it)
    Element ex = (Element) doc.getElementsByTagName("execute").item(0);
    assertEquals("setup.sql", ex.getAttribute("uri"));
    assertEquals("db", ex.getAttribute("target"));
    assertEquals("", ex.getAttribute("type"));

    // A setup-level <if> has no valid DATAMIMIC target (<condition> is only valid inside <generate>), so it
    // is dropped (as a TODO comment) and flagged, rather than emitting a <condition> that would not parse.
    assertEquals("no setup-level <condition> emitted", 0, doc.getElementsByTagName("condition").getLength());
    assertTrue("setup-level <if> flagged", report.format().contains("setup-level <if>"));
  }

  private static Element first(Document doc, String tag, String attr, String value) {
    NodeList nodes = doc.getElementsByTagName(tag);
    for (int i = 0; i < nodes.getLength(); i++) {
      Element e = (Element) nodes.item(i);
      if (value.equals(e.getAttribute(attr))) {
        return e;
      }
    }
    return null;
  }
}
