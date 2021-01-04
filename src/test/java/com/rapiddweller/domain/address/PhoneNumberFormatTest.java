package com.rapiddweller.domain.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.text.FieldPosition;
import java.text.ParsePosition;

import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PhoneNumberFormatTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFormat() {
        PhoneNumberFormat phoneNumberFormat = new PhoneNumberFormat("Pattern");
        FieldPosition pos = new FieldPosition(1);
        PhoneNumber obj = new PhoneNumber();
        StringBuffer stringBuffer = new StringBuffer();
        assertSame(stringBuffer, phoneNumberFormat.format(obj, stringBuffer, pos));
    }

    @Test
    public void testFormat2() {
        PhoneNumberFormat phoneNumberFormat = new PhoneNumberFormat("");
        FieldPosition pos = new FieldPosition(1);
        PhoneNumber obj = new PhoneNumber();
        StringBuffer stringBuffer = new StringBuffer();
        assertSame(stringBuffer, phoneNumberFormat.format(obj, stringBuffer, pos));
    }

    @Test
    public void testParseObject() {
        PhoneNumberFormat phoneNumberFormat = new PhoneNumberFormat("Pattern");
        thrown.expect(IllegalArgumentException.class);
        phoneNumberFormat.parseObject("Source", new ParsePosition(1));
    }

    @Test
    public void testParseObject2() {
        PhoneNumberFormat phoneNumberFormat = new PhoneNumberFormat("");
        assertEquals("", ((PhoneNumber) phoneNumberFormat.parseObject("Source", new ParsePosition(1))).getCountryCode());
        assertEquals("", ((PhoneNumber) phoneNumberFormat.parseObject("Source", new ParsePosition(1))).getAreaCode());
        assertFalse(((PhoneNumber) phoneNumberFormat.parseObject("Source", new ParsePosition(1))).isMobile());
        assertEquals("", ((PhoneNumber) phoneNumberFormat.parseObject("Source", new ParsePosition(1))).getLocalNumber());
    }

    @Test
    public void testParseObject3() {
        PhoneNumberFormat phoneNumberFormat = new PhoneNumberFormat("");
        assertEquals("", ((PhoneNumber) phoneNumberFormat.parseObject("", new ParsePosition(1))).getCountryCode());
        assertEquals("", ((PhoneNumber) phoneNumberFormat.parseObject("", new ParsePosition(1))).getAreaCode());
        assertFalse(((PhoneNumber) phoneNumberFormat.parseObject("", new ParsePosition(1))).isMobile());
        assertEquals("", ((PhoneNumber) phoneNumberFormat.parseObject("", new ParsePosition(1))).getLocalNumber());
    }
}

