package com.rapiddweller.platform.db.mongodb;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

public class MongoDBSimpleTest extends AbstractBeneratorIntegrationTest {
    @Test
    public void mongoDBIdTest() {
        assumeTestActive("mongodb");
        BeneratorContext context = parseAndExecuteFile("demo/db/mongodb-ObjectId.ben.xml");
        Assert.assertEquals("demo/db", context.getContextUri());

    }
}
