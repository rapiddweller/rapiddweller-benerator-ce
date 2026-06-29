/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the {@link RandomWalkSequence}.<br/><br/>
 * Created: 2026
 * @author Alexander Kell
 */
public class RandomWalkSequenceTest extends GeneratorTest {

  @Test
  public void testIntegralNumberGenerator() {
    RandomWalkSequence seq = new RandomWalkSequence(BigDecimal.ONE, BigDecimal.ONE);
    NonNullGenerator<Integer> gen = seq.createNumberGenerator(Integer.class, 1, 10, 1, false);
    gen.init(context);
    for (int i = 0; i < 50; i++) {
      Integer v = gen.generate();
      assertTrue("out of range: " + v, v >= 1 && v <= 10);
    }
  }

  @Test
  public void testDoubleNumberGenerator() {
    RandomWalkSequence seq = new RandomWalkSequence(BigDecimal.ZERO.subtract(BigDecimal.ONE), BigDecimal.ONE);
    NonNullGenerator<Double> gen = seq.createNumberGenerator(Double.class, 0., 5., 0.5, false);
    gen.init(context);
    for (int i = 0; i < 50; i++) {
      Double v = gen.generate();
      assertTrue("out of range: " + v, v >= 0. && v <= 5.);
    }
  }

  /** maxStep null defaults to the number type's max value. */
  @Test
  public void testNullMaxDefaults() {
    RandomWalkSequence seq = new RandomWalkSequence();
    NonNullGenerator<Integer> gen = seq.createNumberGenerator(Integer.class, 1, null, 1, false);
    gen.init(context);
    assertNotNull(gen.generate());
  }

  /** minStep and maxStep both positive -> a skipping proxy is applied to the source. */
  @Test
  public void testApplyToSkip() {
    RandomWalkSequence seq = new RandomWalkSequence(BigDecimal.ONE, new BigDecimal(2));
    Generator<Integer> result = seq.applyTo(new ConstantGenerator<>(5), false);
    result.init(context);
    assertEquals(Integer.valueOf(5), GeneratorUtil.generateNonNull(result));
  }

  /** A unique random walk whose range includes zero cannot be guaranteed unique. */
  @Test
  public void testUniqueAcrossZeroFails() {
    RandomWalkSequence seq = new RandomWalkSequence(BigDecimal.ZERO.subtract(BigDecimal.ONE), BigDecimal.ONE);
    try {
      seq.createNumberGenerator(Long.class, -5L, 5L, 1L, true);
      fail("expected InvalidGeneratorSetupException");
    } catch (InvalidGeneratorSetupException e) {
      // expected
    }
  }

  @Test
  public void testSetters() {
    RandomWalkSequence seq = new RandomWalkSequence();
    seq.setMinStep(BigDecimal.ZERO);
    seq.setMaxStep(BigDecimal.TEN);
    seq.setInitial(BigDecimal.ONE);
    NonNullGenerator<Long> gen = seq.createNumberGenerator(Long.class, 0L, 100L, 1L, false);
    gen.init(context);
    assertNotNull(gen.generate());
  }

}
