/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link SHA1HashBase64}.<br/><br/>
 * Created: 06.09.2021 14:40:40
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class SHA1Base64Test extends AbstractHashTest {

  @Test
  public void test() {
    Hash hash = new SHA1HashBase64();
    assertEquals(EMPTY_SHA1_BASE64, hash.convert(null));
    assertEquals(EMPTY_SHA1_BASE64, hash.convert(""));
    assertEquals(TEST_SHA1_BASE64, hash.convert("Test"));
  }

}
