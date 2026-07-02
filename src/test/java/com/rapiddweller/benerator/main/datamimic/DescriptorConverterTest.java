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

    // untyped numeric range: <attribute min="1" max="27" distribution="cumulated"> defaults to int in
    // Benerator -> type="int" + IntegerGenerator carrying the distribution; no 'distribution' flag left.
    Element items = first(doc, "key", "name", "number_of_items");
    assertNotNull("number_of_items mapped as <key>", items);
    assertEquals("int", items.getAttribute("type"));
    assertEquals("IntegerGenerator(min=1, max=27, distribution='cumulated')", items.getAttribute("generator"));
    assertTrue("no 'distribution needs a numeric generator' flag left", report.attention().stream()
        .noneMatch(it -> it.detail.contains("'distribution'")));

    // selector reference with a single-column select-list -> <variable source/selector> + <key script>.
    Element refVar = first(doc, "variable", "name", "_ref_order_id");
    assertNotNull("selector reference emitted as <variable>", refVar);
    assertEquals("db", refVar.getAttribute("source"));
    assertTrue(refVar.getAttribute("selector").contains("db_order"));
    assertEquals("true", refVar.getAttribute("cyclic"));
    Element refKey = first(doc, "key", "name", "order_id");
    assertNotNull("selector reference column picked via <key script>", refKey);
    assertEquals("_ref_order_id.id", refKey.getAttribute("script"));
    assertTrue("selector reference no longer flagged", report.attention().stream()
        .noneMatch(it -> it.detail.contains("order_id")));

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

  @Test
  public void dropsMongoIdMapsCountryGeneratorAndNoConsumer() throws Exception {
    // (a) mongodb-inserter: <generate consumer="mongo"> with <id generator="MongoDBObjectIdGenerator">
    // -> field dropped (Mongo assigns _id on insert); scalar CountryGenerator -> Country entity fragment.
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/demo/resources/demo/db/mongodb-inserter.ben.xml", report);
    assertEquals("_id dropped (Mongo assigns it on insert)", null, first(doc, "id", "name", "_id"));
    assertEquals("_id not emitted as key either", null, first(doc, "key", "name", "_id"));
    Element countryVar = first(doc, "variable", "name", "_country_country");
    assertNotNull("CountryGenerator -> <variable entity='Country'>", countryVar);
    assertEquals("Country", countryVar.getAttribute("entity"));
    Element countryKey = first(doc, "key", "name", "country");
    assertNotNull(countryKey);
    assertEquals("_country_country.iso_code", countryKey.getAttribute("script"));
    assertTrue("neither generator flagged for manual work", report.attention().stream()
        .noneMatch(it -> it.detail.contains("MongoDBObjectIdGenerator") || it.detail.contains("CountryGenerator")));

    // (b) NoConsumer maps to the empty target deliberately - informational, not a manual-work flag.
    MigrationReport mongoReport = new MigrationReport();
    Document mongoDoc = convert("src/demo/resources/demo/db/mongodb-ObjectId.ben.xml", mongoReport);
    Element iterate = first(mongoDoc, "iterate", "name", "testben");
    assertNotNull(iterate);
    assertEquals("NoConsumer -> empty target", "", iterate.getAttribute("target"));
    assertTrue("NoConsumer no longer flagged", mongoReport.attention().stream()
        .noneMatch(it -> "consumer".equals(it.kind)));

    // (c) reference type= is informational (the column type comes from the source in DATAMIMIC).
    MigrationReport keyReport = new MigrationReport();
    Document keyDoc = convert("src/demo/resources/demo/db/compositekey.ben.xml", keyReport);
    Element playlistRef = first(keyDoc, "reference", "name", "PLAYLIST_ID");
    assertNotNull(playlistRef);
    assertEquals("playlist", playlistRef.getAttribute("sourceType"));
    assertTrue("reference 'type' demoted to info", keyReport.attention().stream()
        .noneMatch(it -> it.detail.contains("'type'")));

    // (d) <database environment= system=>: DATAMIMIC resolves the connection at runtime - no dbms flag.
    MigrationReport envReport = new MigrationReport();
    Document envDoc = convert("src/demo/resources/demo/db/dbenv-new.ben.xml", envReport);
    Element db = first(envDoc, "database", "id", "database");
    assertNotNull(db);
    assertEquals("database_new", db.getAttribute("environment"));
    assertEquals("system passes through (selects the env properties prefix)", "target", db.getAttribute("system"));
    assertTrue("no manual dbms flag for environment-resolved connections", envReport.attention().stream()
        .noneMatch(it -> it.detail.contains("set dbms manually")));
  }

  @Test
  public void mapsDataFakerDropsSqliteSchemaAndResolvesExecuteUri() throws Exception {
    // (a) DataFakerGenerator: Benerator (provider, camelCaseMethod) -> DATAMIMIC (snake_case method).
    MigrationReport fakerReport = new MigrationReport();
    Document fakerDoc = convert("src/demo/resources/demo/faker/datafaker_0to100.ben.xml", fakerReport);
    Element state = first(fakerDoc, "key", "name", "address_state");
    assertNotNull(state);
    assertEquals("DataFakerGenerator('state')", state.getAttribute("generator")); // provider dropped

    // (b) shop-h2: hsqldb-mem -> sqlite, so schema="PUBLIC" is dropped (SQLite has no schemas) and the
    // {ftl:${database}/...} execute uri resolves to a concrete path (extension -> type inferrable).
    MigrationReport shopReport = new MigrationReport();
    Document shopDoc = convert("src/demo/resources/demo/shop/shop-h2.ben.xml", shopReport);
    Element db = first(shopDoc, "database", "id", "db");
    assertNotNull(db);
    assertEquals("sqlite", db.getAttribute("dbms"));
    assertEquals("no schema on sqlite", "", db.getAttribute("schema"));
    NodeList execs = shopDoc.getElementsByTagName("execute");
    boolean anyResolved = false;
    for (int i = 0; i < execs.getLength(); i++) {
      String uri = ((Element) execs.item(i)).getAttribute("uri");
      if (uri.endsWith(".sql") && !uri.contains("{")) {
        anyResolved = true;
      }
    }
    assertTrue("execute uri placeholder resolved to a .sql path", anyResolved);
  }

  @Test
  public void bareDateTypeBecomesDateTimeGenerator() throws Exception {
    // <attribute name="birth_date" type="date"/> has no generation mode; Benerator's built-in date
    // generator maps to DATAMIMIC's DateTimeGenerator (else an invalid mode-less <key> is emitted).
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/demo/resources/demo/file/create_xml.ben.xml", report);
    Element birth = first(doc, "key", "name", "birth_date");
    assertNotNull(birth);
    assertEquals("DateTimeGenerator()", birth.getAttribute("generator"));
    assertEquals("no leftover date type", "", birth.getAttribute("type"));
    assertTrue("not flagged as a missing generation mode",
        report.attention().stream().noneMatch(it -> it.detail.contains("birth_date")));
  }

  @Test
  public void rewritesJavaScriptIdiomsToPython() {
    // Java ternary -> Python ternary
    assertEquals("(1) if (TX.CARD == 'Y') else (0)", DescriptorConverter.rewriteScript("TX.CARD == 'Y' ? 1 : 0"));
    // this.field -> bare field (DATAMIMIC exposes siblings by name)
    assertEquals("this.age + 1", DescriptorConverter.rewriteScript("this.age + 1")); // this.* left for DM native binding
    // Java enum accessor dropped (gender is already a string in DATAMIMIC)
    assertEquals("person.gender", DescriptorConverter.rewriteScript("person.gender.name()"));
    // a lone ':' in a slice/dict is NOT a ternary
    assertEquals("d['a:b']", DescriptorConverter.rewriteScript("d['a:b']"));
    // no ternary -> untouched
    assertEquals("a + b", DescriptorConverter.rewriteScript("a + b"));
    // nested ternary
    assertEquals("(1) if (x) else ((2) if (y) else (3))",
        DescriptorConverter.rewriteScript("x ? 1 : y ? 2 : 3"));
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
