/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/** The exact cases the previous indexOf/split(",") heuristics broke on. */
public class ArgSplitterTest {

  @Test
  public void splitsOnlyTopLevelCommas() {
    assertEquals(List.of("bar(1)", "'a,b'"), ArgSplitter.splitTopLevel("bar(1), 'a,b'"));
    assertEquals(List.of("db.updater(a, b)", "ConsoleExporter"),
        ArgSplitter.splitTopLevel("db.updater(a, b), ConsoleExporter"));
    assertEquals(List.of("CSVEntityExporter(uri='x,y.csv')"),
        ArgSplitter.splitTopLevel("CSVEntityExporter(uri='x,y.csv')"));
    assertEquals(List.of("a", "b"), ArgSplitter.splitTopLevel(" a ,, b "));
    assertEquals(List.of(), ArgSplitter.splitTopLevel(""));
  }

  @Test
  public void callStartSkipsQuotesAndFindsFirstOpener() {
    assertEquals(15, ArgSplitter.callStart("PersonGenerator{minAgeYears=21}"));
    assertEquals(12, ArgSplitter.callStart("FooGenerator(bar(1))"));
    assertEquals(-1, ArgSplitter.callStart("ConsoleExporter"));
  }

  @Test
  public void appendArgRespectsNestingAndQuotes() {
    assertEquals("AddressGenerator(dataset='DE')", ArgSplitter.appendArg("AddressGenerator", "dataset='DE'"));
    assertEquals("Foo(bar(1), dataset='DE')", ArgSplitter.appendArg("Foo(bar(1))", "dataset='DE'"));
    assertEquals("Foo(')', dataset='DE')", ArgSplitter.appendArg("Foo(')')", "dataset='DE'"));
    assertEquals("Foo(dataset='DE')", ArgSplitter.appendArg("Foo()", "dataset='DE'"));
  }
}
