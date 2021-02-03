package com.rapiddweller.domain.address;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The type Street test.
 */
public class StreetTest {
  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    Street actualStreet = new Street(
        new City(new State(), "Name", "Addition", new String[] {"foo", "foo", "foo"}, "Area Code"), "Name");
    assertEquals("Name", actualStreet.getName());
    assertEquals(50, actualStreet.getMaxHouseNumber());
  }

  /**
   * Test constructor 2.
   */
  @Test
  public void testConstructor2() {
    Street actualStreet = new Street(
        new City(new State(), "Name", "Addition", new String[] {"foo", "foo", "foo"}, "Area Code"), "Name", 3);
    assertEquals("Name", actualStreet.getName());
    assertEquals(3, actualStreet.getMaxHouseNumber());
  }

  /**
   * Test set name.
   */
  @Test
  public void testSetName() {
    Street street = new Street(
        new City(new State(), "Name", "Addition", new String[] {"foo", "foo", "foo"}, "Area Code"), "Name");
    street.setName("Name");
    assertEquals("Name", street.getName());
  }

  /**
   * Test set max house number.
   */
  @Test
  public void testSetMaxHouseNumber() {
    Street street = new Street(
        new City(new State(), "Name", "Addition", new String[] {"foo", "foo", "foo"}, "Area Code"), "Name");
    street.setMaxHouseNumber(3);
    assertEquals(3, street.getMaxHouseNumber());
  }

  /**
   * Test generate house number with postal code.
   */
  @Test
  public void testGenerateHouseNumberWithPostalCode() {
    assertEquals(2,
        (new Street(new City(new State(), "Name", "Addition", new String[] {"foo", "foo", "foo"}, "Area Code"), "Name"))
            .generateHouseNumberWithPostalCode().length);
    assertEquals(2,
        (new Street(new City(new State(), "Name", "Addition", new String[] {"foo", "foo", "foo"}, "Area Code"), "Name",
            3)).generateHouseNumberWithPostalCode().length);
  }
}

