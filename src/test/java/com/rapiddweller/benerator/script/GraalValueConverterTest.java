/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.script;

import org.graalvm.polyglot.Context;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link GraalValueConverter} along the use case "turn the result of an evaluated JS
 * expression into a plain Java object" (numbers, strings, booleans, arrays).
 *
 * @author rapiddweller
 */
public class GraalValueConverterTest {

  @Test
  public void testConvertsJsScalarsAndArrays() {
    try (Context ctx = Context.create("js")) {
      assertEquals(Integer.valueOf(42), GraalValueConverter.value2JavaConverter(ctx.eval("js", "40 + 2")));
      assertEquals("hello", GraalValueConverter.value2JavaConverter(ctx.eval("js", "'hel' + 'lo'")));
      assertEquals(Boolean.TRUE, GraalValueConverter.value2JavaConverter(ctx.eval("js", "1 === 1")));
      assertEquals(Boolean.FALSE, GraalValueConverter.value2JavaConverter(ctx.eval("js", "1 === 2")));
      assertArrayEquals(new Object[]{1, 2, 3},
          (Object[]) GraalValueConverter.value2JavaConverter(ctx.eval("js", "[1, 2, 3]")));
    }
  }

  @Test
  public void testConverterInstanceDelegatesToStaticConversion() {
    try (Context ctx = Context.create("js")) {
      GraalValueConverter converter = new GraalValueConverter();
      assertEquals(Integer.valueOf(7), converter.convert(ctx.eval("js", "3 + 4")));
    }
  }
}
