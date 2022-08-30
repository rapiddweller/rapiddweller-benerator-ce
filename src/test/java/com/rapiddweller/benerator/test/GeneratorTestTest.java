/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.test;

import com.rapiddweller.benerator.distribution.sequence.RandomLongGenerator;
import com.rapiddweller.benerator.sample.SequenceGenerator;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.ui.BufferedTextPrinter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GeneratorTest}.<br/><br/>
 * Created: 28.09.2021 18:19:41
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class GeneratorTestTest extends GeneratorTest {

  @Test
  public void testPrintProducts() {
    BufferedTextPrinter printer = new BufferedTextPrinter();
    SequenceGenerator<Integer> generator = new SequenceGenerator<>(Integer.class, 1, 2, 3, 4);
    printProducts(generator, 4, printer);
    String expected = "1" + SystemInfo.getLineSeparator() + "2" + SystemInfo.getLineSeparator() + "3" + SystemInfo.getLineSeparator() + "4" + SystemInfo.getLineSeparator();
    assertEquals(expected, printer.toString());
  }

  @Test
  public void testEqualDistribution_ellipse() {
    checkEqualDistribution(RandomLongGenerator.class, 0L, 3L, 1L, 10000, 0.1,
        0L, 1L, 2L, 3L);
  }

  @Test
  public void testEqualDistribution_set() {
    checkEqualDistribution(RandomLongGenerator.class, 0L, 3L, 1L, 10000, 0.1,
        CollectionUtil.toSet(0L, 1L, 2L, 3L));
  }

}
