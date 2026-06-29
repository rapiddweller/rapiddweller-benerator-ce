/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.task;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link TaskUnavailableException}.<br/><br/>
 * @author Alexander Kell
 */
public class TaskUnavailableExceptionTest {

  @Test
  public void testNotAvailable() {
    TaskUnavailableException e = new TaskUnavailableException(null, 5, 0);
    assertEquals(5, e.getRequiredCount());
    assertEquals(0, e.getActualCount());
    assertNull(e.getTask());
    assertTrue(e.getMessage(), e.getMessage().contains("not available"));
  }

  @Test
  public void testPartiallyExecuted() {
    TaskUnavailableException e = new TaskUnavailableException(null, 10, 3);
    assertEquals(10, e.getRequiredCount());
    assertEquals(3, e.getActualCount());
    assertTrue(e.getMessage(), e.getMessage().contains("only 3"));
  }

}
