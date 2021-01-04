package com.rapiddweller.benerator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BeneratorFactoryTest {
    @Test
    public void testGetSchemaPathForCurrentVersion() {
        assertEquals("com/rapiddweller/benerator/benerator-1.1.0.xsd", BeneratorFactory.getSchemaPathForCurrentVersion());
    }
}

