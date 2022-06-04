package com.rapiddweller.domain.address;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link PhoneNumber} class.
 * @author Volker Bergmann
 */
public class PhoneNumberTest {

  @Test
  public void testStringConstructor() {
    PhoneNumber n = new PhoneNumber("49 89 1234 567");
    assertEquals("49", n.getCountryCode());
    assertEquals("89", n.getAreaCode());
    assertEquals("1234 567", n.getLocalNumber());
  }

  @Test
  public void testSetCountryCode() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setCountryCode("Country Code");
    assertEquals("Country Code", phoneNumber.getCountryCode());
  }

  @Test
  public void testSetAreaCode() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setAreaCode("Oxford");
    assertEquals("Oxford", phoneNumber.getAreaCode());
  }

  @Test
  public void testSetLocalNumber() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setLocalNumber("Local Number");
    assertEquals("Local Number", phoneNumber.getLocalNumber());
  }

  @Test
  public void testSetMobile() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setMobile(true);
    assertTrue(phoneNumber.isMobile());
  }

  @Test
  public void testToString() {
    assertEquals("+--", (new PhoneNumber()).toString());
  }

  @Test
  public void testEquals() {
    assertNotEquals(null, new PhoneNumber("1 2 3"));
    assertEquals(new PhoneNumber("1 2 3"), new PhoneNumber("1 2 3"));
    assertNotEquals(new PhoneNumber("1 2 4"), new PhoneNumber("1 2 3"));
    assertNotEquals(new PhoneNumber("1 3 3"), new PhoneNumber("1 2 3"));
    assertNotEquals(new PhoneNumber("2 2 3"), new PhoneNumber("1 2 3"));
    assertFalse(new PhoneNumber("1 2 3").equals("nada"));
    PhoneNumber n = new PhoneNumber("1 2 4");
    assertEquals(n, n);
  }

  @Test
  public void testHashCode() {
    assertEquals(29791, new PhoneNumber().hashCode());
    assertEquals(78481, new PhoneNumber("1 2 3").hashCode());
  }

}

