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

package com.rapiddweller.domain.br;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link CNPJGenerator}.<br/><br/>
 * Created: 17.10.2009 08:24:59
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class CNPJGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Cnpj generator test.
   */
  public CNPJGeneratorTest() {
    super(CNPJGenerator.class);
  }

  /**
   * Test by validator.
   */
  @Test
  public void testByValidator() {
    expectGenerations(initialize(new CNPJGenerator()), 100, new CNPJValidator());
  }

  /**
   * Test formatted number generation.
   */
  @Test
  public void testFormattedNumberGeneration() {
    CNPJGenerator generator = new CNPJGenerator(true);
    generator.init(context);
    CNPJValidator validator = new CNPJValidator(true);
    for (int i = 0; i < 100; i++) {
      String cnpj = generator.generate();
      assertEquals(18, cnpj.length());
      assertTrue(validator.valid(cnpj));
    }
  }

}
