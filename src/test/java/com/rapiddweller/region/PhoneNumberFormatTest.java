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

package com.rapiddweller.region;

import com.rapiddweller.domain.address.PhoneNumber;
import com.rapiddweller.domain.address.PhoneNumberFormat;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the {@link PhoneNumberFormat}.<br/><br/>
 *
 * @author Volker Bergmann
 * @since 0.4.0
 */
public class PhoneNumberFormatTest {

  /**
   * Test format.
   */
  @Test
  public void testFormat() {
    PhoneNumber number = new PhoneNumber("49", "1234", "5678");
    assertEquals("formatting with pattern 'cal' failed for phone number: " + number,
        "4912345678", new PhoneNumberFormat("cal").format(number));
  }

  /**
   * Test parsing with invalid pattern.
   */
  @Test
  public void testParsingWithInvalidPattern() {
    try {
      new PhoneNumberFormat("cal").parseObject("4912345678");
      fail("ParseException expected");
    } catch (ParseException e) {
      // this is the desired behavior
    }
  }

  /**
   * Test parsing invalid number.
   */
  @Test
  public void testParsingInvalidNumber() {
    try {
      new PhoneNumberFormat("+c-a-l").parseObject("49(1234)5678");
      fail("ParseException expected");
    } catch (ParseException e) {
      // this is the desired behavior
    }
  }

  /**
   * Test bijective numbers.
   *
   * @throws ParseException the parse exception
   */
  @Test
  public void testBijectiveNumbers() throws ParseException {
    PhoneNumber n = new PhoneNumber("49", "1234", "5678");
    check(n, "+c-a-l", "+49-1234-5678");
    check(n, "00c(a)l", "0049(1234)5678");
  }

  private static void check(PhoneNumber number, String pattern, String formatted) throws ParseException {
    PhoneNumberFormat format = new PhoneNumberFormat(pattern);
    assertEquals("formatting with pattern '" + pattern + "' failed for phone number: " + number,
        formatted, format.format(number));
    assertEquals("parsing with pattern '" + pattern + "' failed for string: " + formatted,
        number, format.parseObject(formatted));
  }

}
