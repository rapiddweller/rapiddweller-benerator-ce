/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.exception.SyntaxError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests the {@link UriParser}.<br/><br/>
 * Created: 10.12.2021 13:20:25
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class UriParserTest {

  private final UriParser p = new UriParser();

  @Test
  public void testOK() {
    assertEquals("ABC", p.parse("ABC"));
    assertEquals("file://test.text", p.parse("file://test.text"));
    assertEquals("http://xyz.com/index.html", p.parse("http://xyz.com/index.html"));
  }

  @Test
  public void testIllegal() {
    assertThrows(SyntaxError.class, () -> p.parse(null));
    assertThrows(SyntaxError.class, () -> p.parse(""));
    assertThrows(SyntaxError.class, () -> p.parse("illegal://uri"));
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
