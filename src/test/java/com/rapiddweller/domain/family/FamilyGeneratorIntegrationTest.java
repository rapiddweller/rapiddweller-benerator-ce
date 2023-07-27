package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Test;

public class FamilyGeneratorIntegrationTest extends AbstractBeneratorIntegrationTest {

    private final String PREFIX_PATH = "com/rapiddweller/domain/family";

    @Test
    public void familyGenTest() {
        parseAndExecuteFile(PREFIX_PATH + "/familyGeneratorTest.ben.xml");
    }

    @Test
    public void familyGenTest1() {
        parseAndExecuteFile(PREFIX_PATH + "/familyGeneratorTest1.ben.xml");
    }

}
