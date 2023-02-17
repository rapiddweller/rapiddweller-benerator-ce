package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Test;

public class FakerIntegrationTest extends AbstractBeneratorIntegrationTest {


    @Test
    public void testJavaFaker() {
        String prefixPath = "com/rapiddweller/domain/faker";
        parseAndExecuteFile(prefixPath + "/faker.ben.xml");

    }
}
