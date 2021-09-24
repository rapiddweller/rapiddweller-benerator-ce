/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link MD5Hash}.<br/><br/>
 * Created: 06.09.2021 14:35:58
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class MD5HashTest extends AbstractHashTest {

  @Test
  public void test() {
    MD5Hash hash = new MD5Hash();
    assertEquals(EMPTY_MD5_HEX, hash.convert(null));
    assertEquals(EMPTY_MD5_HEX, hash.convert(""));
    assertEquals(TEST_MD5_HEX, hash.convert("Test"));
  }

}
