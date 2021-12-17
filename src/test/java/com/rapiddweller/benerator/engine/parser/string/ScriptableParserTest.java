/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.common.parser.PositiveIntegerParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ConstantExpression;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests the {@link ScriptableParser}.<br/><br/>
 * Created: 10.12.2021 13:21:33
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ScriptableParserTest {

  Parser<Expression<Integer>> p = new ScriptableParser<>(new PositiveIntegerParser());

  @Test
  public void testConstant() {
    assertEquals(new ConstantExpression<>(  1), p.parse("1"));
    assertEquals(new ConstantExpression<>(123), p.parse("123"));
  }

  @Test
  public void testScripts() {
    assertEquals(  1, (int) p.parse("{ftl:${1}}").evaluate(null));
    assertEquals(154, (int) p.parse("{ftl:${ 265 - 111 }}").evaluate(null));
  }

  @Test
  public void testIllegal() {
    assertThrows(SyntaxError.class, () -> p.parse("Alice"));
    assertThrows(SyntaxError.class, () -> p.parse(""));
    assertThrows(SyntaxError.class, () -> p.parse("-1"));
    assertThrows(SyntaxError.class, () -> p.parse("0"));
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
