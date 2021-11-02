/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO JavaDoc.<br/><br/>
 * Created: 02.11.2021 16:15:39
 *
 * @author Volker Bergmann
 * @since TODO
 */
public class ExecutionModeTest {

  ExecutionMode ce  = new ExecutionMode(false, 1);
  ExecutionMode ee1 = new ExecutionMode(true,  1);
  ExecutionMode ee2 = new ExecutionMode(true,  2);
  ExecutionMode ee4 = new ExecutionMode(true,  4);

  @Test
  public void testComparison() {
    // less
    assertEquals(-1, ce.compareTo(ee1));
    assertEquals(-1, ce.compareTo(ee2));
    assertEquals(-1, ce.compareTo(ee4));
    assertEquals(-1, ee1.compareTo(ee2));
    assertEquals(-1, ee1.compareTo(ee4));
    assertEquals(-1, ee2.compareTo(ee4));
    // equals
    assertEquals(0, ce.compareTo(ce));
    assertEquals(0, ee1.compareTo(ee1));
    assertEquals(0, ee2.compareTo(ee2));
    assertEquals(0, ee4.compareTo(ee4));
    // greater
    assertEquals(1, ee1.compareTo(ce));
    assertEquals(1, ee2.compareTo(ce));
    assertEquals(1, ee4.compareTo(ce));
    assertEquals(1, ee2.compareTo(ee1));
    assertEquals(1, ee4.compareTo(ee1));
    assertEquals(1, ee4.compareTo(ee2));
  }

  @Test
  public void testEquals() {
    // less
    assertNotEquals(ce, ee1);
    assertNotEquals(ce, ee2);
    assertNotEquals(ce, ee4);
    assertNotEquals(ee1, ee2);
    assertNotEquals(ee1, ee4);
    assertNotEquals(ee2, ee4);
    // equals
    assertEquals(ce, ce);
    assertEquals(ee1, ee1);
    assertEquals(ee2, ee2);
    assertEquals(ee4, ee4);
    // greater
    assertNotEquals(ee1, ce);
    assertNotEquals(ee2, ce);
    assertNotEquals(ee4, ce);
    assertNotEquals(ee2, ee1);
    assertNotEquals(ee4, ee1);
    assertNotEquals(ee4, ee2);
  }

  @Test
  public void testHashCode() {
    assertEquals(ce.hashCode(), ce.hashCode());
    assertEquals(ee1.hashCode(), ee1.hashCode());
    assertEquals(ee2.hashCode(), ee2.hashCode());
    assertEquals(ee4.hashCode(), ee4.hashCode());
  }

}
