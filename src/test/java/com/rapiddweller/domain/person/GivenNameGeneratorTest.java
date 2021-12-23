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

package com.rapiddweller.domain.person;

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.collection.ObjectCounter;
import com.rapiddweller.domain.address.Country;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link GivenNameGenerator}.
 * Created: 09.06.2006 21:37:05
 * @author Volker Bergmann
 * @since 0.1
 */
public class GivenNameGeneratorTest extends GeneratorClassTest {

  public GivenNameGeneratorTest() {
    super(GivenNameGenerator.class);
  }

  @Test
  public void test_default_us() throws IllegalGeneratorStateException {
    Country defaultCountry = Country.getDefault();
    try {
      Country.setDefault(Country.US);
      ObjectCounter<String> samples = checkDiversity(new GivenNameGenerator(), 5000, 20);
      assertContains(samples, "James", "John", "David");
    } finally {
      Country.setDefault(defaultCountry);
    }
  }

  @Test
  public void test_us_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("US", Gender.MALE), 20, "Dorothy", "James", "John");
  }

  @Test
  public void test_us_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("US", Gender.FEMALE), 20, "David", "Dorothy", "Helen");
  }

  @Test
  public void test_de_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("DE", Gender.MALE), 20, "Ursula", "Peter", "Wolfgang");
  }

  @Test
  public void test_de_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("DE", Gender.FEMALE), 20, "Pater", "Maria", "Ursula");
  }

  @Test
  public void test_at_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("AT", Gender.MALE), 20, "Lena", "Lukas", "Tobias");
  }

  @Test
  public void test_at_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("AT", Gender.FEMALE), 20, "Lukas", "Lena", "Leonie");
  }

  @Test
  public void test_au_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("AU", Gender.MALE), 10, "Olivia", "Jack", "Lachlan");
  }

  @Test
  public void test_au_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("AU", Gender.FEMALE), 10, "Jack", "Olivia", "Charlotte");
  }

  @Test
  public void test_be_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("BE", Gender.MALE), 19, "Lieke", "Daan", "Sem");
  }

  @Test
  public void test_be_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("BE", Gender.FEMALE), 19, "Daan", "Lieke", "Sophie");
  }

  @Test
  public void test_br_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("BR", Gender.MALE), 20, "Mariana", "Lucas", "Guilherme");
  }

  @Test
  public void test_br_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("BR", Gender.FEMALE), 20, "Lucas", "Mariana", "Amanda");
  }

  @Test
  public void test_ca_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("CA", Gender.MALE), 20, "Mégane", "William", "Mathis");
  }

  @Test
  public void test_ca_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("CA", Gender.FEMALE), 20, "William", "Mégane", "Léa");
  }

  @Test
  public void test_ch_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("CH", Gender.MALE), 20, "Sandra", "Hans", "Peter");
  }

  @Test
  public void test_ch_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("CH", Gender.FEMALE), 10, "Hans", "Sandra", "Claudia");
  }

  @Test
  public void test_cz_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("CZ", Gender.MALE), 10, "Marie", "Jiri", "Josef");
  }

  @Test
  public void test_cz_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("CZ", Gender.FEMALE), 10, "Jiri", "Marie", "Jana");
  }

  @Test
  public void test_it_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("IT", Gender.MALE), 20, "Sofia", "Francesco", "Alessandro");
  }

  @Test
  public void test_it_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("IT", Gender.FEMALE), 20, "Francesco", "Sofia", "Giulia");
  }

  @Test
  public void test_gb_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("GB", Gender.MALE), 10, "Olivia", "Jack", "Thomas");
  }

  @Test
  public void test_gb_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("GB", Gender.FEMALE), 10, "Jack", "Olivia", "Grace");
  }

  @Test
  public void test_ie_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("IE", Gender.MALE), 10, "Chloe", "Adam", "Matthew");
  }

  @Test
  public void test_ie_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("IE", Gender.FEMALE), 10, "Adam", "Chloe", "Lauren");
  }

  @Test
  public void test_il_male() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("IL", Gender.MALE), 20, "Avigail", "Avraham", "Aharon");
  }

  @Test
  public void test_il_female() throws IllegalGeneratorStateException {
    check(new GivenNameGenerator("IL", Gender.FEMALE), 20, "Avraham", "Avigail", "Avital");
  }

  @Test(expected = InvalidGeneratorSetupException.class)
  public void test_xy() throws IllegalGeneratorStateException {
    checkDiversity(new GivenNameGenerator("XY", Gender.MALE), 100, 20);
    checkDiversity(new GivenNameGenerator("XY", Gender.FEMALE), 100, 20);
  }

  // Private helper methods ------------------------------------------------------------------------------------------

  private void check(NonNullGenerator<String> generator, int minDiversity, String forbidden, String... requireds) {
    ObjectCounter<String> samples = checkDiversity(generator, 1000, minDiversity);
    assertEquals(0, samples.getCount(forbidden));
    for (String required : requireds) {
      assertContains(samples, required);
    }
  }
}
