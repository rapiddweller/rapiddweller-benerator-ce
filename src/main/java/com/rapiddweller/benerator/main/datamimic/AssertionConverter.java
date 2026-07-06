/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Converts Benerator's assertion idioms into DATAMIMIC {@code <assert>} elements: the
 * {@code <if test><error>MSG</error></if>} idiom and {@code <evaluate assert="...">}.
 */
class AssertionConverter {

  private final MigrationReport report;

  AssertionConverter(MigrationReport report) {
    this.report = report;
  }

  /**
   * The Benerator assertion idiom {@code <if test="X"><error>MSG</error></if>} (the {@code <if>} body is
   * NOTHING but a single {@code <error>}) -&gt; DATAMIMIC {@code <assert condition="not (X)" message="MSG"/>}
   * (an {@code <assert>} fails when its condition is NOT true, so the test is negated). {@code {ftl:...}}
   * templating in the message is kept verbatim - same policy as all scripts. Returns null when the
   * {@code <if>} is not this idiom (then/else bodies convert via convertIfNode or stay flagged).
   */
  Node errorOnlyIfToAssert(Document out, Element src, String path) {
    Element error = null;
    for (Node c = src.getFirstChild(); c != null; c = c.getNextSibling()) {
      if (c.getNodeType() == Node.ELEMENT_NODE) {
        if (error != null || !DomUtil.local((Element) c).equals("error")) {
          return null; // body is not exactly one <error> -> not the assertion idiom
        }
        error = (Element) c;
      }
    }
    if (error == null) {
      return null;
    }
    String test = DomUtil.attributes(src).get("test");
    if (test != null && test.matches(".*\\b\\w+\\.counter\\b.*")) {
      // Benerator's row-count check reads the generate's runtime counter after the run
      // (X.counter) - that scope does not exist in DATAMIMIC; a converted <assert> would
      // always fail. Flag it instead of emitting a runtime-dead assertion.
      report.add(path, "assert", "row-count check '" + test + "' reads a Benerator runtime counter - "
          + "verify the count in a test or via <execute type='sql'> instead");
      return out.createComment(" TODO(datamimic-migration): row-count check '" + test
          + "' has no DATAMIMIC runtime counter - verify externally ");
    }
    Element assertEl = out.createElement("assert");
    if (test != null) {
      assertEl.setAttribute("condition", "not (" + test + ")");
    } else {
      report.add(path, "if", "<if> without a test condition - review");
    }
    String message = error.getTextContent().trim();
    if (!message.isEmpty()) {
      assertEl.setAttribute("message", message);
    }
    report.info(path, "assert", "assertion converted to <assert> - verify the expression evaluates in DATAMIMIC");
    return assertEl;
  }

  /**
   * Benerator {@code <evaluate assert="A" [target="db"]>BODY</evaluate>} -&gt; two DATAMIMIC elements
   * (returned as a fragment that dissolves into the parent): a {@code <variable name="result">} that
   * captures the body - {@code source=target selector=BODY} for SQL against a store, {@code script=BODY}
   * otherwise - followed by {@code <assert condition="A"/>}. An {@code <evaluate>} without {@code assert}
   * stays flagged (it is a side effect, not an assertion).
   */
  Node convertEvaluateNode(Document out, Element src, String path) {
    Map<String, String> attrs = DomUtil.attributes(src);
    String assertion = attrs.get("assert");
    if (assertion == null) {
      report.add(path, "evaluate", "<evaluate> without assert has no DATAMIMIC equivalent - dropped "
          + "(use <execute type='sql'> for a side effect, or verify the count in a test)");
      return out.createComment(" TODO(datamimic-migration): <evaluate> dropped - review,"
          + " see MIGRATION_PLAYBOOK.md#evaluate-without-assert ");
    }
    Element variable = out.createElement("variable");
    variable.setAttribute("name", "result");
    String body = src.getTextContent().trim();
    String target = attrs.get("target");
    if (target != null) {
      variable.setAttribute("source", target);
      variable.setAttribute("selector", body);
      report.info(path, "assert", "assertion converted to <variable source> + <assert> - "
          + "verify the expression evaluates in DATAMIMIC");
    } else {
      variable.setAttribute("script", body);
      report.info(path, "assert", "assertion converted to <variable script> + <assert> - "
          + "verify the script and condition evaluate in DATAMIMIC");
    }
    Element assertEl = out.createElement("assert");
    assertEl.setAttribute("condition", assertion);
    return DomUtil.fragmentOf(out, variable, assertEl);
  }
}
