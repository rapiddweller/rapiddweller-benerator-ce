/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link CutLength} converter.<br/><br/>
 * Created: 12.10.2021 14:50:26
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CutLengthTest {

  @Test
  public void test_normal() {
    CutLength c = new CutLength(5);
    assertEquals("12345", c.convert("123456789"));
    assertEquals("12345", c.convert("123456"));
    assertEquals("12345", c.convert("12345"));
  }

  @Test
  public void test_short() {
    CutLength c = new CutLength(5);
    assertEquals("1234", c.convert("1234"));
    assertEquals("1", c.convert("1"));
  }

  @Test
  public void test_empty() {
    CutLength c = new CutLength(5);
    assertEquals("", c.convert(""));
  }

  @Test
  public void test_null() {
    CutLength c = new CutLength(5);
    assertNull(c.convert(null));
  }

}
