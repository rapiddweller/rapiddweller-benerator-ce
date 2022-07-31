package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.RegexUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/** Tests the {@link RegexStringGenerator}.
 *  @author Volker Bergmann
 */
public class RegexStringGeneratorTest extends GeneratorTest {
    @Test
    public void testIsParallelizable_non_unique() {
        assertTrue((new RegexStringGenerator()).isParallelizable());
    }

    @Test
    public void testIsParallelizable_unique() {
        RegexStringGenerator regexStringGenerator = new RegexStringGenerator();
        regexStringGenerator.setUnique(true);
        assertFalse(regexStringGenerator.isParallelizable());
    }

    @Test
    public void testToString() {
        assertEquals("RegexStringGenerator['null']", (new RegexStringGenerator()).toString());
    }

    @Test
    public void testToString_unique() {
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

    @Test
    public void testFix() {
        expectProducts("ABC", "ABC");
    }

    @Test
    public void testCharset() {
        expectProducts("[A-D]", "A", "B", "C", "D");
    }

    @Test
    public void testNotCharset() {
        assertProductsMatch("[^ABC]");
    }

    @Test
    public void testAlternative() {
        expectProducts("ABC|DEF", "ABC", "DEF");
    }

    @Test
    public void testQuadruplet() {
        assertProductsMatch("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
    }

    // private helper ------------------------------------------------------------------------------------------------

    private static void expectProducts(String regex, String... expectedProducts) {
        RegexStringGenerator gen = new RegexStringGenerator(regex);
        gen.init(new DefaultBeneratorContext());
        checkProducts(gen, 100, expectedProducts);
    }

    private static void assertProductsMatch(String regex) {
        RegexStringGenerator gen = new RegexStringGenerator(regex);
        gen.init(new DefaultBeneratorContext());
        for (int i = 0; i < 1000; i++) {
            assertTrue(RegexUtil.matches(regex, gen.generate()));
        }
    }

}

