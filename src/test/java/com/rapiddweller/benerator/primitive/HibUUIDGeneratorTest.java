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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the HexUUIDGenerator.<br/>
 * <br/>
 * Created: 15.11.2007 11:00:42
 *
 * @author Volker Bergmann
 */
public class HibUUIDGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Hib uuid generator test.
   */
  public HibUUIDGeneratorTest() {
    super(HibUUIDGenerator.class);
  }

  /**
   * Test without separator.
   */
  @Test
  public void testWithoutSeparator() {
    HibUUIDGenerator generator = new HibUUIDGenerator();
    generator.init(context);
    for (int i = 0; i < 5; i++) {
      String id = generator.generate();
      assertEquals(32, id.length());
    }
  }

  /**
   * Test minus separator.
   */
  @Test
  public void testMinusSeparator() {
    HibUUIDGenerator generator = new HibUUIDGenerator("-");
    generator.init(context);
    for (int i = 0; i < 5; i++) {
      String id = generator.generate();
      assertEquals(36, id.length());
      assertEquals('-', id.charAt(8));
      assertEquals('-', id.charAt(17));
      assertEquals('-', id.charAt(22));
      assertEquals('-', id.charAt(31));
      logger.debug(id);
    }
  }

  /**
   * Test uniqueness.
   */
  @Test
  public void testUniqueness() {
    Generator<String> generator = new HibUUIDGenerator();
    generator.init(context);
    expectUniqueGenerations(generator, 100);
  }

}
