/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.common.parser.FullyQualifiedNameParser;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests the {@link ListParser}.<br/><br/>
 * Created: 10.12.2021 13:01:28
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ListParserTest {

  private final ListParser<String> p = new ListParser<>(new FullyQualifiedNameParser());

  @Test
  public void testPlain() {
    assertArrayEquals(new String[] { "Alice", "Bob", "Charly" },
        p.parse(" Alice ,   Bob, Charly      "));
  }

  @Test
  public void testFQN() {
    assertArrayEquals(new String[] { "a.b", "x.y.z" }, p.parse(" a.b ,   x.y.z      "));
  }

  @Test
  public void testSingleElement() {
    assertArrayEquals(new String[] { "a.b" }, p.parse(" a.b      "));
  }

  @Test
  public void testEmpty() {
    assertArrayEquals(new String[0], p.parse("     "));
  }

  @Test
  public void testIllegal() {
    assertThrows(SyntaxError.class, () -> p.parse(";@!%"));
  }

  @Test
  public void testNull() {
    assertThrows(SyntaxError.class, () -> p.parse(null));
  }

}
