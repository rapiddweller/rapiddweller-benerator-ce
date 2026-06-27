/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;
import java.util.function.Function;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** Regression: when the source yields a null Number (e.g. via NullInjectingGeneratorProxy), every
 *  As*GeneratorWrapper must pass the null through, like AsInteger already did -- not throw NPE. */
public class AsTypeNullHandlingTest extends GeneratorTest {

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void assertNullPassedThrough(Function<Generator<Integer>, Generator<?>> wrap) {
    Generator<Integer> nullSource = new NullInjectingGeneratorProxy<>(new SequenceTestGenerator<>(1, 2), 1.0);
    Generator<?> g = wrap.apply(nullSource);
    g.init(context);
    ProductWrapper w = g.generate(new ProductWrapper());
    assertNotNull("a wrapped null is still a wrapper, not unavailability", w);
    assertNull(w.unwrap());
  }

  @Test public void testAsByte()   { assertNullPassedThrough(AsByteGeneratorWrapper::new); }
  @Test public void testAsShort()  { assertNullPassedThrough(AsShortGeneratorWrapper::new); }
  @Test public void testAsLong()   { assertNullPassedThrough(AsLongGeneratorWrapper::new); }
  @Test public void testAsFloat()  { assertNullPassedThrough(AsFloatGeneratorWrapper::new); }
  @Test public void testAsDouble() { assertNullPassedThrough(AsDoubleGeneratorWrapper::new); }
  @Test public void testAsBigInteger() { assertNullPassedThrough(AsBigIntegerGeneratorWrapper::new); }
}
