/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link Mask} converter.<br/><br/>
 * Created: 12.10.2021 14:41:56
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class MaskTest {

  @Test
  public void testNormal() {
    Mask m = new Mask();
    assertEquals("*********", m.convert("123456789"));
    assertEquals("*", m.convert("1"));
  }

  @Test
  public void testEmpty() {
    Mask m = new Mask();
    assertEquals("", m.convert(""));
  }

  @Test
  public void testNull() {
    Mask m = new Mask();
    assertNull(m.convert(null));
  }

  @Test
  public void test_mask() {
    Mask m = new Mask('-');
    assertEquals("---------", m.convert("123456789"));
    assertEquals("-", m.convert("1"));
    assertEquals("", m.convert(""));
    assertNull(m.convert(null));
  }

}
