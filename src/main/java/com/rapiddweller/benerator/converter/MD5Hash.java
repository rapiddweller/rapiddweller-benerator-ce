/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

/**
 * Calculates the MD5 hash of an object in hexadecimal format.<br/><br/>
 * Created: 06.09.2021 14:26:36
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class MD5Hash extends Hash {

  public MD5Hash() {
    super("MD5", HashFormat.hex);
  }

}
