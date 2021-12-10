/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.exception.SyntaxError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests the {@link IdParser}.<br/><br/>
 * Created: 10.12.2021 13:21:21
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class IdParserTest {

  private final IdParser p = new IdParser();

  @Test
  public void testOK() {
    assertEquals("ABC", p.parse("ABC"));
    assertEquals("__w", p.parse("__w"));
    assertEquals("ISO2", p.parse("ISO2"));
  }

  @Test
  public void testIllegal() {
    assertThrows(SyntaxError.class, () -> p.parse("+*-.!"));
  }

  @Test
  public void testEmpty() {
    assertThrows(SyntaxError.class, () -> p.parse(""));
  }

  @Test
  public void testNull() {
    assertThrows(SyntaxError.class, () -> p.parse(null));
  }

}
