/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.engine.expression.TypedScriptExpression;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.script.Expression;

/**
 * Parses scripts.<br/><br/>
 * Created: 09.12.2021 17:20:00
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ScriptParser<E> extends AbstractParser<Expression<E>> {

  private final Class<E> expressionResultType;

  public ScriptParser(Class<E> expressionResultType) {
    super(expressionResultType.getSimpleName() + " script");
    this.expressionResultType = expressionResultType;
  }

  protected Expression<E> parseImpl(String spec) {
    return new TypedScriptExpression<>(spec, expressionResultType);
  }

}
