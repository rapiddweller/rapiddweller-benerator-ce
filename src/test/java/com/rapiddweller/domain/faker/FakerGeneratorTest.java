package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class FakerGeneratorTest extends GeneratorTest {

    @Test
    public void testDataFaker() {
        DataFakerGenerator generator = new DataFakerGenerator();
        generator.init(context);
        for (int i = 0; i < 10; i++) {
            Object tld = generator.generate();
            assertNotNull(tld);
        }
    }
}
