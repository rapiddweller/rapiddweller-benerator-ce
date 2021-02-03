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
 * Tests the {@link CPFValidator}.<br/><br/>
 * Created: 17.10.2009 08:25:15
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class CPFValidatorTest extends SimpleValidatorTest<String> {

  /**
   * Instantiates a new Cpf validator test.
   */
  public CPFValidatorTest() {
    super(new CPFValidator());
  }

  /**
   * Test valid plain samples.
   */
  @Test
  public void testValidPlainSamples() {
    assertValid("04303340790");
  }

  /**
   * Test valid formatted samples.
   */
  @Test
  public void testValidFormattedSamples() {
    CPFValidator validator = new CPFValidator(true);
    assertTrue(validator.valid("043.033.407-90"));
    assertTrue(validator.valid("04303340790"));
  }

  /**
   * Test illegal samples.
   */
  @Test
  public void testIllegalSamples() {
    assertInvalid(null);
    assertInvalid("");
    assertInvalid("12");
    assertInvalid("1234567890123456789");
  }

  /**
   * Test invalid samples.
   */
  @Test
  public void testInvalidSamples() {
    assertInvalid("04303340791"); // last check digit wrong
    assertInvalid("04303340780"); // first check digit wrong
    assertInvalid("043.033.407-91"); // last check digit wrong
    assertInvalid("043.033.407-80"); // first check digit wrong
  }

}
