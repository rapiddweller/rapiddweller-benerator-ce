/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.parser.AbstractTypedParser;

import java.util.Set;

/**
 * Parses URIs in Strings.<br/><br/>
 * Created: 10.12.2021 12:21:03
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class UriParser extends AbstractTypedParser<String> {

  private static final Set<String> PROTOCOLS = CollectionUtil.toSet("string", "http", "https", "file", "ftp");

  public UriParser() {
    super("URI", String.class);
  }

  @Override
  protected String parseImpl(String spec) {
    Assert.notEmpty(spec, "URI is empty");
    int protocolSep = spec.indexOf("://");
    if (protocolSep > 0) { // if a protocol was specified, then verify it
      String protocol = spec.substring(0, protocolSep).toLowerCase();
      if (!PROTOCOLS.contains(protocol)) {
        throw BeneratorExceptionFactory.getInstance().configurationError("Not a supported protocol: " + protocol);
      }
    }
    return spec;
  }

}
