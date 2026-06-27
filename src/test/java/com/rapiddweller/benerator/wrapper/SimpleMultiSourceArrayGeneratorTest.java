/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/** Tests {@link SimpleMultiSourceArrayGenerator}: one value from each source per generated array. */
public class SimpleMultiSourceArrayGeneratorTest extends GeneratorTest {

  @Test
  @SuppressWarnings("unchecked")
  public void testCombinesOneFromEachSource() {
    Generator<Integer[]> g = new SimpleMultiSourceArrayGenerator<>(Integer.class,
        new SequenceTestGenerator<>(1, 2, 3), new SequenceTestGenerator<>(10, 20, 30));
    g.init(context);
    assertArrayEquals(new Integer[]{1, 10}, GeneratorUtil.generateNonNull(g));
    assertArrayEquals(new Integer[]{2, 20}, GeneratorUtil.generateNonNull(g));
    assertArrayEquals(new Integer[]{3, 30}, GeneratorUtil.generateNonNull(g));
  }
}
