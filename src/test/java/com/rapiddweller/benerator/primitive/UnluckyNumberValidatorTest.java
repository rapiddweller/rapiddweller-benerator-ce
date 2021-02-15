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

import com.rapiddweller.domain.address.Country;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link UnluckyNumberValidator}.<br/>
 * <br/>
 * Created at 03.07.2009 08:57:20
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class UnluckyNumberValidatorTest {
  // file deepcode ignore NullPassTo/test: it is testfile
  private Country defaultCountry;

  /**
   * Sets up.
   */
  @Before
  public void setUp() {
    defaultCountry = Country.getDefault();
  }

  /**
   * Tear down.
   */
  @After
  public void tearDown() {
    Country.setDefault(defaultCountry);
  }

  /**
   * Test illegal numbers.
   */
  @Test
  public void testIllegalNumbers() {
    UnluckyNumberValidator validator = new UnluckyNumberValidator();

    assertFalse(validator.isValid(null, null));
    assertFalse(validator.isValid("", null));
  }

  /**
   * Test germany.
   */
  @Test
  public void testGermany() {
    Country.setDefault(Country.GERMANY);
    UnluckyNumberValidator validator = new UnluckyNumberValidator();
    assertFalse(validator.isValid("1133", null));
    assertFalse(validator.isValid("7137", null));
    assertTrue(validator.isValid("0123456789", null));
    validator.setLuckyNumberRequired(true);
    assertFalse(validator.isValid("1133", null));
    assertFalse(validator.isValid("7137", null));
    assertTrue(validator.isValid("0123456789", null));
    assertFalse(validator.isValid("012345689", null));
  }

  /**
   * Test italy.
   */
  @Test
  public void testItaly() {
    Country.setDefault(Country.ITALY);
    UnluckyNumberValidator validator = new UnluckyNumberValidator();
    assertFalse(validator.isValid("1133", null));
    assertFalse(validator.isValid("7137", null));
    assertTrue(validator.isValid("0123456789", null));
    validator.setLuckyNumberRequired(true);
    assertFalse(validator.isValid("1133", null));
    assertFalse(validator.isValid("7137", null));
    assertTrue(validator.isValid("0123456789", null));
    assertFalse(validator.isValid("012345689", null));
  }

  /**
   * Test china.
   */
  @Test
  public void testChina() {
    Country.setDefault(Country.CHINA);
    UnluckyNumberValidator validator = new UnluckyNumberValidator();
    assertFalse(validator.isValid("141", null));
    assertFalse(validator.isValid("848", null));
    assertTrue(validator.isValid("012356789", null));
    validator.setLuckyNumberRequired(true);
    assertFalse(validator.isValid("141", null));
    assertFalse(validator.isValid("848", null));
    assertTrue(validator.isValid("012356789", null));
    assertFalse(validator.isValid("0103567", null));
  }

  /**
   * Test japan.
   */
  @Test
  public void testJapan() {
    Country.setDefault(Country.JAPAN);
    UnluckyNumberValidator validator = new UnluckyNumberValidator();
    assertFalse(validator.isValid("141", null));
    assertFalse(validator.isValid("848", null));
    assertTrue(validator.isValid("01235678", null));
    validator.setLuckyNumberRequired(true);
    assertFalse(validator.isValid("141", null));
    assertFalse(validator.isValid("848", null));
    assertTrue(validator.isValid("01235678", null));
    assertFalse(validator.isValid("0123567", null));
  }

  /**
   * Test custom.
   */
  @Test
  public void testCustom() {
    UnluckyNumberValidator validator = new UnluckyNumberValidator();
    validator.setLuckyNumbers("0", "2", "4");
    validator.setUnluckyNumbers("1", "3", "5");
    assertFalse(validator.isValid("818", null));
    assertFalse(validator.isValid("212", null));
    assertTrue(validator.isValid("0246789", null));
    validator.setLuckyNumberRequired(true);
    assertFalse(validator.isValid("818", null));
    assertFalse(validator.isValid("212", null));
    assertTrue(validator.isValid("0246789", null));
    assertFalse(validator.isValid("6789", null));
  }

}
