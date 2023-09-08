/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.converter;

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.ThreadAware;
import com.rapiddweller.common.exception.ExceptionFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Creates a hash code for any given object.
 * Sypported <code>types</code> are MD5, SHA-1 and SHA-256,
 * <code>format</code> may be 'hex' or 'base64'.<br/><br/>
 * Created: 02.09.2021 20:50:51
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class Hash implements Converter<Object,String>, ThreadAware, Closeable {

  private String type;
  private HashFormat format;
  private ThreadLocal<MessageDigest> md;
  private String salt;

  public Hash() {
    this("MD5", HashFormat.hex);
  }

  public Hash(String type, HashFormat format) {
    this(type,format,"");
  }

  public Hash(String type, HashFormat format, String salt) {
    this.type = type;
    this.format = format;
    this.md = ThreadLocal.withInitial(() -> getMessageDigest(this.type));
    this.salt = salt;
  }

  public static MessageDigest getMessageDigest(String type) {
    if (type == null)
      throw ExceptionFactory.getInstance().illegalArgument("No hash type specified");
    try {
      return MessageDigest.getInstance(type);
    } catch (NoSuchAlgorithmException e) {
      throw ExceptionFactory.getInstance().configurationError("Error creating message digest", e);
    }
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
  public String convert(Object sourceValue) throws ConversionException {
    String sourceText = (sourceValue != null ? sourceValue.toString() : "");
    //add salt if exist
    if (salt != null && !salt.isBlank()) {
      md.get().update(salt.getBytes(StandardCharsets.UTF_8));
    }
    byte[] digest = md.get().digest(sourceText.getBytes(StandardCharsets.UTF_8));
    switch (format) {
      case hex:    return DatatypeConverter.printHexBinary(digest);
      case base64: return DatatypeConverter.printBase64Binary(digest);
      default:     throw ExceptionFactory.getInstance().illegalArgument("Not a supported format: " + format);
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

  @Override
  public void close() {
    md.remove();
  }

  public enum HashFormat {
    hex, base64
  }

}
