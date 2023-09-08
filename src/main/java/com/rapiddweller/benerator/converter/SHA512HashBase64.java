/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.benerator.converter.Hash;

/**
 * Calculates the SHA-512 hash of an object in hexadecimal format.<br/><br/>
 */
public class SHA512HashBase64 extends Hash {

  public SHA512HashBase64() {
    super("SHA-512", HashFormat.base64);
  }

  public SHA512HashBase64(String salt) {
    super("SHA-512", HashFormat.base64, salt);
  }

}
