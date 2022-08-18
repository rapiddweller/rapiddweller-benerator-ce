/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.parser.AbstractTypedParser;

import java.nio.charset.Charset;

/**
 * Parses the name of an encoding.<br/><br/>
 * Created: 19.12.2021 23:02:47
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PlainEncodingParser extends AbstractTypedParser<String> {

  public PlainEncodingParser() {
    super("encoding", String.class);
  }

  @Override
  protected String parseImpl(String spec) {
    Charset.forName(spec);
    return spec;
  }

}
