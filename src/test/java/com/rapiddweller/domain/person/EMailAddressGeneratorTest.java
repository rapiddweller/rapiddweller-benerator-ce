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

import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * A EMailGeneratorTest.<br/><br/>
 * Created at 20.04.2008 08:16:28
 *
 * @author Volker Bergmann
 * @since 0.5.2
 */
public class EMailAddressGeneratorTest extends GeneratorClassTest {

  private static final Logger logger = LogManager.getLogger(EMailAddressGeneratorTest.class);

  /**
   * Instantiates a new E mail address generator test.
   */
  public EMailAddressGeneratorTest() {
    super(EMailAddressGenerator.class);
  }

  /**
   * Test de.
   */
  @Test
  public void testDE() {
    check("DE");
  }

  /**
   * Test us.
   */
  @Test
  public void testUS() {
    check("US");
  }

  private void check(String datasetName) {
    EMailAddressGenerator generator = new EMailAddressGenerator(datasetName);
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      String email = generator.generate();
      assertNotNull(email);
      logger.debug(email);
    }
  }

}
