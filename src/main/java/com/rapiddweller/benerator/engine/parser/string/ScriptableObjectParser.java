/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ConstantExpression;

/**
 * Parses a string which may be an expression, providing any expression result 'as is',
 * without further conversion.<br/><br/>
 * Created: 15.12.2021 08:23:08
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ScriptableObjectParser extends AbstractParser<Expression<Object>> {

  private boolean unescape;

  public ScriptableObjectParser(String description) {
    this(description, true);
  }

  public ScriptableObjectParser(String description, boolean unescape) {
    super(description);
    this.unescape = unescape;
  }

  protected Expression<Object> parseImpl(String spec) {
    if (unescape) {
      spec = StringUtil.unescape(spec);
    }
    if (ScriptUtil.isScript(spec)) {
      return new ScriptExpression<>(spec);
    } else {
      return new ConstantExpression<>(spec);
    }
  }

}
