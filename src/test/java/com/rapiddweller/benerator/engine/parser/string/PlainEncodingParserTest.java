/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.engine.parser.attr.EncodingAttribute;
import com.rapiddweller.common.exception.SyntaxError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests the {@link PlainEncodingParser}.<br/><br/>
 * Created: 19.12.2021 23:05:23
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PlainEncodingParserTest {

  PlainEncodingParser p = new PlainEncodingParser();

  @Test
  public void testOK() {
    assertEquals("UTF-8", p.parse("UTF-8"));
    assertEquals("ASCII", p.parse("ASCII"));
    assertEquals("ISO-8859-1", p.parse("ISO-8859-1"));
  }

  @Test
  public void testIllegal() {
    assertThrows(SyntaxError.class, () -> p.parse("+*-.!"));
  }

  @Test
  public void testNull() {
    assertThrows(SyntaxError.class, () -> p.parse(null));
  }

  @Test
  public void testEmpty() {
    assertThrows(SyntaxError.class, () -> p.parse(""));
  }

}
