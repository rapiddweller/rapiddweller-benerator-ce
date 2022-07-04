package com.rapiddweller.platform.mongodb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlatformDescriptorTest {
    /**
     * Method under test: default or parameterless constructor of {@link PlatformDescriptor}
     */
    @Test
    public void testConstructor() {
        PlatformDescriptor actualPlatformDescriptor = new PlatformDescriptor();
        assertEquals("mongodb", actualPlatformDescriptor.toString());
        assertEquals(1, actualPlatformDescriptor.getPackagesToImport().length);
    }

    /**
     * Method under test: {@link PlatformDescriptor#getParsers()}
     */
    @Test
    public void testGetParsers() {
        assertEquals(1, (new PlatformDescriptor()).getParsers().length);
    }
}

