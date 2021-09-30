package com.rapiddweller.benerator.sample;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeigthedLiteralGeneratorTest {
    @Test
    public void testConstructor() {
        Class<?> forNameResult = Object.class;
        WeigthedLiteralGenerator<Object> actualWeigthedLiteralGenerator = new WeigthedLiteralGenerator<Object>(
                (Class<Object>) forNameResult);
        actualWeigthedLiteralGenerator.setUnique(true);
        actualWeigthedLiteralGenerator.setValueSpec("42");
        Class<Object> generatedType = actualWeigthedLiteralGenerator.getGeneratedType();
        assertSame(forNameResult, generatedType);
        assertSame(Object.class, generatedType);
        assertEquals("WeigthedLiteralGenerator[null]", actualWeigthedLiteralGenerator.toString());
        assertNull(actualWeigthedLiteralGenerator.getSource());
    }

    @Test
    public void testConstructor2() {
        Class<?> forNameResult = Object.class;
        WeigthedLiteralGenerator<Object> actualWeigthedLiteralGenerator = new WeigthedLiteralGenerator<Object>(
                (Class<Object>) forNameResult);
        assertSame(forNameResult, actualWeigthedLiteralGenerator.getGeneratedType());
        assertEquals("WeigthedLiteralGenerator[null]", actualWeigthedLiteralGenerator.toString());
    }

    @Test
    public void testConstructor3() {
        Class<?> forNameResult = Object.class;
        WeigthedLiteralGenerator<Object> actualWeigthedLiteralGenerator = new WeigthedLiteralGenerator<Object>(
                (Class<Object>) forNameResult, "42");

        assertSame(forNameResult, actualWeigthedLiteralGenerator.getGeneratedType());
        assertEquals("WeigthedLiteralGenerator[null]", actualWeigthedLiteralGenerator.toString());
    }

    @Test
    public void testConstructor4() {
        Class<?> forNameResult = Object.class;
        WeigthedLiteralGenerator<Object> actualWeigthedLiteralGenerator = new WeigthedLiteralGenerator<Object>(
                (Class<Object>) forNameResult, "42", true);

        assertSame(forNameResult, actualWeigthedLiteralGenerator.getGeneratedType());
        assertEquals("WeigthedLiteralGenerator[null]", actualWeigthedLiteralGenerator.toString());
    }
}

