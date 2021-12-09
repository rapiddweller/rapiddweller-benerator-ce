/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.Level;
import com.rapiddweller.common.parser.TypedParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses an {@link ErrorHandler} sspec.<br/><br/>
 * Created: 08.12.2021 16:38:14
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ErrorHandlerParser extends TypedParser<ErrorHandler> {

  private static final Map<String, ErrorHandler> handlers = new HashMap<>();

  static {
    createErrorHandler(Level.trace);
    createErrorHandler(Level.debug);
    createErrorHandler(Level.info);
    createErrorHandler(Level.warn);
    createErrorHandler(Level.error);
    createErrorHandler(Level.fatal);
  }

  private static void createErrorHandler(Level level) {
    ErrorHandler handler = new ErrorHandler("Benerator", level);
    handlers.put(level.name(), handler);
  }

  public ErrorHandlerParser() {
    super("error handler specification", ErrorHandler.class);
  }

  @Override
  protected ErrorHandler parseImpl(String spec) {
    ErrorHandler result = handlers.get(spec);
    if (result == null) {
      throw syntaxError(spec, null);
    }
    return result;
  }

}
