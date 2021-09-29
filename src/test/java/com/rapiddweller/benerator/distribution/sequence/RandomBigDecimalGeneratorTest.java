/*
 * (c) Copyright 2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.validator.ConstantValidator;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Tests the {@link RandomBigDecimalGenerator}.<br/><br/>
 * Created: 29.09.2021 08:41:21
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class RandomBigDecimalGeneratorTest extends GeneratorClassTest {

  private static final BigDecimal MINUS_TWO = new BigDecimal("-2");
  private static final BigDecimal MINUS_ONE = new BigDecimal("-1");
  private static final BigDecimal ZERO = BigDecimal.ZERO;
  private static final BigDecimal ONE = BigDecimal.ONE;
  private static final BigDecimal TWO = new BigDecimal("2");
  private static final BigDecimal THREE = new BigDecimal("3");

  public RandomBigDecimalGeneratorTest() {
    super(RandomBigDecimalGenerator.class);
  }

  @Test
  public void testZeroRange() {
    RandomBigDecimalGenerator generator = new RandomBigDecimalGenerator(THREE, THREE);
    generator.init(context);
    expectGenerations(generator, 3000, new ConstantValidator(THREE));
  }

  @Test
  public void testSimple() {
    RandomBigDecimalGenerator generator = new RandomBigDecimalGenerator(ZERO, ONE);
    generator.init(context);
    Set<BigDecimal> expectedSet = CollectionUtil.toSet(ZERO, ONE);
    checkEqualDistribution(generator, 3000, 0.1, expectedSet);
  }

  @Test
  public void testGranularity() {
    RandomBigDecimalGenerator generator = new RandomBigDecimalGenerator(MINUS_TWO, TWO, TWO);
    generator.init(context);
    Set<BigDecimal> expectedSet = CollectionUtil.toSet(MINUS_TWO, ZERO, TWO);
    checkEqualDistribution(generator, 3000, 0.1, expectedSet);
  }

  @Test
  public void testGranularityOffset() {
    RandomBigDecimalGenerator generator = new RandomBigDecimalGenerator(MINUS_ONE, THREE, TWO);
    generator.init(context);
    Set<BigDecimal> expectedSet = CollectionUtil.toSet(MINUS_ONE, ONE, THREE);
    checkEqualDistribution(generator, 3000, 0.1, expectedSet);
  }

  @Test
  public void testEqualDistribution() {
    Set<BigDecimal> expectedSet = CollectionUtil.toSet(ZERO, ONE, TWO, THREE);
    checkEqualDistribution(new RandomBigDecimalGenerator(ZERO, THREE), 10000, 0.1, expectedSet);
  }

}
