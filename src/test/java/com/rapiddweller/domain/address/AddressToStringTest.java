/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.domain.address;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

/** Regression: toString() must not NPE when country is null (previously dereferenced
 *  country.getIsoCode() directly). */
public class AddressToStringTest {
  @Test public void testToStringWithNullCountryDoesNotThrow() {
    assertNotNull(new Address().toString());
  }
}
