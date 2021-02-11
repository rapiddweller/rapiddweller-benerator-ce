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

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the {@link StepDoubleGenerator}.<br/>
 * <br/>
 * Created: 26.07.2007 18:41:19
 *
 * @author Volker Bergmann
 */
public class StepDoubleGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Step double generator test.
   */
  public StepDoubleGeneratorTest() {
    super(StepDoubleGenerator.class);
  }

  /**
   * Test increment 1.
   *
   * @throws IllegalGeneratorStateException the illegal generator state exception
   */
  @Test
  public void testIncrement1() throws IllegalGeneratorStateException {
    // test increment 1
    StepDoubleGenerator simpleGenerator = new StepDoubleGenerator(1, 5, 1);
    simpleGenerator.init(context);
    expectGeneratedSequence(simpleGenerator, 1., 2., 3., 4., 5.).withCeasedAvailability();
  }

  /**
   * Test increment 2.
   *
   * @throws IllegalGeneratorStateException the illegal generator state exception
   */
  @Test
  public void testIncrement2() throws IllegalGeneratorStateException {
    // test increment 2
    StepDoubleGenerator oddGenerator = new StepDoubleGenerator(1, 5, 2);
    oddGenerator.init(context);
    expectGeneratedSequence(oddGenerator, 1., 3., 5.).withCeasedAvailability();
  }

  /**
   * Test initial.
   */
  @Test
  public void testInitial() {
    StepDoubleGenerator incGenerator = new StepDoubleGenerator(1., 5., 2., 2.);
    incGenerator.init(context);
    expectGeneratedSequence(incGenerator, 2., 4.).withCeasedAvailability();

    StepDoubleGenerator decGenerator = new StepDoubleGenerator(1., 5., -2.);
    decGenerator.init(context);
    expectGeneratedSequence(decGenerator, 5., 3., 1.).withCeasedAvailability();
  }

  /**
   * Test decrement.
   *
   * @throws IllegalGeneratorStateException the illegal generator state exception
   */
  @Test
  public void testDecrement() throws IllegalGeneratorStateException {
    StepDoubleGenerator simpleGenerator = new StepDoubleGenerator(1, 5, -1);
    simpleGenerator.init(context);
    expectGeneratedSequence(simpleGenerator, 5., 4., 3., 2., 1.).withCeasedAvailability();

    StepDoubleGenerator oddGenerator = new StepDoubleGenerator(1, 5, -2);
    oddGenerator.init(context);
    expectGeneratedSequence(oddGenerator, 5., 3., 1.).withCeasedAvailability();
  }

}
