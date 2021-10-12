/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link MiddleMask} converter.<br/><br/>
 * Created: 12.10.2021 12:24:27
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class MiddleMaskTest {

  @Test
  public void testConvert_mask() {
    MiddleMask m = new MiddleMask(3, 2, '-');
    assertEquals("123----89", m.convert("123456789"));
    assertEquals("123-56", m.convert("123456"));
  }

  @Test
  public void testConvert_normal() {
    MiddleMask m = new MiddleMask(3, 2);
    assertEquals("123****89", m.convert("123456789"));
    assertEquals("123*56", m.convert("123456"));
  }

  @Test
  public void testConvert_short() {
    MiddleMask m = new MiddleMask(3, 2);
    assertEquals("123", m.convert("123"));
    assertEquals("12", m.convert("12"));
    assertEquals("1", m.convert("1"));
  }

  @Test
  public void testConvertEmpty() {
    MiddleMask m = new MiddleMask(3, 2);
    assertEquals("", m.convert(""));
  }

  @Test
  public void testConvertNull() {
    MiddleMask m = new MiddleMask(3, 2);
    assertNull(m.convert(null));
  }

  @Test
  public void testConvert_headOnly() {
    MiddleMask m = new MiddleMask(2, 0);
    assertEquals("12***", m.convert("12345"));
    assertEquals("12", m.convert("12"));
    assertEquals("", m.convert(""));
    assertNull(m.convert(null));
  }

  @Test
  public void testConvert_tailOnly() {
    MiddleMask m = new MiddleMask(0, 3);
    assertEquals("**345", m.convert("12345"));
    assertEquals("12", m.convert("12"));
    assertEquals("", m.convert(""));
    assertNull(m.convert(null));
  }

}
