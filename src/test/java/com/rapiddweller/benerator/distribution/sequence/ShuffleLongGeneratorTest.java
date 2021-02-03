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

import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the {@link ShuffleLongGenerator}.<br/><br/>
 * Created: 07.06.2006 20:23:39
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class ShuffleLongGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Shuffle long generator test.
   */
  public ShuffleLongGeneratorTest() {
    super(ShuffleLongGenerator.class);
  }

  /**
   * Test instantiation.
   */
  @Test
  public void testInstantiation() {
    new ShuffleLongGenerator();
    new ShuffleLongGenerator(0, 10);
    new ShuffleLongGenerator(0, 10, 1, 1);
  }

  /**
   * Test increment 0.
   */
  @Test(expected = InvalidGeneratorSetupException.class)
  public void testIncrement0() {
    ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 0);
    generator.init(context);
  }

  /**
   * Test increment 1.
   */
  @Test
  public void testIncrement1() {
    ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 1);
    generator.init(context);
    expectGeneratedSequence(generator, 0L, 1L, 2L, 3L).withCeasedAvailability();
  }

  /**
   * Test increment 2.
   */
  @Test
  public void testIncrement2() {
    ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 2);
    generator.init(context);
    expectGeneratedSequence(generator, 0L, 2L, 1L, 3L).withCeasedAvailability();
  }

  /**
   * Test increment 3.
   */
  @Test
  public void testIncrement3() {
    ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 3);
    generator.init(context);
    expectGeneratedSequence(generator, 0L, 3L, 1L, 2L).withCeasedAvailability();
  }

  /**
   * Test increment 4.
   */
  @Test
  public void testIncrement4() {
    ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 4);
    generator.init(context);
    expectGeneratedSequence(generator, 0L, 1L, 2L, 3L).withCeasedAvailability();
  }

  /**
   * Test reset.
   */
  @Test
  public void testReset() {
    ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 2);
    generator.init(context);
    expectGeneratedSequence(generator, 0L, 2L, 1L, 3L).withCeasedAvailability();
  }

}
