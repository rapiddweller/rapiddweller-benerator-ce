/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.script.BeneratorScriptFactory;
import com.rapiddweller.common.context.DefaultContext;
import com.rapiddweller.common.exception.ScriptException;
import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.common.Expression;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests the {@link ScriptParser}.<br/><br/>
 * Created: 10.12.2021 13:21:45
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ScriptParserTest {

  private static String defaultScriptEngine;

  @BeforeClass
  public static void setUp() {
    ScriptUtil.addFactory("ben", new BeneratorScriptFactory());
    defaultScriptEngine = ScriptUtil.getDefaultScriptEngine();
    ScriptUtil.setDefaultScriptEngine("ben");
  }

  @AfterClass
  public static void tearDown() {
    ScriptUtil.setDefaultScriptEngine(defaultScriptEngine);
  }

  private final Parser<Expression<Integer>> p = new ScriptParser<>(Integer.class);

  @Test
  public void testConstant() {
    assertEquals(123, (int) p.parse("123").evaluate(null));
  }

  @Test
  public void testScript() {
    assertEquals(154, (int) p.parse("265 - 111").evaluate(null));
  }

  @Test
  public void testContextAccess() {
    DefaultContext ctx = new DefaultContext();
    ctx.set("x", 5);
    assertEquals(165, (int) p.parse("x*33").evaluate(ctx));
    assertEquals(165, (int) p.parse(" x * 33  ").evaluate(ctx));
  }

  @Test
  public void testIllegal() {
    Expression<Integer> expression = p.parse("3 * x");
    DefaultContext context = new DefaultContext();
    assertThrows(ScriptException.class, () -> expression.evaluate(context));
  }

  @Test(expected = SyntaxError.class)
  public void testEmpty() {
    p.parse("");
  }

  @Test(expected = SyntaxError.class)
  public void testNull() {
    p.parse(null);
  }

}
