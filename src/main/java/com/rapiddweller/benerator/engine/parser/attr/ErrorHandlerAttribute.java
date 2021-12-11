/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.expression.context.DefaultPageSizeExpression;
import com.rapiddweller.benerator.engine.parser.string.LocalErrorHandlerParser;
import com.rapiddweller.benerator.engine.parser.string.PageSizeParser;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.Level;
import com.rapiddweller.format.xml.AttributeInfo;
import com.rapiddweller.script.Expression;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ON_ERROR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PAGESIZE;

/**
 * {@link AttributeInfo} for the error handler.<br/><br/>
 * Created: 10.12.2021 19:54:07
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ErrorHandlerAttribute extends AttributeInfo<Expression<ErrorHandler>> {

  public ErrorHandlerAttribute(String errorId) {
    super(ATT_ON_ERROR, false, errorId, new LocalErrorHandlerParser(), new DefaultErrorHandlerExpression());
  }

  static class DefaultErrorHandlerExpression implements Expression<ErrorHandler> {
    @Override
    public ErrorHandler evaluate(Context context) {
      String spec = ((BeneratorContext) context).getDefaultErrorHandler();
      return new ErrorHandler("Benerator", Level.valueOf(spec));
    }
    @Override
    public boolean isConstant() {
      return false;
    }
  }
}
