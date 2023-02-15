package com.rapiddweller.domain.java_faker;

import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class FakerGeneratorTest extends GeneratorTest {
    @Test
    public void testCat() {
        FakerGenerator generator = new FakerGenerator();
        generator.init(context);
        for (int i = 0; i < 10; i++) {
            String tld = generator.generate();
            assertNotNull(tld);
        }
    }
}
