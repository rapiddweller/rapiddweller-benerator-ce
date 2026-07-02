/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import com.rapiddweller.common.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        // maxLength now passes through as a native DATAMIMIC attribute (no longer flagged as unmapped).
        intKey = "int".equals(k.getAttribute("type")) && "10".equals(k.getAttribute("maxLength"));
      }
      if ("double_001".equals(k.getAttribute("name"))) {
        // Benerator double + min/max/granularity -> DATAMIMIC float with NATIVE min/max/granularity attrs.
        floatGenKey = "float".equals(k.getAttribute("type"))
            && k.getAttribute("min").equals("0.0")
            && k.getAttribute("max").equals("10.0")
            && k.getAttribute("granularity").equals("0.01")
            && k.getAttribute("generator").isEmpty();
      }
    }
    assertTrue("int type mapped", intKey);
    assertTrue("numeric range passes through as native min/max/granularity", floatGenKey);

    String rep = report.format();
    assertFalse("maxLength passes through natively, not flagged", rep.contains("maxLength"));
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

    // <database url="{dbUrl}"> resolves via <setting stage default="dev"> + shop.dev.properties:
    // jdbc:hsqldb:mem -> sqlite, no manual dbms flag left.
    Element db = first(doc, "database", "id", "db");
    assertNotNull("database element mapped", db);
    assertEquals("sqlite", db.getAttribute("dbms"));
    assertTrue("no manual dbms flag", report.attention().stream().noneMatch(it -> it.detail.contains("dbms")));
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

  @Test
  public void convertsAssertionsReferencesAndCrudConsumers() throws Exception {
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/test/resources/com/rapiddweller/benerator/main/datamimic/assertions.ben.xml", report);

    // (a) the assertion idiom <if test="X"><error>MSG</error></if> -> <assert condition="not (X)" message>
    Element ifAssert = first(doc, "assert", "condition", "not (db_order.counter != 10)");
    assertNotNull("if+error idiom converted to <assert>", ifAssert);
    assertEquals("{ftl: ${db_order.counter} items}", ifAssert.getAttribute("message")); // ftl kept verbatim
    assertFalse("error-only <if> no longer dropped", report.format().contains("setup-level <if>"));

    // (b) <evaluate assert target="db">SQL</evaluate> -> <variable source selector> + <assert>
    Element sqlVar = first(doc, "variable", "selector", "select count(*) from db_order");
    assertNotNull("evaluate SQL body -> <variable selector>", sqlVar);
    assertEquals("result", sqlVar.getAttribute("name"));
    assertEquals("db", sqlVar.getAttribute("source"));
    assertEquals("the <assert> follows its <variable>", "assert", nextElement(sqlVar).getTagName());
    assertEquals("result == 10", nextElement(sqlVar).getAttribute("condition"));

    // <evaluate assert>EXPR</evaluate> without target -> <variable script> + <assert>
    Element scriptVar = first(doc, "variable", "script", "mem.entityCount('db_order')");
    assertNotNull("evaluate script body -> <variable script>", scriptVar);
    assertEquals("result", scriptVar.getAttribute("name"));
    assertEquals("assert", nextElement(scriptVar).getTagName());
    assertFalse("<evaluate assert> is converted, not flagged", report.format().contains("<evaluate"));

    // (c) reference distribution/cyclic pass through as native DATAMIMIC attributes now
    Element ref = first(doc, "reference", "name", "category_id");
    assertNotNull(ref);
    assertEquals("cumulated", ref.getAttribute("distribution"));
    assertEquals("true", ref.getAttribute("cyclic"));
    assertTrue("distribution/cyclic no longer flagged", report.attention().stream()
        .noneMatch(it -> it.detail.contains("distribution") || it.detail.contains("cyclic")));

    // (d) CRUD consumers -> DATAMIMIC CRUD targets (inserter = plain store: insert is the default)
    assertEquals("db.update", first(doc, "generate", "name", "db_order").getAttribute("target"));
    assertEquals("mongo", first(doc, "iterate", "name", "products").getAttribute("target"));
    assertTrue("no consumer flag left", report.attention().stream()
        .noneMatch(it -> "consumer".equals(it.kind)));
  }

  @Test
  public void convertsCompositeGeneratorBraceArgsToEntityModifiers() throws Exception {
    // Real corpus case (csv demo): age bounds have dedicated entity attributes, the XML-level
    // dataset/locale attrs pass through - no manual-rewrite flag left.
    MigrationReport csvReport = new MigrationReport();
    Document csvDoc = convert("src/demo/resources/demo/projects/csv/csv.ben.xml", csvReport);
    Element person = first(csvDoc, "variable", "name", "person");
    assertNotNull("person variable kept", person);
    assertEquals("Person", person.getAttribute("entity"));
    assertEquals("16", person.getAttribute("ageMin"));
    assertEquals("122", person.getAttribute("ageMax"));
    assertEquals("DE", person.getAttribute("dataset"));
    assertEquals("de", person.getAttribute("locale"));
    assertEquals("no generator attribute left", "", person.getAttribute("generator"));
    String csvRep = csvReport.format();
    assertFalse("brace syntax no longer flagged", csvRep.contains("{k=v}"));
    assertFalse("no manual entity-modifier port left", csvRep.contains("port to entity modifiers"));

    // Constructor-only args (quotas) switch the whole call to constructor form; unknown args are
    // flagged individually while the rest still converts.
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/test/resources/com/rapiddweller/benerator/main/datamimic/composite_generator.ben.xml", report);
    Element quota = first(doc, "variable", "name", "quota_person");
    assertNotNull(quota);
    assertEquals("Person(min_age=18, max_age=80, dataset='US', female_quota=0.5)", quota.getAttribute("entity"));
    assertEquals("", quota.getAttribute("ageMin")); // constructor form carries everything

    Element odd = first(doc, "variable", "name", "odd_person");
    assertNotNull(odd);
    assertEquals("Person", odd.getAttribute("entity"));
    assertEquals("21", odd.getAttribute("ageMin")); // mappable arg converted despite the unknown one
    assertTrue("only the unknown arg is flagged",
        report.attention().stream().anyMatch(it -> it.detail.contains("mysteryArg")));
    assertTrue("no whole-call rewrite flag",
        report.attention().stream().noneMatch(it -> it.detail.contains("minAgeYears")));
  }

  private static Document convert(String input, MigrationReport report) throws Exception {
    File out = File.createTempFile("converted", ".datamimic.xml");
    out.deleteOnExit();
    new DescriptorConverter(report).convert(new File(input), out);
    return XMLUtil.parse(out.getAbsolutePath());
  }

  /** The next element sibling, skipping the whitespace text nodes of the re-parsed output. */
  private static Element nextElement(Element el) {
    org.w3c.dom.Node n = el.getNextSibling();
    while (n != null && n.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
      n = n.getNextSibling();
    }
    return (Element) n;
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
