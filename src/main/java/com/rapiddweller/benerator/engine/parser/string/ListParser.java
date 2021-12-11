/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.common.parser.TypedParser;

/**
 * Parses a list of elements.<br/><br/>
 * Created: 10.12.2021 12:54:30
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ListParser<E> extends AbstractParser<E[]> {

  private final TypedParser<E> elementParser;

  public ListParser(TypedParser<E> elementParser) {
    super(elementParser.getDescription() + " list");
    this.elementParser = elementParser;
  }

  @Override
  protected E[] parseImpl(String spec) {
    if (spec.isBlank()) {
      return emptyArray();
    } else {
      return parseList(spec);
    }
  }

  private E[] emptyArray() {
    return ArrayUtil.newInstance(elementParser.getResultType(), 0);
  }

  private E[] parseList(String spec) {
    String[] tokens = spec.split(",");
    E[] result = ArrayUtil.newInstance(elementParser.getResultType(), tokens.length);
    for (int i = 0; i < tokens.length; i++) {
      result[i] = elementParser.parse(StringUtil.trim(tokens[i]));
    }
    return result;
  }

}
