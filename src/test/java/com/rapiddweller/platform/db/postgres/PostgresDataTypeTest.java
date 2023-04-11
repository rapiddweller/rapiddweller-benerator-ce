package com.rapiddweller.platform.db.postgres;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

public class PostgresDataTypeTest extends AbstractBeneratorIntegrationTest {
    /**
     * postgres type test
     */
    @Test
    public void PostgresTypeTest() {
        assumeTestActive("postgres");
        BeneratorContext benCtx = parseAndExecuteFile("demo/db/postgres.types.ben.xml");
        Assert.assertEquals("demo/db", benCtx.getContextUri());
    }

    @Test
    public void PostgresAllTypesTest() {
        assumeTestActive("postgres");
        BeneratorContext benCtx = parseAndExecuteFile("demo/db/postgresalltypes.ben.xml");
        Assert.assertEquals("demo/db", benCtx.getContextUri());
    }
}
