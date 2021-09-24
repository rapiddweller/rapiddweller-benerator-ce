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

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.domain.address.Country;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link PersonGenerator}.<br/>
 * <br/>
 * Created: 09.06.2006 22:14:08
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class PersonGeneratorTest extends GeneratorClassTest {

  private static final Logger logger = LoggerFactory.getLogger(PersonGeneratorTest.class);

  /**
   * Instantiates a new Person generator test.
   */
  public PersonGeneratorTest() {
    super(PersonGenerator.class);
  }

  /**
   * Test germany.
   */
  @Test
  public void testGermany() {
    PersonGenerator generator = new PersonGenerator(Country.GERMANY.getIsoCode(), Locale.GERMANY);
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      Person person = generator.generate();
      logger.debug(person.toString());
    }
  }

  /**
   * Test switzerland.
   */
  @Test
  public void testSwitzerland() {
    PersonGenerator generator = new PersonGenerator(Country.SWITZERLAND.getIsoCode(), Locale.GERMAN);
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      Person person = generator.generate();
      logger.debug(person.toString());
    }
  }

  /**
   * Test female quota.
   */
  @Test
  public void testFemaleQuota() {
    PersonGenerator generator = new PersonGenerator();
    generator.setDataset("DE");
    generator.setLocale(new Locale("de_DE"));
    generator.setFemaleQuota(0.1);
    generator.init(context);
    int femCount = 0;
    for (int i = 0; i < 1000; i++) {
      Person person = generator.generate();
      if (person.getGender() == Gender.FEMALE) {
        femCount++;
      }
      logger.debug(person.toString());
    }
    assertTrue(femCount < 200);
  }

  /**
   * Test russia.
   */
  @Test
  public void testRussia() {
    PersonGenerator generator = new PersonGenerator(Country.RUSSIA.getIsoCode(), new Locale("ru"));
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      Person person = generator.generate();
      assertNotNull(person);
      logger.debug(person.toString());
    }
  }

  /**
   * Test poland.
   */
  @Test
  public void testPoland() {
    PersonGenerator generator = new PersonGenerator(Country.POLAND.getIsoCode(), new Locale("pl"));
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      Person person = generator.generate();
      assertNotNull(person);
      logger.debug(person.toString());
    }
  }

  /**
   * Test china.
   */
  @Test
  public void testChina() {
    PersonGenerator generator = new PersonGenerator(Country.CHINA.getIsoCode(), Locale.CHINESE);
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      Person person = generator.generate();
      assertNotNull(person);
      logger.debug(person.toString());
    }
  }

  /**
   * Test generate for dataset de.
   */
  @Test
  public void testGenerateForDataset_DE() {
    PersonGenerator generator = new PersonGenerator("dach");
    generator.init(context);
    for (int i = 0; i < 10; i++) {
      Person person = generator.generateForDataset("DE");
      assertNotNull(person);
      logger.debug(person.toString());
    }
  }

  /**
   * Test generate for dataset fallback li to de.
   */
  @Test
  public void testGenerateForDataset_fallback_LI_to_DE() {
    PersonGenerator generator = new PersonGenerator("dach");
    generator.init(context);
    // test fallback for requested LI data in dach region
    generator.generateForDataset("LI");
  }

  /**
   * Test generate for dataset fallback for invalid set.
   */
  @Test
  public void testGenerateForDataset_fallbackForInvalidSet() {
    PersonGenerator generator = new PersonGenerator("dach");
    generator.init(context);
    generator.generateForDataset("US");
  }

  /**
   * Test generate for dataset fallback for illegal set.
   */
  @Test
  public void testGenerateForDataset_fallbackForIllegalSet() {
    PersonGenerator generator = new PersonGenerator("dach");
    generator.init(context);
    generator.generateForDataset("xx");
  }

  /**
   * Test min max age years.
   */
  @Test
  public void testMinMaxAgeYears() {
    PersonGenerator generator = new PersonGenerator();
    generator.setMinAgeYears(18);
    generator.setMaxAgeYears(21);
    generator.init(context);
    Date today = TimeUtil.today();
    Set<Integer> agesUsed = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      Person person = generator.generate();
      int age = person.getAge();
      assertTrue("Person is expected to be at least 18 years old, but is " + age + ", " +
              "birthDate=" + person.getBirthDate(),
          age >= 18);
      assertTrue("Person is expected to be at most 21 years old, but is " + age + ", " +
          "birthDate=" + person.getBirthDate(), age <= 21);
      agesUsed.add(age);
      logger.debug(person.toString());
    }
    assertEquals(CollectionUtil.toSet(18, 19, 20, 21), agesUsed);
  }

}
