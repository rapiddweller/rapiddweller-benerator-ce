/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects setup-time values ({@code <setting>} defaults + included {@code .properties} files) and
 * resolves {@code {var}} / {@code {ftl:...${var}...}} placeholders against them, so descriptor
 * attributes like {@code <database url="{dbUrl}">} become concrete without a runtime.
 */
class SettingsResolver {

  /** Setup-time values collected from {@code <setting>} defaults and included {@code .properties} files,
   *  so {@code {dbUrl}}/{@code {ftl:${var}}} placeholders resolve to concrete connection values. */
  private final Map<String, String> settings = new LinkedHashMap<>();
  private final File sourceDir;

  SettingsResolver(File sourceDir) {
    this.sourceDir = sourceDir;
  }

  /**
   * Collect setup-time values in document order, the way Benerator would see them at startup:
   * {@code <setting name value|default>} entries, then any included {@code .properties} file (whose uri
   * may itself use the settings, e.g. {@code {ftl:conf/${stage}.properties}}) overriding the defaults.
   * This makes {@code <database url="{dbUrl}">} resolvable without a runtime.
   */
  void scanSettings(Element el) {
    String tag = DomUtil.local(el);
    if ((tag.equals("setting") || tag.equals("property")) && el.hasAttribute("name")) {
      String value = el.hasAttribute("value") ? el.getAttribute("value") : el.getAttribute("default");
      if (!value.isEmpty() || el.hasAttribute("value")) {
        settings.put(el.getAttribute("name"), value);
      }
    } else if (tag.equals("include") && el.hasAttribute("uri")) {
      String uri = resolve(el.getAttribute("uri"));
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
  String resolve(String value) {
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
}
