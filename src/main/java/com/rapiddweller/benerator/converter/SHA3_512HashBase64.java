/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.benerator.converter.Hash;

/**
 * Calculates the SHA-512 hash of an object in hexadecimal format.<br/><br/>
 */
public class SHA3_512HashBase64 extends Hash {

  public SHA3_512HashBase64() {
    super("SHA3-512", HashFormat.base64);
  }

  public SHA3_512HashBase64(String salt) {
    super("SHA3-512", HashFormat.base64, salt);
  }
}
