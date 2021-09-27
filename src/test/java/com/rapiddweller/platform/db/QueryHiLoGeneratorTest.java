package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.engine.statement.EvaluateStatementTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryHiLoGeneratorTest {
  @Test
  public void testConstructor() {
    assertEquals("QueryHiLoGenerator[AsNonNullGenerator[QueryLongGenerator[Selector]],100]",
        (new QueryHiLoGenerator("Selector", new EvaluateStatementTest.StSys())).toString());
  }
}

