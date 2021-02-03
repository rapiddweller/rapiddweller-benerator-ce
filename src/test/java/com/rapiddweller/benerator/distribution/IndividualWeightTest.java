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
import com.rapiddweller.benerator.sample.SequenceGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link IndividualWeight}.<br/>
 * <br/>
 * Created at 01.07.2009 16:39:01
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class IndividualWeightTest extends GeneratorTest {

  /**
   * Test create generator.
   */
  @Test(expected = Exception.class)
  public void testCreateGenerator() {
    createWeight().createNumberGenerator(Integer.class, 1, 3, 1, false);
  }

  /**
   * Test apply to.
   */
  @Test
  public void testApplyTo() {
    SequenceGenerator<Integer> source = new SequenceGenerator<>(Integer.class, 1, 2, 3);
    source.init(context);
    Generator<Integer> generator = createWeight().applyTo(source, false);
    generator.init(context);
    expectRelativeWeights(generator, 5000, 1, 1, 2, 2, 3, 3);
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  private static IndividualWeight<Integer> createWeight() {
    return new IndividualWeight<>() {
      @Override
      public double weight(Integer object) {
        return object;
      }
    };
  }

}
