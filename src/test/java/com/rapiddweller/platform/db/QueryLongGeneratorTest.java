package com.rapiddweller.platform.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link QueryLongGenerator}.
 */
public class QueryLongGeneratorTest {
  @Test
  public void testConstructor() {
    QueryLongGenerator actualQueryLongGenerator = new QueryLongGenerator();
    assertEquals("QueryLongGenerator[null]", actualQueryLongGenerator.toString());
    assertNull(actualQueryLongGenerator.getSource());
  }
}

