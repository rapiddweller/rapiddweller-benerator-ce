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
import com.rapiddweller.common.CollectionUtil;
import org.junit.Test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Tests the {@link CharacterGenerator}.<br/>
 * <br/>
 *
 * @author Volker Bergmann
 * @since 0.1  Created: 09.06.2006 21:03:42
 */
public class CharacterGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Character generator test.
   */
  public CharacterGeneratorTest() {
    super(CharacterGenerator.class);
  }

  /**
   * Test digit.
   */
  @Test
  public void testDigit() {
    checkProductSet(create("\\d"), 1000,
        CollectionUtil.toSet('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
  }

  /**
   * Test range.
   */
  @Test
  public void testRange() {
    checkProductSet(create("[1-2]"), 1000, CollectionUtil.toSet('1', '2'));
    checkProductSet(create("[12]"), 1000, CollectionUtil.toSet('1', '2'));
  }

  /**
   * Test locale.
   */
  @Test
  public void testLocale() {
    HashSet<Character> expectedSet = new HashSet<>();
    for (char c = 'A'; c <= 'Z'; c++) {
      expectedSet.add(c);
    }
    for (char c = 'a'; c <= 'z'; c++) {
      expectedSet.add(c);
    }
    for (char c = '0'; c <= '9'; c++) {
      expectedSet.add(c);
    }
    expectedSet.add('_');
    expectedSet.add('ä');
    expectedSet.add('ö');
    expectedSet.add('ü');
    expectedSet.add('Ä');
    expectedSet.add('Ö');
    expectedSet.add('Ü');
    expectedSet.add('ß');

    checkProductSet(create("\\w", Locale.GERMAN), 10000, expectedSet);
  }

  /**
   * Test set.
   */
  @Test
  public void testSet() {
    Set<Character> values = CollectionUtil.toSet('A', 'B');
    checkProductSet(create(values), 1000, values);
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private CharacterGenerator create(String pattern) {
    CharacterGenerator generator = new CharacterGenerator(pattern);
    generator.init(context);
    return generator;
  }

  private Generator<Character> create(String pattern, Locale locale) {
    CharacterGenerator generator = new CharacterGenerator(pattern, locale);
    generator.init(context);
    return generator;
  }

  private Generator<Character> create(Set<Character> values) {
    CharacterGenerator generator = new CharacterGenerator(values);
    generator.init(context);
    return generator;
  }

}
