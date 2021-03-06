package com.rapiddweller.domain.address;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * The type Phone number test.
 */
public class PhoneNumberTest {
  /**
   * Test set country code.
   */
  @Test
  public void testSetCountryCode() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setCountryCode("Country Code");
    assertEquals("Country Code", phoneNumber.getCountryCode());
  }

  /**
   * Test set area code.
   */
  @Test
  public void testSetAreaCode() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setAreaCode("Oxford");
    assertEquals("Oxford", phoneNumber.getAreaCode());
  }

  /**
   * Test set local number.
   */
  @Test
  public void testSetLocalNumber() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setLocalNumber("Local Number");
    assertEquals("Local Number", phoneNumber.getLocalNumber());
  }

  /**
   * Test set mobile.
   */
  @Test
  public void testSetMobile() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setMobile(true);
    assertTrue(phoneNumber.isMobile());
  }

  /**
   * Test to string.
   */
  @Test
  public void testToString() {
    assertEquals("+--", (new PhoneNumber()).toString());
  }

  /**
   * Test hash code.
   */
  @Test
  public void testHashCode() {
    assertEquals(29791, (new PhoneNumber()).hashCode());
  }

  /**
   * Test hash code 2.
   */
  @Test
  public void testHashCode2() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setLocalNumber(null);
    assertEquals(29791, phoneNumber.hashCode());
  }

  /**
   * Test hash code 3.
   */
  @Test
  public void testHashCode3() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setCountryCode(null);
    assertEquals(29791, phoneNumber.hashCode());
  }

  /**
   * Test equals.
   */
  @Test
  public void testEquals() {
    assertNotEquals("obj", (new PhoneNumber()));
    assertNotEquals(null, (new PhoneNumber()));
  }
}

