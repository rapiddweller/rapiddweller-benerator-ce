/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.domain.person;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GenderConverter}.<br/><br/>
 * Created: 02.10.2021 15:55:42
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class GenderConverterTest {

  @Test
  public void testDefault() {
    GenderConverter c = new GenderConverter();
    assertEquals("m", c.convert(Gender.MALE));
    assertEquals("f", c.convert(Gender.FEMALE));
  }

  @Test
  public void testCustomConstructor() {
    GenderConverter c = new GenderConverter("MAL", "FEM");
    assertEquals("MAL", c.convert(Gender.MALE));
    assertEquals("FEM", c.convert(Gender.FEMALE));
  }

  @Test
  public void testCustomProperties() {
    GenderConverter c = new GenderConverter();
    c.setMale("M");
    c.setFemale("F");
    assertEquals("M", c.convert(Gender.MALE));
    assertEquals("F", c.convert(Gender.FEMALE));
  }
}
