/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.exception.IllegalArgumentError;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** Tests {@link NullInjectingGeneratorProxy} at the deterministic null quotas 0 and 1. */
public class NullInjectingGeneratorProxyTest extends GeneratorTest {

  @Test
  public void testQuotaZero_passesSourceThrough() {
    Generator<Integer> g = new NullInjectingGeneratorProxy<>(new SequenceTestGenerator<>(1, 2, 3), 0.0);
    g.init(context);
    expectGeneratedSequence(g, 1, 2, 3).withCeasedAvailability();
  }

  @Test
  public void testQuotaOne_alwaysInjectsNull() {
    Generator<Integer> g = new NullInjectingGeneratorProxy<>(new SequenceTestGenerator<>(1, 2, 3), 1.0);
    g.init(context);
    for (int i = 0; i < 3; i++) {
      ProductWrapper<Integer> w = g.generate(new ProductWrapper<>());
      assertNotNull("a null value is still a wrapper, not unavailability", w);
      assertNull(w.unwrap());
    }
  }

  @Test(expected = IllegalArgumentError.class)
  public void testIllegalQuotaRejected() {
    new NullInjectingGeneratorProxy<>(new SequenceTestGenerator<>(1), 1.5);
  }
}
