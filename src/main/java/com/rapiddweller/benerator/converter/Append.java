/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.AbstractConverter;
import com.rapiddweller.common.converter.ToStringConverter;

/**
 * {@link com.rapiddweller.common.Converter} implementation which creates a String representation
 * of the object and appends a suffix.<br/><br/>
 * Created: 27.10.2021 11:42:49
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class Append extends AbstractConverter<Object, String> {

  private String suffix;

  public Append() {
    this("_");
  }

  public Append(String suffix) {
    super(Object.class, String.class);
    this.suffix = suffix;
  }

  @Override
  public String convert(Object sourceValue) throws ConversionException {
    if (sourceValue == null) {
      return null;
    }
    String string = ToStringConverter.convert(sourceValue, null);
    return string + suffix;
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
