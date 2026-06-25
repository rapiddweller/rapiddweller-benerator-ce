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

package com.rapiddweller.domain.address;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.factory.InstanceGeneratorFactory;
import com.rapiddweller.benerator.parser.ModelParser;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import org.junit.Test;
import org.w3c.dom.Element;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Tests the AddressGenerator.<br/><br/>
 * Created: 12.06.2007 06:45:41
 * @author Volker Bergmann
 * @since 0.1
 */
public class AddressGeneratorTest extends GeneratorClassTest {

  public AddressGeneratorTest() {
    super(AddressGenerator.class);
  }

  // tests -----------------------------------------------------------------------------------------------------------

  @Test
  public void testGermany() {
    check(Country.GERMANY, true);
  }

  @Test
  public void testUSA() {
    check(Country.US, true);
  }


  @Test
  public void testBrazil() {
    check(Country.BRAZIL, true);
  }

  @Test
  public void testSwitzerland() {
    check(Country.SWITZERLAND, true);
  }

  @Test
  public void testSwissLocale() {
    AddressGenerator generator = new AddressGenerator("CH");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      Address address = generator.generate();
      logger.debug("{}", address);
      Locale language = address.getCity().getLanguage();
      String languageCode = language.getLanguage();
      String street = address.getStreet();
      if ("de".equals(languageCode)) {
        assertFalse(street.startsWith("Chaussée "));
        assertFalse(street.startsWith("Route "));
        assertFalse(street.startsWith("Rue "));
        assertFalse(street.startsWith("Via "));
      } else if ("fr".equals(languageCode)) {
        assertFalse(street.endsWith("strasse"));
        assertFalse(street.startsWith("Via "));
      } else if ("it".equals(languageCode)) {
        assertFalse(street.startsWith("Chaussée "));
        assertFalse(street.startsWith("Route "));
        assertFalse(street.startsWith("Rue "));
        assertFalse(street.endsWith("strasse"));
      } else {
        fail("Illegal language for Switzerland: " + language);
      }
    }
  }

  @Test
  public void testSingapore() {
    check(Country.SINGAPORE, false);
  }

  @Test
  public void testDefaultDescriptorMapping() {
    Country country = Country.getDefault();
    try {
      Country.setDefault(Country.GERMANY);
      checkDescriptorMapping(null);
    } finally {
      Country.setDefault(country);
    }
  }

  @Test
  public void testUSDescriptorMapping() {
    checkDescriptorMapping(Country.US);
  }

  @Test
  public void testDEDescriptorMapping() {
    checkDescriptorMapping(Country.GERMANY);
  }

  // state / city filtering --------------------------------------------------------------------------------------------

  /** state="FL" restricts generation to Florida addresses (matched by state id). */
  @Test
  public void testStateFilter() {
    AddressGenerator generator = new AddressGenerator("US");
    generator.setStateFilter("FL");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      Address address = generator.generate();
      assertEquals("FL", address.getCity().getState().getId());
    }
  }

  /** The state filter also accepts the full state name, case-insensitively. */
  @Test
  public void testStateFilterByName() {
    AddressGenerator generator = new AddressGenerator("US");
    generator.setStateFilter("florida");
    generator.init(context);
    for (int i = 0; i < 50; i++) {
      assertEquals("FL", generator.generate().getCity().getState().getId());
    }
  }

  /** state + city yields correlated city/state/zip for that one city (Orlando, FL). */
  @Test
  public void testStateAndCityFilter() {
    AddressGenerator generator = new AddressGenerator("US");
    generator.setStateFilter("FL");
    generator.setCityFilter("orlando");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      Address address = generator.generate();
      assertEquals("FL", address.getCity().getState().getId());
      assertEquals("ORLANDO", address.getCity().getName().toUpperCase());
      assertNotNull(address.getPostalCode());
    }
  }

  /** A city name occurring in several states (Orlando exists in FL and WV) stays cross-state when no
   *  state is given -- documenting that state+city is how you pin a specific one. */
  @Test
  public void testCityFilterIsCrossStateWithoutState() {
    AddressGenerator generator = new AddressGenerator("US");
    generator.setCityFilter("orlando");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      Address address = generator.generate();
      assertEquals("ORLANDO", address.getCity().getName().toUpperCase());
    }
  }

  /** An unknown state is a configuration error, surfaced clearly rather than silently falling back. */
  @Test(expected = ConfigurationError.class)
  public void testUnknownStateFilterFails() {
    AddressGenerator generator = new AddressGenerator("US");
    generator.setStateFilter("XX");
    generator.init(context);
  }

  /** Filtering also works for Germany: state id "BY" restricts to Bavarian addresses. */
  @Test
  public void testGermanStateFilter() {
    AddressGenerator generator = new AddressGenerator("DE");
    generator.setStateFilter("BY");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      assertEquals("BY", generator.generate().getCity().getState().getId());
    }
  }

  /** Germany, state by full name + city: Bayern / München stays correlated. */
  @Test
  public void testGermanStateAndCityFilter() {
    AddressGenerator generator = new AddressGenerator("DE");
    generator.setStateFilter("Bayern");
    generator.setCityFilter("München");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      Address address = generator.generate();
      assertEquals("BY", address.getCity().getState().getId());
      assertEquals("München", address.getCity().getName());
      assertNotNull(address.getPostalCode());
    }
  }

  /** Filtering also works for France, whose state ids are numeric region codes ("11" = Île-de-France). */
  @Test
  public void testFrenchStateFilter() {
    AddressGenerator generator = new AddressGenerator("FR");
    generator.setStateFilter("11");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      assertEquals("11", generator.generate().getCity().getState().getId());
    }
  }

  /** France, state by full name + city: Île-de-France / Paris stays correlated. */
  @Test
  public void testFrenchStateAndCityFilter() {
    AddressGenerator generator = new AddressGenerator("FR");
    generator.setStateFilter("Île-de-France");
    generator.setCityFilter("Paris");
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      Address address = generator.generate();
      assertEquals("11", address.getCity().getState().getId());
      assertEquals("Paris", address.getCity().getName());
      assertNotNull(address.getPostalCode());
    }
  }

  // helper ----------------------------------------------------------------------------------------------------------

  private void check(Country country, boolean supported) {
    AddressGenerator generator = new AddressGenerator(country.getIsoCode());
    generator.init(context);
    for (int i = 0; i < 100; i++) {
      Address address = generator.generate();
      logger.debug("{}", address);
      assertNotNull(address);
      // check generated phone numbers
      String cityAreaCode = address.getCity().getAreaCode();
      if (country.isMobilePhoneCityRelated()) {
        assertEquals(cityAreaCode, address.getMobilePhone().getAreaCode());
      }
      assertEquals(cityAreaCode, address.getOfficePhone().getAreaCode());
      assertEquals(cityAreaCode, address.getFax().getAreaCode());
      assertNotNull(address.getState());
      assertNotNull(address.getCountry());
      assertNotNull("No organization specified", address.getOrganization());
      assertNotNull("No department specified", address.getDepartment());
      // check country
      if (supported) {
        assertEquals(country, address.getCountry());
      } else {
        assertEquals(Country.US, address.getCountry());
      }
    }
  }

  @Test
  public void testConstructor() {
    AddressGenerator actualAddressGenerator = new AddressGenerator("Dataset");
    assertEquals("AddressGenerator[Dataset]", actualAddressGenerator.toString());
    Class<?> expectedGeneratedType = Address.class;
    assertSame(expectedGeneratedType, actualAddressGenerator.getGeneratedType());
  }

  @SuppressWarnings("unchecked")
  public void checkDescriptorMapping(Country country) {
    String xml =
        "<variable name='x' " +
            "generator='com.rapiddweller.domain.address.AddressGenerator' ";
    if (country != null) {
      xml += "dataset='" + country.getIsoCode() + "'";
    }
    xml += "/>";
    Element element = XMLUtil.parseStringAsElement(xml);
    ModelParser parser = new ModelParser(context, true);
    ComplexTypeDescriptor parent = createComplexType("y");
    InstanceDescriptor descriptor = parser.parseVariable(element);
    Generator<Address> generator = (Generator<Address>) InstanceGeneratorFactory.createSingleInstanceGenerator(
        descriptor, Uniqueness.NONE, context);
    generator.init(context);
    Country generatedCountry = GeneratorUtil.generateNonNull(generator).getCountry();
    if (country == null) {
      assertEquals(Country.getDefault(), generatedCountry);
    } else {
      assertEquals(country, generatedCountry);
    }
  }

}