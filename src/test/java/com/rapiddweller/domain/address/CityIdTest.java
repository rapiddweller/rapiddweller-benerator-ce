package com.rapiddweller.domain.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class CityIdTest {
    @Test
    public void testSetName() {
        CityId cityId = new CityId("Name", "Name Extension");
        cityId.setName("Name");
        assertEquals("Name", cityId.getName());
    }

    @Test
    public void testSetNameExtension() {
        CityId cityId = new CityId("Name", "Name Extension");
        cityId.setNameExtension("Name Extension");
        assertEquals("Name Extension", cityId.getNameExtension());
    }

    @Test
    public void testEquals() {
        assertNotEquals("o", (new CityId("Name", "Name Extension")));
        assertNotEquals(null, (new CityId("Name", "Name Extension")));
    }

    @Test
    public void testHashCode() {
        assertEquals(375602761, (new CityId("Name", "Name Extension")).hashCode());
        assertEquals(70191455, (new CityId("Name", null)).hashCode());
    }

    @Test
    public void testHashCode2() {
        CityId cityId = new CityId(null, "Name Extension");
        cityId.setName("Name");
        assertEquals(375602761, cityId.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals("Name Name Extension", (new CityId("Name", "Name Extension")).toString());
        assertEquals("Name", (new CityId("Name", "")).toString());
    }
}

