package com.rapiddweller.benerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The type Benerator factory test.
 */
public class BeneratorFactoryTest {
  /**
   * Test get schema path for current version.
   */
  @Test
  public void testGetSchemaPathForCurrentVersion() {
    assertEquals("com/rapiddweller/benerator/benerator-1.1.1.xsd", BeneratorFactory.getSchemaPathForCurrentVersion());
  }
}

