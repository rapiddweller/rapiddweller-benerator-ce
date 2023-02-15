package com.rapiddweller.domain.faker;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Test;

public class FakerIntegrationTest extends AbstractBeneratorIntegrationTest {

//    @Before
//    public void tearDown() {
//        FileUtil.deleteDirectory(new File("results/java_faker_data.csv"));
//    }

    @Test
    public void testJavaFaker() {
        String prefixPath = "com/rapiddweller/domain/faker";
        parseAndExecuteFile(prefixPath + "/faker.ben.xml");
        //Assert.assertEquals(IOUtil.readTextLines("results/java_faker_data.csv", false).length, 101);
    }
}
