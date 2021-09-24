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

package com.rapiddweller.domain.organization;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.ConfigurationError;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the CompanyNameGenerator.<br/><br/>
 * Created: 14.03.2008 08:31:26
 *
 * @author Volker Bergmann
 */
public class CompanyNameGeneratorTest extends GeneratorClassTest {

  private static final Logger logger = LoggerFactory.getLogger(CompanyNameGeneratorTest.class);

  /**
   * Instantiates a new Company name generator test.
   */
  public CompanyNameGeneratorTest() {
    super(CompanyNameGenerator.class);
  }

  /**
   * Test germany.
   */
  @Test
  public void testGermany() {
    check("DE");
  }

  /**
   * Test usa.
   */
  @Test
  public void testUSA() {
    check("US");
  }

  /**
   * Test brazil.
   */
  @Test
  public void testBrazil() {
    check("BR");
  }

  /**
   * Test xx.
   */
  @Test(expected = ConfigurationError.class)
  public void testXX() {
    check("XX");
  }

  /**
   * Test generate for dach.
   */
  @Test
  public void testGenerateForDACH() {
    CompanyNameGenerator generator = new CompanyNameGenerator("dach");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      CompanyName name = generator.generate();
      if (logger.isDebugEnabled()) {
        logger.debug(name.toString());
      }
      assertNotNull(name);
      assertTrue(name.toString().length() > 1);
      System.out.println(name);
    }
  }

  /**
   * Test world.
   */
  @Test
  public void testWorld() {
    CompanyNameGenerator generator = new CompanyNameGenerator("world");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      CompanyName name = generator.generate();
      if (logger.isDebugEnabled()) {
        logger.debug(name.toString());
      }
      assertNotNull(name);
      assertTrue(name.toString().length() > 1);
      System.out.println(name);
    }
  }

  /**
   * Check.
   *
   * @param dataset the dataset
   */
  public void check(String dataset) {
    CompanyNameGenerator generator = new CompanyNameGenerator(dataset);
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      CompanyName name = generator.generate();
      if (logger.isDebugEnabled()) {
        logger.debug(name.toString());
      }
      assertNotNull(name);
      assertTrue(name.toString().length() > 1);
    }
  }

}
