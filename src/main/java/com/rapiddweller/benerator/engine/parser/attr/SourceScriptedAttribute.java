/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.script.expression.ConstantExpression;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SOURCE_SCRIPTED;

/**
 * {@link AttrInfo} for a 'sourceScripted' attribute.<br/><br/>
 * Created: 19.12.2021 22:40:36
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class SourceScriptedAttribute extends AttrInfo<Expression<Boolean>> {

  public SourceScriptedAttribute(String errorId) {
    super(ATT_SOURCE_SCRIPTED, false, errorId, new SourceScriptedParser());
  }

  static class SourceScriptedParser extends AbstractParser<Expression<Boolean>> {

    private static final BooleanParser BASE = new BooleanParser();

    protected SourceScriptedParser() {
      super("sourceScripted spec");
    }

    @Override
    protected Expression<Boolean> parseImpl(String spec) {
      if (!StringUtil.isEmpty(spec)) {
        return new ConstantExpression<>(BASE.parse(spec));
      } else {
        return new DefaultSourceScriptedExpression();
      }
    }
  }

  static class DefaultSourceScriptedExpression implements Expression<Boolean> {
    @Override
    public Boolean evaluate(Context context) {
      return ((BeneratorContext) context).isDefaultSourceScripted();
    }

    @Override
    public boolean isConstant() {
      return true;
    }
  }

}
