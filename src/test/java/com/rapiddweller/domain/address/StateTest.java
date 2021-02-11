package com.rapiddweller.domain.address;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * The type State test.
 */
public class StateTest {
  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    assertNull((new State()).getId());
    assertEquals("42", (new State("42")).getId());
  }

  /**
   * Test set id.
   */
  @Test
  public void testSetId() {
    State state = new State();
    state.setId("42");
    assertEquals("42", state.getId());
  }

  /**
   * Test set name.
   */
  @Test
  public void testSetName() {
    State state = new State();
    state.setName("Name");
    assertEquals("Name", state.getName());
  }

  /**
   * Test get default language.
   */
  @Test
  public void testGetDefaultLanguage() {
    Locale defaultLanguage = new Locale("en");
    State state = new State();
    state.setDefaultLanguageLocale(defaultLanguage);
    assertEquals("en", state.getDefaultLanguage());
  }

  /**
   * Test set default language.
   */
  @Test
  public void testSetDefaultLanguage() {
    State state = new State();
    state.setDefaultLanguage("Default Language");
    assertEquals("default language", state.getDefaultLanguage());
  }

  /**
   * Test get default language locale.
   */
  @Test
  public void testGetDefaultLanguageLocale() {
    Locale locale = new Locale("en");
    State state = new State();
    state.setDefaultLanguageLocale(locale);
    assertSame(locale, state.getDefaultLanguageLocale());
  }

  /**
   * Test set default language locale.
   */
  @Test
  public void testSetDefaultLanguageLocale() {
    Locale defaultLanguage = new Locale("en");
    State state = new State();
    state.setDefaultLanguageLocale(defaultLanguage);
    assertEquals("en", state.getDefaultLanguage());
  }

  /**
   * Test set population.
   */
  @Test
  public void testSetPopulation() {
    State state = new State();
    state.setPopulation(2);
    assertEquals(2, state.getPopulation());
  }

  /**
   * Test add city.
   */
  @Test
  public void testAddCity() {
    CityId id = new CityId("Name", "Name Extension");
    City city = new City(new State(), "Name", "Addition", new String[] {"foo", "foo", "foo"}, "Area Code");
    (new State()).addCity(id, city);
    assertNull(city.getCountry());
  }

  /**
   * Test to string.
   */
  @Test
  public void testToString() {
    assertNull((new State()).toString());
  }

  /**
   * Test to string 2.
   */
  @Test
  public void testToString2() {
    State state = new State();
    state.setName("Name");
    assertEquals("Name", state.toString());
  }

  /**
   * Test hash code.
   */
  @Test
  public void testHashCode() {
    State state = new State();
    state.setName("Name");
    assertEquals(2420395, state.hashCode());
  }

  /**
   * Test equals.
   */
  @Test
  public void testEquals() {
    assertNotEquals("obj", (new State()));
    assertNotEquals(null, (new State()));
  }
}

