/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Tests the {@code As*GeneratorWrapper} family, which converts the {@link Number} products of a
 * source generator to a specific numeric type.<br/><br/>
 * @author rapiddweller
 */
public class AsTypeGeneratorWrapperTest extends GeneratorTest {

  @Test
  public void testAsByte() {
    Generator<Byte> g = new AsByteGeneratorWrapper<>(new SequenceTestGenerator<>(1, 2, 3));
    g.init(context);
    expectGeneratedSequence(g, (byte) 1, (byte) 2, (byte) 3).withCeasedAvailability();
  }

  @Test
  public void testAsShort() {
    Generator<Short> g = new AsShortGeneratorWrapper<>(new SequenceTestGenerator<>(1, 2, 3));
    g.init(context);
    expectGeneratedSequence(g, (short) 1, (short) 2, (short) 3).withCeasedAvailability();
  }

  /** Truncates the fractional part (intValue()). */
  @Test
  public void testAsInteger() {
    Generator<Integer> g = new AsIntegerGeneratorWrapper<>(new SequenceTestGenerator<>(1.7, 2.9));
    g.init(context);
    expectGeneratedSequence(g, 1, 2).withCeasedAvailability();
  }

  @Test
  public void testAsLong() {
    Generator<Long> g = new AsLongGeneratorWrapper<>(new SequenceTestGenerator<>(1.7, 2.9));
    g.init(context);
    expectGeneratedSequence(g, 1L, 2L).withCeasedAvailability();
  }

  @Test
  public void testAsFloat() {
    Generator<Float> g = new AsFloatGeneratorWrapper<>(new SequenceTestGenerator<>(1, 2));
    g.init(context);
    expectGeneratedSequence(g, 1.0f, 2.0f).withCeasedAvailability();
  }

  @Test
  public void testAsDouble() {
    Generator<Double> g = new AsDoubleGeneratorWrapper<>(new SequenceTestGenerator<>(1, 2));
    g.init(context);
    expectGeneratedSequence(g, 1.0, 2.0).withCeasedAvailability();
  }

  /** BigInteger.valueOf(longValue()). */
  @Test
  public void testAsBigInteger() {
    Generator<BigInteger> g = new AsBigIntegerGeneratorWrapper<>(new SequenceTestGenerator<>(5L, 7L));
    g.init(context);
    expectGeneratedSequence(g, BigInteger.valueOf(5), BigInteger.valueOf(7)).withCeasedAvailability();
  }
}
