/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

/**
 * Calculates the MD5 hash of an object in hexadecimal format.<br/><br/>
 * Created: 06.09.2021 14:31:07
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class MD5HashBase64 extends Hash {

  public MD5HashBase64() {
    super("MD5", HashFormat.base64);
  }

}
