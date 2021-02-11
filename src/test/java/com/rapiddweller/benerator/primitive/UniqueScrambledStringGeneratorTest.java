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

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.CollectionUtil;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link UniqueScrambledStringGenerator}.<br/><br/>
 * Created: 11.03.2013 22:03:47
 *
 * @author Volker Bergmann
 * @since 0.8.1
 */
public class UniqueScrambledStringGeneratorTest extends GeneratorTest {

  /**
   * Test.
   */
  @Test
  public void test() {
    for (int n = 1; n < 5; n++) {
      UniqueScrambledStringGenerator generator = new UniqueScrambledStringGenerator(CollectionUtil.toSet('0', '1'), n, n + 1);
      generator.init(new DefaultBeneratorContext());
      for (int i = 0; i < 30; i++) {
        int exp = (int) Math.round(Math.pow(2, n)) + (int) Math.round(Math.pow(2, n + 1));
        for (int c = 0; c < exp; c++) {
          assertNotNull(generator.generate());
        }
        assertNull(generator.generate());
        generator.reset();
      }

    }

  }

}
