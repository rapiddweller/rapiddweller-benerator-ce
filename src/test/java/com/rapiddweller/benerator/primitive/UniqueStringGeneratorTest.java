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

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.CollectionUtil;
import org.junit.Test;

/**
 * Tests the UniqueStringGenerator.<br/>
 * <br/>
 * Created: 16.11.2007 12:03:55
 *
 * @author Volker Bergmann
 */
public class UniqueStringGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Unique string generator test.
   */
  public UniqueStringGeneratorTest() {
    super(UniqueScrambledStringGenerator.class);
  }

  /**
   * Test unique volume.
   */
  @Test
  public void testUniqueVolume() {
    expectUniqueProducts(create(0, 1, '0', '1'), 3).withCeasedAvailability();
    expectUniqueProducts(create(0, 2, '0', '1'), 7).withCeasedAvailability();
    expectUniqueProducts(create(3, 4, '0', '1', '3'), 27 + 81).withCeasedAvailability();
  }

  private UniqueScrambledStringGenerator create(int minLength, int maxLength, char... chars) {
    UniqueScrambledStringGenerator generator = new UniqueScrambledStringGenerator(
        CollectionUtil.toCharSet(chars), minLength, maxLength);
    generator.init(context);
    return generator;
  }

}
