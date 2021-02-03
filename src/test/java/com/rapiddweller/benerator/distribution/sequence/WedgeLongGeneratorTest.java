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
import org.junit.Test;

/**
 * Tests the {@link WedgeLongGenerator}.<br/><br/>
 * Created: 13.11.2007 13:10:39
 *
 * @author Volker Bergmann
 */
public class WedgeLongGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Wedge long generator test.
   */
  public WedgeLongGeneratorTest() {
    super(WedgeLongGenerator.class);
  }

  /**
   * Test instantiation.
   */
  @Test
  public void testInstantiation() {
    new WedgeLongGenerator(0, 10, 1);
  }

  /**
   * Test granularity 1.
   */
  @Test
  public void testGranularity1() {
    expectGeneratedSequence(create(1, 3, 1), 1L, 3L, 2L).withCeasedAvailability();
    expectGeneratedSequence(create(1, 4, 1), 1L, 4L, 2L, 3L).withCeasedAvailability();
    expectGeneratedSequence(create(-3, -1, 1), -3L, -1L, -2L).withCeasedAvailability();
    expectGeneratedSequence(create(-4, -1, 1), -4L, -1L, -3L, -2L).withCeasedAvailability();
    expectGeneratedSequence(create(-1, 1, 1), -1L, 1L, 0L).withCeasedAvailability();
    expectGeneratedSequence(create(-1, 2, 1), -1L, 2L, 0L, 1L).withCeasedAvailability();
    expectGeneratedSequence(create(0, 5, 1), 0L, 5L, 1L, 4L, 2L, 3L).withCeasedAvailability();
  }

  /**
   * Test granularity 5.
   */
  @Test
  public void testGranularity5() {
    expectGeneratedSequence(create(1, 11, 5), 1L, 11L, 6L).withCeasedAvailability();
    expectGeneratedSequence(create(1, 16, 5), 1L, 16L, 6L, 11L).withCeasedAvailability();
    expectGeneratedSequence(create(-11, -1, 5), -11L, -1L, -6L).withCeasedAvailability();
    expectGeneratedSequence(create(-16, -1, 5), -16L, -1L, -11L, -6L).withCeasedAvailability();
    expectGeneratedSequence(create(-11, 4, 5), -11L, 4L, -6L, -1L).withCeasedAvailability();
  }

  private WedgeLongGenerator create(long min, long max, long granularity) {
    return initialize(new WedgeLongGenerator(min, max, granularity));
  }
}
