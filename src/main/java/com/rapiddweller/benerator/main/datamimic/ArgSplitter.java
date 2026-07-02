/*
 * Copyright (C) 2025 rapiddweller GmbH.
 * Licensed under the GPL (see the LICENSE file of the Benerator CE project).
 */

package com.rapiddweller.benerator.main.datamimic;

import java.util.ArrayList;
import java.util.List;

/**
 * Character-level scanning of Benerator expression strings (generator specs, consumer lists,
 * constructor args) that respects paren/brace/bracket nesting and quoted literals — the cases a
 * naive {@code indexOf}/{@code split(",")} breaks on, e.g. {@code new Foo(bar(1), 'a,b')}.
 * Deliberately not an expression parser: it only tracks depth and quotes.
 */
final class ArgSplitter {

  private ArgSplitter() {
  }

  /** Split on commas at nesting depth 0 and outside quotes; parts are trimmed, empties dropped. */
  static List<String> splitTopLevel(String s) {
    List<String> parts = new ArrayList<>();
    int depth = 0;
    char quote = 0;
    StringBuilder cur = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (quote != 0) {
        if (ch == quote) {
          quote = 0;
        }
      } else if (ch == '\'' || ch == '"') {
        quote = ch;
      } else if (ch == '(' || ch == '{' || ch == '[') {
        depth++;
      } else if (ch == ')' || ch == '}' || ch == ']') {
        depth--;
      } else if (ch == ',' && depth == 0) {
        addTrimmed(parts, cur);
        continue;
      }
      cur.append(ch);
    }
    addTrimmed(parts, cur);
    return parts;
  }

  /** Index of the first top-level {@code (} or <code>{</code> outside quotes, or -1 (= no call args). */
  static int callStart(String s) {
    char quote = 0;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (quote != 0) {
        if (ch == quote) {
          quote = 0;
        }
      } else if (ch == '\'' || ch == '"') {
        quote = ch;
      } else if (ch == '(' || ch == '{') {
        return i;
      }
    }
    return -1;
  }

  /**
   * Append an argument to a call expression: {@code Foo} -&gt; {@code Foo(arg)},
   * {@code Foo(a)} -&gt; {@code Foo(a, arg)}. Inserts before the close matching the first call paren.
   */
  static String appendArg(String call, String arg) {
    int open = callStart(call);
    if (open < 0) {
      return call + "(" + arg + ")";
    }
    int close = matchingClose(call, open);
    if (close < 0) { // unbalanced — leave untouched rather than guess
      return call;
    }
    boolean empty = call.substring(open + 1, close).trim().isEmpty();
    return call.substring(0, close) + (empty ? arg : ", " + arg) + call.substring(close);
  }

  /** Index of the close matching the opener at {@code open}, quote-aware; -1 when unbalanced. */
  private static int matchingClose(String s, int open) {
    int depth = 0;
    char quote = 0;
    for (int i = open; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (quote != 0) {
        if (ch == quote) {
          quote = 0;
        }
      } else if (ch == '\'' || ch == '"') {
        quote = ch;
      } else if (ch == '(' || ch == '{' || ch == '[') {
        depth++;
      } else if (ch == ')' || ch == '}' || ch == ']') {
        depth--;
        if (depth == 0) {
          return i;
        }
      }
    }
    return -1;
  }

  private static void addTrimmed(List<String> parts, StringBuilder cur) {
    String p = cur.toString().trim();
    if (!p.isEmpty()) {
      parts.add(p);
    }
    cur.setLength(0);
  }
}
