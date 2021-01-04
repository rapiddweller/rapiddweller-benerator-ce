package com.rapiddweller.domain.address;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StreetTest {
    @Test
    public void testConstructor() {
        Street actualStreet = new Street(
                new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code"), "Name");
        assertEquals("Name", actualStreet.getName());
        assertEquals(50, actualStreet.getMaxHouseNumber());
    }

    @Test
    public void testConstructor2() {
        Street actualStreet = new Street(
                new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code"), "Name", 3);
        assertEquals("Name", actualStreet.getName());
        assertEquals(3, actualStreet.getMaxHouseNumber());
    }

    @Test
    public void testSetName() {
        Street street = new Street(
                new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code"), "Name");
        street.setName("Name");
        assertEquals("Name", street.getName());
    }

    @Test
    public void testSetMaxHouseNumber() {
        Street street = new Street(
                new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code"), "Name");
        street.setMaxHouseNumber(3);
        assertEquals(3, street.getMaxHouseNumber());
    }

    @Test
    public void testGenerateHouseNumberWithPostalCode() {
        assertEquals(2,
                (new Street(new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code"), "Name"))
                        .generateHouseNumberWithPostalCode().length);
        assertEquals(2,
                (new Street(new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code"), "Name",
                        3)).generateHouseNumberWithPostalCode().length);
    }
}

