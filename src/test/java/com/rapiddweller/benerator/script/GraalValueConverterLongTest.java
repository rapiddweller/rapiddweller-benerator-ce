/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.benerator.script;

import org.graalvm.polyglot.Context;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** Regression: a JS number larger than Integer.MAX_VALUE (e.g. a millis timestamp) must convert,
 *  not throw. Previously handleLongValue() truncated to int and threw. */
public class GraalValueConverterLongTest {
  @Test public void testConvertsLongBeyondIntRange() {
    try (Context ctx = Context.create("js")) {
      assertEquals(3_000_000_000L, GraalValueConverter.value2JavaConverter(ctx.eval("js", "3000000000")));
      assertEquals(1_700_000_000_000L, GraalValueConverter.value2JavaConverter(ctx.eval("js", "1700000000000")));
    }
  }
}
