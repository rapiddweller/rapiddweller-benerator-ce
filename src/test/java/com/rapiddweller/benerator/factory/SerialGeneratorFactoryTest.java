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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.model.data.Uniqueness;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link SerialGeneratorFactory}.<br/><br/>
 * @author Alexander Kell
 */
public class SerialGeneratorFactoryTest extends GeneratorTest {

  private final SerialGeneratorFactory factory = new SerialGeneratorFactory();

  @Test
  public void testNumberGenerator() {
    NonNullGenerator<Integer> gen = factory.createNumberGenerator(
        Integer.class, 1, true, 10, true, 1, null, Uniqueness.NONE);
    gen.init(context);
    for (int i = 0; i < 20; i++) {
      Integer v = gen.generate();
      if (v == null) {
        break; // serial sequence exhausted
      }
      assertTrue("out of range: " + v, v >= 1 && v <= 10);
    }
  }

  @Test
  public void testDateGenerator() {
    Generator<Date> gen = factory.createDateGenerator(new Date(0L), new Date(1_000_000_000L), 86_400_000L, null);
    gen.init(context);
    assertNotNull(GeneratorUtil.generateNonNull(gen));
  }

  @Test
  public void testStringGenerator() {
    Set<Character> chars = new HashSet<>(Arrays.asList('a', 'b', 'c'));
    NonNullGenerator<String> gen = factory.createStringGenerator(chars, 2, 4, 1, null, Uniqueness.NONE);
    gen.init(context);
    for (int i = 0; i < 20; i++) {
      String s = gen.generate();
      if (s == null) {
        break; // serial sequence exhausted
      }
      assertTrue("bad length: " + s, s.length() >= 2 && s.length() <= 4);
    }
  }

  @Test
  public void testCharacterGenerator() {
    NonNullGenerator<Character> gen = factory.createCharacterGenerator(new HashSet<>(Arrays.asList('x', 'y')));
    gen.init(context);
    assertNotNull(gen.generate());
  }

  @Test
  public void testSampleGenerator() {
    Generator<String> gen = factory.createSampleGenerator(Arrays.asList("a", "b", "c"), String.class, false);
    gen.init(context);
    assertNotNull(GeneratorUtil.generateNonNull(gen));
  }

  @Test
  public void testSingleValueAndNullGenerator() {
    Generator<String> single = factory.createSingleValueGenerator("v", false);
    single.init(context);
    assertEquals("v", GeneratorUtil.generateNonNull(single));

    Generator<String> nullGen = factory.createNullGenerator(String.class);
    nullGen.init(context);
    assertNotNull(nullGen);
  }

  @Test
  public void testDefaultsAndNullSettings() {
    assertEquals(SequenceManager.STEP_SEQUENCE, factory.defaultDistribution(Uniqueness.NONE));
    Set<Character> set = new HashSet<>(Arrays.asList('a', 'b'));
    assertEquals(set, factory.defaultSubSet(set));

    // nullable=false, nullQuota=0 -> source returned unchanged; nullable=true -> null is prepended
    assertNotNull(factory.applyNullSettings(factory.createSingleValueGenerator("v", false), false, 0.));
    assertNotNull(factory.applyNullSettings(factory.createSingleValueGenerator("v", false), true, null));
  }

}
