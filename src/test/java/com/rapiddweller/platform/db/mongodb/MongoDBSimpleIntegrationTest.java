package com.rapiddweller.platform.db.mongodb;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

public class MongoDBSimpleIntegrationTest extends AbstractBeneratorIntegrationTest {
    @Test
    public void mongoDBIdTest() {
        assumeTestActive("mongodb");
        BeneratorContext context = parseAndExecuteFile("demo/db/mongodb-ObjectId.ben.xml");
        Assert.assertEquals("demo/db", context.getContextUri());
    }

    @Test
    public void mongoDBInserterTest() {
        assumeTestActive("mongodb");
        BeneratorContext context = parseAndExecuteFile("demo/db/mongodb-inserter.ben.xml");
        Assert.assertEquals("demo/db", context.getContextUri());
    }
}
