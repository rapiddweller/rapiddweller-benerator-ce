/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;
import java.util.Map;

/** Shared DOM helpers for the descriptor conversion collaborators. */
final class DomUtil {

  private DomUtil() {
  }

  /** Local attribute map, skipping XML namespace declarations and xsi:* schema hints. */
  static Map<String, String> attributes(Element el) {
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

  static String local(Node node) {
    return node.getLocalName() != null ? node.getLocalName() : node.getNodeName();
  }
}
