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
import org.junit.Test;

/**
 * Tests the {@link CumulatedDoubleGenerator}.
 * Created: 07.06.2006 20:23:39
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class CumulatedDoubleGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Cumulated double generator test.
   */
  public CumulatedDoubleGeneratorTest() {
    super(CumulatedDoubleGenerator.class);
  }

  /**
   * Test single.
   */
  @Test
  public void testSingle() {
    checkProductSet(createAndInit(0, 0), 100, CollectionUtil.toSet(0.));
    checkProductSet(createAndInit(-1, -1), 100, CollectionUtil.toSet(-1.));
    checkProductSet(createAndInit(1, 1), 100, CollectionUtil.toSet(1.));
    checkProductSet(createAndInit(1, 1, 1), 100, CollectionUtil.toSet(1.));
  }

  /**
   * Test range.
   */
  @Test
  public void testRange() {
    checkProductSet(createAndInit(0, 1, 1), 1000, CollectionUtil.toSet(0., 1.));
    checkProductSet(createAndInit(1, 2, 1), 1000, CollectionUtil.toSet(1., 2.));
    checkProductSet(createAndInit(-2, -1, 1), 1000, CollectionUtil.toSet(-2., -1.));
    checkProductSet(createAndInit(-1, 0, 1), 1000, CollectionUtil.toSet(-1., 0.));
    checkProductSet(createAndInit(-1, 1, 1), 1000, CollectionUtil.toSet(-1., 0., 1.));
  }

  /**
   * Test granularity.
   */
  @Test
  public void testGranularity() {
    checkProductSet(createAndInit(1, 3, 2), 100, CollectionUtil.toSet(1., 3.));
    checkProductSet(createAndInit(-3, -1, 2), 100, CollectionUtil.toSet(-3., -1.));
    checkProductSet(createAndInit(-1, 1, 2), 100, CollectionUtil.toSet(-1., 1.));
  }

  private CumulatedDoubleGenerator createAndInit(int min, int max) {
    return initialize(new CumulatedDoubleGenerator(min, max));
  }

  private CumulatedDoubleGenerator createAndInit(int min, int max, int granularity) {
    return initialize(new CumulatedDoubleGenerator(min, max, granularity));
  }

}
