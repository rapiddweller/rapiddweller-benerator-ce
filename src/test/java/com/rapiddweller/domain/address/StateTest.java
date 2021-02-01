package com.rapiddweller.domain.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.junit.Test;

public class StateTest {
    @Test
    public void testConstructor() {
        assertNull((new State()).getId());
        assertEquals("42", (new State("42")).getId());
    }

    @Test
    public void testSetId() {
        State state = new State();
        state.setId("42");
        assertEquals("42", state.getId());
    }

    @Test
    public void testSetName() {
        State state = new State();
        state.setName("Name");
        assertEquals("Name", state.getName());
    }

    @Test
    public void testGetDefaultLanguage() {
        Locale defaultLanguage = new Locale("en");
        State state = new State();
        state.setDefaultLanguageLocale(defaultLanguage);
        assertEquals("en", state.getDefaultLanguage());
    }

    @Test
    public void testSetDefaultLanguage() {
        State state = new State();
        state.setDefaultLanguage("Default Language");
        assertEquals("default language", state.getDefaultLanguage());
    }

    @Test
    public void testGetDefaultLanguageLocale() {
        Locale locale = new Locale("en");
        State state = new State();
        state.setDefaultLanguageLocale(locale);
        assertSame(locale, state.getDefaultLanguageLocale());
    }

    @Test
    public void testSetDefaultLanguageLocale() {
        Locale defaultLanguage = new Locale("en");
        State state = new State();
        state.setDefaultLanguageLocale(defaultLanguage);
        assertEquals("en", state.getDefaultLanguage());
    }

    @Test
    public void testSetPopulation() {
        State state = new State();
        state.setPopulation(2);
        assertEquals(2, state.getPopulation());
    }

    @Test
    public void testAddCity() {
        CityId id = new CityId("Name", "Name Extension");
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        (new State()).addCity(id, city);
        assertNull(city.getCountry());
    }

    @Test
    public void testToString() {
        assertNull((new State()).toString());
    }

    @Test
    public void testToString2() {
        State state = new State();
        state.setName("Name");
        assertEquals("Name", state.toString());
    }

    @Test
    public void testHashCode() {
        State state = new State();
        state.setName("Name");
        assertEquals(2420395, state.hashCode());
    }

    @Test
    public void testEquals() {
        assertNotEquals("obj", (new State()));
        assertNotEquals(null, (new State()));
    }
}

