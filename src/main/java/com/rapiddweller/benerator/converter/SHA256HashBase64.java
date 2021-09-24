/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

/**
 * Calculates the SHA-256 hash of an object in hexadecimal format.<br/><br/>
 * Created: 06.09.2021 14:33:17
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class SHA256HashBase64 extends Hash {

  public SHA256HashBase64() {
    super("SHA-256", HashFormat.base64);
  }

}
