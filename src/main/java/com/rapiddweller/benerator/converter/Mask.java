/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.AbstractConverter;
import com.rapiddweller.common.converter.ToStringConverter;

import java.util.Arrays;

/**
 * Overwrites each character of the source value with a mask character.<br/><br/>
 * Created: 12.10.2021 12:10:42
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class Mask extends AbstractConverter<Object, String> {

  private char maskChar;

  public Mask() {
    this('*');
  }

  public Mask(char maskChar) {
    super(Object.class, String.class);
    this.maskChar = maskChar;
  }

  @Override
  public String convert(Object sourceValue) throws ConversionException {
    if (sourceValue == null) {
      return null;
    }
    String string = ToStringConverter.convert(sourceValue, null);
    int length = string.length();
    char[] chars = new char[string.length()];
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
