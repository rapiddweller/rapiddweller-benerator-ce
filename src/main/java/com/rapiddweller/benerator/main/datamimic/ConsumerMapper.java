/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps Benerator consumers (exporter names, store ids, CRUD expressions, exporter beans, nested
 * {@code <consumer>} elements) to DATAMIMIC {@code target} values.
 */
class ConsumerMapper {

  /** A Benerator CRUD consumer expression: {@code db.updater()}, {@code mongo.inserter('coll')}, ... */
  static final java.util.regex.Pattern CRUD_CONSUMER = java.util.regex.Pattern.compile(
      "([A-Za-z_][A-Za-z0-9_]*)\\.(" + String.join("|", VocabularyMap.CRUD_CONSUMER_OP.keySet()) + ")\\(.*\\)");

  private final MigrationReport report;
  /** {@code <bean id="xml" class="...XMLEntityExporter">} -&gt; DATAMIMIC target ("XML"), so consumer="xml" resolves. */
  private final Map<String, String> beanExporters = new LinkedHashMap<>();

  ConsumerMapper(MigrationReport report) {
    this.report = report;
  }

  /** Record an exporter bean id -&gt; DATAMIMIC target mapping, so consumer="id" resolves to it. */
  void registerExporter(String id, String target) {
    beanExporters.put(id, target);
  }

  /** The DATAMIMIC target a {@code <bean id>} exporter maps to, or null when the id is no exporter bean. */
  String exporterTarget(String id) {
    return beanExporters.get(id);
  }

  /**
   * Benerator consumer -&gt; DATAMIMIC target: a known exporter maps by name (ConsoleExporter, CSV, ...),
   * a bare store/db id ("db", "mem") passes through, a CRUD expression maps to DATAMIMIC's CRUD target
   * suffix ({@code db.updater()} -&gt; {@code db.update}, {@code inserter} -&gt; the plain store = insert),
   * and comma-separated consumers map element-wise. Unmappable entries (a bean id) drop out; the caller
   * flags an empty result.
   */
  String consumerToTarget(String consumer) {
    java.util.List<String> targets = new java.util.ArrayList<>();
    for (String c : ArgSplitter.splitTopLevel(consumer)) {
      if (c.equals("Auto")) {
        // Benerator's Auto consumer picks an exporter from context - no DATAMIMIC equivalent;
        // skipping it makes the caller's empty-target flag fire (configure a target manually).
        continue;
      }
      String mapped = VocabularyMap.CONSUMER_TARGET.get(c);
      if (mapped == null) {
        mapped = beanExporters.get(c); // consumer="xml" where <bean id="xml"> is an exporter
      }
      if (mapped == null && (c.startsWith("new ") || c.endsWith(")"))) {
        // inline exporter: consumer="new XLSEntityExporter('out.xlsx')" -> strip "new "/args to the class name
        String simple = ExpressionMapper.beneratorGeneratorClass(c);
        mapped = VocabularyMap.CONSUMER_TARGET.get(simple.substring(simple.lastIndexOf('.') + 1));
      }
      java.util.regex.Matcher crud = CRUD_CONSUMER.matcher(c);
      if (mapped != null) {
        if (!mapped.isEmpty()) {
          targets.add(mapped);
        }
      } else if (crud.matches()) {
        String store = crud.group(1);
        // updater -> update, deleter -> delete, upserter -> upsert; inserter is DATAMIMIC's default (plain store)
        String op = VocabularyMap.CRUD_CONSUMER_OP.get(crud.group(2));
        targets.add(op.isEmpty() ? store : store + "." + op);
      } else if (c.matches("[A-Za-z_][A-Za-z0-9_]*") && !c.endsWith("Exporter") && !c.endsWith("Consumer")) {
        targets.add(c); // a bare store/db id
      }
    }
    return String.join(",", targets);
  }

  /** The value of a {@code <property name="X" value="Y"/>} child of a {@code <consumer>}, or null. */
  private static String consumerProperty(Element consumer, String name) {
    for (Node c = consumer.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.ELEMENT_NODE && DomUtil.local((Element) c).equals("property")) {
        Element p = (Element) c;
        if (name.equals(p.getAttribute("name")) && p.hasAttribute("value")) {
          return p.getAttribute("value");
        }
      }
    }
    return null;
  }

  /** Map a nested {@code <consumer class="pkg.CSVEntityExporter">} to a DATAMIMIC target; null if none. */
  String consumerFromChildElement(Element generate, String path) {
    for (Node c = generate.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() != Node.ELEMENT_NODE || !DomUtil.local((Element) c).equals("consumer")) {
        continue;
      }
      Element cons = (Element) c;
      String spec = cons.hasAttribute("class") ? cons.getAttribute("class") : cons.getAttribute("ref");
      if (spec.isEmpty()) {
        continue;
      }
      String simple = spec.substring(spec.lastIndexOf('.') + 1);
      // FixedWidthEntityExporter needs the column spec, which DATAMIMIC carries IN the target:
      // target="FixedWidth(columns='id[8r0],name[30]')" (the exporter cannot be self-describing).
      if (simple.equals("FixedWidthEntityExporter")) {
        String columns = consumerProperty(cons, "columns");
        if (columns != null) {
          return "FixedWidth(columns='" + columns + "')";
        }
        report.add(path, "consumer", "<consumer class='" + spec + "'> has no 'columns' - set the fixed-width spec manually");
        return "";
      }
      String tgt = consumerToTarget(simple); // FQN -> simple exporter name
      if (tgt.isEmpty()) {
        if (isNoConsumerOnly(spec.substring(spec.lastIndexOf('.') + 1))) {
          report.info(path, "consumer", "consumer 'NoConsumer' -> empty target (capture only)");
        } else {
          report.add(path, "consumer", "<consumer class='" + spec + "'> -> configure a DATAMIMIC target manually");
        }
      }
      return tgt;
    }
    return null;
  }

  /** True when every comma-separated consumer entry is exactly {@code NoConsumer} (no output on purpose). */
  static boolean isNoConsumerOnly(String consumer) {
    java.util.List<String> parts = ArgSplitter.splitTopLevel(consumer);
    if (parts.isEmpty()) {
      return false;
    }
    for (String p : parts) {
      if (!p.equals("NoConsumer")) {
        return false;
      }
    }
    return true;
  }
}
