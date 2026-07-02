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
  /** {@code <bean id="X" spec="new Generator(...)">} definitions, so a {@code generator="X"} reference can
   *  be resolved to the bean's actual generator expression instead of being flagged as unknown. */
  private final Map<String, String> beanSpecs = new LinkedHashMap<>();
  /** Setup-time values collected from {@code <setting>} defaults and included {@code .properties} files,
   *  so {@code {dbUrl}}/{@code {ftl:${var}}} placeholders resolve to concrete connection values. */
  private final Map<String, String> settings = new LinkedHashMap<>();
  private File sourceDir;

  public DescriptorConverter(MigrationReport report) {
    this.report = report;
  }

  public void convert(File input, File output) throws Exception {
    Document src = XMLUtil.parseWithLocators(input.getAbsolutePath());
    Document out = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element root = src.getDocumentElement();
    sourceDir = input.getAbsoluteFile().getParentFile();
    scanBeans(root);
    scanSettings(root);
    Node converted = convertNode(out, root, "/" + local(root));
    if (converted != null) {
      out.appendChild(converted);
    }
    XMLUtil.saveDocument(out, output, "utf-8");
  }

  /** Record every {@code <bean id spec>} so generator references to it can be inlined. */
  private void scanBeans(Element el) {
    if (local(el).equals("bean") && el.hasAttribute("id") && el.hasAttribute("spec")) {
      beanSpecs.put(el.getAttribute("id"), el.getAttribute("spec"));
    }
    for (Node c = el.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.ELEMENT_NODE) {
        scanBeans((Element) c);
      }
    }
  }

  /**
   * Collect setup-time values in document order, the way Benerator would see them at startup:
   * {@code <setting name value|default>} entries, then any included {@code .properties} file (whose uri
   * may itself use the settings, e.g. {@code {ftl:conf/${stage}.properties}}) overriding the defaults.
   * This makes {@code <database url="{dbUrl}">} resolvable without a runtime.
   */
  private void scanSettings(Element el) {
    String tag = local(el);
    if ((tag.equals("setting") || tag.equals("property")) && el.hasAttribute("name")) {
      String value = el.hasAttribute("value") ? el.getAttribute("value") : el.getAttribute("default");
      if (!value.isEmpty() || el.hasAttribute("value")) {
        settings.put(el.getAttribute("name"), value);
      }
    } else if (tag.equals("include") && el.hasAttribute("uri")) {
      String uri = resolvePlaceholders(el.getAttribute("uri"));
      if (uri.endsWith(".properties") && !uri.contains("{")) {
        File f = new File(sourceDir, uri);
        if (f.isFile()) {
          try (java.io.Reader r = new java.io.FileReader(f)) {
            java.util.Properties p = new java.util.Properties();
            p.load(r);
            p.stringPropertyNames().forEach(k -> settings.put(k, p.getProperty(k)));
          } catch (java.io.IOException e) {
            // unreadable include: leave placeholders unresolved, existing flags will fire
          }
        }
      }
    }
    for (Node c = el.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.ELEMENT_NODE) {
        scanSettings((Element) c);
      }
    }
  }

  /**
   * Resolve {@code {var}} and {@code {ftl:...${var}...}} placeholders from the collected settings.
   * Anything unknown stays verbatim, so the existing "set manually" flags still fire.
   */
  private String resolvePlaceholders(String value) {
    if (value == null || value.indexOf('{') < 0) {
      return value;
    }
    String v = value;
    if (v.startsWith("{ftl:") && v.endsWith("}")) {
      v = v.substring(5, v.length() - 1).trim();
    } else if (v.startsWith("{") && v.endsWith("}") && settings.containsKey(v.substring(1, v.length() - 1))) {
      return settings.get(v.substring(1, v.length() - 1));
    }
    java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\$\\{(\\w+)\\}").matcher(v);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      String rep = settings.get(m.group(1));
      if (rep == null) {
        return value; // unresolvable part: keep the original untouched
      }
      m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(rep));
    }
    m.appendTail(sb);
    // A ${complex + expr} the \w+ pattern can't substitute must not leak out half-stripped.
    return sb.indexOf("${") >= 0 ? value : sb.toString();
  }

  /** @return the converted node (Element, or a TODO Comment when the source element is unmapped). */
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
      if (isKnownGeneratorSpec(el.getAttribute("spec"))) {
        report.info(path, "bean", "<bean id='" + el.getAttribute("id") + "'> generator inlined into its references - removed");
        return null;
      }
      report.add(path, "element", "<bean> has no DATAMIMIC equivalent - migrate manually");
      return out.createComment(" TODO(datamimic-migration): <bean> not supported - migrate manually ");
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
        for (Map.Entry<String, String> a : attributes(result).entrySet()) {
          result.setAttribute(a.getKey(), resolvePlaceholders(a.getValue())); // {ftl:${mongoHost}} etc.
        }
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
    // Does another attribute already produce the value? Then an unmapped type= is cosmetic, not a gap.
    boolean hasMode = attrs.containsKey("script") || attrs.containsKey("source") || attrs.containsKey("values")
        || attrs.containsKey("generator") || attrs.containsKey("constant") || attrs.containsKey("pattern");
    String mappedType = mapType(attrs.get("type"), tag, hasMode, path);

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
          String entity = tag.equals("variable") ? VocabularyMap.GENERATOR_TO_ENTITY.get(beneratorGeneratorClass(val)) : null;
          if (entity != null) {
            // Benerator composite generator on a <variable> -> DATAMIMIC entity; script field access is
            // resolved camelCase->snake_case by DATAMIMIC, so <key script="x.givenName"> passes through.
            out.setAttribute("entity", entity);
            report.info(path, "generator", "<variable generator='" + beneratorGeneratorClass(val)
                + "'> -> entity='" + entity + "'");
            if (val.contains("(") || val.contains("{")) {
              report.add(path, "generator", "generator '" + val
                  + "' args -> port to entity modifiers (dataset/locale/ageMin/ageMax) manually");
            }
          } else {
            out.setAttribute("generator", foldDataset
                ? foldDatasetIntoGenerator(mapGenerator(val, path), attrs.get("dataset"))
                : mapGenerator(val, path));
          }
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
        case "nullable":
          // DATAMIMIC fields are non-null by default, so nullable="false" needs nothing; nullable="true"
          // needs an explicit nullQuota to actually emit nulls.
          if (!"false".equals(val)) {
            report.info(path, "attribute", "nullable=\"true\" -> add nullQuota to emit nulls (DATAMIMIC defaults to non-null)");
          }
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
    return ArgSplitter.appendArg(generator, "dataset='" + dataset + "'");
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
    String dist = attrs.get("distribution");
    if (dist != null && VocabularyMap.KNOWN_DISTRIBUTIONS.contains(dist) && !dist.equals("random")) {
      args.append(", distribution='").append(dist).append("'");
    }
    return sb.append(args).append(")").toString();
  }

  /** The bare class name of a Benerator generator ("new PersonGenerator{...}" -&gt; "PersonGenerator"). */
  private static String beneratorGeneratorClass(String generator) {
    String g = generator.startsWith("new ") ? generator.substring(4).trim() : generator.trim();
    int cut = ArgSplitter.callStart(g);
    return (cut >= 0 ? g.substring(0, cut) : g).trim();
  }

  /** True when a bean spec like {@code "new IncrementGenerator(1000)"} names a generator DATAMIMIC knows. */
  private boolean isKnownGeneratorSpec(String spec) {
    if (spec == null || spec.isEmpty()) {
      return false;
    }
    String expr = spec.startsWith("new ") ? spec.substring(4).trim() : spec.trim();
    int paren = ArgSplitter.callStart(expr);
    String cls = (paren >= 0 ? expr.substring(0, paren) : expr).trim();
    if (cls.equals("RandomDoubleGenerator") || cls.equals("RandomFloatGenerator")) {
      return true;
    }
    return VocabularyMap.KNOWN_GENERATORS.contains(VocabularyMap.GENERATOR_RENAME.getOrDefault(cls, cls));
  }

  private String mapGenerator(String name, String path) {
    // Resolve a <bean id="X" spec="..."> reference (generator="X") to the bean's own generator expression.
    String resolved = beanSpecs.getOrDefault(name.trim(), name);
    // Strip Benerator's "new " instantiation prefix -> DATAMIMIC evaluates Class(args) directly.
    String expr = resolved.startsWith("new ") ? resolved.substring(4).trim() : resolved.trim();
    if (expr.contains("{")) { // Benerator's PersonGenerator{k='v'} property-brace form has no direct equivalent
      report.add(path, "generator", "generator '" + name + "' uses Benerator {k=v} syntax - rewrite as Class(k=v) manually");
      return expr;
    }
    int paren = ArgSplitter.callStart(expr);
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
    int paren = ArgSplitter.callStart(expr);
    String cls = (paren >= 0 ? expr.substring(0, paren) : expr).trim();
    String args = paren >= 0 ? expr.substring(paren) : "";
    // Benerator SHA*/MD5 hash converters expand to DATAMIMIC's parameterised Hash(algorithm, format).
    String expansion = VocabularyMap.CONVERTER_EXPANSION.get(cls);
    if (expansion != null) {
      return expansion;
    }
    String mapped = VocabularyMap.CONVERTER_RENAME.getOrDefault(cls, cls);
    if (!VocabularyMap.KNOWN_CONVERTERS.contains(mapped)) {
      report.add(path, "converter", "converter '" + value + "' not known to DATAMIMIC - verify/replace manually");
    }
    return mapped + args;
  }

  /** {@code new RandomDoubleGenerator(min, max, decimals)} -&gt; {@code FloatGenerator(min=, max=, granularity=)}. */
  private String randomDoubleToFloat(String args, String path) {
    java.util.List<String> parts = ArgSplitter.splitTopLevel(args.replaceAll("^\\(|\\)$", ""));
    if (parts.size() >= 2) {
      String g = "FloatGenerator(min=" + parts.get(0) + ", max=" + parts.get(1);
      if (parts.size() >= 3) {
        g += ", granularity=1e-" + parts.get(2);
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
      report.info(path, "reference", "reference '" + name + "' is a constant/script value -> emitted as <key>");
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
    report.info(path, "reference", "reference '" + name + "' -> defaulted sourceKey=\"id\"; verify the FK column");
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
    Map<String, String> attrs = new LinkedHashMap<>(attributes(src));
    attrs.replaceAll((k, v) -> resolvePlaceholders(v));
    if (!attrs.equals(attributes(src))) {
      report.info(path, "database", "database '" + attrs.get("id")
          + "' placeholders resolved from <setting> defaults / included .properties");
    }
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
    } else if (isNumeric(value)) {
      out.setAttribute("script", value); // numeric literal -> evaluated to a number, not a string
    } else {
      out.setAttribute("constant", value);
    }
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
    for (String c : ArgSplitter.splitTopLevel(consumer)) {
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
