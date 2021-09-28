/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.test;

import com.rapiddweller.benerator.distribution.sequence.RandomLongGenerator;
import com.rapiddweller.benerator.sample.SequenceGenerator;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.ui.BufferedInfoPrinter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GeneratorTest}.<br/><br/>
 * Created: 28.09.2021 18:19:41
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class GeneratorTestTest extends GeneratorTest {

  @Test
  public void testPrintProducts() {
    BufferedInfoPrinter printer = new BufferedInfoPrinter();
    SequenceGenerator<Integer> generator = new SequenceGenerator<>(Integer.class, 1, 2, 3, 4);
    printProducts(generator, 4, printer);
    String expected = "1" + SystemInfo.LF + "2" + SystemInfo.LF + "3" + SystemInfo.LF + "4" + SystemInfo.LF;
    assertEquals(expected, printer.toString());
  }

  @Test
  public void testEqualDistribution_ellipse() {
    checkEqualDistribution(RandomLongGenerator.class, 0L, 3L, 1L, 1000, 0.1,
        0L, 1L, 2L, 3L);
  }

  @Test
  public void testEqualDistribution_set() {
    checkEqualDistribution(RandomLongGenerator.class, 0L, 3L, 1L, 1000, 0.1,
        CollectionUtil.toSet(0L, 1L, 2L, 3L));
  }

}
