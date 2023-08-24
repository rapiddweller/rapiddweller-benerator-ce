package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

public class FakerIntegrationTest extends AbstractBeneratorIntegrationTest {


    @Ignore("Fails on CI server, but works locally. Needs further investigation. (ake2l)")
    @Test
    public void testJavaFaker() {
        String prefixPath = "com/rapiddweller/domain/faker";
        parseAndExecuteFile(prefixPath + "/faker.ben.xml");

    }
}
