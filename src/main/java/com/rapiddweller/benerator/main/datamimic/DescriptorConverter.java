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
          out.setAttribute("target", "");
          targetSet = true;
          report.add(path, "consumer", "consumer '" + val + "' -> configure a DATAMIMIC target/exporter manually");
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
      out.setAttribute("target", ""); // DATAMIMIC generate needs a target; empty = capture only
    }
  }

  private void convertFieldAttributes(Element src, Element out, String tag, String path) {
    Map<String, String> attrs = attributes(src);
    String mappedType = mapType(attrs.get("type"), tag, path);

    // Benerator min/max/granularity have no native DATAMIMIC field attrs -> fold into a numeric generator.
    boolean numericRange = (attrs.containsKey("min") || attrs.containsKey("max"))
        && mappedType != null && (VocabularyMap.INTEGER_TYPES.contains(mappedType)
        || VocabularyMap.FLOAT_TYPES.contains(mappedType))
        && !attrs.containsKey("generator");
    if (numericRange) {
      out.setAttribute("type", mappedType);
      out.setAttribute("generator", numericGenerator(mappedType, attrs));
    } else if (mappedType != null) {
      out.setAttribute("type", mappedType);
    }

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
      switch (key) {
        case "generator":
          out.setAttribute("generator", mapGenerator(val, path));
          break;
        case "distribution":
          if (tag.equals("variable") || tag.equals("part")) {
            out.setAttribute("distribution", val); // source distribution is native there
          } else {
            report.add(path, "attribute", "'distribution' on <" + tag + "> needs a numeric generator or source - dropped");
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
    String renamed = VocabularyMap.GENERATOR_RENAME.get(name);
    if (renamed != null) {
      return renamed;
    }
    // constructor form like "new EANGenerator(true)" or "IncrementGenerator" - check the leading identifier.
    String simple = name.startsWith("new ") ? name.substring(4).trim() : name;
    int paren = simple.indexOf('(');
    String cls = paren >= 0 ? simple.substring(0, paren).trim() : simple.trim();
    if (!VocabularyMap.KNOWN_GENERATORS.contains(cls)) {
      report.add(path, "generator", "generator '" + name + "' not known to DATAMIMIC - verify/replace manually");
    }
    return name;
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
