/**
 * Integration test for rapiddweller-benerator-ce CSV functionality.<br/><br/>
 * Created: 1/15/21
 *
 * @author akell
 * @since 1.1.0
 */

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.test.BeneratorIntegrationTest;
import com.rapiddweller.common.IOUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class QuotedTableIntegrationTest extends BeneratorIntegrationTest {

    @Test
    public void testHeadless() throws IOException {
        context.setContextUri("src/demo/resources/demo/projects/shop");
        parseAndExecuteFile("src/demo/resources/demo/projects/shop/shop.ben.xml");
//        String generatedContent = IOUtil.getContentOfURI("target/headless-out.csv");
//        String expectedContent =
//                "ALICE,231,A\r\n" +
//                        "BOB,341,B";
//
//        assertEquals(expectedContent, generatedContent);
    }

}