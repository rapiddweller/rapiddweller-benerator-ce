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

package com.rapiddweller.domain.person;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the {@link GenderGenerator}.<br/>
 * <br/>
 * Created: 09.06.2006 21:47:53
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class GenderGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Gender generator test.
   */
  public GenderGeneratorTest() {
    super(GenderGenerator.class);
  }

  /**
   * Test default settings.
   *
   * @throws IllegalGeneratorStateException the illegal generator state exception
   */
  @Test
  public void testDefaultSettings() throws IllegalGeneratorStateException {
    Generator<Gender> generator = new GenderGenerator();
    generator.init(context);
    expectRelativeWeights(generator, 1000, Gender.FEMALE, 0.5, Gender.MALE, 0.5);
  }

  /**
   * Test female quota.
   *
   * @throws IllegalGeneratorStateException the illegal generator state exception
   */
  @Test
  public void testFemaleQuota() throws IllegalGeneratorStateException {
    Generator<Gender> generator = new GenderGenerator(0.3);
    generator.init(context);
    expectRelativeWeights(generator, 1000, Gender.FEMALE, 0.3, Gender.MALE, 0.7);
  }

}
