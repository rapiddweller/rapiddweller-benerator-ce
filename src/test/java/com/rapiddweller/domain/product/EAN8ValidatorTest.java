package com.rapiddweller.domain.product;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class EAN8ValidatorTest {
    @Test
    public void testIsValid() {
        assertFalse((new EAN8Validator()).isValid("Number", null));
        assertFalse((new EAN8Validator()).isValid(null, null));
    }
}

