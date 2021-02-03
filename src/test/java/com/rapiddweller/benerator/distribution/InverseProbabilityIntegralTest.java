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

package com.rapiddweller.benerator.distribution;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link CumulativeDistributionFunction}.<br/><br/>
 * Created: 12.03.2010 15:06:33
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class InverseProbabilityIntegralTest extends GeneratorTest {

  private final Fcn fcn = new Fcn();

  /**
   * Test create double generator unique.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateDoubleGenerator_unique() {
    fcn.createNumberGenerator(Double.class, 1., 4., 0.5, true);
  }

  /**
   * Test create double generator not unique.
   */
  @Test
  public void testCreateDoubleGenerator_notUnique() {
    Generator<Double> generator = fcn.createNumberGenerator(Double.class, 1., 4., 0.5, false);
    generator.init(context);
    int n = 1000;
    Map<Double, AtomicInteger> counts = countProducts(generator, n);
    assertEquals(7, counts.size());
    for (double d = 1; d <= 4; d += 0.5) {
      assertEquals(1. / 7, counts.get(d).doubleValue() / n, 0.05);
    }
  }

  /**
   * Test apply unique.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testApply_unique() {
    Generator<String> source = new SequenceTestGenerator<>("A", "B");
    source.init(context);
    fcn.applyTo(source, true);
  }

  /**
   * Test apply not unique.
   */
  @Test
  public void testApply_notUnique() {
    Generator<String> source = new SequenceTestGenerator<>("A", "B");
    source.init(context);
    Generator<String> generator = fcn.applyTo(source, false);
    generator.init(context);
    int n = 1000;
    Map<String, AtomicInteger> counts = countProducts(generator, n);
    assertEquals(2, counts.size());
    assertEquals(0.5, counts.get("A").doubleValue() / n, 0.05);
    assertEquals(0.5, counts.get("B").doubleValue() / n, 0.05);
  }

  /**
   * The type Fcn.
   */
  static class Fcn extends CumulativeDistributionFunction {

    @Override
    public double inverse(double probability) {
      return probability * 8;
    }

    @Override
    public double cumulativeProbability(double value) {
      return value / 8;
    }

  }

}
