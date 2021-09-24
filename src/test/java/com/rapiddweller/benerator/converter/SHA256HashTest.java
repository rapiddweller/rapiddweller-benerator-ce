/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link SHA256Hash}.<br/><br/>
 * Created: 06.09.2021 14:41:39
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class SHA256HashTest extends AbstractHashTest {

  @Test
  public void test() {
    Hash hash = new SHA256Hash();
    assertEquals(EMPTY_SHA256_HEX, hash.convert(null));
    assertEquals(EMPTY_SHA256_HEX, hash.convert(""));
    assertEquals(TEST_SHA256_HEX, hash.convert("Test"));
  }

}
