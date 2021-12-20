/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.EncodingAttribute;
import com.rapiddweller.common.SystemInfo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link EncodingAttribute}.<br/><br/>
 * Created: 19.12.2021 23:00:24
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class EncodingAttributeTest {

  EncodingAttribute.EncodingParser parser = new EncodingAttribute.EncodingParser();

  @Test
  public void testParse_regular() {
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    context.setDefaultEncoding("iso-8859-1");
    assertEquals("UTF-8", parser.parse("UTF-8").evaluate(context));
  }

  @Test
  public void testParse_fallback() {
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    context.setDefaultEncoding("iso-8859-1");
    assertEquals("iso-8859-1", parser.parse(null).evaluate(context));
  }

  @Test
  public void testParse_default() {
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    assertEquals(SystemInfo.getFileEncoding(), parser.parse(null).evaluate(context));
  }

}
