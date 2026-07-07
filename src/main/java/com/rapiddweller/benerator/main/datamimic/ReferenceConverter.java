/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Converts a Benerator {@code <reference>} into its DATAMIMIC counterpart: an FK {@code <reference>}
 * (targetType), a constant/script {@code <key>}, or a selector-backed {@code <variable>}+{@code <key>} pair.
 */
class ReferenceConverter {

  /** {@code select COL from ...} with exactly one bare identifier in the select-list, case-insensitive. */
  private static final java.util.regex.Pattern SINGLE_COLUMN_SELECT = java.util.regex.Pattern.compile(
      "(?is)\\s*select\\s+([A-Za-z_][A-Za-z0-9_]*)\\s+from\\s+.+");

  private final MigrationReport report;
  /** part entity name -> [collection, dotted document path] and the mongo store ids, set by the
   *  DescriptorConverter's scan pass (empty when the descriptor has no mongo stores). */
  private Map<String, String[]> mongoEntityPaths = java.util.Collections.emptyMap();
  private java.util.Set<String> mongoStoreIds = java.util.Collections.emptySet();
  /** table (lowercased) -> its PRIMARY KEY column, from the executed DDL - the real FK target, used
   *  as sourceKey instead of guessing "id". A live reference to the DescriptorConverter's map, so it
   *  is populated by the DDL scan before any reference is converted. */
  private Map<String, String> ddlPrimaryKey = java.util.Collections.emptyMap();

  ReferenceConverter(MigrationReport report) {
    this.report = report;
  }

  void setMongoContext(Map<String, String[]> entityPaths, java.util.Set<String> storeIds,
      Map<String, String> ddlPrimaryKey) {
    this.mongoEntityPaths = entityPaths;
    this.mongoStoreIds = storeIds;
    this.ddlPrimaryKey = ddlPrimaryKey;
  }

  /**
   * A Benerator {@code <reference>} is used several ways: an FK by {@code targetType}, or a
   * constant/script value. Only the FK maps to a DATAMIMIC {@code <reference>} (table + column);
   * constant/script become a {@code <key>}; a selector-only reference is flagged for manual work.
   */
  Node convertReferenceNode(Document out, Element src, String path) {
    Map<String, String> attrs = DomUtil.attributes(src);
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
        key.setAttribute("script", ExpressionMapper.rewriteScript(attrs.get("script")));
      }
      report.info(path, "reference", "reference '" + name + "' is a constant/script value -> emitted as <key>");
      return key;
    }

    if (!attrs.containsKey("targetType")) {
      // A selector-only reference whose select-list is one bare column maps losslessly: DATAMIMIC's
      // <variable source selector> yields the query's row dicts, and a <key script="var.col"> picks the
      // column (verified against CE's VariableModel: source/selector/cyclic/distribution are native).
      Node selectorFragment = selectorReferenceToVariableFragment(out, attrs, path);
      if (selectorFragment != null) {
        return selectorFragment;
      }
      report.add(path, "reference",
          "reference '" + name + "' has no targetType -> migrate manually (DATAMIMIC references a table/column)");
      return out.createComment(
          " TODO(datamimic-migration): <reference name=\"" + name + "\"> needs a table/column - migrate manually,"
              + " see MIGRATION_PLAYBOOK.md#reference-selector-type ");
    }

    Element ref = out.createElement("reference");
    if (name != null) {
      ref.setAttribute("name", name);
    }
    if (attrs.containsKey("source")) {
      ref.setAttribute("source", attrs.get("source"));
    }
    String targetType = attrs.get("targetType");
    String[] entityPath = mongoStoreIds.contains(attrs.get("source")) ? mongoEntityPaths.get(targetType) : null;
    if (entityPath != null) {
      // The target entity is nested INSIDE a mongo collection document (a converted <part>):
      // read the collection and descend by dotted sourceKey (DATAMIMIC unwinds lists on the way).
      ref.setAttribute("sourceType", entityPath[0]);
      ref.setAttribute("sourceKey", entityPath[1] + ".id");
      report.info(path, "reference", "reference '" + name + "' targets nested '" + targetType
          + "' -> collection '" + entityPath[0] + "', path '" + entityPath[1] + ".id'");
    } else {
      ref.setAttribute("sourceType", targetType);
      // Benerator infers the FK column from DB metadata; DATAMIMIC needs it explicit. Use the target
      // table's real PRIMARY KEY when the DDL was introspected, else fall back to "id".
      String pk = ddlPrimaryKey.get(targetType.toLowerCase());
      if (pk != null) {
        ref.setAttribute("sourceKey", pk);
        report.info(path, "reference", "reference '" + name + "' sourceKey=\"" + pk + "\" (target PK from DDL)");
      } else {
        ref.setAttribute("sourceKey", "id");
        report.info(path, "reference", "reference '" + name + "' -> defaulted sourceKey=\"id\"; verify the FK column");
      }
    }
    if ("true".equals(attrs.get("unique"))) {
      ref.setAttribute("unique", "true");
    }
    // distribution (random/ordered/cumulated) and cyclic are native DATAMIMIC <reference> attributes now.
    // A Benerator distribution expression ('new WeightedNumbers(...)') is still a gap -> flagged.
    String distribution = attrs.get("distribution");
    if (distribution != null) {
      if (VocabularyMap.KNOWN_DISTRIBUTIONS.contains(distribution)) {
        ref.setAttribute("distribution", distribution);
      } else {
        report.add(path, "reference", "reference '" + name + "' distribution '" + distribution
            + "' not supported by DATAMIMIC reference - dropped");
      }
    }
    if (attrs.containsKey("cyclic")) {
      ref.setAttribute("cyclic", attrs.get("cyclic"));
    }
    // The FK column type comes from the referenced source column in DATAMIMIC, so dropping type= loses
    // (almost) nothing - informational, not manual work.
    if (attrs.containsKey("type")) {
      report.info(path, "reference", "reference '" + name + "' 'type' - column type comes from the source - dropped");
    }
    for (String drop : new String[] {"selector", "nullQuota", "mode", "offset"}) {
      if (attrs.containsKey(drop)) {
        report.add(path, "reference", "reference '" + name + "' '" + drop + "' not supported by DATAMIMIC reference - dropped");
      }
    }
    return ref;
  }

  /**
   * {@code <reference name source selector="select COL from ..." [cyclic] [distribution]>} (no targetType)
   * -&gt; {@code <variable name="_ref_<name>" source selector [cyclic] [distribution]/>} +
   * {@code <key name script="_ref_<name>.<COL>"/>}. Only fires when the select-list is exactly one bare
   * column name (no comma/parenthesis/alias/quote) so the script access is unambiguous; anything else
   * returns null and keeps the honest flag.
   */
  private Node selectorReferenceToVariableFragment(Document out, Map<String, String> attrs, String path) {
    String name = attrs.get("name");
    String selector = attrs.get("selector");
    String source = attrs.get("source");
    if (name == null || selector == null || source == null) {
      return null;
    }
    java.util.regex.Matcher m = SINGLE_COLUMN_SELECT.matcher(selector);
    if (!m.matches()) {
      return null;
    }
    String column = m.group(1);
    String varName = "_ref_" + name;
    Element variable = out.createElement("variable");
    variable.setAttribute("name", varName);
    variable.setAttribute("source", source);
    variable.setAttribute("selector", selector);
    if (attrs.containsKey("cyclic")) {
      variable.setAttribute("cyclic", attrs.get("cyclic"));
    }
    String distribution = attrs.get("distribution");
    if (distribution != null) {
      if (VocabularyMap.KNOWN_DISTRIBUTIONS.contains(distribution)) {
        variable.setAttribute("distribution", distribution);
      } else {
        report.add(path, "reference", "reference '" + name + "' distribution '" + distribution
            + "' not supported by DATAMIMIC - dropped");
      }
    }
    Element key = out.createElement("key");
    key.setAttribute("name", name);
    key.setAttribute("script", varName + "." + column);
    if (attrs.containsKey("type")) {
      report.info(path, "reference", "reference '" + name + "' 'type' - column type comes from the source - dropped");
    }
    for (Map.Entry<String, String> a : attrs.entrySet()) {
      String k = a.getKey();
      if (!k.equals("name") && !k.equals("source") && !k.equals("selector") && !k.equals("cyclic")
          && !k.equals("distribution") && !k.equals("type")) {
        report.add(path, "reference", "reference '" + name + "' '" + k
            + "' not supported by DATAMIMIC reference - dropped");
      }
    }
    report.info(path, "reference", "selector reference '" + name + "' -> <variable source/selector> + <key script='"
        + varName + "." + column + "'>");
    return DomUtil.fragmentOf(out, variable, key);
  }
}
