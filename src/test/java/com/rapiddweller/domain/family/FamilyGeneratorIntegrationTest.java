package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FamilyGeneratorIntegrationTest extends AbstractBeneratorIntegrationTest {

    private final String PREFIX_PATH = "com/rapiddweller/domain/family";

    @Test
    public void familyGenGenerateManyEntityTest() {
        parseAndExecuteFile(PREFIX_PATH + "/familyGeneratorTest.ben.xml");
        assertNotNull(this.toString());
    }

    @Test
    public void familyGeneratorAccessAttributeTest() {
        parseAndExecuteFile(PREFIX_PATH + "/familyGeneratorTest1.ben.xml");
        assertNotNull(this.toString());
    }

}
