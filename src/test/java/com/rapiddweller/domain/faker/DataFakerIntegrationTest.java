package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.exception.IllegalArgumentError;
import org.junit.Test;

public class DataFakerIntegrationTest extends AbstractBeneratorIntegrationTest {


    private final String PREFIX_PATH = "com/rapiddweller/domain/faker";
    @Test
    public void testDataFaker() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker.ben.xml");
    }

    @Test(expected = IllegalArgumentError.class)
    public void testMissingProperty(){
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_errortest.ben.xml");
    }

}
