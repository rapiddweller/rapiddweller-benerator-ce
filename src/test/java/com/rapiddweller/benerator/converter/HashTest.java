/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.benerator.converter.Hash;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link Hash}.<br/><br/>
 * Created: 02.09.2021 21:19:28
 *
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class HashTest extends AbstractHashTest {

  @Test
  public void testDefault() {
    Hash hash = new Hash();
    assertEquals(EMPTY_MD5_HEX, hash.convert(null));
    assertEquals(EMPTY_MD5_HEX, hash.convert(""));
    assertEquals(TEST_MD5_HEX, hash.convert("Test"));
  }

  @Test
  public void test_MD5_hex() {
    Hash hash = create("MD5", Hash.HashFormat.hex);
    assertEquals(EMPTY_MD5_HEX, hash.convert(null));
    assertEquals(EMPTY_MD5_HEX, hash.convert(""));
    assertEquals(TEST_MD5_HEX, hash.convert("Test"));
  }

  @Test
  public void test_MD5_base64() {
    Hash hash = create("MD5", Hash.HashFormat.base64);
    assertEquals(EMPTY_MD5_BASE64, hash.convert(null));
    assertEquals(EMPTY_MD5_BASE64, hash.convert(""));
    assertEquals(TEST_MD5_BASE64, hash.convert("Test"));
  }

  @Test
  public void test_SHA1_hex() {
    Hash hash = create("SHA-1", Hash.HashFormat.hex);
    assertEquals(EMPTY_SHA1_HEX, hash.convert(null));
    assertEquals(EMPTY_SHA1_HEX, hash.convert(""));
    assertEquals(TEST_SHA1_HEX, hash.convert("Test"));
  }

  @Test
  public void test_SHA1_base64() {
    Hash hash = create("SHA-1", Hash.HashFormat.base64);
    assertEquals(EMPTY_SHA1_BASE64, hash.convert(null));
    assertEquals(EMPTY_SHA1_BASE64, hash.convert(""));
    assertEquals(TEST_SHA1_BASE64, hash.convert("Test"));
  }

  @Test
  public void test_SHA256_hex() {
    Hash hash = create("SHA-256", Hash.HashFormat.hex);
    assertEquals(EMPTY_SHA256_HEX, hash.convert(null));
    assertEquals(EMPTY_SHA256_HEX, hash.convert(""));
    assertEquals(TEST_SHA256_HEX, hash.convert("Test"));
  }

  @Test
  public void test_SHA256_base64() {
    Hash hash = create("SHA-256", Hash.HashFormat.base64);
    assertEquals(EMPTY_SHA256_BASE64, hash.convert(null));
    assertEquals(EMPTY_SHA256_BASE64, hash.convert(""));
    assertEquals(TEST_SHA256_BASE64, hash.convert("Test"));
  }

  private Hash create(String type, Hash.HashFormat format) {
    Hash hash = new Hash();
    hash.setType(type);
    hash.setFormat(format);
    return hash;
  }
}
