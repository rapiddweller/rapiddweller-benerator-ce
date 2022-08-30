/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.domain.address;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AddressFormat}.<br/><br/>
 * Created: 28.09.2021 20:43:13
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class AddressFormatTest {

  @Test
  public void testUS() {
    State state = new State("ST");
    City city = new City(state, "My City", null, new String[] {"12345"}, "987");
    PhoneNumber privatePhone = new PhoneNumber("+12", "987", "12345678");
    PhoneNumber officePhone = new PhoneNumber("+12", "987", "12345678");
    PhoneNumber mobilePhone = new PhoneNumber("+12", "987", "12345678");
    PhoneNumber fax = new PhoneNumber("+12", "987", "12345678");
    Address address = new Address(
        "My Street", "1A", "12345", city, state, Country.US,
        privatePhone, officePhone, mobilePhone, fax);
    String expected = "1A My Street\n" +
        "My City, ST 12345\n" +
        "United States";
    assertEquals(expected, AddressFormat.getInstance(address.getCountry().getIsoCode()).format(address));
  }

}
