package com.rapiddweller.domain.product;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The type Ean 13 generator test.
 */
public class EAN13GeneratorTest {
  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    EAN13Generator actualEan13Generator = new EAN13Generator();
    assertNull(actualEan13Generator.getSource());
    assertFalse(actualEan13Generator.isOrdered());
    assertEquals("EAN13Generator", actualEan13Generator.toString());
  }

  /**
   * Test constructor 2.
   */
  @Test
  public void testConstructor2() {
    EAN13Generator actualEan13Generator = new EAN13Generator(true);
    assertNull(actualEan13Generator.getSource());
    assertFalse(actualEan13Generator.isOrdered());
    assertEquals("EAN13Generator[unique]", actualEan13Generator.toString());
  }

  /**
   * Test constructor 3.
   */
  @Test
  public void testConstructor3() {
    EAN13Generator actualEan13Generator = new EAN13Generator(true, true);
    assertNull(actualEan13Generator.getSource());
    assertTrue(actualEan13Generator.isOrdered());
    assertEquals("EAN13Generator[unique]", actualEan13Generator.toString());
  }

  /**
   * Test set ordered.
   */
  @Test
  public void testSetOrdered() {
    EAN13Generator ean13Generator = new EAN13Generator();
    ean13Generator.setOrdered(true);
    assertTrue(ean13Generator.isOrdered());
  }

  /**
   * Test to string.
   */
  @Test
  public void testToString() {
    assertEquals("EAN13Generator", (new EAN13Generator()).toString());
    assertEquals("EAN13Generator[unique]", (new EAN13Generator(true)).toString());
  }
}

