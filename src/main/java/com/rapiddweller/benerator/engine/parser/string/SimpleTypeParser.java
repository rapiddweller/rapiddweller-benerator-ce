/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.parser.AbstractTypedParser;
import com.rapiddweller.script.PrimitiveType;

/**
 * Parses the names of simple types.<br/><br/>
 * Created: 14.12.2021 09:07:33
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class SimpleTypeParser extends AbstractTypedParser<String> {

  public SimpleTypeParser() {
    super("simple type", String.class);
  }

  @Override
  protected String parseImpl(String spec) {
    PrimitiveType primitiveType = PrimitiveType.getInstance(spec);
    if (primitiveType == null) {
      throw BeneratorExceptionFactory.getInstance().syntaxErrorForText("Not a simple type", spec);
    }
    return spec;
  }

}
