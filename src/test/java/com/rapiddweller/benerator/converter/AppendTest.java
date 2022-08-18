/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link Append} converter.<br/><br/>
 * Created: 27.10.2021 12:01:15
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class AppendTest {

  @Test
  public void testNormal() {
    Append a = new Append();
    assertEquals("123_", a.convert("123"));
    assertEquals("__", a.convert("_"));
  }

  @Test
  public void testEmpty() {
    Append a = new Append();
    assertEquals("_", a.convert(""));
  }

  @Test
  public void testNull() {
    Append a = new Append();
    assertNull(a.convert(null));
  }

  @Test
  public void test_suffix() {
    Append a = new Append("_demo");
    assertEquals("123_demo", a.convert("123"));
    assertEquals("_demo", a.convert(""));
    assertNull(a.convert(null));
  }

  @Test
  public void test_non_string_source() {
    Append a = new Append("_demo");
    assertEquals("123_demo", a.convert(123));
    assertEquals("-1_demo", a.convert(-1));
    assertEquals("true_demo", a.convert(true));
    assertEquals("A_demo", a.convert('A'));
  }

}
