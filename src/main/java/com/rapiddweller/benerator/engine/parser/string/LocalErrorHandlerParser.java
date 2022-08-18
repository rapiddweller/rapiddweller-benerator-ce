/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.Level;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.common.Expression;

import java.util.Objects;

/**
 * Parses a local error handler spec. If none is defined, it falls back to the global one.<br/><br/>
 * Created: 10.12.2021 18:27:59
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class LocalErrorHandlerParser extends AbstractParser<Expression<ErrorHandler>> {

  private final Parser<Expression<ErrorHandler>> realParser;
  private final Expression<ErrorHandler> fallback;

  public LocalErrorHandlerParser() {
    super("error handler expression");
    this.realParser = new ScriptableParser<>(new GlobalErrorHandlerParser());
    this.fallback = new ErrorHandlerFallback();
  }

  @Override
  protected Expression<ErrorHandler> parseImpl(String spec) {
    if (StringUtil.isEmpty(spec)) {
      return fallback;
    } else {
      return realParser.parse(spec);
    }
  }

  static class ErrorHandlerFallback implements Expression<ErrorHandler> {

    private final ErrorHandler defaultErrorHandler = new ErrorHandler("Benerator", Level.fatal);

    @Override
    public ErrorHandler evaluate(Context context) {
      String defaultLevel = ((BeneratorContext) context).getDefaultErrorHandler();
      ErrorHandler result = new ErrorHandler("Benerator", Level.valueOf(defaultLevel));
      return Objects.requireNonNullElse(result, this.defaultErrorHandler);
    }

    @Override
    public boolean isConstant() {
      return false;
    }
  }

}
