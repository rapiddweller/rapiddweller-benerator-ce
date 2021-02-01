package com.rapiddweller.domain.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PhoneNumberTest {
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
    public void testHashCode() {
        assertEquals(29791, (new PhoneNumber()).hashCode());
    }

    @Test
    public void testHashCode2() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setLocalNumber(null);
        assertEquals(29791, phoneNumber.hashCode());
    }

    @Test
    public void testHashCode3() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setCountryCode(null);
        assertEquals(29791, phoneNumber.hashCode());
    }

    @Test
    public void testEquals() {
        assertNotEquals("obj", (new PhoneNumber()));
        assertNotEquals(null, (new PhoneNumber()));
    }
}

