/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.AbstractConverter;

/**
 * {@link com.rapiddweller.common.Converter} implementation
 * which cuts the length of a maximum.<br/><br/>
 * Created: 12.10.2021 11:46:31
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CutLength extends AbstractConverter<String, String> {

  private final int maxLength;

  public CutLength(int maxLength) {
    super(String.class, String.class);
    this.maxLength = maxLength;
  }

  @Override
  public String convert(String sourceValue) throws ConversionException {
    if (sourceValue == null || sourceValue.isEmpty()) {
      return sourceValue;
    }
    return (sourceValue.length() > maxLength ? sourceValue.substring(0, maxLength) : sourceValue);

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
