/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.model.data;

import com.rapiddweller.common.Encodings;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link SimpleTypeDescriptor}.<br/><br/>
 * Created: 30.09.2021 20:17:35
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class SimpleTypeDescriptorTest {
  DataModel m;
  DescriptorProvider p;

  @Before
  public void setUp() {
    m = new DataModel();
    p = new DefaultDescriptorProvider("default", m);
  }

  @Test
  public void testParent() {
    SimpleTypeDescriptor stringType = m.getPrimitiveTypeDescriptor(String.class);
    assertNotNull(stringType);
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("mystring", p, stringType);
    assertEquals(stringType, d.getParent());
  }

  @Test
  public void testNumberDetails() {
    SimpleTypeDescriptor intType = m.getPrimitiveTypeDescriptor(Integer.class);
    assertNotNull(intType);
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("number", p, intType);
    // test 1
    d.withMin("10").withMax("20").withGranularity("2").withDistribution("increment");
    d.setMinInclusive(true);
    d.setMaxInclusive(true);
    assertEquals("10", d.getMin());
    assertEquals("20", d.getMax());
    assertEquals("2", d.getGranularity());
    assertEquals("increment", d.getDistribution());
    assertEquals(true, d.isMinInclusive());
    assertEquals(true, d.isMaxInclusive());
    // test 2 with different values to make sure we did not accidentally use default values in test 1 and missed a bug
    d.withMin("100").withMax("200").withGranularity("20").withDistribution("random");
    d.setMinInclusive(false);
    d.setMaxInclusive(false);
    assertEquals("100", d.getMin());
    assertEquals("200", d.getMax());
    assertEquals("20", d.getGranularity());
    assertEquals("random", d.getDistribution());
    assertEquals(false, d.isMinInclusive());
    assertEquals(false, d.isMaxInclusive());
  }

  @Test
  public void testLengthDetails() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("number", p);
    // test 1
    d.setMinLength(10);
    d.setMaxLength(20);
    d.setLengthDistribution("increment");
    assertEquals(10, (int) d.getMinLength());
    assertEquals(20, (int) d.getMaxLength());
    assertEquals("increment", d.getLengthDistribution());
    // test 2 with different values to make sure we did not accidentally use default values in test 1 and missed a bug
    d.setMinLength(100);
    d.setMaxLength(200);
    d.setLengthDistribution("random");
    assertEquals(100, (int) d.getMinLength());
    assertEquals(200, (int) d.getMaxLength());
    assertEquals("random", d.getLengthDistribution());
  }

  @Test
  public void testDiverseDetails() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("number", p);
    d.setTrueQuota(0.5);
    d.setValues("1,2,3");
    assertEquals(0.5, d.getTrueQuota(), 0.00001);
    assertEquals("1,2,3", d.getValues());
  }

  @Test
  public void testConstant() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("number", p);
    d.setConstant("C");
    assertEquals("C", d.getConstant());
  }

  @Test
  public void testRowBased() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.isRowBased());
    d.setRowBased(false);
    assertFalse(d.isRowBased());
  }

  @Test
  public void testFilter() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getFilter());
    d.setFilter("true");
    assertEquals("true", d.getFilter());
  }

  @Test
  public void testCondition() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getCondition());
    d.setCondition("true");
    assertEquals("true", d.getCondition());
  }

  @Test
  public void testFormat() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getFormat());
    d.setFormat(Format.formatted);
    assertEquals(Format.formatted, d.getFormat());
  }

  @Test
  public void testSegment() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getSegment());
    d.setSegment("main");
    assertEquals("main", d.getSegment());
  }

  @Test
  public void testSubSelector() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getSubSelector());
    d.setSubSelector("subs");
    assertEquals("subs", d.getSubSelector());
  }

  @Test
  public void testEmptyMarker() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getEmptyMarker());
    d.setEmptyMarker("<x>");
    assertEquals("<x>", d.getEmptyMarker());
  }

  @Test
  public void testNullMarker() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getNullMarker());
    d.setNullMarker("-");
    assertEquals("-", d.getNullMarker());
  }

  @Test
  public void testEncoding() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getEncoding());
    d.setEncoding(Encodings.ASCII);
    assertEquals(Encodings.ASCII, d.getEncoding());
  }

  @Test
  public void testScope() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getScope());
    d.setScope("supi");
    assertEquals("supi", d.getScope());
  }

  @Test
  public void testLocale() {
    SimpleTypeDescriptor d = new SimpleTypeDescriptor("x", p);
    assertNull(d.getLocale());
    d.setLocaleId("de_DE");
    assertEquals(Locale.GERMANY, d.getLocale());
  }

}
