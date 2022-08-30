/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.Level;
import com.rapiddweller.common.exception.SyntaxError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests the {@link GlobalErrorHandlerParser}.<br/><br/>
 * Created: 10.12.2021 13:20:13
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ErrorHandlerParserTest {

  private final GlobalErrorHandlerParser p = new GlobalErrorHandlerParser();

  @Test
  public void testOK() {
    assertEquals(new ErrorHandler("Benerator", Level.trace), p.parse("trace"));
    assertEquals(new ErrorHandler("Benerator", Level.fatal), p.parse("fatal"));
  }

  @Test
  public void testEmpty() {
    assertThrows(SyntaxError.class, () -> p.parse(""));
  }

  @Test
  public void testIllegal() {
    assertThrows(SyntaxError.class, () -> p.parse(";@!%"));
    assertThrows(SyntaxError.class, () -> p.parse("none"));
  }

  @Test
  public void testNull() {
    assertThrows(SyntaxError.class, () -> p.parse(null));
  }

}
