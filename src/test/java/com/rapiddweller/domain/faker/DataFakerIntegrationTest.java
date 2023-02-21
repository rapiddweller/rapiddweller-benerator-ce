package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Test;

public class DataFakerIntegrationTest extends AbstractBeneratorIntegrationTest {


    private final String PREFIX_PATH = "com/rapiddweller/domain/faker";
    @Test
    public void testDataFaker100() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_0to100.ben.xml");
    }

    @Test
    public void testDataFaker200() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_101to200.ben.xml");
    }

    @Test
    public void testDataFaker300() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_201to300.ben.xml");
    }

    @Test
    public void testDataFaker400() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_301to400.ben.xml");
    }

    @Test
    public void testDataFaker500() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_401to500.ben.xml");
    }

    @Test
    public void testDataFaker600() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_501to600.ben.xml");
    }

    @Test
    public void testDataFaker700() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_601to700.ben.xml");
    }

    @Test
    public void testDataFaker800() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_701to800.ben.xml");
    }

    @Test
    public void testDataFaker900() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_801to900.ben.xml");
    }

    @Test
    public void testDataFaker1000() {
        parseAndExecuteFile(PREFIX_PATH + "/datafaker_901to990.ben.xml");
    }
}
