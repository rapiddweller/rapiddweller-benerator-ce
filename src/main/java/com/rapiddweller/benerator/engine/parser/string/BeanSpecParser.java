/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.parser.AbstractParser;

import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.common.Expression;

/**
 * Parses a bean specification.<br/><br/>
 * Created: 10.12.2021 17:58:57
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeanSpecParser extends AbstractParser<Expression<?>> {

  public BeanSpecParser() {
    super("bean spec");
  }

  @Override @SuppressWarnings("unchecked")
  protected Expression<Object> parseImpl(String spec) {
    return (Expression<Object>) DatabeneScriptParser.parseBeanSpec(spec);
  }

}
