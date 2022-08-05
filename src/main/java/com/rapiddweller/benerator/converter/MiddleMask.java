/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.AbstractConverter;

import java.util.Arrays;

/**
 * Masks the middle of strings.<br/><br/>
 * Created: 12.10.2021 11:55:42
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class MiddleMask extends AbstractConverter<String, String> {

  private int fromHead;
  private int fromTail;
  private char mask;

  public MiddleMask(int fromHead, int fromTail) {
    this(fromHead, fromTail, '*');
  }

  public MiddleMask(int fromHead, int fromTail, char mask) {
    super(String.class, String.class);
    this.fromHead = fromHead;
    this.fromTail = fromTail;
    this.mask = mask;
  }

  @Override
  public String convert(String sourceValue) throws ConversionException {
    if (sourceValue == null) {
      return null;
    }
    int length = sourceValue.length();
    if (length < fromHead) {
      return sourceValue;
    }
    char[] chars = new char[sourceValue.length()];
    sourceValue.getChars(0, chars.length, chars, 0);
    int max = Math.max(length - fromTail, fromHead);
    Arrays.fill(chars, fromHead, max, mask);
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
