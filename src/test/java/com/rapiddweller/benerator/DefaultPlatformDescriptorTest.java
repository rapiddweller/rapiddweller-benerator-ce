package com.rapiddweller.benerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link DefaultPlatformDescriptor}.<br/><br/>
 * @author Volker Bergmann
 */
public class DefaultPlatformDescriptorTest {

  @Test
  public void testConstructor() {
    DefaultPlatformDescriptor desc = new DefaultPlatformDescriptor("java.text");
    assertEquals(0, desc.getParsers().length);
    assertEquals(0, desc.getClassesToImport().length);
    assertEquals(1, desc.getPackagesToImport().length);
    assertEquals("java.text", desc.getPackagesToImport()[0]);
  }

}

