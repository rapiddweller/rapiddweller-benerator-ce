package com.rapiddweller.domain.product;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class EAN13ValidatorTest {
    @Test
    public void testIsValid() {
        assertFalse((new EAN13Validator()).isValid("Number", null));
        assertFalse((new EAN13Validator()).isValid(null, null));
    }
}

