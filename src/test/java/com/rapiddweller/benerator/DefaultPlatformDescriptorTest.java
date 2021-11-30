package com.rapiddweller.benerator;

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.xml.EvaluateParser;
import com.rapiddweller.format.xml.XMLElementParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link DefaultPlatformDescriptor}.<br/><br/>
 * @author Volker Bergmann
 */
public class DefaultPlatformDescriptorTest {
  @Test
  public void testConstructor() {
    List<XMLElementParser<Statement>> parsers = (new DefaultPlatformDescriptor("java.text")).getParsers();
    assertTrue(parsers instanceof java.util.ArrayList);
    assertTrue(parsers.isEmpty());
  }

  @Test
  public void testAddParser() {
    DefaultPlatformDescriptor defaultPlatformDescriptor = new DefaultPlatformDescriptor("java.text");
    defaultPlatformDescriptor.addParser(new EvaluateParser());
    assertEquals(1, defaultPlatformDescriptor.getParsers().size());
  }
}

