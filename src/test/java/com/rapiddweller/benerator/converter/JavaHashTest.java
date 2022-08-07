/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link JavaHash} class.<br/><br/>
 * Created: 04.11.2021 20:16:41
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class JavaHashTest {

  @Test
  public void test() {
    JavaHash hash = new JavaHash();
    assertEquals("00000000", hash.convert(null));
    assertEquals("00000000", hash.convert(""));
    assertEquals("0027b8b2", hash.convert("Test"));
  }

}
