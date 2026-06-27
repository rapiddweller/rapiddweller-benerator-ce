/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** Tests {@link LastFlagGenerator}: appends a boolean "is last" flag to each generated array. */
public class LastFlagGeneratorTest extends GeneratorTest {
  @Test
  public void testAppendsLastFlagWhenIndexEqualsLength() {
    Generator<Object[]> source = new SequenceTestGenerator<>(new Object[][]{{1, 2}});
    LastFlagGenerator g = new LastFlagGenerator(source, 2); // index == length -> grow array by one
    g.init(context);
    ProductWrapper<Object[]> w = g.generate(new ProductWrapper<>());
    assertNotNull(w);
    Object[] product = w.unwrap();
    assertEquals(3, product.length);
    assertEquals(Boolean.FALSE, product[2]); // source carries no "last" tag
  }
}
