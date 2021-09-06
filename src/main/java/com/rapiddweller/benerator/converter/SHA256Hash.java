/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

/**
 * Calculates the SHA-256 hash of an object in hexadecimal format.<br/><br/>
 * Created: 06.09.2021 14:29:30
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class SHA256Hash extends Hash {

  public SHA256Hash() {
    super("SHA-256", Hash.HashFormat.hex);
  }

}
