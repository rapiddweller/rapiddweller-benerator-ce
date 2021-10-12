/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.AbstractConverter;

import java.util.Arrays;

/**
 * Overwrites each character of the source value with a mask character.<br/><br/>
 * Created: 12.10.2021 12:10:42
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class Mask extends AbstractConverter<String, String> {

  private char maskChar;

  public Mask() {
    this('*');
  }

  public Mask(char maskChar) {
    super(String.class, String.class);
    this.maskChar = maskChar;
  }

  @Override
  public String convert(String sourceValue) throws ConversionException {
    if (sourceValue == null || sourceValue.isEmpty()) {
      return sourceValue;
    }
    int length = sourceValue.length();
    char[] chars = new char[sourceValue.length()];
    Arrays.fill(chars, 0, length, maskChar);
    return new String(chars);
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

}
