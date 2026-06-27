/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** Tests {@link MessageGenerator}: formats a MessageFormat pattern from source generators, and
 *  reports unavailability (not NPE) once a source is exhausted. */
public class MessageGeneratorTest extends GeneratorTest {

  @Test
  public void testFormatsPatternFromSources() {
    MessageGenerator g = new MessageGenerator(
        "Hi {0} #{1}", new SequenceTestGenerator<>("Bob"), new SequenceTestGenerator<>(7));
    g.init(context);
    ProductWrapper<String> w = g.generate(new ProductWrapper<>());
    assertNotNull(w);
    assertEquals("Hi Bob #7", w.unwrap());
  }

  /** Regression: once the source is exhausted the generator must return unavailability, not NPE. */
  @Test
  public void testReturnsUnavailableWhenSourceExhausts() {
    MessageGenerator g = new MessageGenerator("X{0}", new SequenceTestGenerator<>(1));
    g.init(context);
    assertEquals("X1", g.generate(new ProductWrapper<>()).unwrap());
    assertNull(g.generate(new ProductWrapper<>()));
  }
}
