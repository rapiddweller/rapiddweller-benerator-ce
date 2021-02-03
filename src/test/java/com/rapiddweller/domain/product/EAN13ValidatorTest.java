package com.rapiddweller.domain.product;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * The type Ean 13 validator test.
 */
public class EAN13ValidatorTest {
  /**
   * Test is valid.
   */
  @Test
  public void testIsValid() {
    assertFalse((new EAN13Validator()).isValid("Number", null));
    assertFalse((new EAN13Validator()).isValid(null, null));
  }
}

