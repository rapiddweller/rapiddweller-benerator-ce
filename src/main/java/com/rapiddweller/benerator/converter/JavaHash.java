/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.ThreadAware;

/**
 * Converts an object into a hex representation of its hash code.<br/><br/>
 * Created: 13.09.2021 15:43:54
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class JavaHash implements Converter<Object,String>, ThreadAware {

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

  @Override
  public Class<Object> getSourceType() {
    return Object.class;
  }

  @Override
  public Class<String> getTargetType() {
    return String.class;
  }

  @Override
  public String convert(Object sourceValue) throws ConversionException {
    String sourceText = (sourceValue != null ? sourceValue.toString() : "");
    int hashCode = sourceText.hashCode();
    return StringUtil.padLeft(Integer.toHexString(hashCode), 8, '0');
  }

}