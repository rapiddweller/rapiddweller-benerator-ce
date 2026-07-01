/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import com.rapiddweller.common.xml.XMLUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translates a Benerator XML descriptor into a native DATAMIMIC DSL descriptor by walking the parsed
 * DOM (parsed + XSD-validated by Benerator's {@link XMLUtil}) and rebuilding it under the DATAMIMIC
 * vocabulary. Anything it cannot translate is flagged into a {@link MigrationReport} and left as an
 * XML TODO comment - a partial, honest migration rather than a silently wrong one.
 */
public class DescriptorConverter {

  private final MigrationReport report;

  public DescriptorConverter(MigrationReport report) {
    this.report = report;
  }

  public void convert(File input, File output) throws Exception {
    Document src = XMLUtil.parseWithLocators(input.getAbsolutePath());
    Document out = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element root = src.getDocumentElement();
    Node converted = convertNode(out, root, "/" + local(root));
    if (converted != null) {
      out.appendChild(converted);
    }
    XMLUtil.saveDocument(out, output, "utf-8");
  }

  /** @return the converted node (Element, or a TODO Comment when the source element is unmapped). */
  private Node convertNode(Document out, Element el, String path) {
    String tag = local(el);
    if (VocabularyMap.DROP_ELEMENTS.contains(tag)) {
      report.add(path, "dropped", "<" + tag + "> is not needed in DATAMIMIC (auto-discovered) - removed");
      return null;
    }
    if (tag.equals("consumer")) {
      return null; // a <consumer> element is folded into the parent <generate>'s target (see convertGenerateAttributes)
    }
    if (tag.equals("reference")) {
      return convertReferenceNode(out, el, path); // may become <reference> or <key> (constant/script)
    }
    if (tag.equals("if")) {
      if (isSetupChild(path)) { // DATAMIMIC <condition> is per-<generate>; a setup-level <if><error> is an assertion
        report.add(path, "condition", "setup-level <if>/<error> assertion has no DATAMIMIC equivalent - dropped "
            + "(use <execute type='python'>raise ...</execute> to keep it)");
        return out.createComment(" TODO(datamimic-migration): setup-level <if> assertion dropped - review ");
      }
      return convertIfNode(out, el, path); // <if test><then>/<else> -> <condition><if condition>/<else>
    }
    if (tag.equals("execute")) {
      return convertExecuteNode(out, el, path); // uri-based; inline code is flagged
    }
    if (tag.equals("evaluate")) { // Benerator <evaluate assert="..."> is a post-generation assertion
      report.add(path, "evaluate", "<evaluate assert> has no DATAMIMIC equivalent - dropped "
          + "(use <execute type='sql'> for a side effect, or verify the count in a test)");
      return out.createComment(" TODO(datamimic-migration): <evaluate> assertion dropped - review ");
    }
    String target = VocabularyMap.ELEMENT.get(tag);
    if (target == null) {
      report.add(path, "element", "<" + tag + "> has no DATAMIMIC equivalent - migrate manually");
      return out.createComment(" TODO(datamimic-migration): <" + tag + "> not supported - migrate manually ");
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
        break;
      case "memstore":
        copyAttributes(el, result, "id"); // DATAMIMIC memstore is just an id
        break;
      case "include":
        copyAttributes(el, result, "uri"); // DATAMIMIC include is uri-based
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
        convertFieldAttributes(el, result, tag, path);
        break;
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
        result.appendChild(out.createTextNode(child.getNodeValue()));
      }
    }
    return result;
  }

  private void convertSetupAttributes(Element src, Element out, String path) {
    for (Map.Entry<String, String> a : attributes(src).entrySet()) {
      if (VocabularyMap.SETUP_ATTR_KEEP.contains(a.getKey())) {
        out.setAttribute(a.getKey(), a.getValue());
      } else {
        report.add(path, "attribute", "<setup> '" + a.getKey() + "' has no DATAMIMIC equivalent - dropped");
      }
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
        case "count":
        case "pageSize":
          out.setAttribute(key, val);
          break;
        case "threads":
          out.setAttribute("numProcess", val);
          break;
        case "consumer":
          String tgt = consumerToTarget(val);
          out.setAttribute("target", tgt);
          targetSet = true;
          if (tgt.isEmpty()) {
            report.add(path, "consumer", "consumer '" + val + "' -> configure a DATAMIMIC target/exporter manually");
          }
          break;
        case "source":
        case "selector":
        case "separator":
        case "encoding":
          out.setAttribute(key, val);
          break;
        default:
          report.add(path, "attribute", "<generate> '" + key + "' not mapped - dropped");
          break;
      }
    }
    if (!targetSet) {
      // no consumer= attribute: fold a nested <consumer class="X"> element into target instead
      String childTarget = consumerFromChildElement(src, path);
      if (childTarget != null) {
        out.setAttribute("target", childTarget);
        targetSet = true;
      }
    }
    if (!targetSet) {
      out.setAttribute("target", ""); // DATAMIMIC generate needs a target; empty = capture only
    }
  }

  /** Map a nested {@code <consumer class="pkg.CSVEntityExporter">} to a DATAMIMIC target; null if none. */
  private String consumerFromChildElement(Element generate, String path) {
    for (Node c = generate.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() != Node.ELEMENT_NODE || !local((Element) c).equals("consumer")) {
        continue;
      }
      Element cons = (Element) c;
      String spec = cons.hasAttribute("class") ? cons.getAttribute("class") : cons.getAttribute("ref");
      if (spec.isEmpty()) {
        continue;
      }
      String tgt = consumerToTarget(spec.substring(spec.lastIndexOf('.') + 1)); // FQN -> simple exporter name
      if (tgt.isEmpty()) {
        report.add(path, "consumer", "<consumer class='" + spec + "'> -> configure a DATAMIMIC target manually");
      }
      return tgt;
    }
    return null;
  }

  private void convertFieldAttributes(Element src, Element out, String tag, String path) {
    Map<String, String> attrs = attributes(src);
    String mappedType = mapType(attrs.get("type"), tag, path);

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

    // A dataset-aware generator (AddressGenerator, ...) takes the dataset as a constructor arg; DATAMIMIC
    // <key> has no 'dataset' attribute, so fold it into the generator call instead of keeping it.
    boolean foldDataset = attrs.containsKey("generator") && attrs.containsKey("dataset");

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
          out.setAttribute("generator", foldDataset
              ? foldDatasetIntoGenerator(mapGenerator(val, path), attrs.get("dataset"))
              : mapGenerator(val, path));
          break;
        case "distribution":
          if (tag.equals("variable") || tag.equals("part")) {
            out.setAttribute("distribution", val); // source distribution is native there
          } else {
            report.add(path, "attribute", "'distribution' on <" + tag + "> needs a numeric generator or source - dropped");
          }
          break;
        case "converter":
          out.setAttribute("converter", mapConverter(val, path));
          break;
        default:
          if (VocabularyMap.FIELD_ATTR_KEEP.contains(key)) {
            out.setAttribute(key, val);
          } else {
            report.add(path, "attribute", "<" + tag + "> '" + key + "' not mapped - dropped");
          }
          break;
      }
    }
  }

  /** Add {@code dataset='X'} to a generator string: {@code AddressGenerator} -&gt; {@code AddressGenerator(dataset='X')}. */
  private static String foldDatasetIntoGenerator(String generator, String dataset) {
    String arg = "dataset='" + dataset + "'";
    if (!generator.contains("(")) {
      return generator + "(" + arg + ")";
    }
    int close = generator.lastIndexOf(')');
    String inner = generator.substring(generator.indexOf('(') + 1, close).trim();
    return generator.substring(0, generator.indexOf('(') + 1) + (inner.isEmpty() ? arg : inner + ", " + arg) + ")";
  }

  private String mapType(String beneratorType, String tag, String path) {
    if (beneratorType == null) {
      return null;
    }
    String mapped = VocabularyMap.TYPE.get(beneratorType);
    if (mapped == null) {
      // e.g. entity/date/timestamp/binary/object - a source-backed variable simply drops it.
      if (!(tag.equals("variable") && "entity".equals(beneratorType))) {
        report.add(path, "type", "type '" + beneratorType + "' has no DATAMIMIC type - dropped");
      }
      return null;
    }
    return mapped;
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
    String dist = attrs.get("distribution");
    if (dist != null && VocabularyMap.KNOWN_DISTRIBUTIONS.contains(dist) && !dist.equals("random")) {
      args.append(", distribution='").append(dist).append("'");
    }
    return sb.append(args).append(")").toString();
  }

  private String mapGenerator(String name, String path) {
    // Strip Benerator's "new " instantiation prefix -> DATAMIMIC evaluates Class(args) directly.
    String expr = name.startsWith("new ") ? name.substring(4).trim() : name.trim();
    if (expr.contains("{")) { // Benerator's PersonGenerator{k='v'} property-brace form has no direct equivalent
      report.add(path, "generator", "generator '" + name + "' uses Benerator {k=v} syntax - rewrite as Class(k=v) manually");
      return expr;
    }
    int paren = expr.indexOf('(');
    String cls = (paren >= 0 ? expr.substring(0, paren) : expr).trim();
    String args = paren >= 0 ? expr.substring(paren) : "";
    if (cls.equals("RandomDoubleGenerator") || cls.equals("RandomFloatGenerator")) {
      return randomDoubleToFloat(args, path);
    }
    String mapped = VocabularyMap.GENERATOR_RENAME.getOrDefault(cls, cls);
    if (!VocabularyMap.KNOWN_GENERATORS.contains(mapped)) {
      report.add(path, "generator", "generator '" + name + "' not known to DATAMIMIC - verify/replace manually");
    }
    return mapped + args;
  }

  /** True when {@code path} makes the element a direct child of a {@code <setup>} (e.g. "/setup/if"). */
  private static boolean isSetupChild(String path) {
    int slash = path.lastIndexOf('/');
    return slash > 0 && path.substring(0, slash).endsWith("/setup");
  }

  /** Benerator converter -&gt; DATAMIMIC: strip "new ", rename (CaseConverter -&gt; UpperCase), flag the unknown. */
  private String mapConverter(String value, String path) {
    String expr = value.startsWith("new ") ? value.substring(4).trim() : value.trim();
    int paren = expr.indexOf('(');
    String cls = (paren >= 0 ? expr.substring(0, paren) : expr).trim();
    String args = paren >= 0 ? expr.substring(paren) : "";
    String mapped = VocabularyMap.CONVERTER_RENAME.getOrDefault(cls, cls);
    if (!VocabularyMap.KNOWN_CONVERTERS.contains(mapped)) {
      report.add(path, "converter", "converter '" + value + "' not known to DATAMIMIC - verify/replace manually");
    }
    return mapped + args;
  }

  /** {@code new RandomDoubleGenerator(min, max, decimals)} -&gt; {@code FloatGenerator(min=, max=, granularity=)}. */
  private String randomDoubleToFloat(String args, String path) {
    String[] parts = args.replaceAll("^\\(|\\)$", "").split(",");
    if (parts.length >= 2) {
      String g = "FloatGenerator(min=" + parts[0].trim() + ", max=" + parts[1].trim();
      if (parts.length >= 3) {
        g += ", granularity=1e-" + parts[2].trim();
      }
      return g + ")";
    }
    report.add(path, "generator", "RandomDoubleGenerator args '" + args + "' - map to FloatGenerator manually");
    return "FloatGenerator" + args;
  }

  /**
   * A Benerator {@code <reference>} is used several ways: an FK by {@code targetType}, or a
   * constant/script value. Only the FK maps to a DATAMIMIC {@code <reference>} (table + column);
   * constant/script become a {@code <key>}; a selector-only reference is flagged for manual work.
   */
  private Node convertReferenceNode(Document out, Element src, String path) {
    Map<String, String> attrs = attributes(src);
    String name = attrs.get("name");

    if (attrs.containsKey("constant") || (attrs.containsKey("script") && !attrs.containsKey("targetType"))) {
      Element key = out.createElement("key");
      if (name != null) {
        key.setAttribute("name", name);
      }
      if (attrs.containsKey("constant")) {
        key.setAttribute("constant", attrs.get("constant"));
      }
      if (attrs.containsKey("script")) {
        key.setAttribute("script", attrs.get("script"));
      }
      report.add(path, "reference", "reference '" + name + "' is a constant/script value -> emitted as <key>");
      return key;
    }

    if (!attrs.containsKey("targetType")) {
      report.add(path, "reference",
          "reference '" + name + "' has no targetType -> migrate manually (DATAMIMIC references a table/column)");
      return out.createComment(
          " TODO(datamimic-migration): <reference name=\"" + name + "\"> needs a table/column - migrate manually ");
    }

    Element ref = out.createElement("reference");
    if (name != null) {
      ref.setAttribute("name", name);
    }
    if (attrs.containsKey("source")) {
      ref.setAttribute("source", attrs.get("source"));
    }
    ref.setAttribute("sourceType", attrs.get("targetType"));
    ref.setAttribute("sourceKey", "id"); // Benerator infers the FK column; DATAMIMIC needs it explicit
    report.add(path, "reference", "reference '" + name + "' -> defaulted sourceKey=\"id\"; verify the FK column");
    if ("true".equals(attrs.get("unique"))) {
      ref.setAttribute("unique", "true");
    }
    for (String drop : new String[] {"selector", "distribution", "cyclic", "type", "nullQuota", "mode", "offset"}) {
      if (attrs.containsKey(drop)) {
        report.add(path, "reference", "reference '" + name + "' '" + drop + "' not supported by DATAMIMIC reference - dropped");
      }
    }
    return ref;
  }

  private void convertDatabaseAttributes(Element src, Element out, String path) {
    Map<String, String> attrs = attributes(src);
    for (String keep : new String[] {"id", "host", "port", "database", "schema", "environment", "user", "password"}) {
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
      return;
    }
    // No parseable URL: derive dbms from the driver/url, else flag for manual attention.
    String dbms = deriveDbms(attrs.get("driver"), attrs.get("url"));
    if (dbms != null) {
      out.setAttribute("dbms", dbms);
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
    if (value == null) {
      report.add(path, "attribute", "<" + local(src) + "> without a value (source/ref form) - review");
    } else if (value.startsWith("{") && value.endsWith("}")) {
      out.setAttribute("script", value.substring(1, value.length() - 1));
    } else if (value.matches("-?\\d+(\\.\\d+)?")) {
      out.setAttribute("script", value); // numeric literal -> evaluated to a number, not a string
    } else {
      out.setAttribute("constant", value);
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

    // File-based execute: DATAMIMIC infers the language from the uri extension.
    if (attrs.containsKey("uri")) {
      Element ex = out.createElement("execute");
      ex.setAttribute("uri", attrs.get("uri"));
      if (attrs.containsKey("target")) {
        ex.setAttribute("target", attrs.get("target"));
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
      ex.appendChild(out.createTextNode(src.getTextContent()));
      return ex;
    }
    report.add(path, "execute", "inline <execute type='" + benType
        + "'> - DATAMIMIC supports inline python/bash/sql; rewrite this snippet or use a .py file (uri=)");
    return out.createComment(" TODO(datamimic-migration): inline <execute type='" + benType
        + "'> - rewrite as python/bash/sql or move to a .py file ");
  }

  /**
   * Benerator consumer -&gt; DATAMIMIC target: a known exporter maps by name (ConsoleExporter, CSV, ...),
   * a bare store/db id ("db", "mem") passes through, and comma-separated consumers map element-wise.
   * Unmappable entries (a bean id, a {@code db.updater()} expression) drop out; the caller flags an empty result.
   */
  private static String consumerToTarget(String consumer) {
    java.util.List<String> targets = new java.util.ArrayList<>();
    for (String raw : consumer.split(",")) {
      String c = raw.trim();
      if (c.isEmpty()) {
        continue;
      }
      String mapped = VocabularyMap.CONSUMER_TARGET.get(c);
      if (mapped != null) {
        if (!mapped.isEmpty()) {
          targets.add(mapped);
        }
      } else if (c.matches("[A-Za-z_][A-Za-z0-9_]*") && !c.endsWith("Exporter") && !c.endsWith("Consumer")) {
        targets.add(c); // a bare store/db id
      }
    }
    return String.join(",", targets);
  }

  private static void copyAttributes(Element src, Element out, String... names) {
    Map<String, String> attrs = attributes(src);
    for (String n : names) {
      if (attrs.containsKey(n)) {
        out.setAttribute(n, attrs.get(n));
      }
    }
  }

  /** Local attribute map, skipping XML namespace declarations and xsi:* schema hints. */
  private static Map<String, String> attributes(Element el) {
    Map<String, String> map = new LinkedHashMap<>();
    NamedNodeMap attrs = el.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Attr a = (Attr) attrs.item(i);
      String prefix = a.getPrefix();
      String name = a.getLocalName() != null ? a.getLocalName() : a.getName();
      String ns = a.getNamespaceURI();
      if ("xmlns".equals(prefix) || "xmlns".equals(name)
          || (ns != null && (ns.contains("XMLSchema-instance") || ns.contains("/2000/xmlns/")))) {
        continue;
      }
      map.put(name, a.getValue());
    }
    return map;
  }

  private static String local(Node node) {
    return node.getLocalName() != null ? node.getLocalName() : node.getNodeName();
  }
}
