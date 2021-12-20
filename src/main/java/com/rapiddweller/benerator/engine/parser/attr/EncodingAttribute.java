/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.string.PlainEncodingParser;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.script.expression.ConstantExpression;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ENCODING;

/**
 * {@link AttrInfo} for an encoding.<br/><br/>
 * Created: 19.12.2021 22:17:07
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class EncodingAttribute extends AttrInfo<Expression<String>> {

  public EncodingAttribute(String errorId) {
    super(ATT_ENCODING, false, errorId, new EncodingParser());
  }

  public static class EncodingParser extends AbstractParser<Expression<String>> {

    private static final PlainEncodingParser BASE = new PlainEncodingParser();

    public EncodingParser() {
      super("encoding");
    }

    @Override
    protected Expression<String> parseImpl(String spec) {
      if (spec == null) {
        return new DefaultEncodingExpression();
      } else {
        return new ConstantExpression<>(BASE.parse(spec));
      }
    }
  }

  static class DefaultEncodingExpression implements Expression<String> {
    @Override
    public String evaluate(Context context) {
      return ((BeneratorContext) context).getDefaultEncoding();
    }

    @Override
    public boolean isConstant() {
      return true;
    }
  }

}
