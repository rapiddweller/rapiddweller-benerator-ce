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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.Context;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.DynamicExpression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link DynamicLongGenerator}.<br/><br/>
 * Created: 28.03.2010 12:36:26
 *
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class DynamicLongGeneratorTest extends GeneratorTest {

  private static final Expression<Long> ONE = ExpressionUtil.constant(1L);
  private static final Expression<Long> TWO = ExpressionUtil.constant(2L);
  private static final Expression<Long> THREE = ExpressionUtil.constant(3L);
  private static final Expression<Sequence> RANDOM_SEQUENCE = ExpressionUtil.constant(SequenceManager.RANDOM_SEQUENCE);
  private static final Expression<Sequence> STEP_SEQUENCE = ExpressionUtil.constant(SequenceManager.STEP_SEQUENCE);
  private static final Expression<Boolean> NOT_UNIQUE = ExpressionUtil.constant(false);

  /**
   * Test constant.
   */
  @Test
  public void testConstant() {
    Generator<Long> generator = new DynamicLongGenerator(ONE, THREE, TWO, RANDOM_SEQUENCE, NOT_UNIQUE);
    generator.init(context);
    Map<Long, AtomicInteger> productCounts = countProducts(generator, 100);
    assertEquals(2, productCounts.size());
    assertTrue(productCounts.containsKey(1L));
    assertTrue(productCounts.containsKey(3L));
  }

  /**
   * Test life cycle.
   */
  @Test
  public void testLifeCycle() {
    Generator<Long> generator = new DynamicLongGenerator(new IncrementExpression(1), new IncrementExpression(2),
        ONE, STEP_SEQUENCE, NOT_UNIQUE);
    generator.init(context); // min==1, max==2
    expectGeneratedSequenceOnce(generator, 1L, 2L);
    generator.reset(); // min==2, max==3
    expectGeneratedSequenceOnce(generator, 2L, 3L);
  }

  /**
   * The type Increment expression.
   */
  static class IncrementExpression extends DynamicExpression<Long> {

    private long value;

    /**
     * Instantiates a new Increment expression.
     *
     * @param value the value
     */
    public IncrementExpression(long value) {
      this.value = value;
    }

    @Override
    public Long evaluate(Context context) {
      return value++;
    }
  }

}
