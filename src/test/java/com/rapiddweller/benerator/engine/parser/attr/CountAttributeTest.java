/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link CountAttribute} class.<br/><br/>
 * Created: 20.12.2021 00:21:45
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class CountAttributeTest {

  DefaultBeneratorContext context = new DefaultBeneratorContext();
  { context.setMaxCount(10L); }

  CountAttribute.CountParser p = new CountAttribute.CountParser();

  @Test
  public void testParserPlain() {
    assertEquals(2L, (long) p.parse("2").evaluate(context));
  }

  @Test
  public void testParserFallback() {
    assertEquals(10L, (long) p.parse("100").evaluate(context));
  }

  @Test
  public void testParserUnbounded() {
    assertEquals(10L, (long) p.parse("unbounded").evaluate(context));
  }

}
