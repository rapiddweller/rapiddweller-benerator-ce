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

package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.contiperf.PerfTest;
import com.rapiddweller.contiperf.Required;
import com.rapiddweller.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the performance of the {@link AddressGenerator}.<br/><br/>
 * Created: 17.04.2010 07:12:51
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class AddressGeneratorPerfTest {

  /**
   * The Rule.
   */
  @Rule
  public ContiPerfRule rule = new ContiPerfRule();

  private AddressGenerator generatorDE;

  /**
   * Sets up.
   */
  @Before
  public void setUp() {
    generatorDE = new AddressGenerator(Country.GERMANY.getIsoCode());
    generatorDE.init(new DefaultBeneratorContext());
  }

  /**
   * Tear down.
   */
  @After
  public void tearDown() {
    generatorDE.close();
  }

  /**
   * Verifies that the {@link AddressGenerator} generates at least 1000 addresses per second
   */
  @Test
  @PerfTest(invocations = 1000)
  @Required(throughput = 1000)
  public void test_DE() {
    generatorDE.generate();
  }

}
