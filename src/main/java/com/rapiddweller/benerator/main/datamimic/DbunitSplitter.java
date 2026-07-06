/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Splits a DbUnit flat-XML dataset into per-table rows. In the flat format each child element of
 * {@code <dataset>} is one row: the element name is the table, its attributes are the columns
 * (rows of the same table may carry different columns - "ragged"). Tables are returned in
 * first-appearance order, which is the dependency order the dataset is authored in
 * (referenced tables before referencing ones), so inserts satisfy foreign keys.
 */
final class DbunitSplitter {

  private DbunitSplitter() {
  }

  /** table name -&gt; rows (each row: column -&gt; value), tables and rows in document order. */
  static Map<String, List<Map<String, String>>> split(File dbunitXml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    Document doc = factory.newDocumentBuilder().parse(dbunitXml);
    Element dataset = doc.getDocumentElement();

    Map<String, List<Map<String, String>>> tables = new LinkedHashMap<>();
    for (Node child = dataset.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      Element row = (Element) child;
      tables.computeIfAbsent(DomUtil.local(row), k -> new java.util.ArrayList<>())
          .add(DomUtil.attributes(row));
    }
    return tables;
  }

  /**
   * Write one table's rows as a JSON array of objects (DATAMIMIC reads {@code source="*.json"}).
   * Columns are unified across all rows (first-appearance order): a column absent from a ragged row is
   * emitted as {@code null}, so an RDBMS batch insert sees a uniform column set (SQLAlchemy requires it)
   * and MongoDB simply stores the null.
   */
  static void writeTableJson(List<Map<String, String>> rows, File out) throws Exception {
    java.util.LinkedHashSet<String> columns = new java.util.LinkedHashSet<>();
    for (Map<String, String> row : rows) {
      columns.addAll(row.keySet());
    }
    StringBuilder sb = new StringBuilder("[\n");
    for (int i = 0; i < rows.size(); i++) {
      Map<String, String> row = rows.get(i);
      sb.append("  {");
      int c = 0;
      for (String col : columns) {
        if (c++ > 0) {
          sb.append(", ");
        }
        String value = row.get(col);
        sb.append(jsonString(col)).append(": ").append(value == null ? "null" : jsonString(value));
      }
      sb.append(i < rows.size() - 1 ? "},\n" : "}\n");
    }
    sb.append("]\n");
    Files.write(out.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
  }

  /** A JSON string literal with the mandatory escapes (quote, backslash, control chars). */
  private static String jsonString(String s) {
    StringBuilder sb = new StringBuilder("\"");
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case '"': sb.append("\\\""); break;
        case '\\': sb.append("\\\\"); break;
        case '\n': sb.append("\\n"); break;
        case '\r': sb.append("\\r"); break;
        case '\t': sb.append("\\t"); break;
        default:
          if (ch < 0x20) {
            sb.append(String.format("\\u%04x", (int) ch));
          } else {
            sb.append(ch);
          }
      }
    }
    return sb.append('"').toString();
  }
}
