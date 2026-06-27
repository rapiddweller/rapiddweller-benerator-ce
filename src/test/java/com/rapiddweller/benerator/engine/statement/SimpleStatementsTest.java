/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.common.OperationFailed;
import com.rapiddweller.script.expression.ConstantExpression;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the small, self-contained engine statements (error / echo / comment).<br/><br/>
 * @author rapiddweller
 */
public class SimpleStatementsTest extends AbstractStatementTest {

  @Test
  public void testErrorStatement_raisesOperationFailedWithMessageAndExitCode() {
    ErrorStatement statement = new ErrorStatement("BEN-1", 7, "boom");
    assertEquals("BEN-1", statement.id);
    assertEquals("boom", statement.message);
    assertEquals(7, statement.exitCode);
    try {
      statement.execute(context);
      fail("ErrorStatement should raise OperationFailed");
    } catch (OperationFailed e) {
      assertTrue(String.valueOf(e.getMessage()).contains("boom"));
    }
  }

  @Test
  public void testEchoStatement_printsAndReturnsTrue() {
    EchoStatement statement = new EchoStatement(
        new ConstantExpression<>("hello"), new ConstantExpression<>(EchoType.console));
    assertNotNull(statement.getExpression());
    assertTrue(statement.execute(context));
  }

  @Test
  public void testCommentStatement_keepsCommentAndExecutes() {
    CommentStatement statement = new CommentStatement("a remark");
    assertEquals("a remark", statement.getComment());
    statement.execute(context); // logs to the COMMENT category; just exercise the path
  }
}
