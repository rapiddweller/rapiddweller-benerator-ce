package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.exception.IllegalArgumentError;
import org.junit.Ignore;
import org.junit.Test;

public class DataFakerIntegrationTest extends AbstractBeneratorIntegrationTest {


    private final String PREFIX_PATH = "com/rapiddweller/domain/faker";
    @Ignore("Fails on CI server, but works locally. Needs further investigation. (ake2l)")
    @Test
    public void testDataFaker() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker.ben.xml");
    }

    @Test(expected = IllegalArgumentError.class)
    public void testMissingProperty(){
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_errortest.ben.xml");
    }

}
