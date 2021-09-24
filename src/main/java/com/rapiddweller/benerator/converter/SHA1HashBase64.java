/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

/**
 * Calculates the SHA-1 hash of an object in hexadecimal format.<br/><br/>
 * Created: 06.09.2021 14:32:01
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class SHA1HashBase64 extends Hash {

  public SHA1HashBase64() {
    super("SHA-1", HashFormat.base64);
  }

}
