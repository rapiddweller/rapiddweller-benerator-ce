/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.benerator.converter.Hash;

/**
 * Calculates the SHA-512 hash of an object in hexadecimal format.<br/><br/>
 */
public class SHA3_512Hash extends Hash {

  public SHA3_512Hash() {
    super("SHA3-512", HashFormat.hex);
  }

  public SHA3_512Hash(String salt) {
    super("SHA3-512", HashFormat.hex, salt);
  }

}
