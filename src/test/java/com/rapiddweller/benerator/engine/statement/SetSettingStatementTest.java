/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.script.expression.ConstantExpression;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Tests {@link SetSettingStatement}: evaluates the value and stores it in the context. */
public class SetSettingStatementTest extends AbstractStatementTest {

  @Test
  public void testStoresSettingInContext() {
    new SetSettingStatement("greeting", new ConstantExpression<>("hello")).execute(context);
    assertEquals("hello", context.get("greeting"));
  }
}
