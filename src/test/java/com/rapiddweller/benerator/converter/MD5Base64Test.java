/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link MD5HashBase64}.<br/><br/>
 * Created: 06.09.2021 14:38:09
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class MD5Base64Test extends AbstractHashTest {

  @Test
  public void test() {
    Hash hash = new MD5HashBase64();
    assertEquals(EMPTY_MD5_BASE64, hash.convert(null));
    assertEquals(EMPTY_MD5_BASE64, hash.convert(""));
    assertEquals(TEST_MD5_BASE64, hash.convert("Test"));
  }

}
