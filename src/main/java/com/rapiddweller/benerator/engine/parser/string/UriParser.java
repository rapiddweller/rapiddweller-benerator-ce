/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.Assert;
import com.rapiddweller.common.parser.TypedParser;

/**
 * Parses URIs in Strings.<br/><br/>
 * Created: 10.12.2021 12:21:03
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class UriParser extends TypedParser<String> {

  public UriParser() {
    super("URI", String.class);
  }

  @Override
  protected String parseImpl(String spec) {
    Assert.notEmpty(spec, "URI is empty");
    return spec;
  }

}
