/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AbstractBeneratorDescriptorParser}.<br/><br/>
 * Created: 06.09.2021 17:50:28
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class AbstractBeneratorDescriptorParserTest {

  @Test
  public void testNormalizeAttributeName() {
    assertEquals("id", AbstractBeneratorDescriptorParser.normalizeAttributeName("id"));
    assertEquals("pageSize", AbstractBeneratorDescriptorParser.normalizeAttributeName("page.size"));
    assertEquals("enableAutoCommit", AbstractBeneratorDescriptorParser.normalizeAttributeName("enable.auto.commit"));
  }
}
