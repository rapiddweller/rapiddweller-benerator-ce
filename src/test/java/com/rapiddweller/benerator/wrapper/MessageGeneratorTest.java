/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
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

  /** No-arg construction with property setters, plus the convenience accessors and lifecycle methods. */
  @Test
  public void testAccessorsAndNoArgConstruction() {
    MessageGenerator g = new MessageGenerator();
    g.setPattern("V={0}");
    g.setMinLength(0);
    g.setMaxLength(20);
    g.setSources(new Generator[] {new SequenceTestGenerator<>("a")});
    assertEquals("V={0}", g.getPattern());
    assertEquals(0, g.getMinLength());
    assertEquals(20, g.getMaxLength());
    assertEquals(String.class, g.getGeneratedType());
    g.init(context);
    assertEquals("V=a", g.generate()); // no-arg generate() convenience method
    assertNotNull(g.toString());
    g.isParallelizable();
    g.isThreadSafe();
    g.reset();
    g.close();
  }

  /** The length-bounded constructor variant. */
  @Test
  public void testLengthBoundedConstructor() {
    MessageGenerator g = new MessageGenerator("{0}", 1, 5, new SequenceTestGenerator<>("abc"));
    g.init(context);
    assertEquals("abc", g.generate());
  }

  /** init() without a pattern is an invalid setup. */
  @Test(expected = InvalidGeneratorSetupException.class)
  public void testInitWithoutPatternFails() {
    new MessageGenerator().init(context);
  }
}
