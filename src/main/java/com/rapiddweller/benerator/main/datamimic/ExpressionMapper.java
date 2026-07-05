/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps Benerator generator/converter/script expressions into their DATAMIMIC equivalents:
 * generator renames + argument rewrites (DataFaker, RandomDouble), converter renames/expansions,
 * and Java-script-to-Python rewriting (ternary, enum accessor, scope self-reference).
 */
class ExpressionMapper {

  private final MigrationReport report;
  /** {@code <bean id="X" spec="new Generator(...)">} definitions, so a {@code generator="X"} reference can
   *  be resolved to the bean's actual generator expression instead of being flagged as unknown. */
  private final Map<String, String> beanSpecs = new LinkedHashMap<>();

  ExpressionMapper(MigrationReport report) {
    this.report = report;
  }

  /** Record a {@code <bean id spec>} so generator references to it can be inlined. */
  void registerBeanSpec(String id, String spec) {
    beanSpecs.put(id, spec);
  }

  String mapGenerator(String name, String path) {
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
    if (cls.equals("DataFakerGenerator")) {
      return mapDataFaker(args, path);
    }
    String mapped = VocabularyMap.GENERATOR_RENAME.getOrDefault(cls, cls);
    if (mapped.equals("CompanyNameGenerator") && !args.isEmpty() && !args.equals("()")) {
      // DATAMIMIC's CompanyNameGenerator takes no constructor args (no dataset variants).
      report.info(path, "generator", "CompanyNameGenerator args " + args + " dropped (no DATAMIMIC equivalent)");
      args = "";
    }
    if (mapped.equals("EANGenerator") && args.matches("(?i)\\(\\s*(true|false)\\s*\\)")) {
      // Benerator's boolean arg is the 'unique' flag; DATAMIMIC's EANGenerator(locale) has none -
      // it would swallow the boolean as locale. Random EAN-13 collisions are statistically negligible.
      report.info(path, "generator", "EANGenerator unique flag " + args + " dropped (no DATAMIMIC equivalent)");
      args = "";
    }
    if (!VocabularyMap.KNOWN_GENERATORS.contains(mapped)) {
      report.add(path, "generator", "generator '" + name + "' not known to DATAMIMIC - verify/replace manually");
    }
    return mapped + javaLiteralsToPython(args);
  }

  /** Benerator converter -&gt; DATAMIMIC: strip "new ", rename (CaseConverter -&gt; UpperCase), flag the unknown. */
  String mapConverter(String value, String path) {
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
    if (mapped.equals("Substring")) {
      // Benerator SubstringExtractor(from, 0) with a negative from means "to the end" -> Substring(from).
      java.util.List<String> parts = ArgSplitter.splitTopLevel(args.replaceAll("^\\(|\\)$", ""));
      if (parts.size() == 2 && parts.get(1).equals("0") && parts.get(0).startsWith("-")) {
        args = "(" + parts.get(0) + ")";
      }
    }
    if (!VocabularyMap.KNOWN_CONVERTERS.contains(mapped)) {
      report.add(path, "converter", "converter '" + value + "' not known to DATAMIMIC - verify/replace manually");
    }
    return mapped + javaLiteralsToPython(args);
  }

  /** Java/JS literals -&gt; python: bare {@code true}/{@code false}/{@code null} outside string
   *  literals become {@code True}/{@code False}/{@code None} (DATAMIMIC evaluates args/scripts as python). */
  static String javaLiteralsToPython(String s) {
    StringBuilder sb = new StringBuilder();
    char quote = 0;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (quote != 0) {
        sb.append(ch);
        if (ch == quote) {
          quote = 0;
        }
        continue;
      }
      if (ch == '\'' || ch == '"') {
        quote = ch;
        sb.append(ch);
        continue;
      }
      boolean wordStart = Character.isLetter(ch)
          && (i == 0 || (!Character.isLetterOrDigit(s.charAt(i - 1)) && s.charAt(i - 1) != '_' && s.charAt(i - 1) != '.'));
      if (wordStart) {
        int j = i;
        while (j < s.length() && (Character.isLetterOrDigit(s.charAt(j)) || s.charAt(j) == '_')) {
          j++;
        }
        String word = s.substring(i, j);
        sb.append(word.equals("true") ? "True" : word.equals("false") ? "False" : word.equals("null") ? "None" : word);
        i = j - 1;
        continue;
      }
      sb.append(ch);
    }
    return sb.toString();
  }

  /** The body of an {@code {ftl:...}} value, or null when the value is not FTL-templated. */
  static String ftlBody(String value) {
    if (value != null && value.startsWith("{ftl:") && value.endsWith("}")) {
      return value.substring(5, value.length() - 1);
    }
    return null;
  }

  /**
   * FTL text with only simple {@code ${var}} substitutions -&gt; a DATAMIMIC {@code string=} template
   * ({@code ${count} orders} -&gt; {@code __count__ orders}). Null when the text uses real FTL
   * expressions/directives (arithmetic, builtins, {@code <#if>}), which plain substitution cannot express.
   */
  static String ftlToStringTemplate(String text) {
    if (text.contains("<#")) {
      return null; // FTL directive - no plain-substitution equivalent
    }
    java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\$\\{([^}]*)\\}").matcher(text);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      if (!m.group(1).matches("\\w+")) {
        return null; // ${a + b} etc. - an expression, not a substitution
      }
      m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement("__" + m.group(1) + "__"));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * FTL text -&gt; a DATAMIMIC f-string body: {@code ${expr}} becomes {@code {expr}} (DATAMIMIC's
   * {@code <echo>} and inline SQL evaluate {@code {...}} as python f-string fields).
   */
  static String ftlToFString(String text) {
    return text.replaceAll("\\$\\{([^}]*)\\}", "{$1}");
  }

  /** True for a Benerator DYNAMIC selector ({@code {{...}}}), re-evaluated per generated record. */
  static boolean isDynamicSelector(String value) {
    if (value == null) {
      return false;
    }
    String s = value.trim();
    return s.startsWith("{{") && s.endsWith("}}");
  }

  /**
   * Benerator selector -&gt; DATAMIMIC selector interpolation: the {@code {{...}}}/{@code {ftl:...}}
   * wrappers are stripped and each {@code ${expr}} becomes {@code __expr__} (DATAMIMIC's selector
   * variable syntax, rewritten like a script so a self-reference by the enclosing scope name turns
   * into {@code this}). A plain selector passes through unchanged.
   */
  static String selectorToInterpolated(String value, String enclosingScope) {
    if (value == null || value.isEmpty()) {
      return value;
    }
    String s = value.trim();
    if (s.startsWith("{{") && s.endsWith("}}")) {
      s = s.substring(1, s.length() - 1);
    }
    String ftl = ftlBody(s);
    if (ftl != null) {
      s = ftl;
    }
    java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\$\\{([^}]*)\\}").matcher(s);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(
          "__" + rewriteScript(m.group(1), enclosingScope) + "__"));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * Benerator {@code DataFakerGenerator('provider','method')} names a Faker provider + method; DATAMIMIC's
   * {@code DataFakerGenerator(method, locale='en_US')} calls {@code faker.<method>()} directly (no provider),
   * so drop the provider and keep the method. A single arg is already the method.
   */
  private String mapDataFaker(String args, String path) {
    java.util.List<String> parts = ArgSplitter.splitTopLevel(args.replaceAll("^\\(|\\)$", ""));
    if (parts.isEmpty()) {
      report.add(path, "generator", "DataFakerGenerator with no method arg - specify a Faker method");
      return "DataFakerGenerator" + args;
    }
    // DATAMIMIC calls python faker.<method>() flat - the Benerator provider (first arg) is irrelevant,
    // only the method (last arg) matters, in snake_case.
    String method = unquote(parts.get(parts.size() - 1));
    String mapped = VocabularyMap.FAKER_METHOD_RENAME.get(method);
    if (mapped != null) {
      return "DataFakerGenerator('" + mapped + "')";
    }
    if (VocabularyMap.FAKER_UNAVAILABLE_METHODS.contains(method)) {
      // A Java-datafaker-only provider (massEffect, theExpanse, ...) with no Python Faker equivalent.
      // Fall back to a generic word so the descriptor still RUNS; info (not a manual gap) points at the
      // better fix (a DATAMIMIC entity like Product/MedicalProcedure, or a value list).
      report.info(path, "generator", "DataFakerGenerator('" + method + "') has no Python Faker equivalent"
          + " -> using faker.word(); replace with <variable entity=...> or a value list for domain data");
      return "DataFakerGenerator('word')";
    }
    return "DataFakerGenerator('" + fakerSnake(method) + "')";
  }

  /** camelCase Faker method -&gt; snake_case ({@code streetName} -&gt; {@code street_name}). */
  private static String fakerSnake(String method) {
    return method.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
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

  /** The bare class name of a Benerator generator ("new PersonGenerator{...}" -&gt; "PersonGenerator"). */
  static String beneratorGeneratorClass(String generator) {
    String g = generator.startsWith("new ") ? generator.substring(4).trim() : generator.trim();
    int cut = ArgSplitter.callStart(g);
    return (cut >= 0 ? g.substring(0, cut) : g).trim();
  }

  /** True when a bean spec like {@code "new IncrementGenerator(1000)"} names a generator DATAMIMIC knows. */
  boolean isKnownGeneratorSpec(String spec) {
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

  /** Constructor-arg rendering: numbers stay bare ({@code min_age=21}), strings get quoted ({@code dataset='DE'}). */
  static String ctorValue(String raw) {
    return raw.matches("[-+]?\\d+(\\.\\d+)?([eE][-+]?\\d+)?") ? raw : "'" + raw + "'";
  }

  /** Strip one pair of surrounding quotes: {@code 'DE'} / {@code "DE"} -&gt; {@code DE}. */
  static String unquote(String s) {
    if (s.length() >= 2 && (s.charAt(0) == '\'' || s.charAt(0) == '"') && s.charAt(s.length() - 1) == s.charAt(0)) {
      return s.substring(1, s.length() - 1);
    }
    return s;
  }

  /** Add {@code dataset='X'} to a generator string: {@code AddressGenerator} -&gt; {@code AddressGenerator(dataset='X')}. */
  static String foldDatasetIntoGenerator(String generator, String dataset) {
    if (generator.startsWith("CompanyNameGenerator")) {
      return generator; // takes no constructor args in DATAMIMIC (no dataset variants)
    }
    return ArgSplitter.appendArg(generator, "dataset='" + dataset + "'");
  }

  /**
   * Rewrite a Benerator/Java script expression into the Python DATAMIMIC evaluates (context.py eval):
   * the Java ternary {@code cond ? a : b} becomes {@code a if cond else b}, and a Java enum accessor
   * {@code .name()} is dropped (DATAMIMIC's gender/enum-like fields are already strings). {@code this.field}
   * is left untouched: DATAMIMIC binds {@code this} to the current content scope (essential in nested
   * scopes where a bare sibling name does not resolve).
   */
  static String rewriteScript(String expr) {
    return rewriteScript(expr, null);
  }

  /**
   * As {@link #rewriteScript(String)}, plus: a Benerator self-reference by the enclosing scope name
   * ({@code <generate type="abc"> ... script="abc.j"}) becomes {@code this.j}, since DATAMIMIC exposes the
   * current scope as {@code this} (a bare sibling name would not resolve inside a nested scope).
   */
  static String rewriteScript(String expr, String enclosingScope) {
    if (expr == null || expr.isEmpty()) {
      return expr;
    }
    String s = rewriteTernary(expr);
    s = javaLiteralsToPython(s); // true/false/null -> True/False/None
    s = s.replaceAll("\\.name\\(\\)", ""); // gender.name() -> gender (Java enum -> already a string)
    if (enclosingScope != null && !enclosingScope.isEmpty()) {
      // Self-reference by the enclosing scope's own name -> `this` (the current-scope alias).
      s = s.replaceAll("\\b" + java.util.regex.Pattern.quote(enclosingScope) + "\\.", "this.");
    }
    return s;
  }

  /**
   * Java ternary {@code cond ? a : b} -&gt; Python {@code (a) if (cond) else (b)}, honoring nesting and
   * string literals, applied recursively to each part. Leaves the expression untouched when it has no
   * top-level {@code ?} (so a lone {@code :} in a dict/slice is never mistaken for a ternary).
   */
  private static String rewriteTernary(String expr) {
    int q = topLevelIndex(expr, '?');
    if (q < 0) {
      return expr;
    }
    int colon = matchingTernaryColon(expr, q + 1);
    if (colon < 0) {
      return expr; // unbalanced - not a ternary we can safely rewrite
    }
    String cond = expr.substring(0, q).trim();
    String thenPart = expr.substring(q + 1, colon).trim();
    String elsePart = expr.substring(colon + 1).trim();
    return "(" + rewriteTernary(thenPart) + ") if (" + rewriteTernary(cond) + ") else (" + rewriteTernary(elsePart) + ")";
  }

  /** Index of the first {@code c} at paren/bracket depth 0 and outside quotes, or -1. */
  private static int topLevelIndex(String s, char c) {
    int depth = 0;
    char quote = 0;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (quote != 0) {
        if (ch == quote) {
          quote = 0;
        }
      } else if (ch == '\'' || ch == '"') {
        quote = ch;
      } else if (ch == '(' || ch == '[' || ch == '{') {
        depth++;
      } else if (ch == ')' || ch == ']' || ch == '}') {
        depth--;
      } else if (ch == c && depth == 0) {
        return i;
      }
    }
    return -1;
  }

  /** The {@code :} that closes the ternary opened at/after {@code from}, skipping nested {@code ? :} pairs. */
  private static int matchingTernaryColon(String s, int from) {
    int depth = 0;
    int ternary = 0;
    char quote = 0;
    for (int i = from; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (quote != 0) {
        if (ch == quote) {
          quote = 0;
        }
      } else if (ch == '\'' || ch == '"') {
        quote = ch;
      } else if (ch == '(' || ch == '[' || ch == '{') {
        depth++;
      } else if (ch == ')' || ch == ']' || ch == '}') {
        depth--;
      } else if (depth == 0 && ch == '?') {
        ternary++;
      } else if (depth == 0 && ch == ':') {
        if (ternary == 0) {
          return i;
        }
        ternary--;
      }
    }
    return -1;
  }
}
