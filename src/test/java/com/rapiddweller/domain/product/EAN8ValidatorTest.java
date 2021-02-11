package com.rapiddweller.domain.product;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * The type Ean 8 validator test.
 */
public class EAN8ValidatorTest {
  /**
   * Test is valid.
   */
  @Test
  public void testIsValid() {
    assertFalse((new EAN8Validator()).isValid("Number", null));
    assertFalse((new EAN8Validator()).isValid(null, null));
  }
}

