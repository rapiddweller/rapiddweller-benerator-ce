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
 * Tests the UniqueFixedLengthStringGenerator.<br/>
 * <br/>
 * Created: 15.11.2007 14:30:28
 *
 * @author Volker Bergmann
 */
public class UniqueFixedLengthStringGeneratorTest extends GeneratorClassTest {

  /**
   * Instantiates a new Unique fixed length string generator test.
   */
  public UniqueFixedLengthStringGeneratorTest() {
    super(UniqueFixedLengthStringGenerator.class);
  }

  /**
   * Test zero length.
   */
  @Test
  public void testZeroLength() {
    expectGeneratedSequence(createAndInit(0, false), "").withCeasedAvailability();
  }

  /**
   * Test constant digit.
   */
  @Test
  public void testConstantDigit() {
    expectGeneratedSequence(createAndInit(1, false, '0'), "0").withCeasedAvailability();
  }

  /**
   * Test two binary digits ordered.
   */
  @Test
  public void testTwoBinaryDigitsOrdered() {
    expectGeneratedSequence(createAndInit(2, true, '0', '1'), "00", "01", "10", "11").withCeasedAvailability();
  }

  /**
   * Test two binary digits scrambled.
   */
  @Test
  public void testTwoBinaryDigitsScrambled() {
    expectGeneratedSequence(createAndInit(2, false, '0', '1'), "10", "01", "11", "00").withCeasedAvailability();
  }

  /**
   * Test one binary digit.
   */
  @Test
  public void testOneBinaryDigit() {
    expectUniquelyGeneratedSet(createAndInit(1, false, '0', '1'), "0", "1").withCeasedAvailability();
    expectUniqueProducts(createAndInit(1, false, '0', '1'), 2).withCeasedAvailability();
  }

  /**
   * Test two binary digits.
   */
  @Test
  public void testTwoBinaryDigits() {
    expectUniquelyGeneratedSet(createAndInit(2, true, '0', '1'), "00", "01", "10", "11").withCeasedAvailability();
    expectUniqueProducts(createAndInit(2, true, '0', '1'), 4).withCeasedAvailability();
  }

  /**
   * Test three binary digits.
   */
  @Test
  public void testThreeBinaryDigits() {
    expectUniquelyGeneratedSet(createAndInit(3, true, '0', '1'),
        "000", "001", "010", "011", "100", "101", "110", "111"
    ).withCeasedAvailability();
    expectUniqueProducts(createAndInit(3, true, '0', '1'), 8).withCeasedAvailability();
  }

  /**
   * Test two alpha digits.
   */
  @Test
  public void testTwoAlphaDigits() {
    expectUniquelyGeneratedSet(createAndInit(2, false, 'A', 'O'), "AA", "AO", "OA", "OO").withCeasedAvailability();
    expectUniqueProducts(createAndInit(2, false, 'A', 'O'), 4).withCeasedAvailability();
    expectUniqueProducts(createAndInit(2, false, 'A', 'B', 'C'), 9).withCeasedAvailability();
  }

  /**
   * Test long string.
   */
  @Test
  public void testLongString() {
    expectUniqueProducts(createAndInit(4, false, 'A', 'E', 'I', 'O', 'U'), 625).withCeasedAvailability();
  }

  /**
   * Test many.
   */
  @Test
  public void testMany() {
    UniqueFixedLengthStringGenerator generator = createAndInit(7, false, '0', '9', '2', '6', '4', '5', '3', '7', '8', '1');
    expectUniqueProducts(generator, 1000).withContinuedAvailability();
  }

  private UniqueFixedLengthStringGenerator createAndInit(int length, boolean ordered, Character... chars) {
    return initialize(new UniqueFixedLengthStringGenerator(CollectionUtil.toSet(chars), length, ordered));
  }

}
