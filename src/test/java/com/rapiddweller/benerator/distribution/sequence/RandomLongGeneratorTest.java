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

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.validator.ConstantValidator;
import org.junit.Test;

/**
 * Tests the {@link RandomLongGenerator}.<br/><br/>
 * Created: 11.10.2006 23:03:30
 * @author Volker Bergmann
 * @since 0.1
 */
public class RandomLongGeneratorTest extends GeneratorClassTest {

  public RandomLongGeneratorTest() {
    super(RandomLongGenerator.class);
  }

  @Test
  public void testZeroRange() {
    RandomLongGenerator generator = new RandomLongGenerator(42L, 42L);
    generator.init(context);
    expectGenerations(generator, 3000, new ConstantValidator(42L));
  }

  @Test
  public void testSimple() {
    RandomLongGenerator generator = new RandomLongGenerator(0, 1L);
    generator.init(context);
    checkEqualDistribution(generator, 3000, 0.1, CollectionUtil.toSet(0L, 1L));
  }

  @Test
  public void testGranularity() {
    RandomLongGenerator generator = new RandomLongGenerator(-2, 2L, 2);
    generator.init(context);
    checkEqualDistribution(generator, 3000, 0.1, CollectionUtil.toSet(-2L, 0L, 2L));
  }

  @Test
  public void testGranularityOffset() {
    RandomLongGenerator generator = new RandomLongGenerator(-1, 3L, 2);
    generator.init(context);
    checkEqualDistribution(generator, 3000, 0.1, CollectionUtil.toSet(-1L, 1L, 3L));
  }

  @Test
  public void testEqualDistribution() {
    checkEqualDistribution(RandomLongGenerator.class, 0L, 3L, 1L, 1000, 0.1,
        0L, 1L, 2L, 3L);
  }

}
