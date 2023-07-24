package com.rapiddweller.platform.db.mongodb;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

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

    @Test
    public void mongoDBInsertionRuntimeErrorInSameCollectionTest() {
        assumeTestActive("mongodb");
        assertThrows(RuntimeException.class, () -> parseAndExecuteFile("demo/db/mongodb-insert-into-same-collection.ben.xml"));
    }

    @Test
    public void mongoDBUpserterTest() {
        assumeTestActive("mongodb");
        BeneratorContext context = parseAndExecuteFile("demo/db/mongodb-upserter.ben.xml");
        Assert.assertEquals("demo/db", context.getContextUri());
    }

    @Test
    public void mongoDBUpserter2Test() {
        assumeTestActive("mongodb");
        BeneratorContext context = parseAndExecuteFile("demo/db/mongodb-upserter2.ben.xml");
        Assert.assertEquals("demo/db", context.getContextUri());
    }

    @Test
    public void mongoDBDeleterTest() {
        assumeTestActive("mongodb");
        BeneratorContext context = parseAndExecuteFile("demo/db/mongodb-deleteCollection.ben.xml");
        Assert.assertEquals("demo/db", context.getContextUri());
    }
      
    @Test
    public void mongoDBDeleteTest() {
        assumeTestActive("mongodb");
        BeneratorContext context = parseAndExecuteFile("demo/db/mongodb-delete.ben.xml");
        Assert.assertEquals("demo/db", context.getContextUri());
    }
}
