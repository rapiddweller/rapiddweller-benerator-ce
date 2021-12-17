/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.engine.expression.TypedScriptExpression;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.common.parser.TypedParser;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ConstantExpression;

/**
 * Parses a string which can be a script or a constant and converts it into an Expression.
 * The {@link #parse(String)} method will always return an Object of the {@link #base}
 * parser's result type.
 * For an implementation that allows a script to resolve other data types, see the class
 * {@link ScriptableObjectParser}<br/><br/>
 * Created: 08.12.2021 18:34:07
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ScriptableParser<E> extends AbstractParser<Expression<E>> {

  private final Parser<E> base;
  private final Class<E> expressionResultType;

  public ScriptableParser(TypedParser<E> base) {
    this(base, base.getResultType());
  }

  public ScriptableParser(Parser<E> base, Class<E> expressionResultType) {
    super(base.getDescription());
    this.base = base;
    this.expressionResultType = expressionResultType;
  }

  protected Expression<E> parseImpl(String spec) {
    if (ScriptUtil.isScript(spec)) {
      return new TypedScriptExpression<>(spec, expressionResultType);
    } else {
      return new ConstantExpression<>(base.parse(spec));
    }
  }

}
