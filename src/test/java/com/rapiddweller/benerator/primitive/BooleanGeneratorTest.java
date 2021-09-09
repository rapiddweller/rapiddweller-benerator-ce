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

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the BooleanGenerator.<br/><br/>
 * Created: 09.06.2006 20:07:56
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class BooleanGeneratorTest extends GeneratorClassTest {

  private static final Logger logger = LoggerFactory.getLogger(BooleanGeneratorTest.class);

  /**
   * Instantiates a new Boolean generator test.
   */
  public BooleanGeneratorTest() {
    super(BooleanGenerator.class);
  }

  /**
   * Test distribution.
   *
   * @throws IllegalGeneratorStateException the illegal generator state exception
   */
  @Test
  public void testDistribution() throws IllegalGeneratorStateException {
    checkDistribution(0.5, 1000);
    checkDistribution(0.0, 1000);
    checkDistribution(0.1, 1000);
    checkDistribution(0.9, 1000);
    checkDistribution(1.0, 1000);
  }

  private static void checkDistribution(double trueProbability, int n) throws IllegalGeneratorStateException {
    BooleanGenerator generator = new BooleanGenerator((float) trueProbability);
    int[] count = new int[2];
    for (int i = 0; i < n; i++) {
      if (generator.generate()) {
        count[1]++;
      } else {
        count[0]++;
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("prob=" + trueProbability + ", n=" + n + ", falseCount=" + count[0] + ", trueCount=" + count[1]);
    }
    float ratio = (float) count[1] / n;
    assertEquals(trueProbability, ratio, 0.1);
  }

}
