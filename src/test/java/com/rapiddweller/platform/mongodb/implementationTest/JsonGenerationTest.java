/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.platform.mongodb.implementationTest;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extensive Tests for MongoDB Implementation #402.<br/><br/>
 * Created at 23.05.2023
 *
 * @author rapiddwellers
 * @since 1.1.0
 */
public class JsonGenerationTest extends AbstractBeneratorIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(JsonGenerationTest.class);

    //More intense JSON Structure Test
    @Test
    public void JsonGeneration1Test() {
        //People example with 4 nested layer of object/array and write to MongoDB server, use lots of ent / wgt file and also check if percentage was right in WeightCheckTest(HOLD in CE edition)
        context.setContextUri("/com/rapiddweller/platform/mongodb/JsonGeneration/demo1");
        parseAndExecuteFile("/com/rapiddweller/platform/mongodb/JsonGeneration/demo1/json-people.ben.xml");
        //check Benerator script and JSON file
    }

    @Test
    public void JsonGeneration2Test() {
        //Store example with 6 nested layer of object/array, using some selector from H2/Postgres and write to MongoDB server, use iterate to check number collection, and try to iterate to some consumers: NoConsumer / LoggerConsumer / CSVEntityExporter / SQLEntityExporter (HOLD)
        context.setContextUri("/com/rapiddweller/platform/mongodb/JsonGeneration/demo2");
        parseAndExecuteFile("/com/rapiddweller/platform/mongodb/JsonGeneration/demo2/json-product.ben.xml");
        //check Benerator script and JSON file
    }

    @Test
    public void JsonGeneration3Test() {
        //School example with 6 nested layer and wide structure of object/array and write to MongoDB server
        context.setContextUri("/com/rapiddweller/platform/mongodb/JsonGeneration/demo3");
        parseAndExecuteFile("/com/rapiddweller/platform/mongodb/JsonGeneration/demo3/json-school.ben.xml");
        //check Benerator script and JSON file
    }
    @Ignore
    @Test
    public void WeightCheckTest() {
        //ON HOLD
        //Generate People in JsonGeneration1Test, use iterate to count and check percentage
//        context.setContextUri("/com/rapiddweller/platform/mongodb/JsonGeneration/weightCheck");
//        parseAndExecuteFile("/com/rapiddweller/platform/mongodb/JsonGeneration/weightCheck/weight-check.ben.xml");
        //Please check Report on console
    }
    @Test
    public void NegativeCaseTest() {
        //Negative cases test: Empty and Non-Exist Collection
        context.setContextUri("/com/rapiddweller/platform/mongodb/JsonGeneration/demoNegativeCase");
        parseAndExecuteFile("/com/rapiddweller/platform/mongodb/JsonGeneration/demoNegativeCase/NegativeCaseTest.ben.xml");
        //check Benerator script and JSON file
    }
    @Test
    public void SeparateBeneratorTest() {
        //Separate Benerator Generate and Iterate Test
        context.setContextUri("/com/rapiddweller/platform/mongodb/JsonGeneration/demoSeparateBenRun");
        parseAndExecuteFile("/com/rapiddweller/platform/mongodb/JsonGeneration/demoSeparateBenRun/JsonGenTest.ben.xml");
        parseAndExecuteFile("/com/rapiddweller/platform/mongodb/JsonGeneration/demoSeparateBenRun/JsonIterateTest.ben.xml");
        //check Benerator script and JSON file
    }
}
