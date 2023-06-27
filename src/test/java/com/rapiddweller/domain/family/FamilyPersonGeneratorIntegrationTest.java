package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.exception.IllegalArgumentError;
import org.junit.Ignore;
import org.junit.Test;

public class FamilyPersonGeneratorIntegrationTest extends AbstractBeneratorIntegrationTest {

    private final String PREFIX_PATH = "com/rapiddweller/domain/family";

    @Test
    public void familyGenTest() {
        parseAndExecuteFile(PREFIX_PATH + "/familyPersonGeneratorTest.ben.xml");
    }
    @Ignore
    @Test(expected = IllegalArgumentError.class)
    public void testMissingProperty(){
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_errortest.ben.xml");
    }

}
