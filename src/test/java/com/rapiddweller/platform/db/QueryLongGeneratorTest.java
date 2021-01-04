package com.rapiddweller.platform.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class QueryLongGeneratorTest {
    @Test
    public void testConstructor() {
        QueryLongGenerator actualQueryLongGenerator = new QueryLongGenerator();
        assertEquals("QueryLongGenerator[null]", actualQueryLongGenerator.toString());
        assertNull(actualQueryLongGenerator.getSource());
    }
}

