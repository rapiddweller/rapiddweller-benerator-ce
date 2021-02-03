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
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link DistributingGenerator}.<br/><br/>
 * Created: 21.07.2010 06:54:40
 *
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class DistributingGeneratorTest extends GeneratorTest {

  /**
   * Test.
   */
  @Test
  public void test() {
    SequenceTestGenerator<Integer> source = new SequenceTestGenerator<>(1, 2, 3);
    Distribution distribution = new TestDistribution();
    NonNullGenerator<Integer> generator = WrapperFactory.asNonNullGenerator(
        new DistributingGenerator<>(source, distribution, false));
    generator.init(context);
    assertEquals(Integer.valueOf(1), generator.generate());
    generator.reset();
    assertEquals(1, source.resetCount);
    assertEquals(Integer.valueOf(1), generator.generate());
    generator.close();
    assertEquals(2, source.generateCount);
    assertEquals(1, source.closeCount);
  }

  /**
   * The type Test distribution.
   */
  public static class TestDistribution implements Distribution {

    @Override
    public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
      return source;
    }

    @Override
    public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity,
                                                                        boolean unique) {
      throw new UnsupportedOperationException("not implemented");
    }

  }

}
