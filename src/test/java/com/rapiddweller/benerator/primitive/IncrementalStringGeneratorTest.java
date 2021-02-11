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

import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.CollectionUtil;
import org.junit.Test;

/**
 * Tests the {@link IncrementalStringGenerator}.<br/><br/>
 * Created: 02.08.2011 10:48:41
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class IncrementalStringGeneratorTest extends GeneratorTest {

  /**
   * Test granularity 1.
   */
  @Test
  public void testGranularity1() {
    IncrementalStringGenerator generator = new IncrementalStringGenerator(CollectionUtil.toSet('A', 'B'), 1, 3, 1);
    initialize(generator);
    expectGeneratedSequence(generator,
        "A", "B",
        "AA", "AB", "BA", "BB",
        "AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
  }

  /**
   * Test granularity 2.
   */
  @Test
  public void testGranularity2() {
    IncrementalStringGenerator generator = new IncrementalStringGenerator(CollectionUtil.toSet('A', 'B'), 1, 3, 2);
    initialize(generator);
    expectGeneratedSequence(generator,
        "A", "B",
        "AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
  }

}
