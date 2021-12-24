/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.converter.LiteralParserConverter;
import com.rapiddweller.common.parser.AbstractParser;

/**
 * Parses strings that represent the minimum or maximum of a value range.<br/><br/>
 * Created: 15.12.2021 15:07:27
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class MinMaxParser<T> extends AbstractParser<Comparable<T>> {

  public MinMaxParser(String description) {
    super(description);
  }

  @Override
  protected Comparable<T> parseImpl(String spec) {
    Object result = LiteralParserConverter.parse(spec);
    if (!(result instanceof Comparable)) {
      throw BeneratorExceptionFactory.getInstance().syntaxErrorForText("Not a comparable type", spec);
    }
    if (result instanceof String) {
      throw BeneratorExceptionFactory.getInstance().syntaxErrorForText("Not a valid " + getDescription(), spec);
    }
    return (Comparable<T>) result;
  }

}
