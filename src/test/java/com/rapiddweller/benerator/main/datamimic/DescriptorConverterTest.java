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

  /**
   * The two ways a memstore cross-entity reference converts cleanly, runs without error, and is still
   * WRONG - both silent, so neither shows up as a crash:
   * <ul>
   *   <li>a dropped {@code type} on a store-reading {@code <variable>} makes DATAMIMIC resolve the lookup
   *       against the variable name and read nothing ("Data having entity 'cust' is empty in memstore"),</li>
   *   <li>a bare {@code <id type="int">} is incremental in Benerator but a repeating random int in DATAMIMIC.</li>
   * </ul>
   */
  @Test
  public void carriesMemstoreEntityBindingAndIncrementalIds() throws Exception {
    File in = new File("src/test/resources/com/rapiddweller/benerator/main/datamimic/"
        + "roundtrip_corpus/memstore.ben.xml");
    File out = File.createTempFile("memstore", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    Element variable = (Element) doc.getElementsByTagName("variable").item(0);
    assertEquals("the memstore entity to read must survive as sourceEntity",
        "customer", variable.getAttribute("sourceEntity"));
    assertEquals("type is not a DATAMIMIC <variable> attribute", "", variable.getAttribute("type"));
    assertEquals("cyclic converts verbatim", "true", variable.getAttribute("cyclic"));

    NodeList ids = doc.getElementsByTagName("id");
    assertEquals(2, ids.getLength());
    for (int i = 0; i < ids.getLength(); i++) {
      Element id = (Element) ids.item(i);
      assertEquals("a mode-less int <id> is incremental in Benerator",
          "IncrementGenerator()", id.getAttribute("generator"));
      assertEquals("a type= alongside the generator would re-randomize the id",
          "", id.getAttribute("type"));
    }
  }

  /**
   * Benerator's weighted-value literal ({@code values="'A'^70,'B'^30"}, doc: randomFromWeightLiteral)
   * embeds the weight IN the values string. DATAMIMIC has no '^' syntax - {@code ast.literal_eval} on a
   * caret expression is not a literal and raises at task-init, so a verbatim pass-through hard-crashes
   * every run (verified against the real engine). DATAMIMIC's equivalent is a separate {@code weights=}
   * attribute (plain numbers, same order).
   */
  @Test
  public void splitsWeightedValueLiteralIntoValuesAndWeights() throws Exception {
    File in = new File("src/test/resources/com/rapiddweller/benerator/main/datamimic/"
        + "roundtrip_corpus/weighted_values.ben.xml");
    File out = File.createTempFile("weighted", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    Element key = (Element) doc.getElementsByTagName("key").item(0);
    assertEquals("'RETAIL','SME','CORP'", key.getAttribute("values"));
    assertEquals("the caret weight rides along as a separate native attribute",
        "40,35,25", key.getAttribute("weights"));
    assertFalse("no leftover '^' anywhere in the emitted values", key.getAttribute("values").contains("^"));
  }

  /**
   * {@code unique="true"} on a {@code <key source=".wgt.csv">} converts cleanly and then hard-crashes:
   * DATAMIMIC's weighted-CSV key read is sample-WITH-replacement and explicitly rejects unique at
   * task-init (verified against the real engine). A safe auto-rewrite needs the CSV header (which
   * column is the value) that this converter never reads, so it is flagged - not guessed, not passed
   * through to the crash.
   */
  @Test
  public void flagsUniqueOnWeightedSourceInsteadOfCrashing() throws Exception {
    File in = new File("src/test/resources/com/rapiddweller/benerator/main/datamimic/"
        + "roundtrip_corpus/unique_weighted_source.ben.xml");
    File out = File.createTempFile("uniquesrc", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    NodeList keys = doc.getElementsByTagName("key");
    for (int i = 0; i < keys.getLength(); i++) {
      assertFalse("the crashing field must not reach the output as a <key>",
          "country".equals(((Element) keys.item(i)).getAttribute("name")));
    }
    String rep = report.format();
    assertTrue("flagged as manual work with a concrete rewrite recipe",
        rep.contains("samples WITH replacement and rejects unique"));
  }

  /**
   * DATAMIMIC's DateTimeGenerator parses {@code min}/{@code max} with a FIXED
   * {@code "%Y-%m-%d %H:%M:%S"} format. A bare ISO date with no time component - Benerator's OWN
   * default (TimeUtil.createDefaultDateFormat) and the single most common way real projects write date
   * bounds - already crashed there before this fix (verified against the real engine). A Benerator
   * {@code pattern} makes it worse: DATAMIMIC's {@code pattern} attribute is an UNRELATED thing (regex
   * string generation via exrex), so passing it through hijacked the field into garbage output instead
   * of a date. Both bounds must be reparsed at CONVERT TIME with Benerator's own format (its default, or
   * an explicit {@code pattern}) and re-emitted in DATAMIMIC's format; {@code pattern} on a genuinely
   * string-typed field (regex generation) must still pass through untouched.
   */
  @Test
  public void reformatsDateBoundsAndConsumesDateFormatPattern() throws Exception {
    File in = new File("src/test/resources/com/rapiddweller/benerator/main/datamimic/"
        + "roundtrip_corpus/date_bounds.ben.xml");
    File out = File.createTempFile("datebounds", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    NodeList keys = doc.getElementsByTagName("key");
    Element isoKey = null;
    Element euroKey = null;
    Element strKey = null;
    for (int i = 0; i < keys.getLength(); i++) {
      Element k = (Element) keys.item(i);
      switch (k.getAttribute("name")) {
        case "isoBirthdate": isoKey = k; break;
        case "euroBirthdate": euroKey = k; break;
        case "orderCode": strKey = k; break;
        default: break;
      }
    }
    assertNotNull(isoKey);
    assertNotNull(euroKey);
    assertNotNull(strKey);

    assertEquals("a bare ISO date (no time) must gain the time component DATAMIMIC's parser requires",
        "DateTimeGenerator(min='1970-01-01 00:00:00', max='2000-12-31 00:00:00')", isoKey.getAttribute("generator"));
    assertEquals("a 'pattern'-formatted bound reparses to the same DATAMIMIC-native format",
        "DateTimeGenerator(min='1970-01-01 00:00:00', max='2000-12-31 00:00:00')", euroKey.getAttribute("generator"));
    assertFalse("'pattern' is consumed by date-bound parsing, not passed through", euroKey.hasAttribute("pattern"));
    assertFalse("'min'/'max' are folded into the generator string, not left as bare attributes",
        euroKey.hasAttribute("min") || euroKey.hasAttribute("max"));

    assertEquals("a genuinely string-typed field keeps 'pattern' as DATAMIMIC's native regex mode",
        "[A-Z]{3}[0-9]{4}", strKey.getAttribute("pattern"));
    assertEquals("string", strKey.getAttribute("type"));
  }

  /**
   * DATAMIMIC validates {@code unique="true"} at the model level: it draws distinct values from a FINITE
   * POOL, which only {@code values=} or {@code source=} provide. A regex {@code pattern}, a native
   * min/max range, and an explicit {@code generator=} are none of those - all three convert cleanly and
   * then hard-crash Pydantic validation ("'unique' requires 'values' or 'source'"), verified against the
   * real engine. Two fields in Benerator's own EDI test fixture (IFTDGN2.ben.xml) hit exactly this. A
   * safe universal rewrite doesn't exist (a numeric range can be huge; a regex's value set isn't
   * enumerable in general), so unique is dropped and flagged - same policy as the wgt.csv+unique case -
   * rather than passed through to the crash. A values-backed field is untouched (DATAMIMIC natively
   * supports unique sampling there) UNLESS it also names an explicit non-random distribution
   * ({@code ordered}/{@code cumulated}/...), which hits a SECOND, distinct Pydantic check ("'unique' only
   * combines with distribution='random'") - also verified against the real engine, also dropped+flagged
   * (the explicit distribution is kept; it is the more clearly deliberate of the two attributes).
   */
  @Test
  public void dropsUniqueOnNonPoolModesInsteadOfCrashing() throws Exception {
    File in = new File("src/test/resources/com/rapiddweller/benerator/main/datamimic/"
        + "roundtrip_corpus/unique_non_pool_modes.ben.xml");
    File out = File.createTempFile("uniquenonpool", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    NodeList keys = doc.getElementsByTagName("key");
    Element patternKey = null;
    Element rangeKey = null;
    Element valuesKey = null;
    for (int i = 0; i < keys.getLength(); i++) {
      Element k = (Element) keys.item(i);
      switch (k.getAttribute("name")) {
        case "orderCode": patternKey = k; break;
        case "sequenceNo": rangeKey = k; break;
        case "segment": valuesKey = k; break;
        default: break;
      }
    }
    assertNotNull(patternKey);
    assertNotNull(rangeKey);
    assertNotNull(valuesKey);

    assertFalse("pattern (regex) has no finite pool - unique dropped", patternKey.hasAttribute("unique"));
    assertEquals("the field itself still generates", "[A-Z]{3}[0-9]{4}", patternKey.getAttribute("pattern"));
    assertFalse("a native min/max range has no finite pool - unique dropped", rangeKey.hasAttribute("unique"));
    assertEquals("1", rangeKey.getAttribute("min"));
    assertEquals("values=... IS DATAMIMIC's finite pool - left alone", "true", valuesKey.getAttribute("unique"));

    Element orderedVar = (Element) doc.getElementsByTagName("variable").item(0);
    assertEquals("orderedPick", orderedVar.getAttribute("name"));
    assertFalse("unique + an explicit non-random distribution also crashes - unique dropped",
        orderedVar.hasAttribute("unique"));
    assertEquals("the explicit distribution choice is kept", "ordered", orderedVar.getAttribute("distribution"));
    assertEquals("values=... is untouched", "'X','Y','Z'", orderedVar.getAttribute("values"));

    String rep = report.format();
    assertTrue("flagged as manual work with a concrete explanation",
        rep.contains("DATAMIMIC only supports unique sampling from a finite pool"));
    assertTrue("the distribution-conflict case gets its own concrete explanation",
        rep.contains("DATAMIMIC's unique only combines with distribution='random'"));
  }

  /**
   * Benerator's {@code <id>} is GLOBALLY incremental across the whole run, including every invocation of
   * an enclosing {@code <part>} (confirmed against the real Benerator engine: 3 accounts x 2 cards each
   * gives cardIds 1,2 / 3,4 / 5,6). DATAMIMIC's {@code IncrementGenerator} resets to 1 for every PARENT
   * record inside a {@code nestedKey} (confirmed against the real engine: same shape gives 1,2 / 1,2 /
   * 1,2 - documented, intentional DATAMIMIC behavior, not a bug there). Not a crash and often exactly
   * what a child list wants, so it must not become blocking manual work - but it is a real value-level
   * divergence, so it must be visible.
   */
  @Test
  public void flagsNestedIncrementGeneratorAsInfoNotManualWork() throws Exception {
    File in = new File("src/test/resources/com/rapiddweller/benerator/main/datamimic/"
        + "roundtrip_corpus/nested_id.ben.xml");
    File out = File.createTempFile("nestedid", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    NodeList ids = doc.getElementsByTagName("id");
    assertEquals(2, ids.getLength());
    for (int i = 0; i < ids.getLength(); i++) {
      assertEquals("still generates - the field is not dropped or changed",
          "IncrementGenerator()", ((Element) ids.item(i)).getAttribute("generator"));
    }

    long nestedIdFindings = report.items().stream().filter(it -> "nested-id".equals(it.kind)).count();
    assertEquals("exactly the nested cardId, not the top-level accountId", 1, nestedIdFindings);
    assertTrue("must not block the 'no manual work' claim - it's informational", report.attention().stream()
        .noneMatch(it -> "nested-id".equals(it.kind)));
  }

  @Test
  public void convertsShopReferencesAndSources() throws Exception {
    File in = new File("src/demo/resources/demo/shop/shop-hsqlmem.ben.xml");
    File out = File.createTempFile("shop", ".datamimic.xml");
    out.deleteOnExit();

    MigrationReport report = new MigrationReport();
    new DescriptorConverter(report).convert(in, out);
    Document doc = XMLUtil.parse(out.getAbsolutePath());

    // The multi-table dbunit dataset expands into one <iterate type=table source=table.json target=db>
    // per table; no single shop.dbunit.xml iterate remains.
    boolean dbunitLeft = false;
    Element categorySeed = null;
    NodeList iters = doc.getElementsByTagName("iterate");
    for (int i = 0; i < iters.getLength(); i++) {
      Element it = (Element) iters.item(i);
      if (it.getAttribute("source").endsWith(".dbunit.xml")) {
        dbunitLeft = true;
      }
      if ("db_category".equals(it.getAttribute("type")) && "db".equals(it.getAttribute("target"))) {
        categorySeed = it;
      }
    }
    assertFalse("dbunit dataset is expanded, not left as a single iterate", dbunitLeft);
    assertNotNull("dbunit seed table -> <iterate type='db_category' source=*.json>", categorySeed);
    assertEquals("shop.db_category.json", categorySeed.getAttribute("source"));

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
    assertEquals("IntegerGenerator(min=1, max=27, distribution=NumberDistribution.CUMULATED)", items.getAttribute("generator"));
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
    Element ifAssert = first(doc, "assert", "condition", "not (expected_total != 10)");
    assertNotNull("if+error idiom converted to <assert>", ifAssert);
    assertEquals("{ftl: ${expected_total} items}", ifAssert.getAttribute("message")); // ftl kept verbatim
    assertFalse("error-only <if> no longer dropped", report.format().contains("setup-level <if>"));
    // a Benerator runtime-counter check has no DATAMIMIC scope: flagged, no runtime-dead assert emitted
    assertEquals("counter check not emitted as an assert", null,
        first(doc, "assert", "condition", "not (db_order.counter != 10)"));
    assertTrue("counter check flagged for manual verification", report.attention().stream()
        .anyMatch(it -> it.detail.contains("runtime counter")));

    // (b) <evaluate assert target="db">SQL</evaluate> -> <variable source selector> + <assert>
    Element sqlVar = first(doc, "variable", "selector", "select count(*) from db_order");
    assertNotNull("evaluate SQL body -> <variable selector>", sqlVar);
    assertEquals("result", sqlVar.getAttribute("name"));
    assertEquals("db", sqlVar.getAttribute("source"));
    assertEquals("the <assert> follows its <variable>", "assert", nextElement(sqlVar).getTagName());
    // a SQL <evaluate> yields a single result ROW (DotableDict); the condition unwraps its scalar cell
    assertEquals("(list(result.to_dict().values())[0] if result else None) == 10",
        nextElement(sqlVar).getAttribute("condition"));

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
    // <iterate type="products" source="mongo" consumer="mongo.inserter('insertedtable')"/> reads the
    // 'products' collection and writes to 'insertedtable' (name from the inserter arg) - NOT back into
    // the source. The read collection must live in sourceEntity: DATAMIMIC's write-side resolution is
    // targetEntity -> type -> name, so type="products" would override the renamed write target.
    Element mongoIterate = first(doc, "iterate", "name", "insertedtable");
    assertEquals("mongo", mongoIterate.getAttribute("target"));
    assertEquals("products", mongoIterate.getAttribute("sourceEntity"));
    assertFalse("type would override the renamed write target", mongoIterate.hasAttribute("type"));
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
    Element zip = first(fakerDoc, "key", "name", "address_zipCode");
    assertNotNull(zip);
    assertEquals("renamed via FAKER_METHOD_RENAME (python has zipcode, not zip_code)",
        "DataFakerGenerator('zipcode')", zip.getAttribute("generator"));
    Element latLon = first(fakerDoc, "key", "name", "address_latLon");
    assertNotNull(latLon);
    assertEquals("no python equivalent -> word fallback (FAKER_UNAVAILABLE_METHODS)",
        "DataFakerGenerator('word')", latLon.getAttribute("generator"));

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
    assertEquals("(1) if (TX.CARD == 'Y') else (0)", ExpressionMapper.rewriteScript("TX.CARD == 'Y' ? 1 : 0"));
    // this.field -> bare field (DATAMIMIC exposes siblings by name)
    assertEquals("this.age + 1", ExpressionMapper.rewriteScript("this.age + 1"));
    // <generate type="abc"> self-reference abc.j -> this.j
    assertEquals("this.j + 1", ExpressionMapper.rewriteScript("abc.j + 1", "abc"));
    assertEquals("(1) if (this.card == 0) else (0)", ExpressionMapper.rewriteScript("TX.card == 0 ? 1 : 0", "TX"));
    // Java enum accessor dropped (gender is already a string in DATAMIMIC)
    assertEquals("person.gender", ExpressionMapper.rewriteScript("person.gender.name()"));
    // a lone ':' in a slice/dict is NOT a ternary
    assertEquals("d['a:b']", ExpressionMapper.rewriteScript("d['a:b']"));
    // no ternary -> untouched
    assertEquals("a + b", ExpressionMapper.rewriteScript("a + b"));
    // nested ternary
    assertEquals("(1) if (x) else ((2) if (y) else (3))",
        ExpressionMapper.rewriteScript("x ? 1 : y ? 2 : 3"));
  }

  @Test
  public void modelessAttributeOverSourceBecomesScriptOverlay() throws Exception {
    // anon demo: <iterate source="persons.csv"> with <attribute name="familyName" converter="new CutLength(3)"/>
    // overlays the source column -> <key name="familyName" script="familyName" converter="CutLength(3)"/>.
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/demo/resources/demo/anon/anon.ben.xml", report);
    Element family = first(doc, "key", "name", "familyName");
    assertNotNull(family);
    assertEquals("familyName", family.getAttribute("script")); // reads the source column
    assertEquals("CutLength(3)", family.getAttribute("converter")); // converter still applied
    assertTrue("mode-less source attribute not flagged as a gap",
        report.attention().stream().noneMatch(it -> it.detail.contains("familyName")));
  }

  @Test
  public void resolvesExporterBeanToTargetIncludingXls() throws Exception {
    // <bean id="xml" class="...XMLEntityExporter"> + consumer="xml" -> target="XML", bean removed.
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/demo/resources/demo/file/create_xml.ben.xml", report);
    Element gen = first(doc, "generate", "name", "customers");
    assertNotNull(gen);
    assertEquals("XML", gen.getAttribute("target"));
    assertEquals("exporter bean removed", 0, doc.getElementsByTagName("bean").getLength());
    assertTrue("exporter bean not flagged as unsupported",
        report.attention().stream().noneMatch(it -> it.detail.contains("<bean>")));

    // Benerator XLSEntityExporter -> DATAMIMIC's new XLSX target.
    MigrationReport xlsReport = new MigrationReport();
    Document xlsDoc = convert("src/demo/resources/demo/file/create_xls.ben.xml", xlsReport);
    boolean anyXlsx = false;
    NodeList gens = xlsDoc.getElementsByTagName("generate");
    for (int i = 0; i < gens.getLength(); i++) {
      if ("XLSX".equals(((Element) gens.item(i)).getAttribute("target"))) {
        anyXlsx = true;
      }
    }
    assertTrue("XLSEntityExporter -> target=XLSX", anyXlsx);
  }

  @Test
  public void convertsDynamicSelectorAndPositionalRowAccess() throws Exception {
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/demo/resources/demo/shop/shop-postgres.ben.xml", report);

    // (a) dynamic selector {{ftl:select ... ${db_order.id}}} -> <variable iterationSelector> + <key script>
    Element sel = first(doc, "variable", "name", "_total_price_sel");
    assertNotNull("dynamic selector emitted as iterationSelector variable", sel);
    String iterSel = sel.getAttribute("iterationSelector");
    assertTrue("scope self-reference interpolated: " + iterSel, iterSel.contains("order_id = __this.id__"));
    assertFalse("no FTL wrapper left: " + iterSel, iterSel.contains("ftl:") || iterSel.contains("${"));
    Element unwrap = nextElement(sel);
    assertEquals("key", unwrap.getTagName());
    assertEquals("total_price", unwrap.getAttribute("name"));
    assertTrue("unwraps the single-value row", unwrap.getAttribute("script").contains("_total_price_sel"));

    // (b) positional access on a multi-column selector variable -> column access on the row dict
    Element ean = first(doc, "key", "name", "product_ean_code");
    assertNotNull(ean);
    assertEquals("product.ean_code", ean.getAttribute("script"));
    Element scripted = first(doc, "key", "script", "product.price * this.number_of_items");
    assertNotNull("product[1] rewritten to product.price", scripted);

    // (c) computed count over string settings gets int()-casts; a single-identifier count stays plain
    Element orders = first(doc, "generate", "name", "db_order");
    assertNotNull(orders);
    assertEquals("{int(customer_count) * int(orders_per_customer)}", orders.getAttribute("count"));
    Element users = first(doc, "generate", "name", "db_user");
    assertNotNull(users);
    assertEquals("{customer_count}", users.getAttribute("count"));

    // (d) NOT NULL columns Benerator fills via DB metadata get filled from the executed DDL instead
    Element manufacturer = first(doc, "key", "name", "manufacturer");
    assertNotNull("db_product.manufacturer NOT NULL column filled from DDL", manufacturer);
    assertEquals("string", manufacturer.getAttribute("type"));
    assertEquals("30", manufacturer.getAttribute("maxLength"));
    Element birthDate = first(doc, "key", "name", "birth_date");
    assertNotNull("modeless birth_date resolved from DDL instead of flagged", birthDate);
    assertTrue(birthDate.getAttribute("generator").startsWith("DateTimeGenerator"));
  }

  @Test
  public void convertsShopMongodbScopesAndProjectionColumns() throws Exception {
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/demo/resources/demo/shop/shop-mongodb.ben.xml", report);

    // a <part> becomes <nestedKey>, a real runtime scope: the enclosing generate is reachable
    // via the parent alias, not by its name
    assertNotNull("db_user.id inside <part> -> parent.id", first(doc, "id", "script", "parent.id"));

    // positional access on a mongo find-projection variable -> column access on the row dict.
    // ean_code (untyped key) stays a bare access; price feeds a type="float" key, so the store-sourced
    // (schemaless -> possibly string) value is float()-coerced - python str*int would repeat, not multiply.
    assertNotNull(first(doc, "key", "script", "product.ean_code"));
    assertNotNull(first(doc, "key", "script", "float(product.price) * this.number_of_items"));

    // a reference to an entity NESTED in a collection document reads the collection and descends
    // by dotted sourceKey (Benerator mongo entity paths)
    Element addr = first(doc, "reference", "name", "address_id");
    assertNotNull(addr);
    assertEquals("db_user", addr.getAttribute("sourceType"));
    assertEquals("db_customer.db_address.id", addr.getAttribute("sourceKey"));
    Element cust = first(doc, "reference", "name", "customer_id");
    assertNotNull(cust);
    assertEquals("db_user", cust.getAttribute("sourceType"));
    assertEquals("db_customer.id", cust.getAttribute("sourceKey"));

    // DATAMIMIC rule: __name__ interpolation is valid only in selector/iterationSelector/string/pattern,
    // NEVER in script= (a script is evaluated as a Python expression, __x__ is not a name there). The
    // aggregate write-back must interpolate via iterationSelector and merely READ the result in script.
    assertNoInterpolationInScript(doc);
  }

  @Test
  public void typelessInlineExecuteDefaultsToPythonOrSql() throws Exception {
    // memstore: <execute>totalCount = mem.sumEntityColumn(...)</execute> (no type, no target) -> python
    Document doc = convert("src/demo/resources/demo/memstore/memstore.ben.xml", new MigrationReport());
    NodeList execs = doc.getElementsByTagName("execute");
    boolean pythonExec = false;
    boolean sqlExec = false;
    for (int i = 0; i < execs.getLength(); i++) {
      Element e = (Element) execs.item(i);
      if ("python".equals(e.getAttribute("type")) && e.getTextContent().contains("mem.sumEntityColumn")) {
        pythonExec = true;
      }
      if ("sql".equals(e.getAttribute("type")) && e.getTextContent().contains("CREATE TABLE")) {
        sqlExec = true; // targeted inline execute stays SQL
      }
    }
    assertTrue("typeless inline code -> type=python", pythonExec);
    assertTrue("targeted inline execute -> type=sql", sqlExec);
  }

  @Test
  public void fixedWidthSourceBeanAndExporterMap() throws Exception {
    // read: FixedWidthEntitySource bean -> source=".fcw" (spec written into the file as its # header)
    Document read = convert("src/demo/resources/demo/file/import_fixed_width.ben.xml", new MigrationReport());
    Element it = first(read, "iterate", "source", "products.import.fcw");
    assertNotNull("FixedWidthEntitySource bean -> .fcw source", it);
    assertEquals("no leftover <bean>", 0, read.getElementsByTagName("bean").getLength());

    // write: FixedWidthEntityExporter consumer -> target="FixedWidth(columns='...')"
    Document write = convert("src/demo/resources/demo/file/create_fixed_width.ben.xml", new MigrationReport());
    Element gen = first(write, "generate", "name", "transaction");
    assertNotNull(gen);
    assertTrue("FixedWidth target with columns: " + gen.getAttribute("target"),
        gen.getAttribute("target").startsWith("FixedWidth(columns='id[8r0],ean_code[13]"));
    // and the <variable source=fcwBean> resolves to the .fcw too
    assertNotNull(first(write, "variable", "source", "products.import.fcw"));
  }

  @Test
  public void addressGeneratorAsScalarBecomesEntityCityAndCsvSourceBeanInlines() throws Exception {
    // simple/cities: <attribute generator="AddressGenerator" dataset="europe"> as a scalar -> a city
    Document cities = convert("src/demo/resources/demo/simple/cities.ben.xml", new MigrationReport());
    Element europeVar = first(cities, "variable", "name", "_europe_address");
    assertNotNull(europeVar);
    assertEquals("Address", europeVar.getAttribute("entity"));
    assertEquals("europe", europeVar.getAttribute("dataset"));
    assertEquals("_europe_address.city", first(cities, "key", "name", "europe").getAttribute("script"));

    // file/csv_io: a CSVEntitySource bean is inlined at its source="id" (file + separator), bean dropped
    Document io = convert("src/demo/resources/demo/file/csv_io.ben.xml", new MigrationReport());
    Element it = first(io, "iterate", "source", "products.pipe.csv");
    assertNotNull("CSVEntitySource bean -> file source", it);
    assertEquals("|", it.getAttribute("separator"));
    assertEquals("no leftover <bean>", 0, io.getElementsByTagName("bean").getLength());
  }

  @Test
  public void inlineDdlFillsNotNullColumnsAndReferencesUseTheRealPrimaryKey() throws Exception {
    // compositekey: the schema lives INLINE in <execute>, table names are quoted, and the PK is not "id".
    Document doc = convert("src/demo/resources/demo/db/compositekey.ben.xml", new MigrationReport());

    // NOT NULL "name" column, introspected from the inline (quoted) CREATE TABLE, is filled
    Element playlist = first(doc, "generate", "name", "playlist");
    assertNotNull(playlist);
    Element nameKey = null;
    NodeList keys = playlist.getElementsByTagName("key");
    for (int i = 0; i < keys.getLength(); i++) {
      if ("name".equals(((Element) keys.item(i)).getAttribute("name"))) {
        nameKey = (Element) keys.item(i);
      }
    }
    assertNotNull("inline-DDL NOT NULL column 'name' filled", nameKey);
    assertEquals("string", nameKey.getAttribute("type"));

    // a reference to playlist uses its real PK column (PLAYLIST_ID), not the "id" guess
    Element ref = first(doc, "reference", "name", "PLAYLIST_ID");
    assertNotNull(ref);
    assertEquals("playlist", ref.getAttribute("sourceType"));
    assertEquals("PLAYLIST_ID", ref.getAttribute("sourceKey"));
  }

  @Test
  public void ftlScriptBecomesStringInterpolationAndPathsGoDescriptorRelative() throws Exception {
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/demo/resources/demo/file/create_xml.ben.xml", report);

    // script="{ftl: ${addr.postalCode} ${addr.city}}" is string interpolation, not a python
    // expression -> emitted as string= with __var__ interpolation, NEVER as script= (AGENTS rule 7).
    Element line2 = first(doc, "key", "name", "line2");
    assertNotNull(line2);
    assertTrue("FTL script -> string=: " + line2.getAttribute("string"),
        line2.getAttribute("string").contains("__addr.postalCode__")
            && line2.getAttribute("string").contains("__addr.city__"));
    assertTrue("no leftover script= on the FTL field", line2.getAttribute("script").isEmpty());
    assertNoInterpolationInScript(doc);

    // A project-root-relative file source (demo/file/x.csv) is rewritten to the path that exists
    // next to the descriptor (products.import.csv), since DATAMIMIC resolves relative to it.
    Document io = convert("src/demo/resources/demo/file/csv_io.ben.xml", new MigrationReport());
    Element it = first(io, "iterate", "source", "products.import.csv");
    assertNotNull("demo/file/products.import.csv -> products.import.csv", it);
  }

  /** Assert no {@code script="..."} attribute anywhere in the tree carries an {@code __name__}
   *  interpolation token — that idiom belongs in selector/string/pattern only. */
  private static void assertNoInterpolationInScript(Document doc) {
    NodeList all = doc.getElementsByTagName("*");
    for (int i = 0; i < all.getLength(); i++) {
      Element e = (Element) all.item(i);
      if (e.hasAttribute("script")) {
        assertFalse("script= must not carry __interpolation__: " + e.getAttribute("script"),
            e.getAttribute("script").matches(".*__[A-Za-z]\\w*__.*"));
      }
    }
  }

  @Test
  public void realProjectIdioms_dbSequence_currentDatetime_modeIgnored_map_offset_wgtCyclic() throws Exception {
    MigrationReport report = new MigrationReport();
    Document doc = convert("src/test/resources/com/rapiddweller/benerator/main/datamimic/round3.ben.xml", report);

    // DBSequenceGenerator('seq', db) -> SequenceTableGenerator(sequence=...) + database=
    Element id = first(doc, "id", "name", "id");
    assertNotNull(id);
    assertEquals("SequenceTableGenerator(sequence='zsv.t_angebote_id_seq')", id.getAttribute("generator"));
    assertEquals("hsms", id.getAttribute("database"));

    // CurrentDateTimeGenerator -> bare DateTimeGenerator (current mode)
    assertEquals("DateTimeGenerator", first(doc, "key", "name", "created").getAttribute("generator"));

    // mode="ignored" -> field omitted entirely
    assertTrue("mode=ignored field omitted", first(doc, "key", "name", "internal") == null);

    // map="'MALE'->'m',..." folded into the script as dict.get
    Element g = first(doc, "variable", "name", "geschlecht");
    assertNotNull(g);
    assertEquals("{'MALE': 'm', 'FEMALE': 'w'}.get(person.gender, person.gender)", g.getAttribute("script"));

    // offset= passes through natively; cyclic on a .wgt.csv key drops silently (implicit)
    Element it = first(doc, "iterate", "offset", "6");
    assertNotNull("offset passes through", it);
    Element status = first(doc, "key", "name", "status");
    assertNotNull(status);
    assertTrue("cyclic dropped on wgt.csv key", status.getAttribute("cyclic").isEmpty());
    assertTrue("no manual flag for the wgt.csv cyclic", report.attention().stream()
        .noneMatch(i -> i.detail.contains("cyclic")));
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
