/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */

package com.rapiddweller.domain.address;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link Address} value object along the use case "represent a generated postal address":
 * construction, accessors, the deprecated zipCode alias, formatted rendering and value equality.<br/><br/>
 * @author rapiddweller
 */
public class AddressTest {

  private State bavaria() {
    State state = new State("BY");
    state.setName("Bayern");
    state.setCountry(Country.GERMANY);
    return state;
  }

  private City munich(State state) {
    return new City(state, "München", null, new String[]{"80331"}, "89");
  }

  private Address address(String street, String postalCode, City city, State state, PhoneNumber phone) {
    return new Address(street, "5", postalCode, city, state, Country.GERMANY, phone, phone, phone, phone);
  }

  @Test
  public void testConstructorExposesAllComponents() {
    State state = bavaria();
    City city = munich(state);
    PhoneNumber phone = new PhoneNumber("49", "89", "1234567");
    Address a = address("Hauptstraße", "80331", city, state, phone);

    assertEquals("Hauptstraße", a.getStreet());
    assertEquals("5", a.getHouseNumber());
    assertEquals("80331", a.getPostalCode());
    assertEquals(city, a.getCity());
    assertEquals("BY", a.getState().getId());
    assertEquals(Country.GERMANY, a.getCountry());
    assertEquals(phone, a.getPrivatePhone());
    assertEquals(phone, a.getOfficePhone());
    assertEquals(phone, a.getMobilePhone());
    assertEquals(phone, a.getFax());
  }

  @Test
  public void testSettersRoundTrip() {
    Address a = new Address();
    a.setStreet("Bahnhofstraße");
    a.setHouseNumber("1a");
    a.setStreet2("c/o Müller");
    a.setPostalCode("10115");
    a.setOrganization("ACME GmbH");
    a.setDepartment("Sales");

    assertEquals("Bahnhofstraße", a.getStreet());
    assertEquals("1a", a.getHouseNumber());
    assertEquals("c/o Müller", a.getStreet2());
    assertEquals("10115", a.getPostalCode());
    assertEquals("ACME GmbH", a.getOrganization());
    assertEquals("Sales", a.getDepartment());
  }

  /** The deprecated zipCode property must still read/write the postalCode (backward compatibility). */
  @Test
  @SuppressWarnings("deprecation")
  public void testZipCodeAliasesPostalCode() {
    Address a = new Address();
    a.setZipCode("12345");
    assertEquals("12345", a.getPostalCode());
    assertEquals("12345", a.getZipCode());
  }

  @Test
  public void testToStringRendersTheAddress() {
    State state = bavaria();
    Address a = address("Hauptstraße", "80331", munich(state), state, new PhoneNumber("49", "89", "1234567"));
    String rendered = a.toString();
    assertNotNull(rendered);
    assertTrue("rendered address should mention its parts: " + rendered,
        rendered.contains("Hauptstraße") || rendered.contains("München") || rendered.contains("80331"));
  }

  @Test
  public void testValueEquality() {
    // shared components so we exercise Address.equals itself, not the components' equality
    State state = bavaria();
    City city = munich(state);
    PhoneNumber phone = new PhoneNumber("49", "89", "1234567");

    Address a = address("Hauptstraße", "80331", city, state, phone);
    Address b = address("Hauptstraße", "80331", city, state, phone);

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertEquals(a, a);
    assertNotEquals(a, null);
    assertNotEquals(a, "not an address");

    b.setPostalCode("99999");
    assertNotEquals(a, b);
  }
}
