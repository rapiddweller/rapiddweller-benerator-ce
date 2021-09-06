/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

/**
 * Calculates the SHA-1 hash of an object in hexadecimal format.<br/><br/>
 * Created: 06.09.2021 14:28:10
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class SHA1Hash extends Hash {

  public SHA1Hash() {
    super("SHA-1", Hash.HashFormat.hex);
  }

}
