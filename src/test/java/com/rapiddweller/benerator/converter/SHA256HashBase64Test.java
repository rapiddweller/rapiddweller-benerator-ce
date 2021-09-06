/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the SHA256HashBase64.<br/><br/>
 * Created: 06.09.2021 14:43:19
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class SHA256HashBase64Test extends AbstractHashTest {

  @Test
  public void test_SHA256_base64() {
    Hash hash = new SHA256HashBase64();
    assertEquals(EMPTY_SHA256_BASE64, hash.convert(null));
    assertEquals(EMPTY_SHA256_BASE64, hash.convert(""));
    assertEquals(TEST_SHA256_BASE64, hash.convert("Test"));
  }

}
