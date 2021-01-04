package com.rapiddweller.domain.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        assertFalse((new CityId("Name", "Name Extension")).equals("o"));
        assertFalse((new CityId("Name", "Name Extension")).equals(null));
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

