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

import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link DynamicCountGenerator}.<br/><br/>
 * Created: 28.03.2010 12:57:38
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DynamicCountGeneratorTest extends GeneratorTest {

  private static final Expression<Long> ONE = ExpressionUtil.constant(1L);
  private static final Expression<Long> TWO = ExpressionUtil.constant(2L);
  private static final Expression<Long> NULL = ExpressionUtil.constant(null);
  private static final Expression<Sequence> STEP = ExpressionUtil.constant(SequenceManager.STEP_SEQUENCE);
  private static final Expression<Boolean> FALSE = ExpressionUtil.constant(false);

  /**
   * Test normal.
   */
  @Test
  public void testNormal() {
    DynamicCountGenerator generator = new DynamicCountGenerator(ONE, TWO, ONE, STEP, FALSE, false);
    generator.init(context);
    expectGeneratedSequence(generator, 1L, 2L);
    generator.close();
  }

  /**
   * Test max is null.
   */
  @Test
  public void testMaxIsNull() {
    DynamicCountGenerator generator = new DynamicCountGenerator(ONE, NULL, ONE, STEP, FALSE, true);
    generator.init(context);
    assertEquals(1L, GeneratorUtil.generateNonNull(generator).longValue());
    generator.close();
  }

}
