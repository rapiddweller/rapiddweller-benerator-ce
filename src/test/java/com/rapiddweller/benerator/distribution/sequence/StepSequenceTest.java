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
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests the {@link StepSequence}.<br/><br/>
 * Created: 20.07.2010 23:30:08
 *
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class StepSequenceTest extends GeneratorTest {

  /**
   * Test apply to limit.
   */
  @Test
  public void testApplyTo_limit() {
    expectGeneratedSequence(apply(1L, 1L, 1L), 1).withCeasedAvailability();
  }

  /**
   * Test create generator limit.
   */
  @Test
  public void testCreateGenerator_limit() {
    expectGeneratedSequence(numberGen(1L, 1L, 1L), 1L).withCeasedAvailability();
  }

  private Generator<Integer> apply(long initial, long increment, long limit) {
    Generator<Integer> source = new SequenceTestGenerator<>(1, 2, 3);
    StepSequence sequence = new StepSequence(
        new BigDecimal(initial), new BigDecimal(increment), new BigDecimal(limit));
    Generator<Integer> generator = sequence.applyTo(source, false);
    return initialize(generator);
  }

  private Generator<Long> numberGen(long initial, long increment, long limit) {
    StepSequence sequence = new StepSequence(
        new BigDecimal(initial), new BigDecimal(increment), new BigDecimal(limit));
    Generator<Long> generator = sequence.createNumberGenerator(Long.class, initial, limit, increment, false);
    return initialize(generator);
  }

}
