/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.ThreadAware;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Creates a hash code for any given object.
 * Sypported <code>types</code> are MD5, SHA-1 and SHA-256,
 * <code>format</code> may be 'hex' or 'base64'.<br/><br/>
 * Created: 02.09.2021 20:50:51
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class Hash implements Converter<Object,String>, ThreadAware {

  private String type;
  private HashFormat format;

  public Hash() {
    this("MD5", HashFormat.hex);
  }

  public Hash(String type, HashFormat format) {
    this.type = type;
    this.format = format;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public HashFormat getFormat() {
    return format;
  }

  public void setFormat(HashFormat format) {
    this.format = format;
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
  public synchronized String convert(Object sourceValue) throws ConversionException {
    try {
      MessageDigest md = MessageDigest.getInstance(type);
      String sourceText = (sourceValue != null ? sourceValue.toString() : "");
      md.update(sourceText.getBytes(StandardCharsets.UTF_8));
      byte[] digest = md.digest();
      String result;
      switch (format) {
        case hex:    result = DatatypeConverter.printHexBinary(digest); break;
        case base64: result = DatatypeConverter.printBase64Binary(digest); break;
        default:     throw new ConversionException("Unsupported format: " + format);
      }
      return result;
    } catch (NoSuchAlgorithmException e) {
      throw new ConversionException("Error applying hash", e);
    }
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  public enum HashFormat {
    hex, base64
  }

}
