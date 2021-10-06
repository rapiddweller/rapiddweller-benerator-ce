package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class RegexStringGeneratorTest extends GeneratorTest {
    @Test
    public void testConstructor() {
        RegexStringGenerator actualRegexStringGenerator = new RegexStringGenerator();
        Locale locale = new Locale("en");
        actualRegexStringGenerator.setLocale(locale);
        actualRegexStringGenerator.setMinLength(3);
        actualRegexStringGenerator.setOrdered(true);
        actualRegexStringGenerator.setPattern("Pattern");
        actualRegexStringGenerator.setUnique(true);
        Locale locale1 = actualRegexStringGenerator.getLocale();
        assertSame(locale, locale1);
        assertEquals(30, actualRegexStringGenerator.getMaxLength());
        assertEquals(3, actualRegexStringGenerator.getMinLength());
        assertEquals("Pattern", actualRegexStringGenerator.getPattern());
        assertTrue(actualRegexStringGenerator.isOrdered());
        assertTrue(actualRegexStringGenerator.isThreadSafe());
        assertTrue(actualRegexStringGenerator.isUnique());
    }

    @Test
    public void testConstructor2() {
        RegexStringGenerator actualRegexStringGenerator = new RegexStringGenerator();
        Class<?> expectedGeneratedType = String.class;
        assertSame(expectedGeneratedType, actualRegexStringGenerator.getGeneratedType());
        assertFalse(actualRegexStringGenerator.isUnique());
        assertFalse(actualRegexStringGenerator.isOrdered());
        assertNull(actualRegexStringGenerator.getSource());
        assertNull(actualRegexStringGenerator.getPattern());
        assertEquals(30, actualRegexStringGenerator.getMaxLength());
    }

    @Test
    public void testConstructor3() {
        RegexStringGenerator actualRegexStringGenerator = new RegexStringGenerator(3);
        Class<?> expectedGeneratedType = String.class;
        assertSame(expectedGeneratedType, actualRegexStringGenerator.getGeneratedType());
        assertFalse(actualRegexStringGenerator.isUnique());
        assertFalse(actualRegexStringGenerator.isOrdered());
        assertNull(actualRegexStringGenerator.getSource());
        assertNull(actualRegexStringGenerator.getPattern());
        assertEquals(3, actualRegexStringGenerator.getMaxLength());
    }

    @Test
    public void testConstructor4() {
        RegexStringGenerator actualRegexStringGenerator = new RegexStringGenerator("Pattern");
        Class<?> expectedGeneratedType = String.class;
        assertSame(expectedGeneratedType, actualRegexStringGenerator.getGeneratedType());
        assertFalse(actualRegexStringGenerator.isUnique());
        assertFalse(actualRegexStringGenerator.isOrdered());
        assertNull(actualRegexStringGenerator.getSource());
        assertEquals("Pattern", actualRegexStringGenerator.getPattern());
        assertEquals(30, actualRegexStringGenerator.getMaxLength());
    }

    @Test
    public void testConstructor5() {
        RegexStringGenerator actualRegexStringGenerator = new RegexStringGenerator("Pattern", 3);

        Class<?> expectedGeneratedType = String.class;
        assertSame(expectedGeneratedType, actualRegexStringGenerator.getGeneratedType());
        assertFalse(actualRegexStringGenerator.isUnique());
        assertFalse(actualRegexStringGenerator.isOrdered());
        assertNull(actualRegexStringGenerator.getSource());
        assertEquals("Pattern", actualRegexStringGenerator.getPattern());
        assertEquals(3, actualRegexStringGenerator.getMaxLength());
    }

    @Test
    public void testConstructor6() {
        RegexStringGenerator actualRegexStringGenerator = new RegexStringGenerator("Pattern", 3, true);

        Class<?> expectedGeneratedType = String.class;
        assertSame(expectedGeneratedType, actualRegexStringGenerator.getGeneratedType());
        assertTrue(actualRegexStringGenerator.isUnique());
        assertFalse(actualRegexStringGenerator.isOrdered());
        assertNull(actualRegexStringGenerator.getSource());
        assertEquals("Pattern", actualRegexStringGenerator.getPattern());
        assertEquals(3, actualRegexStringGenerator.getMaxLength());
    }

    @Test
    public void testIsParallelizable() {
        assertTrue((new RegexStringGenerator()).isParallelizable());
    }

    @Test
    public void testIsParallelizable2() {
        RegexStringGenerator regexStringGenerator = new RegexStringGenerator();
        regexStringGenerator.setUnique(true);
        assertFalse(regexStringGenerator.isParallelizable());
    }

    @Test
    public void testToString() {
        assertEquals("RegexStringGenerator['null']", (new RegexStringGenerator()).toString());
    }

    @Test
    public void testToString2() {
        RegexStringGenerator regexStringGenerator = new RegexStringGenerator();
        regexStringGenerator.setUnique(true);
        assertEquals("RegexStringGenerator[unique 'null']", regexStringGenerator.toString());
    }

    @Test
    public void testOptionalGroup() {
        RegexStringGenerator gen = new RegexStringGenerator("A(B)?");
        gen.init(new DefaultBeneratorContext());
        checkProducts(gen, 100, "A", "AB");
    }

    @Test
    public void testGroupCount_0_1() {
        RegexStringGenerator gen = new RegexStringGenerator("A(B){0,1}");
        gen.init(new DefaultBeneratorContext());
        checkProducts(gen, 100, "A", "AB");
    }

    @Test
    public void testGroupCount_1_3() {
        RegexStringGenerator gen = new RegexStringGenerator("A(B){1,3}");
        gen.init(new DefaultBeneratorContext());
        checkProducts(gen, 100, "AB", "ABB", "ABBB");
    }

    // TODO add further tests

}

