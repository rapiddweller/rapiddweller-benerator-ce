/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.expression.GlobalMaxCountExpression;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.script.expression.MinExpression;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_COUNT;
import static com.rapiddweller.common.StringUtil.isEmpty;

/**
 * {@link AttrInfo} for 'count' attributes.<br/><br/>
 * Created: 19.12.2021 20:48:04
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CountAttribute extends AttrInfo<Expression<Long>> {

  public CountAttribute(String errorId, boolean required) {
    super(ATT_COUNT, required, errorId, new CountParser());
  }

  static class CountParser extends ScriptableParser<Long> {

    public CountParser() {
      super(new NonNegativeLongParser(), Long.class);
    }

    @Override
    protected Expression<Long> parseImpl(String spec) {
      GlobalMaxCountExpression maxCount = new GlobalMaxCountExpression();
      if ("unbounded".equals(spec) || isEmpty(spec)) {
        return maxCount;
      } else {
        return new MinExpression<>(super.parseImpl(spec), maxCount);
      }
    }
  }

}
