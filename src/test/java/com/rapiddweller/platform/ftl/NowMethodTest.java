package com.rapiddweller.platform.ftl;

import freemarker.template.SimpleDate;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * The type Now method test.
 */
public class NowMethodTest {
  /**
   * Test exec.
   */
  @Test
  public void testExec() {
    NowMethod nowMethod = new NowMethod();
    assertEquals(2, ((SimpleDate) nowMethod.exec(new ArrayList<>())).getDateType());
  }
}

