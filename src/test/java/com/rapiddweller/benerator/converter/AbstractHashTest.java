/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

/**
 * Parent class for hash tests.<br/><br/>
 * Created: 06.09.2021 14:35:17
 * @author Volker Bergmann
 * @since 1.2.0
 */
public abstract class AbstractHashTest {

  public static final String EMPTY_MD5_HEX = "D41D8CD98F00B204E9800998ECF8427E";
  public static final String TEST_MD5_HEX = "0CBC6611F5540BD0809A388DC95A615B";
  public static final String EMPTY_MD5_BASE64 = "1B2M2Y8AsgTpgAmY7PhCfg==";
  public static final String TEST_MD5_BASE64 = "DLxmEfVUC9CAmjiNyVphWw==";

  public static final String EMPTY_SHA1_HEX = "DA39A3EE5E6B4B0D3255BFEF95601890AFD80709";
  public static final String TEST_SHA1_HEX = "640AB2BAE07BEDC4C163F679A746F7AB7FB5D1FA";
  public static final String EMPTY_SHA1_BASE64 = "2jmj7l5rSw0yVb/vlWAYkK/YBwk=";
  public static final String TEST_SHA1_BASE64 = "ZAqyuuB77cTBY/Z5p0b3q3+10fo=";

  public static final String EMPTY_SHA256_HEX = "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855";
  public static final String TEST_SHA256_HEX = "532EAABD9574880DBF76B9B8CC00832C20A6EC113D682299550D7A6E0F345E25";
  public static final String EMPTY_SHA256_BASE64 = "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=";
  public static final String TEST_SHA256_BASE64 = "Uy6qvZV0iA2/drm4zACDLCCm7BE9aCKZVQ16bg80XiU=";

}
