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

package com.rapiddweller.domain.br;

import com.rapiddweller.common.validator.SimpleValidatorTest;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests the CPNJValidator.<br/><br/>
 * Created: 17.10.2009 08:24:46
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class CNPJValidatorTest extends SimpleValidatorTest<CharSequence> {

  /**
   * Instantiates a new Cnpj validator test.
   */
  public CNPJValidatorTest() {
    super(new CNPJValidator());
  }

  /**
   * Test valid plain numbers.
   */
  @Test
  public void testValidPlainNumbers() {
    assertValid("16701716000156");
    assertValid("01679152000125");
  }

  /**
   * Test valid formatted numbers.
   */
  @Test
  public void testValidFormattedNumbers() {
    CNPJValidator validator = new CNPJValidator(true);
    assertTrue(validator.valid("16.701.716/0001-56"));
    assertTrue(validator.valid("01.679.152/0001-25"));
    assertTrue(validator.valid("16701716000156"));
    assertTrue(validator.valid("01679152000125"));
  }

  /**
   * Test invalid formatted numbers.
   */
  @Test
  public void testInvalidFormattedNumbers() {
    assertInvalid("16.701.716-0001-56");
    assertInvalid("01/679.152/0001-25");
  }

  /**
   * Test illegal numbers.
   */
  @Test
  public void testIllegalNumbers() {
    assertInvalid(null);
    assertInvalid("");
    assertInvalid("0");
    assertInvalid("1234567890123456789");
  }

  /**
   * Test invalid numbers.
   */
  @Test
  public void testInvalidNumbers() {
    assertInvalid("16701716000157"); // ultimate verification digit wrong
    assertInvalid("16701716000166"); // penultimate verification digit wrong
  }

}
