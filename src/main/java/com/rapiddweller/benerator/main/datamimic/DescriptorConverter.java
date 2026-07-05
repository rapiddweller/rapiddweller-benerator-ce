/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import com.rapiddweller.common.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Translates a Benerator XML descriptor into a native DATAMIMIC DSL descriptor by walking the parsed
 * DOM (parsed + XSD-validated by Benerator's {@link XMLUtil}) and rebuilding it under the DATAMIMIC
 * vocabulary. Anything it cannot translate is flagged into a {@link MigrationReport} and left as an
 * XML TODO comment - a partial, honest migration rather than a silently wrong one.
 */
public class DescriptorConverter {

  private final MigrationReport report;
  private final ExpressionMapper expressions;
  private final AssertionConverter assertions;
  private final ReferenceConverter references;
  private final ConsumerMapper consumers;
  private SettingsResolver settings;
  /** {@code <mongodb id="X">} store ids, so an {@code <id generator="MongoDBObjectIdGenerator">} inside a
   *  generate that consumes to one of them can be dropped (MongoDB assigns _id on insert itself). */
  private final java.util.Set<String> mongoStoreIds = new java.util.LinkedHashSet<>();
  /** all store ids (&lt;database&gt;/&lt;mongodb&gt;), so an &lt;iterate type=coll source=store&gt; keeps its
   *  source collection/table in {@code type} (DATAMIMIC needs it to read from a store). */
  private final java.util.Set<String> storeIds = new java.util.LinkedHashSet<>();
  private File descriptorDir;
  private File outputDir;
  /** environment name -> (system prefix -> "db"|"mongo") collected from <database>/<mongodb> elements,
   *  so the env-properties migration knows each system's type and the flat-format fallback prefix. */
  private final Map<String, Map<String, String>> envSystems = new LinkedHashMap<>();
  /** table -> (column -> DDL info) parsed from the CREATE TABLE scripts the descriptor itself executes -
   *  the convert-time stand-in for Benerator's runtime DB-metadata introspection. */
  private final Map<String, Map<String, DdlColumn>> ddlColumns = new LinkedHashMap<>();

  private static final class DdlColumn {
    final String benType;
    final boolean required; // NOT NULL and no default - the insert fails without a value
    final String maxLength;

    DdlColumn(String benType, boolean required, String maxLength) {
      this.benType = benType;
      this.required = required;
      this.maxLength = maxLength;
    }
  }

  private static final Map<String, String> DDL_TO_BEN_TYPE = new LinkedHashMap<>();

  static {
    for (String s : new String[] {"varchar", "char", "character", "character varying", "text"}) {
      DDL_TO_BEN_TYPE.put(s, "string");
    }
    for (String s : new String[] {"int", "integer", "smallint", "bigint", "serial", "bigserial"}) {
      DDL_TO_BEN_TYPE.put(s, "int");
    }
    DDL_TO_BEN_TYPE.put("decimal", "big_decimal");
    DDL_TO_BEN_TYPE.put("numeric", "big_decimal");
    DDL_TO_BEN_TYPE.put("date", "date");
    DDL_TO_BEN_TYPE.put("timestamp", "timestamp");
    DDL_TO_BEN_TYPE.put("boolean", "boolean");
    DDL_TO_BEN_TYPE.put("bool", "boolean");
    DDL_TO_BEN_TYPE.put("double", "double");
    DDL_TO_BEN_TYPE.put("float", "double");
    DDL_TO_BEN_TYPE.put("real", "double");
  }

  public DescriptorConverter(MigrationReport report) {
    this.report = report;
    this.expressions = new ExpressionMapper(report);
    this.assertions = new AssertionConverter(report);
    this.references = new ReferenceConverter(report);
    this.consumers = new ConsumerMapper(report);
  }

  public void convert(File input, File output) throws Exception {
    Document src = XMLUtil.parseWithLocators(input.getAbsolutePath());
    Document out = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element root = src.getDocumentElement();
    descriptorDir = input.getAbsoluteFile().getParentFile();
    outputDir = output.getAbsoluteFile().getParentFile();
    settings = new SettingsResolver(descriptorDir);
    scanBeans(root);
    settings.scanSettings(root);
    scanMongoStores(root);
    scanDdlSchemas(root);
    Node converted = convertNode(out, root, "/" + local(root));
    if (converted != null) {
      out.appendChild(converted);
      if (converted instanceof Element) {
        rewritePositionalRowAccess((Element) converted);
      }
    }
    XMLUtil.saveDocument(out, output, "utf-8");
  }

  /** Benerator multi-column selector variables yield ARRAYS ({@code product[0]}); a DATAMIMIC row is a
   *  dict accessed by column ({@code product.price}). Rewrites positional access in every script from
   *  the variable's select-list. */
  private void rewritePositionalRowAccess(Element root) {
    // ponytail: variable names mapped globally, not per scope - split per <generate> if names ever collide
    Map<String, List<String>> columns = new LinkedHashMap<>();
    org.w3c.dom.NodeList vars = root.getElementsByTagName("variable");
    for (int i = 0; i < vars.getLength(); i++) {
      Element v = (Element) vars.item(i);
      String sel = v.hasAttribute("selector") ? v.getAttribute("selector") : v.getAttribute("iterationSelector");
      List<String> cols = selectListColumns(sel);
      if (cols != null) {
        columns.put(v.getAttribute("name"), cols);
      }
    }
    if (columns.isEmpty()) {
      return;
    }
    org.w3c.dom.NodeList all = root.getElementsByTagName("*");
    for (int i = -1; i < all.getLength(); i++) {
      Element e = i < 0 ? root : (Element) all.item(i);
      if (!e.hasAttribute("script")) {
        continue;
      }
      String script = e.getAttribute("script");
      for (Map.Entry<String, List<String>> entry : columns.entrySet()) {
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("\\b" + java.util.regex.Pattern.quote(entry.getKey()) + "\\[(\\d+)\\]").matcher(script);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
          int idx = Integer.parseInt(m.group(1));
          String replacement = idx < entry.getValue().size()
              ? entry.getKey() + "." + entry.getValue().get(idx)
              : m.group(); // out-of-range index: leave it, the runtime error is the honest signal
          m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        script = sb.toString();
      }
      e.setAttribute("script", script);
    }
  }

  /** The bare column names of a simple select-list, or null when any entry is not a plain column
   *  (function call, {@code *}, quoted alias) - then positional access cannot be mapped safely. */
  private static List<String> selectListColumns(String sql) {
    if (sql == null || sql.isEmpty()) {
      return null;
    }
    java.util.regex.Matcher m = java.util.regex.Pattern
        .compile("(?is)^\\s*select\\s+(.+?)\\s+from\\s").matcher(sql);
    if (!m.find()) {
      return null;
    }
    String list = m.group(1);
    if (list.contains("(") || list.contains("*")) {
      return null;
    }
    List<String> cols = new java.util.ArrayList<>();
    for (String part : list.split(",")) {
      String[] words = part.trim().split("\\s+");
      String col = words[words.length - 1]; // the alias when present, else the column itself
      int dot = col.lastIndexOf('.');
      if (dot >= 0) {
        col = col.substring(dot + 1);
      }
      if (!col.matches("\\w+")) {
        return null;
      }
      cols.add(col);
    }
    return cols;
  }

  /** ", see MIGRATION_PLAYBOOK.md#..." for an unmapped element tag with a recipe; "" when there is none.
   *  Comment text only - never changes what gets flagged. */
  private static String elementPlaybookRef(String tag) {
    switch (tag) {
      case "value":
        return ", see MIGRATION_PLAYBOOK.md#value";
      case "pre-parse-generate":
        return ", see MIGRATION_PLAYBOOK.md#pre-parse-generate";
      case "transcodingTask":
      case "transcode":
      case "meta-model":
        return ", see MIGRATION_PLAYBOOK.md#transcoding-meta-model";
      default:
        return "";
    }
  }

  /** Record every {@code <bean id spec>} so generator references to it can be inlined. */
  private void scanBeans(Element el) {
    if (local(el).equals("bean") && el.hasAttribute("id")) {
      String id = el.getAttribute("id");
      if (el.hasAttribute("spec")) {
        expressions.registerBeanSpec(id, el.getAttribute("spec"));
      }
      // A bean whose class/spec is an *EntityExporter (XMLEntityExporter, CSVEntityExporter, ...) is an
      // exporter definition; map its id to the DATAMIMIC target so consumer="id" resolves to it.
      String def = el.hasAttribute("class") ? el.getAttribute("class") : el.getAttribute("spec");
      if (def != null && !def.isEmpty()) {
        String simple = ExpressionMapper.beneratorGeneratorClass(def); // strips "new ", args, and the FQN below
        simple = simple.substring(simple.lastIndexOf('.') + 1);
        String target = VocabularyMap.CONSUMER_TARGET.get(simple);
        if (target != null) {
          consumers.registerExporter(id, target);
        }
      }
    }
    for (Node c = el.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.ELEMENT_NODE) {
        scanBeans((Element) c);
      }
    }
  }

  /** Record every {@code <mongodb id>} so mongo-consumed {@code MongoDBObjectIdGenerator} ids can be dropped,
   *  plus each store's environment binding for the env-properties migration. */
  private void scanMongoStores(Element el) {
    String tag = local(el);
    if (tag.equals("mongodb") && el.hasAttribute("id")) {
      mongoStoreIds.add(el.getAttribute("id"));
    }
    if ((tag.equals("database") || tag.equals("mongodb")) && el.hasAttribute("id")) {
      storeIds.add(el.getAttribute("id"));
    }
    if ((tag.equals("database") || tag.equals("mongodb")) && el.hasAttribute("environment")) {
      // DATAMIMIC resolves <system>.<systemType>.* from conf/<environment>.env.properties;
      // system falls back to the element id when absent.
      String system = el.hasAttribute("system") ? el.getAttribute("system") : el.getAttribute("id");
      envSystems.computeIfAbsent(el.getAttribute("environment"), k -> new LinkedHashMap<>())
          .put(system, tag.equals("mongodb") ? "mongo" : "db");
    }
    for (Node c = el.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.ELEMENT_NODE) {
        scanMongoStores((Element) c);
      }
    }
  }

  /** environment name -&gt; (system prefix -&gt; "db"|"mongo") from this descriptor (see scanMongoStores). */
  Map<String, Map<String, String>> envSystems() {
    return envSystems;
  }

  /** @return the converted node (Element, or a TODO Comment when the source element is unmapped). */
  /** Parse the CREATE TABLE DDL of every {@code <execute uri="*.sql">} the descriptor runs, so missing
   *  NOT NULL columns can be filled in like Benerator's DB-metadata introspection would at runtime. */
  private void scanDdlSchemas(Element el) {
    if (local(el).equals("execute") && el.hasAttribute("uri")) {
      String uri = settings.resolve(el.getAttribute("uri"));
      File f = new File(descriptorDir, uri);
      if (uri.endsWith(".sql") && f.isFile()) {
        try {
          parseDdl(new String(java.nio.file.Files.readAllBytes(f.toPath()), java.nio.charset.StandardCharsets.UTF_8));
        } catch (java.io.IOException e) {
          // unreadable DDL just means no introspection - the converter stays best-effort
        }
      }
    }
    for (Node child = el.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        scanDdlSchemas((Element) child);
      }
    }
  }

  private void parseDdl(String sql) {
    java.util.regex.Matcher t = java.util.regex.Pattern
        .compile("(?is)create\\s+table\\s+(?:if\\s+not\\s+exists\\s+)?([\\w.]+)\\s*\\((.*?)\\)\\s*;").matcher(sql);
    while (t.find()) {
      String table = t.group(1).toLowerCase();
      table = table.substring(table.lastIndexOf('.') + 1);
      Map<String, DdlColumn> cols = ddlColumns.computeIfAbsent(table, k -> new LinkedHashMap<>());
      for (String line : t.group(2).split("\\r?\\n")) {
        line = line.trim().replaceAll(",\\s*$", "");
        if (line.isEmpty() || line.startsWith("--")
            || line.matches("(?i)(primary|constraint|unique|foreign|check|key)\\b.*")) {
          continue;
        }
        java.util.regex.Matcher c = java.util.regex.Pattern
            .compile("(?i)^(\\w+)\\s+([a-z]+(?:\\s+varying)?)\\s*(?:\\((\\d+)[^)]*\\))?").matcher(line);
        if (!c.find()) {
          continue;
        }
        String benType = DDL_TO_BEN_TYPE.get(c.group(2).toLowerCase().replaceAll("\\s+", " "));
        if (benType == null) {
          continue;
        }
        boolean required = line.matches("(?i).*\\bnot\\s+null\\b.*") && !line.matches("(?i).*\\bdefault\\b.*");
        cols.put(c.group(1).toLowerCase(),
            new DdlColumn(benType, required, "string".equals(benType) ? c.group(3) : null));
      }
    }
  }

  /** Benerator fills NOT NULL columns absent from the descriptor via DB metadata at runtime; DATAMIMIC
   *  cannot, so synthesize a matching Benerator {@code <attribute>} for each and convert it normally. */
  private void appendDdlRequiredColumns(Document out, Element src, Element result, String path) {
    Map<String, DdlColumn> cols = ddlColumns.get(result.getAttribute("name").toLowerCase());
    if (cols == null || !consumesToStore(src)) {
      return;
    }
    java.util.Set<String> present = new java.util.LinkedHashSet<>();
    for (Node child = result.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child.getNodeType() == Node.ELEMENT_NODE && !local((Element) child).equals("variable")) {
        present.add(((Element) child).getAttribute("name").toLowerCase());
      }
    }
    for (Map.Entry<String, DdlColumn> col : cols.entrySet()) {
      if (!col.getValue().required || present.contains(col.getKey())) {
        continue;
      }
      Element benAttr = src.getOwnerDocument().createElement("attribute");
      benAttr.setAttribute("name", col.getKey());
      benAttr.setAttribute("type", col.getValue().benType);
      if (col.getValue().maxLength != null) {
        benAttr.setAttribute("maxLength", col.getValue().maxLength);
      }
      src.appendChild(benAttr); // parented, so enclosing-scope lookups during conversion work
      Node converted = convertNode(out, benAttr, path + "/" + col.getKey());
      if (converted != null) {
        result.appendChild(converted);
        report.info(path, "schema", "NOT NULL column '" + col.getKey() + "' (" + col.getValue().benType
            + ") filled from the executed DDL - Benerator fills it via DB metadata at runtime");
      }
    }
  }

  /** True when the generate's consumer includes one of the scanned store ids (db/mongo). */
  private boolean consumesToStore(Element el) {
    String consumer = el.getAttribute("consumer");
    for (String id : storeIds) {
      if (consumer.matches(".*\\b" + java.util.regex.Pattern.quote(id) + "\\b.*")) {
        return true;
      }
    }
    return false;
  }

  private Node convertNode(Document out, Element el, String path) {
    String tag = local(el);
    if (VocabularyMap.DROP_ELEMENTS.contains(tag)) {
      report.info(path, "dropped", "<" + tag + "> is not needed in DATAMIMIC (auto-discovered) - removed");
      return null;
    }
    if (tag.equals("consumer")) {
      return null; // a <consumer> element is folded into the parent <generate>'s target (see convertGenerateAttributes)
    }
    if (tag.equals("bean")) {
      // A <bean spec="new Generator(...)"> is inlined at its generator="id" references, so the bean is gone.
      if (expressions.isKnownGeneratorSpec(el.getAttribute("spec"))) {
        report.info(path, "bean", "<bean id='" + el.getAttribute("id") + "'> generator inlined into its references - removed");
        return null;
      }
      // An exporter bean (<bean id="xml" class="XMLEntityExporter">) is folded into consumer/target - removed.
      String exporterTarget = consumers.exporterTarget(el.getAttribute("id"));
      if (exporterTarget != null) {
        report.info(path, "bean", "<bean id='" + el.getAttribute("id") + "'> exporter -> target='"
            + exporterTarget + "' - removed");
        return null;
      }
      report.add(path, "element", "<bean> has no DATAMIMIC equivalent - migrate manually");
      return out.createComment(" TODO(datamimic-migration): <bean> not supported - migrate manually,"
          + " see MIGRATION_PLAYBOOK.md#bean ");
    }
    if (tag.equals("reference")) {
      return references.convertReferenceNode(out, el, path); // may become <reference> or <key> (constant/script)
    }
    if (tag.equals("if")) {
      Node assertion = assertions.errorOnlyIfToAssert(out, el, path); // <if test><error>MSG</error></if> is the assertion idiom
      if (assertion != null) {
        return assertion;
      }
      if (isSetupChild(path)) { // DATAMIMIC <condition> is per-<generate>; a setup-level <if> has no home
        report.add(path, "condition", "setup-level <if>/<error> assertion has no DATAMIMIC equivalent - dropped "
            + "(use <execute type='python'>raise ...</execute> to keep it)");
        return out.createComment(" TODO(datamimic-migration): setup-level <if> assertion dropped - review,"
            + " see MIGRATION_PLAYBOOK.md#setup-if ");
      }
      return convertIfNode(out, el, path); // <if test><then>/<else> -> <condition><if condition>/<else>
    }
    if (tag.equals("execute")) {
      return convertExecuteNode(out, el, path); // uri-based; inline code is flagged
    }
    if (tag.equals("evaluate")) { // Benerator <evaluate assert="..."> is a post-generation assertion
      return assertions.convertEvaluateNode(out, el, path); // assert= -> <variable> + <assert>; no assert= is flagged
    }
    if ((tag.equals("attribute") || tag.equals("id")) && el.hasAttribute("generator")) {
      String genClass = ExpressionMapper.beneratorGeneratorClass(el.getAttribute("generator"));
      // MongoDB assigns _id itself when the document has none, so a MongoDBObjectIdGenerator id on a
      // mongo-consumed <generate> carries no information - drop the field (verified: CE mongodb_client
      // insert() passes documents straight to insert_many, pymongo/Mongo fill in _id).
      if (genClass.equals("MongoDBObjectIdGenerator") && enclosingTargetsMongoStore(el)) {
        report.info(path, "generator", "MongoDB assigns _id on insert - field '"
            + el.getAttribute("name") + "' dropped");
        return null;
      }
      // Benerator's scalar CountryGenerator.toString() is the ISO code; DATAMIMIC's Country is an entity
      // whose iso_code field carries it -> <variable entity="Country"> + <key script=".iso_code">.
      Node countryFragment = countryGeneratorToEntityFragment(out, el, genClass, path);
      if (countryFragment != null) {
        return countryFragment;
      }
    }
    // A dynamic-selector field (<attribute source="db" selector="{{ftl:select ... ${x}}}"/>) re-queries
    // per record; DATAMIMIC's native form is <variable iterationSelector> (re-run each iteration) plus
    // a <key script> unwrapping the single-value result row.
    if ((tag.equals("attribute") || tag.equals("id")) && el.hasAttribute("source")
        && ExpressionMapper.isDynamicSelector(el.getAttribute("selector"))) {
      return dynamicSelectorFieldFragment(out, el, path);
    }
    // Schema priming for a schemaless store: <pre-parse-generate target="mongo"> and a mongo-consumed
    // <meta-model> only pre-declare collection shapes - MongoDB creates collections on insert, so both
    // are no-ops in DATAMIMIC.
    if ((tag.equals("pre-parse-generate") && mongoStoreIds.contains(el.getAttribute("target")))
        || (tag.equals("meta-model") && mongoStoreIds.contains(el.getAttribute("consumer")))) {
      report.info(path, "element", "<" + tag + "> primes a MongoDB schema - schemaless in DATAMIMIC, removed");
      return null;
    }
    String target = VocabularyMap.ELEMENT.get(tag);
    if (target == null) {
      report.add(path, "element", "<" + tag + "> has no DATAMIMIC equivalent - migrate manually");
      return out.createComment(" TODO(datamimic-migration): <" + tag + "> not supported - migrate manually"
          + elementPlaybookRef(tag) + " ");
    }
    Element result = out.createElement(target);
    switch (tag) {
      case "setup":
        convertSetupAttributes(el, result, path);
        break;
      case "generate":
      case "iterate":
        convertGenerateAttributes(el, result, path);
        break;
      case "database":
        convertDatabaseAttributes(el, result, path);
        break;
      case "mongodb":
        // DATAMIMIC <mongodb> takes the connection directly (no dbms); env keys are migrated separately.
        copyAttributes(el, result, "id", "host", "port", "database", "environment", "system", "user", "password");
        for (Map.Entry<String, String> a : attributes(result).entrySet()) {
          result.setAttribute(a.getKey(), settings.resolve(a.getValue())); // {ftl:${mongoHost}} etc.
        }
        break;
      case "memstore":
        copyAttributes(el, result, "id"); // DATAMIMIC memstore is just an id
        break;
      case "include":
        // A Benerator XSD include only backs XML-editor validation; DATAMIMIC includes are
        // .properties/.xml content and would reject it.
        if (el.getAttribute("uri").endsWith(".xsd")) {
          report.info(path, "include", "<include uri='" + el.getAttribute("uri") + "'> XML-schema include removed");
          return null;
        }
        // Benerator FTL include path {ftl:${var}/...} -> DATAMIMIC f-string {var}/... (dynamic include,
        // resolved at runtime from <setting> values - same as EE's dynamic <include>).
        result.setAttribute("uri", ftlUriToFString(el.getAttribute("uri")));
        break;
      case "while":
        convertWhileAttributes(el, result, path); // <while test> -> <while condition>
        break;
      case "setting":
      case "property":
        convertSettingAttributes(el, result, path); // name + value -> <variable> constant/script
        break;
      case "comment":
      case "echo":
        break; // no attributes to map; text content is copied below
      default: // attribute / id / part / variable
        applyDdlTypeFallback(el, tag);
        convertFieldAttributes(el, result, tag, path);
        // A Benerator <part> GENERATES a nested structure; DATAMIMIC's <nestedKey> needs type="dict"
        // (or "list") to know it builds the structure - without it, DATAMIMIC assumes enrich-mode and
        // looks the name up in the parent product (KeyError). List when the part repeats (count/source).
        if (tag.equals("part") && !result.hasAttribute("type") && !result.hasAttribute("source")
            && !result.hasAttribute("script")) {
          boolean many = el.hasAttribute("count") || el.hasAttribute("source") || el.hasAttribute("minCount");
          result.setAttribute("type", many ? "list" : "dict");
        }
        // A field that ends up with no generation mode at all (Benerator derives its type from DB
        // metadata) would fail DATAMIMIC's parser and kill the whole file - flag it as a comment.
        if ((tag.equals("attribute") || tag.equals("id"))
            && result.getAttributes().getLength() == 1 && result.hasAttribute("name")) {
          String fieldName = result.getAttribute("name");
          report.add(path, "attribute", "<" + tag + " name='" + fieldName + "'> takes its type from DB "
              + "metadata (Benerator introspection) - define type/generator manually");
          return out.createComment(" TODO(datamimic-migration): <key name='" + fieldName
              + "'> type came from DB metadata - define type/generator manually ");
        }
        break;
    }
    // A dbunit dataset source expands into one <iterate> per table (see expandDbunitDataset).
    if (tag.equals("iterate")) {
      Node dbunitFragment = expandDbunitDataset(out, el, result, path);
      if (dbunitFragment != null) {
        return dbunitFragment;
      }
    }
    // children (elements, text, comments) in source order
    for (Node child = el.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        Node converted = convertNode(out, (Element) child, path + "/" + local((Element) child));
        if (converted != null) {
          result.appendChild(converted);
        }
      } else if (child.getNodeType() == Node.COMMENT_NODE) {
        result.appendChild(out.createComment(child.getNodeValue()));
      } else if (child.getNodeType() == Node.TEXT_NODE && !child.getNodeValue().trim().isEmpty()) {
        String text = child.getNodeValue();
        if (tag.equals("echo")) {
          // <echo>{ftl:X ${a}}</echo> -> X {a}: DATAMIMIC's echo evaluates {...} as f-string fields.
          String ftl = ExpressionMapper.ftlBody(text.trim());
          if (ftl != null) {
            text = ExpressionMapper.ftlToFString(ftl);
          }
        }
        result.appendChild(out.createTextNode(text));
      }
    }
    if (tag.equals("generate")) {
      appendDdlRequiredColumns(out, el, result, path);
    }
    return result;
  }

  /** A bare {@code <attribute name>} whose type Benerator reads from DB metadata at runtime: resolve it
   *  from the executed DDL when available, so the field converts normally instead of being flagged. */
  private void applyDdlTypeFallback(Element el, String tag) {
    if (!tag.equals("attribute") && !tag.equals("id")) {
      return;
    }
    for (String key : attributes(el).keySet()) {
      if (!key.equals("name") && !key.equals("nullable")) {
        return; // already has a type or a generation mode
      }
    }
    String scope = enclosingScopeName(el);
    Map<String, DdlColumn> cols = scope == null ? null : ddlColumns.get(scope.toLowerCase());
    DdlColumn col = cols == null ? null : cols.get(el.getAttribute("name").toLowerCase());
    if (col != null) {
      el.setAttribute("type", col.benType);
      if (col.maxLength != null) {
        el.setAttribute("maxLength", col.maxLength);
      }
    }
  }

  /** Benerator scripts inside a nested {@code <generate>} reference ENCLOSING records by name
   *  ({@code db_user.id}); DATAMIMIC resolves outer records only via {@code root.<name>.<field>}.
   *  The nearest scope's own name is already rewritten to {@code this} - prefix every ancestor
   *  scope name above it with {@code root.}. */
  private String rewriteOuterScopeRefs(String script, Element field) {
    if (script == null || script.isEmpty()) {
      return script;
    }
    boolean nearest = true;
    for (Node n = field.getParentNode(); n instanceof Element; n = n.getParentNode()) {
      Element e = (Element) n;
      if (local(e).equals("generate") || local(e).equals("iterate")) {
        if (nearest) {
          nearest = false;
          continue;
        }
        String name = e.hasAttribute("type") ? e.getAttribute("type") : e.getAttribute("name");
        if (!name.isEmpty()) {
          script = script.replaceAll("\\b" + java.util.regex.Pattern.quote(name) + "\\.", "root." + name + ".");
        }
      }
    }
    return script;
  }

  /** A computed count {@code {a * b}} with each bare identifier wrapped in {@code int(...)} - DATAMIMIC
   *  settings from .properties are strings, and python string arithmetic throws. A single-identifier
   *  count ({@code {counts}}) already works (the runtime int()-casts the final result) and stays as-is. */
  private static String intCastCountExpression(String val) {
    if (!val.startsWith("{") || !val.endsWith("}") || !val.matches(".*[+*/-].*")) {
      return val;
    }
    String expr = val.substring(1, val.length() - 1);
    // an identifier not part of an attribute access (x.y) and not a call (f(...))
    java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?<![\\w.])([A-Za-z_]\\w*)(?![\\w(.])").matcher(expr);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      String id = m.group(1);
      boolean keyword = id.equals("if") || id.equals("else") || id.equals("and") || id.equals("or")
          || id.equals("not") || id.equals("None") || id.equals("True") || id.equals("False");
      m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(keyword ? id : "int(" + id + ")"));
    }
    m.appendTail(sb);
    return "{" + sb + "}";
  }

  /** {@code <attribute name="x" source="db" selector="{{...${y}}}"/>} -> {@code <variable name="_x_sel"
   *  source iterationSelector>} + {@code <key name="x" script>} taking the first row's first column. */
  private Node dynamicSelectorFieldFragment(Document out, Element el, String path) {
    String name = el.getAttribute("name");
    String var = "_" + name + "_sel";
    Element variable = out.createElement("variable");
    variable.setAttribute("name", var);
    variable.setAttribute("source", el.getAttribute("source"));
    variable.setAttribute("iterationSelector",
        ExpressionMapper.selectorToInterpolated(el.getAttribute("selector"), enclosingScopeName(el)));
    Element key = out.createElement("key");
    key.setAttribute("name", name);
    // ponytail: first row, first column - the Benerator dynamic-selector idiom is a single-value subquery
    key.setAttribute("script", "list(" + var + "[0].values())[0] if " + var + " else None");
    org.w3c.dom.DocumentFragment frag = out.createDocumentFragment();
    frag.appendChild(variable);
    frag.appendChild(key);
    report.info(path, "selector", "dynamic selector on '" + name
        + "' -> <variable iterationSelector> + <key script>");
    return frag;
  }

  private void convertSetupAttributes(Element src, Element out, String path) {
    for (Map.Entry<String, String> a : attributes(src).entrySet()) {
      if (VocabularyMap.SETUP_ATTR_KEEP.contains(a.getKey())) {
        out.setAttribute(a.getKey(), a.getValue());
      } else {
        report.add(path, "attribute", "<setup> '" + a.getKey() + "' has no DATAMIMIC equivalent - dropped");
      }
    }
    // Benerator's default CSV separator is ',' while DATAMIMIC's is '|' - pin the Benerator default so
    // comma-separated sources parse, unless the descriptor sets its own.
    if (!out.hasAttribute("defaultSeparator")) {
      out.setAttribute("defaultSeparator", ",");
    }
  }

  private void convertGenerateAttributes(Element src, Element out, String path) {
    boolean targetSet = false;
    for (Map.Entry<String, String> a : attributes(src).entrySet()) {
      String key = a.getKey();
      String val = a.getValue();
      switch (key) {
        case "type":
          out.setAttribute("name", val);
          break;
        case "name":
        case "pageSize":
          out.setAttribute(key, val);
          break;
        case "count":
          // DATAMIMIC settings from .properties are strings; a computed count like
          // {customer_count * orders_per_customer} needs each identifier int()-cast or python throws.
          out.setAttribute("count", intCastCountExpression(val));
          break;
        case "threads":
          out.setAttribute("numProcess", val);
          break;
        case "consumer":
          String tgt = consumers.consumerToTarget(val);
          out.setAttribute("target", tgt);
          targetSet = true;
          if (tgt.isEmpty()) {
            if (ConsumerMapper.isNoConsumerOnly(val)) {
              // NoConsumer deliberately produces no output; DATAMIMIC's empty target is the exact match.
              report.info(path, "consumer", "consumer 'NoConsumer' -> empty target (capture only)");
            } else {
              report.add(path, "consumer", "consumer '" + val + "' -> configure a DATAMIMIC target/exporter manually");
            }
          }
          break;
        case "source":
        case "separator":
          out.setAttribute(key, val);
          break;
        case "selector":
          out.setAttribute("selector", ExpressionMapper.selectorToInterpolated(val, null));
          break;
        case "encoding":
          // DATAMIMIC has no encoding attribute; it reads utf-8. Dropping the default is lossless.
          if (val.replace("-", "").equalsIgnoreCase("utf8")) {
            report.info(path, "attribute", "encoding='" + val + "' dropped (DATAMIMIC reads utf-8)");
          } else {
            report.add(path, "attribute", "encoding='" + val + "' - DATAMIMIC reads utf-8; re-encode the source file");
          }
          break;
        default:
          report.add(path, "attribute", "<generate> '" + key + "' not mapped - dropped");
          break;
      }
    }
    if (!targetSet) {
      // no consumer= attribute: fold a nested <consumer class="X"> element into target instead
      String childTarget = consumers.consumerFromChildElement(src, path);
      if (childTarget != null) {
        out.setAttribute("target", childTarget);
        targetSet = true;
      }
    }
    if (!targetSet) {
      out.setAttribute("target", ""); // DATAMIMIC generate needs a target; empty = capture only
    }
    if (!out.hasAttribute("name")) {
      // Benerator allows a nameless <iterate source=...>; DATAMIMIC requires a name - derive it
      // from the source filename so the descriptor parses.
      String source = out.getAttribute("source");
      String derived = source.isEmpty() ? "unnamed" : source.replaceAll(".*/", "").replaceAll("\\..*$", "")
          .replaceAll("\\W", "_");
      out.setAttribute("name", derived);
      report.info(path, "attribute", "nameless iterate -> name='" + derived + "' derived from the source");
    }
    String src2 = out.getAttribute("source");
    // A store CRUD consumer with a collection arg (mongo.inserter('out')) names the OUTPUT collection;
    // DATAMIMIC's store exporter writes to the product name, so that arg becomes name (else it re-inserts
    // into the source). Only store.<crudop>('coll') qualifies - NOT a Java exporter ctor like
    // CSVEntityExporter('out.csv'), which must not rename the generate.
    java.util.regex.Matcher crudColl = CRUD_TARGET_COLLECTION.matcher(src.getAttribute("consumer"));
    while (crudColl.find()) {
      if (storeIds.contains(crudColl.group(1))) {
        out.setAttribute("name", crudColl.group(3));
        break;
      }
    }
    // A store-reading <iterate type="coll" source="store"> needs its SOURCE collection/table declared;
    // the type->name mapping alone loses it. It must be sourceEntity, not type: DATAMIMIC rejects
    // source+type+selector together, and on the write side (targetEntity -> type -> name) a type would
    // override a CRUD-renamed name and re-insert into the source collection.
    if (local(src).equals("iterate") && storeIds.contains(src2) && src.hasAttribute("type")) {
      out.removeAttribute("type");
      out.setAttribute("sourceEntity", src.getAttribute("type"));
    }
  }

  /**
   * A Benerator {@code <iterate source="X.dbunit.xml" consumer="db"/>} reads a multi-table dbunit
   * dataset and inserts each row into its table. DATAMIMIC reads one record list per source, so this
   * expands the single iterate into one {@code <iterate source="X.<table>.json" type="<table>">} per
   * table (in the dataset's dependency order), writing the split rows as JSON next to the descriptor.
   * Returns null when this is not a dbunit iterate or the dataset file cannot be located (then the
   * caller keeps the single iterate and it is flagged).
   */
  private Node expandDbunitDataset(Document out, Element src, Element iterate, String path) {
    String source = settings.resolve(src.getAttribute("source")); // resolve {ftl:${database}/...}
    if (!source.endsWith(".dbunit.xml")) {
      return null;
    }
    File dataset = new File(descriptorDir, source);
    if (!dataset.isFile()) {
      report.add(path, "source", "dbunit dataset '" + source + "' could not be located to split "
          + "(dynamic path?) - split into per-table sources manually");
      return null;
    }
    String dir = source.contains("/") ? source.substring(0, source.lastIndexOf('/') + 1) : "";
    String base = source.substring(dir.length(), source.length() - ".dbunit.xml".length());
    try {
      Map<String, List<Map<String, String>>> tables = DbunitSplitter.split(dataset);
      String target = iterate.getAttribute("target");
      org.w3c.dom.DocumentFragment fragment = out.createDocumentFragment();
      for (Map.Entry<String, List<Map<String, String>>> table : tables.entrySet()) {
        String jsonName = dir + base + "." + table.getKey() + ".json";
        File jsonFile = new File(outputDir, jsonName);
        jsonFile.getParentFile().mkdirs();
        DbunitSplitter.writeTableJson(table.getValue(), jsonFile);
        Element ti = out.createElement("iterate");
        ti.setAttribute("name", table.getKey());
        ti.setAttribute("type", table.getKey());
        ti.setAttribute("source", jsonName);
        if (!target.isEmpty()) {
          ti.setAttribute("target", target);
        }
        fragment.appendChild(ti);
      }
      report.info(path, "source", "dbunit dataset '" + source + "' expanded into " + tables.size()
          + " per-table <iterate> + JSON sources");
      return fragment;
    } catch (Exception e) {
      report.add(path, "source", "dbunit dataset '" + source + "' could not be split: " + e.getMessage());
      return null;
    }
  }

  /** A store CRUD consumer's explicit target collection: {@code mongo.inserter('out')} -&gt; store 'mongo',
   *  op 'inserter', collection 'out'. Restricted to the CRUD ops so a Java exporter ctor does not match. */
  private static final java.util.regex.Pattern CRUD_TARGET_COLLECTION = java.util.regex.Pattern.compile(
      "(\\w+)\\.(" + String.join("|", VocabularyMap.CRUD_CONSUMER_OP.keySet()) + ")\\('([^']+)'\\)");

  private void convertFieldAttributes(Element src, Element out, String tag, String path) {
    Map<String, String> attrs = attributes(src);
    // dataset/locale are static config in DATAMIMIC (attribute values are not interpolated at runtime),
    // so a Benerator dataset="{country}" must be resolved from the settings NOW or it stays literal.
    for (String cfg : new String[] {"dataset", "locale"}) {
      String resolved = attrs.containsKey(cfg) ? settings.resolve(attrs.get(cfg)) : null;
      if (resolved != null && !resolved.equals(attrs.get(cfg))) {
        attrs = new LinkedHashMap<>(attrs);
        attrs.put(cfg, resolved);
        report.info(path, "attribute", cfg + "='" + attributes(src).get(cfg) + "' resolved to '" + resolved
            + "' at conversion (DATAMIMIC does not interpolate it at runtime)");
      }
    }
    // Does another attribute already produce the value? Then an unmapped type= is cosmetic, not a gap.
    boolean hasMode = attrs.containsKey("script") || attrs.containsKey("source") || attrs.containsKey("values")
        || attrs.containsKey("generator") || attrs.containsKey("constant") || attrs.containsKey("pattern");
    // Benerator gives type="date" a built-in default date generator; DATAMIMIC has no 'date' type but has
    // DateTimeGenerator. A bare <attribute type="date"> (no other mode) becomes generator="DateTimeGenerator(...)"
    // (honoring min/max) instead of an invalid mode-less <key>.
    String benType = attrs.get("type");
    if (!hasMode && ("date".equals(benType) || "datetime".equals(benType) || "timestamp".equals(benType))) {
      StringBuilder g = new StringBuilder("DateTimeGenerator(");
      if (attrs.containsKey("min")) {
        g.append("min='").append(attrs.get("min")).append("'");
      }
      if (attrs.containsKey("max")) {
        g.append(g.length() > "DateTimeGenerator(".length() ? ", " : "").append("max='").append(attrs.get("max")).append("'");
      }
      out.setAttribute("generator", g.append(")").toString());
      report.info(path, "type", "type='" + benType + "' -> DateTimeGenerator (DATAMIMIC has no date type)");
      hasMode = true;
      attrs = new LinkedHashMap<>(attrs);
      attrs.remove("type"); // consumed - do not let mapType flag it
    }
    // A mode-less <attribute name="X"> inside a source-backed <generate>/<iterate> overlays the source
    // column X - Benerator's anonymization/enrichment pattern. Read it via script="X" so a converter can
    // transform it: <attribute name="familyName" converter="new CutLength(3)"/> over persons.csv becomes
    // <key name="familyName" script="familyName" converter="CutLength(3)"/>.
    if (!hasMode && attrs.containsKey("name") && enclosingHasSource(src)) {
      out.setAttribute("script", attrs.get("name"));
      report.info(path, "attribute", "<attribute name='" + attrs.get("name")
          + "'> with no generator overlays the source column (script) - anonymization/enrichment");
      hasMode = true;
    }
    String mappedType = mapType(attrs.get("type"), tag, hasMode, path);
    // Benerator defaults an untyped min/max range to int, so <attribute min="1" max="27"> without a type
    // takes the same native-range/numericGenerator path as an explicit type="int" (integer literals only -
    // an untyped date or float bound keeps the existing behavior).
    if (mappedType == null && !attrs.containsKey("type")
        && (attrs.containsKey("min") || attrs.containsKey("max"))
        && isIntegerLiteralOrAbsent(attrs.get("min")) && isIntegerLiteralOrAbsent(attrs.get("max"))) {
      mappedType = "int";
    }

    // min/max/granularity are native DATAMIMIC <key> attrs now, so they pass through untouched. Only fold
    // into a numeric generator when a non-random distribution must ride along (native range has no
    // 'distribution' arg, so IntegerGenerator(distribution=...) is the only way to carry it).
    String distribution = attrs.get("distribution");
    boolean foldDistribution = distribution != null
        && VocabularyMap.KNOWN_DISTRIBUTIONS.contains(distribution) && !distribution.equals("random");
    boolean numericRange = foldDistribution
        && (attrs.containsKey("min") || attrs.containsKey("max"))
        && mappedType != null && (VocabularyMap.INTEGER_TYPES.contains(mappedType)
        || VocabularyMap.FLOAT_TYPES.contains(mappedType))
        && !attrs.containsKey("generator");
    if (numericRange) {
      out.setAttribute("type", mappedType);
      out.setAttribute("generator", numericGenerator(mappedType, attrs));
    } else if (mappedType != null) {
      out.setAttribute("type", mappedType);
    }

    // A composite generator on a <variable> becomes entity="X". Computed up front because it changes
    // how 'dataset' is handled: on an entity it is a native modifier attribute, not a constructor arg.
    String entityName = tag.equals("variable") && attrs.containsKey("generator")
        ? VocabularyMap.GENERATOR_TO_ENTITY.get(ExpressionMapper.beneratorGeneratorClass(attrs.get("generator"))) : null;

    // A dataset-aware generator (AddressGenerator, ...) takes the dataset as a constructor arg; DATAMIMIC
    // <key> has no 'dataset' attribute, so fold it into the generator call instead of keeping it.
    boolean foldDataset = attrs.containsKey("generator") && attrs.containsKey("dataset") && entityName == null;

    for (Map.Entry<String, String> a : attrs.entrySet()) {
      String key = a.getKey();
      String val = a.getValue();
      if (key.equals("type")) {
        continue; // already handled
      }
      if (numericRange && (key.equals("min") || key.equals("max") || key.equals("granularity")
          || key.equals("distribution"))) {
        continue; // folded into the generator
      }
      if (key.equals("dataset") && foldDataset) {
        continue; // folded into the generator call below
      }
      switch (key) {
        case "generator":
          if (entityName != null) {
            // Benerator composite generator on a <variable> -> DATAMIMIC entity; script field access is
            // resolved camelCase->snake_case by DATAMIMIC, so <key script="x.givenName"> passes through.
            convertCompositeGenerator(val, entityName, out, path);
          } else {
            out.setAttribute("generator", foldDataset
                ? ExpressionMapper.foldDatasetIntoGenerator(expressions.mapGenerator(val, path), attrs.get("dataset"))
                : expressions.mapGenerator(val, path));
          }
          break;
        case "distribution":
          if (tag.equals("variable") || tag.equals("part")) {
            out.setAttribute("distribution", val); // source distribution is native there
          } else {
            report.add(path, "attribute", "'distribution' on <" + tag + "> needs a numeric generator or source - dropped");
          }
          break;
        case "script":
          out.setAttribute("script",
              rewriteOuterScopeRefs(ExpressionMapper.rewriteScript(val, enclosingScopeName(src)), src));
          break;
        case "selector":
          out.setAttribute("selector", ExpressionMapper.selectorToInterpolated(val, enclosingScopeName(src)));
          break;
        case "converter":
          out.setAttribute("converter", expressions.mapConverter(val, path));
          break;
        case "nullable":
          // DATAMIMIC fields are non-null by default, so nullable="false" needs nothing; nullable="true"
          // needs an explicit nullQuota to actually emit nulls.
          if (!"false".equals(val)) {
            report.info(path, "attribute", "nullable=\"true\" -> add nullQuota to emit nulls (DATAMIMIC defaults to non-null)");
          }
          break;
        default:
          if (key.equals("cyclic") && !tag.equals("variable")) {
            // DATAMIMIC's cyclic lives on <variable>/<generate>/<reference>, not on <key>.
            report.add(path, "attribute", "'cyclic' on <" + tag + "> is not supported by DATAMIMIC <key> - "
                + "use a <variable source ... cyclic> + <key script> instead");
          } else if (VocabularyMap.FIELD_ATTR_KEEP.contains(key)) {
            out.setAttribute(key, val);
          } else {
            report.add(path, "attribute", "<" + tag + "> '" + key + "' not mapped - dropped");
          }
          break;
      }
    }
  }

  /**
   * Composite generator on a {@code <variable>} -&gt; {@code entity="X"}, porting Benerator's brace args
   * ({@code new PersonGenerator{minAgeYears='21', dataset='DE'}}) to DATAMIMIC entity modifiers:
   * <ul>
   *   <li>args with a dedicated attribute (age/dataset/locale) become {@code ageMin="21" dataset="DE"},</li>
   *   <li>constructor-only args (the quotas) switch the whole call to constructor form,
   *       {@code entity="Person(min_age=21, female_quota=0.5)"} - simpler than mixing both styles,</li>
   *   <li>unknown args are flagged individually; everything mappable still converts.</li>
   * </ul>
   */
  private void convertCompositeGenerator(String spec, String entity, Element out, String path) {
    String expr = spec.startsWith("new ") ? spec.substring(4).trim() : spec.trim();
    int call = ArgSplitter.callStart(expr);
    // Args between the opening ( or { and its trailing close; quote-aware split, no indexOf heuristics.
    String body = call < 0 ? "" : expr.substring(call + 1, expr.length() - (expr.endsWith(")") || expr.endsWith("}") ? 1 : 0));

    Map<String, String> args = new LinkedHashMap<>(); // arg name -> unquoted value, source order kept
    for (String part : ArgSplitter.splitTopLevel(body)) {
      int eq = part.indexOf('=');
      String key = eq > 0 ? part.substring(0, eq).trim() : "";
      if (VocabularyMap.ENTITY_ARG_TO_ATTR.containsKey(key) || VocabularyMap.ENTITY_ARG_TO_CTOR_PARAM.containsKey(key)) {
        args.put(key, ExpressionMapper.unquote(part.substring(eq + 1).trim()));
      } else {
        report.add(path, "generator", "generator '" + ExpressionMapper.beneratorGeneratorClass(spec) + "' arg '" + part
            + "' has no entity modifier equivalent - port manually");
      }
    }

    boolean ctorForm = args.keySet().stream().anyMatch(k -> !VocabularyMap.ENTITY_ARG_TO_ATTR.containsKey(k));
    StringBuilder ctor = new StringBuilder();
    for (Map.Entry<String, String> arg : args.entrySet()) {
      String ctorParam = VocabularyMap.ENTITY_ARG_TO_CTOR_PARAM.get(arg.getKey());
      if (ctorForm && ctorParam != null) {
        ctor.append(ctor.length() > 0 ? ", " : "").append(ctorParam).append("=").append(ExpressionMapper.ctorValue(arg.getValue()));
      } else {
        out.setAttribute(VocabularyMap.ENTITY_ARG_TO_ATTR.get(arg.getKey()), arg.getValue());
      }
    }
    String entityExpr = ctorForm ? entity + "(" + ctor + ")" : entity;
    out.setAttribute("entity", entityExpr);
    report.info(path, "generator", "<variable generator='" + ExpressionMapper.beneratorGeneratorClass(spec)
        + "'> -> entity='" + entityExpr + "'");
  }

  /**
   * A bare scalar {@code <attribute generator="CountryGenerator">} (Benerator emits the ISO code) -&gt;
   * {@code <variable name="_<name>_country" entity="Country"/>} + {@code <key name script="..._country.iso_code"/>}
   * (iso_code verified against CE's Country schema in country_service.py). Only fires for the plain
   * corpus shape (name/type/generator[/dataset], direct child of a generate/iterate, no generator args) -
   * anything richer keeps the honest flag. Returns null when not applicable.
   */
  private Node countryGeneratorToEntityFragment(Document out, Element src, String genClass, String path) {
    // Only the bare CountryGenerator (no ctor args) maps cleanly; CountryGenerator(dataset=..) keeps flagging.
    if (!genClass.equals("CountryGenerator") || ArgSplitter.callStart(src.getAttribute("generator")) >= 0) {
      return null;
    }
    if (!isGenerateOrIterate(src.getParentNode())) {
      return null; // a <variable> is only valid directly under <generate>/<iterate>
    }
    Map<String, String> attrs = attributes(src);
    for (String key : attrs.keySet()) {
      if (!key.equals("name") && !key.equals("type") && !key.equals("generator") && !key.equals("dataset")) {
        return null; // unexpected extra attribute: keep the existing flagging instead of guessing
      }
    }
    String name = attrs.get("name");
    Element variable = out.createElement("variable");
    variable.setAttribute("name", "_" + name + "_country");
    variable.setAttribute("entity", "Country");
    if (attrs.containsKey("dataset")) {
      variable.setAttribute("dataset", attrs.get("dataset"));
    }
    Element key = out.createElement("key");
    key.setAttribute("name", name);
    key.setAttribute("script", "_" + name + "_country.iso_code");
    report.info(path, "generator", "<" + local(src) + " generator='CountryGenerator'> -> <variable entity='Country'>"
        + " + <key script='_" + name + "_country.iso_code'> (ISO code, as in Benerator)");
    return DomUtil.fragmentOf(out, variable, key);
  }

  /**
   * True when the nearest enclosing {@code <generate>}/{@code <iterate>} consumes to a {@code <mongodb id>}
   * store - via the consumer attribute or a nested {@code <consumer>} element, bare ({@code mongo}) or in
   * CRUD form ({@code mongo.inserter('coll')}), possibly in a comma-separated combination.
   */
  private boolean enclosingTargetsMongoStore(Element field) {
    Node p = field.getParentNode();
    while (p instanceof Element && !isGenerateOrIterate(p)) {
      p = p.getParentNode();
    }
    if (!(p instanceof Element)) {
      return false;
    }
    Element gen = (Element) p;
    java.util.List<String> specs = new java.util.ArrayList<>();
    if (gen.hasAttribute("consumer")) {
      specs.addAll(ArgSplitter.splitTopLevel(gen.getAttribute("consumer")));
    }
    for (Node c = gen.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.ELEMENT_NODE && local((Element) c).equals("consumer")) {
        Element cons = (Element) c;
        specs.add(cons.hasAttribute("ref") ? cons.getAttribute("ref") : cons.getAttribute("class"));
      }
    }
    for (String spec : specs) {
      if (mongoStoreIds.contains(spec)) {
        return true;
      }
      java.util.regex.Matcher crud = ConsumerMapper.CRUD_CONSUMER.matcher(spec);
      if (crud.matches() && mongoStoreIds.contains(crud.group(1))) {
        return true;
      }
    }
    return false;
  }

  /** True when the nearest enclosing {@code <generate>}/{@code <iterate>} reads from a {@code source}. */
  private static boolean enclosingHasSource(Element field) {
    Node p = field.getParentNode();
    while (p instanceof Element) {
      if (isGenerateOrIterate(p)) {
        return ((Element) p).hasAttribute("source");
      }
      p = p.getParentNode();
    }
    return false;
  }

  /** Benerator identity ({@code type}, else {@code name}) of the nearest enclosing generate/iterate/nestedKey. */
  private static String enclosingScopeName(Element field) {
    Node p = field.getParentNode();
    while (p instanceof Element) {
      String tag = local(p);
      if (tag.equals("generate") || tag.equals("iterate") || tag.equals("part") || tag.equals("nestedKey")) {
        Element e = (Element) p;
        String type = e.getAttribute("type");
        return !type.isEmpty() ? type : e.getAttribute("name");
      }
      p = p.getParentNode();
    }
    return null;
  }

  private String mapType(String beneratorType, String tag, boolean hasMode, String path) {
    if (beneratorType == null) {
      return null;
    }
    String mapped = VocabularyMap.TYPE.get(beneratorType);
    if (mapped != null) {
      return mapped;
    }
    // Unmapped type (date/object/entity/binary/...). Not a gap when another mode provides the value: a
    // <key type="date" script="person.birthDate"> generates from the script, and DATAMIMIC has no type
    // coercion to lose - so drop the type silently. Only a bare unmapped type (no mode) is a real gap.
    if (!hasMode && !(tag.equals("variable") && "entity".equals(beneratorType))) {
      report.add(path, "type", "type '" + beneratorType + "' has no DATAMIMIC type and no generation mode - "
          + "add min/max, a generator, or a source");
    }
    return null;
  }

  private String numericGenerator(String mappedType, Map<String, String> attrs) {
    boolean integer = VocabularyMap.INTEGER_TYPES.contains(mappedType);
    StringBuilder sb = new StringBuilder(integer ? "IntegerGenerator(" : "FloatGenerator(");
    StringBuilder args = new StringBuilder();
    if (attrs.containsKey("min")) {
      args.append("min=").append(attrs.get("min"));
    }
    if (attrs.containsKey("max")) {
      if (args.length() > 0) {
        args.append(", ");
      }
      args.append("max=").append(attrs.get("max"));
    }
    if (!integer && attrs.containsKey("granularity")) {
      args.append(", granularity=").append(attrs.get("granularity"));
    }
    // Numeric generators take the NumberDistribution ENUM, not a string (a string raises TypeError).
    // Only 'cumulated' exists besides the uniform default.
    if ("cumulated".equals(attrs.get("distribution"))) {
      args.append(", distribution=NumberDistribution.CUMULATED");
    }
    return sb.append(args).append(")").toString();
  }

  /**
   * SQLite has no schemas, so a Benerator {@code schema="PUBLIC"} (from h2/hsqldb) would make DATAMIMIC
   * qualify every table as {@code PUBLIC.<t>} and fail with "no such table: PUBLIC.sqlite_master".
   */
  private void dropSchemaForSqlite(Element out, String dbms, String path) {
    if ("sqlite".equals(dbms) && out.hasAttribute("schema")) {
      out.removeAttribute("schema");
      report.info(path, "database", "dropped schema= for SQLite (it has no schemas)");
    }
  }

  /** True when {@code n} is a Benerator data container - a {@code <generate>} or {@code <iterate>}. */
  private static boolean isGenerateOrIterate(Node n) {
    if (!(n instanceof Element)) {
      return false;
    }
    String tag = local((Element) n);
    return tag.equals("generate") || tag.equals("iterate");
  }

  /** True when {@code path} makes the element a direct child of a {@code <setup>} (e.g. "/setup/if"). */
  private static boolean isSetupChild(String path) {
    int slash = path.lastIndexOf('/');
    return slash > 0 && path.substring(0, slash).endsWith("/setup");
  }

  /**
   * Benerator FTL include path -&gt; DATAMIMIC dynamic-include f-string: {@code {ftl:${database}/x.properties}}
   * becomes {@code {database}/x.properties}. DATAMIMIC resolves {@code {var}} against the setup context at
   * runtime (see dynamic {@code <include>}). A non-FTL uri passes through unchanged.
   */
  private static String ftlUriToFString(String uri) {
    String body = ExpressionMapper.ftlBody(uri);
    return body == null ? uri : ExpressionMapper.ftlToFString(body);
  }

  private void convertDatabaseAttributes(Element src, Element out, String path) {
    Map<String, String> attrs = new LinkedHashMap<>(attributes(src));
    attrs.replaceAll((k, v) -> settings.resolve(v));
    if (!attrs.equals(attributes(src))) {
      report.info(path, "database", "database '" + attrs.get("id")
          + "' placeholders resolved from <setting> defaults / included .properties");
    }
    for (String keep : new String[] {"id", "host", "port", "database", "schema", "environment", "system", "user", "password"}) {
      if (attrs.containsKey(keep)) {
        out.setAttribute(keep, attrs.get(keep));
      }
    }
    // Split an inline JDBC URL into DATAMIMIC host/port/database/dbms (only when not already given).
    EnvironmentMigrator.Coordinates c = EnvironmentMigrator.parseJdbcUrl(attrs.get("url"));
    if (c != null) {
      if (c.host != null && !attrs.containsKey("host")) {
        out.setAttribute("host", c.host);
      }
      if (c.port != null && !attrs.containsKey("port")) {
        out.setAttribute("port", c.port);
      }
      if (c.database != null && !attrs.containsKey("database")) {
        out.setAttribute("database", c.database);
      }
      out.setAttribute("dbms", c.dbms);
      // Benerator ignores conf/environment.env.properties when the connection is inline; DATAMIMIC
      // lets env keys matching '<system|id>.db.*' OVERRIDE the element (verified: CE parser_util
      // fulfill_credentials). A sibling demo's env file (e.g. the shop's mongo env, system 'db')
      // would silently redirect this store - point system at a name no env key can match.
      if (!attrs.containsKey("environment") && !attrs.containsKey("system")) {
        out.setAttribute("system", attrs.get("id") + "_inline");
      }
      dropSchemaForSqlite(out, c.dbms, path);
      return;
    }
    // No parseable URL: derive dbms from the driver/url, else flag for manual attention.
    String dbms = deriveDbms(attrs.get("driver"), attrs.get("url"));
    if (dbms != null) {
      out.setAttribute("dbms", dbms);
      dropSchemaForSqlite(out, dbms, path);
    } else if (attrs.containsKey("environment")) {
      // DATAMIMIC resolves environment=/system= from conf/<environment>.env.properties at runtime
      // (verified: CE parser_util.fulfill_credentials), so nothing needs to be set manually.
      report.info(path, "database", "database '" + attrs.get("id")
          + "' connection resolved from environment '" + attrs.get("environment") + "' at runtime");
    } else {
      report.add(path, "database", "database '" + attrs.get("id") + "' -> set dbms manually (could not derive from driver/url)");
    }
    if (attrs.containsKey("url") && !attrs.containsKey("host")) {
      report.add(path, "database", "database '" + attrs.get("id")
          + "' url '" + attrs.get("url") + "' -> Java-embedded DB (h2/hsqldb): migrate to postgres/sqlite manually");
    }
  }

  /** DATAMIMIC dbms from a Benerator JDBC driver class or url (e.g. jdbc:postgresql://... -&gt; postgresql). */
  private static String deriveDbms(String driver, String url) {
    for (String hay : new String[] {driver, url}) {
      if (hay == null) {
        continue;
      }
      String lower = hay.toLowerCase();
      for (Map.Entry<String, String> e : VocabularyMap.DBMS.entrySet()) {
        if (lower.contains(e.getKey())) {
          return e.getValue();
        }
      }
    }
    return null;
  }

  /**
   * Benerator {@code <setting name value>} / {@code <property name value>} -&gt; DATAMIMIC {@code <variable>}.
   * A {@code {expression}} or a numeric literal becomes {@code script=} (so it evaluates to a number, not
   * the string "1.1"); everything else stays a string {@code constant=}.
   */
  private void convertSettingAttributes(Element src, Element out, String path) {
    Map<String, String> attrs = attributes(src);
    if (attrs.containsKey("name")) {
      out.setAttribute("name", attrs.get("name"));
    }
    String value = attrs.containsKey("value") ? attrs.get("value") : attrs.get("default");
    String ftl = ExpressionMapper.ftlBody(value);
    if (value == null) {
      report.add(path, "attribute", "<" + local(src) + "> without a value (source/ref form) - review");
    } else if (ftl != null) {
      // FTL text templating -> DATAMIMIC's native string= template (${var} -> __var__).
      String template = ExpressionMapper.ftlToStringTemplate(ftl);
      if (template != null) {
        out.setAttribute("string", template);
        report.info(path, "attribute", "FTL template -> <variable string=...> (__var__ substitution)");
      } else {
        report.add(path, "attribute", "<" + local(src) + "> uses an FTL expression/directive - "
            + "rewrite as <variable string=...> or script= manually");
      }
    } else if (value.startsWith("{") && value.endsWith("}")) {
      // a plain {expr} script expression (non-FTL)
      out.setAttribute("script", ExpressionMapper.rewriteScript(value.substring(1, value.length() - 1)));
    } else if (isNumeric(value)) {
      out.setAttribute("script", value); // numeric literal -> evaluated to a number, not a string
    } else {
      out.setAttribute("constant", value);
    }
  }

  /** True when the value is absent or a plain integer literal ({@code 27}, {@code -3}). */
  private static boolean isIntegerLiteralOrAbsent(String s) {
    return s == null || s.matches("[-+]?\\d+");
  }

  /** True for a numeric literal incl. scientific/signed forms ({@code 1e5}, {@code +5}) — parse, don't pattern-match. */
  private static boolean isNumeric(String s) {
    char c = s.isEmpty() ? 0 : s.charAt(0);
    if (!(Character.isDigit(c) || c == '-' || c == '+' || c == '.')) {
      return false; // keeps parseDouble's "NaN"/"Infinity" words out
    }
    try {
      Double.parseDouble(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /** Benerator {@code <while test>} -&gt; DATAMIMIC {@code <while condition>} (children convert normally). */
  private void convertWhileAttributes(Element src, Element out, String path) {
    Map<String, String> attrs = attributes(src);
    String test = attrs.get("test");
    if (test != null) {
      out.setAttribute("condition", test);
    } else {
      report.add(path, "while", "<while> without a test condition - review");
    }
    if (attrs.containsKey("maxIterations")) {
      out.setAttribute("maxIterations", attrs.get("maxIterations"));
    }
  }

  /**
   * Benerator {@code <if test><then>..</then><else>..</else></if>} -&gt; DATAMIMIC
   * {@code <condition><if condition="..">..</if><else>..</else></condition>} (the {@code <then>}
   * wrapper is unwrapped; a bare child of {@code <if>} goes straight into the DATAMIMIC {@code <if>}).
   */
  private Node convertIfNode(Document out, Element src, String path) {
    Element condition = out.createElement("condition");
    Element ifEl = out.createElement("if");
    String test = attributes(src).get("test");
    if (test != null) {
      ifEl.setAttribute("condition", test);
    } else {
      report.add(path, "if", "<if> without a test condition - review");
    }
    condition.appendChild(ifEl);

    Element elseEl = null;
    for (Node c = src.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.COMMENT_NODE) {
        condition.appendChild(out.createComment(c.getNodeValue()));
      } else if (c.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) c;
        String ctag = local(child);
        if (ctag.equals("then")) {
          appendConvertedChildren(out, child, ifEl, path + "/then");
        } else if (ctag.equals("else")) {
          elseEl = out.createElement("else");
          appendConvertedChildren(out, child, elseEl, path + "/else");
        } else {
          Node conv = convertNode(out, child, path + "/" + ctag);
          if (conv != null) {
            ifEl.appendChild(conv);
          }
        }
      }
    }
    if (elseEl != null) {
      condition.appendChild(elseEl);
    }
    return condition;
  }

  private void appendConvertedChildren(Document out, Element parent, Element target, String path) {
    for (Node c = parent.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.ELEMENT_NODE) {
        Node conv = convertNode(out, (Element) c, path + "/" + local((Element) c));
        if (conv != null) {
          target.appendChild(conv);
        }
      } else if (c.getNodeType() == Node.COMMENT_NODE) {
        target.appendChild(out.createComment(c.getNodeValue()));
      }
    }
  }

  /**
   * {@code <execute>} runs a script file (uri) or, in DATAMIMIC, inline code. Benerator sql -&gt; sql and
   * shell -&gt; bash are emitted inline verbatim; js/ftl/ben have no DATAMIMIC language and are flagged.
   */
  private Node convertExecuteNode(Document out, Element src, String path) {
    Map<String, String> attrs = attributes(src);

    // File-based execute: DATAMIMIC infers the language from the uri extension, so a {ftl:${...}}
    // placeholder must be resolved to a concrete path first (else the extension - and the type - is lost).
    if (attrs.containsKey("uri")) {
      Element ex = out.createElement("execute");
      String uri = settings.resolve(attrs.get("uri"));
      ex.setAttribute("uri", uri);
      if (attrs.containsKey("target")) {
        ex.setAttribute("target", attrs.get("target"));
      }
      if (uri.contains("{")) { // still-unresolved placeholder -> the extension/type cannot be inferred
        report.info(path, "execute", "<execute uri='" + attrs.get("uri")
            + "'> has an unresolved placeholder - set type= manually if DATAMIMIC cannot infer it");
      }
      return ex;
    }

    // Inline code: DATAMIMIC supports <execute type="python|bash|sql">code</execute>.
    String benType = attrs.get("type");
    String dmType = benType == null ? null : VocabularyMap.EXECUTE_TYPE.get(benType);
    if (dmType == null && benType == null && attrs.containsKey("target")) {
      dmType = "sql"; // inline <execute target="db"> with no type is SQL against that store in Benerator
    }
    if (dmType != null) {
      Element ex = out.createElement("execute");
      ex.setAttribute("type", dmType);
      if (attrs.containsKey("target")) {
        ex.setAttribute("target", attrs.get("target"));
      }
      String body = src.getTextContent();
      if (dmType.equals("sql") && body.contains("${")) {
        // FTL placeholders in inline SQL -> {var}: DATAMIMIC interpolates {...} f-string style.
        body = ExpressionMapper.ftlToFString(body);
        report.info(path, "execute", "FTL placeholders in inline SQL rewritten to {var} interpolation");
      }
      ex.appendChild(out.createTextNode(body));
      return ex;
    }
    report.add(path, "execute", "inline <execute type='" + benType
        + "'> - DATAMIMIC supports inline python/bash/sql; rewrite this snippet or use a .py file (uri=)");
    return out.createComment(" TODO(datamimic-migration): inline <execute type='" + benType
        + "'> - rewrite as python/bash/sql or move to a .py file, see MIGRATION_PLAYBOOK.md#execute-js ");
  }

  private static void copyAttributes(Element src, Element out, String... names) {
    Map<String, String> attrs = attributes(src);
    for (String n : names) {
      if (attrs.containsKey(n)) {
        out.setAttribute(n, attrs.get(n));
      }
    }
  }

  /** Local attribute map, skipping XML namespace declarations and xsi:* schema hints (see {@link DomUtil}). */
  private static Map<String, String> attributes(Element el) {
    return DomUtil.attributes(el);
  }

  private static String local(Node node) {
    return DomUtil.local(node);
  }
}
