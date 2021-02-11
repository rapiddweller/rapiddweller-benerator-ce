package com.my;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.SimpleConverter;

import java.text.DecimalFormat;

/**
 * The type Number converter.
 */
public class NumberConverter extends SimpleConverter<Long, String> {

  private DecimalFormat format;

  /**
   * Instantiates a new Number converter.
   *
   * @param pattern the pattern
   */
  public NumberConverter(String pattern) {
    super(Long.class, String.class);
    this.format = new DecimalFormat(pattern);
  }

  /**
   * Convert string.
   *
   * @param source the source
   * @return the string
   * @throws ConversionException the conversion exception
   */
  public String convert(Long source) throws ConversionException {
    return format.format(source);
  }

}
