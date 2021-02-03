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

package com.rapiddweller.benerator.util;

import com.rapiddweller.common.StringUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link LuhnUtil} class.<br/><br/>
 * Created: 18.10.2009 10:29:43
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class LuhnUtilTest {

  private static final String LUHN_VALID_NUMBER = "49927398716";

  /**
   * Test required luhn digit.
   */
  @Test
  public void testRequiredLuhnDigit() {
    assertEquals('0', LuhnUtil.requiredCheckDigit("0000000009"));
    assertEquals(StringUtil.lastChar(LUHN_VALID_NUMBER), LuhnUtil.requiredCheckDigit(LUHN_VALID_NUMBER));
    assertEquals('0', LuhnUtil.requiredCheckDigit("1234001234560"));
    assertEquals('1', LuhnUtil.requiredCheckDigit("234001234560"));
  }

  /**
   * Test luhn valid.
   */
  @Test
  public void testLuhnValid() {
    assertFalse(LuhnUtil.luhnValid("0000000009"));
    assertTrue(LuhnUtil.luhnValid("0000000000"));
    assertTrue(LuhnUtil.luhnValid("1234001234560"));
  }

}
