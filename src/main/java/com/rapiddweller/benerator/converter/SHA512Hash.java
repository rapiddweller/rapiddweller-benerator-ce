/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.benerator.converter.Hash;

/**
 * Calculates the SHA-512 hash of an object in hexadecimal format.<br/><br/>
 */
public class SHA512Hash extends Hash {

  public SHA512Hash() {
    super("SHA-512", HashFormat.hex);
  }

  public SHA512Hash(String salt) {
    super("SHA-512", HashFormat.hex, salt);
  }

}
