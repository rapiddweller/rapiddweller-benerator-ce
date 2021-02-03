package com.rapiddweller.domain.address;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * The type City id test.
 */
public class CityIdTest {
  /**
   * Test set name.
   */
  @Test
  public void testSetName() {
    CityId cityId = new CityId("Name", "Name Extension");
    cityId.setName("Name");
    assertEquals("Name", cityId.getName());
  }

  /**
   * Test set name extension.
   */
  @Test
  public void testSetNameExtension() {
    CityId cityId = new CityId("Name", "Name Extension");
    cityId.setNameExtension("Name Extension");
    assertEquals("Name Extension", cityId.getNameExtension());
  }

  /**
   * Test equals.
   */
  @Test
  public void testEquals() {
    assertNotEquals("o", (new CityId("Name", "Name Extension")));
    assertNotEquals(null, (new CityId("Name", "Name Extension")));
  }

  /**
   * Test hash code.
   */
  @Test
  public void testHashCode() {
    assertEquals(375602761, (new CityId("Name", "Name Extension")).hashCode());
    assertEquals(70191455, (new CityId("Name", null)).hashCode());
  }

  /**
   * Test hash code 2.
   */
  @Test
  public void testHashCode2() {
    CityId cityId = new CityId(null, "Name Extension");
    cityId.setName("Name");
    assertEquals(375602761, cityId.hashCode());
  }

  /**
   * Test to string.
   */
  @Test
  public void testToString() {
    assertEquals("Name Name Extension", (new CityId("Name", "Name Extension")).toString());
    assertEquals("Name", (new CityId("Name", "")).toString());
  }
}

