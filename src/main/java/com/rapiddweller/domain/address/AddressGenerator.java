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

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.primitive.VarLengthStringGenerator;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Generates {@link Address} objects.<br/><br/>
 * Created: 11.06.2006 08:07:40
 * @author Volker Bergmann
 * @since 0.1
 */
public class AddressGenerator extends CompositeGenerator<Address>
    implements NonNullGenerator<Address> {

  VarLengthStringGenerator localPhoneNumberGenerator;
  private String dataset;
  private CityGenerator cityGenerator;
  private StreetNameGenerator streetNameGenerator;

  // constructors ----------------------------------------------------------------------------------------------------

  public AddressGenerator() {
    this(Country.getDefault().getIsoCode());
  }

  public AddressGenerator(String dataset) {
    super(Address.class);
    logger.debug("Instantiated AddressGenerator with dataset '{}'", dataset);
    setDataset(dataset);
  }

  // properties ------------------------------------------------------------------------------------------------------

  public void setCountry(Country country) {
    setDataset(country.getIsoCode());
  }

  public void setDataset(String dataset) {
    this.dataset = dataset;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    try {
      initMembers(context);
    } catch (RuntimeException e) {
      logger.error("Error initializing members", e);
      Country fallBackCountry = Country.getFallback();
      if (!fallBackCountry.getIsoCode().equals(this.dataset)) {
        logger.error("Cannot generate addresses for " + dataset +
            ", falling back to " + fallBackCountry);
        setCountry(fallBackCountry);
        initMembers(context);
      } else {
        throw e;
      }
    }
    super.init(context);
  }

  @Override
  public ProductWrapper<Address> generate(ProductWrapper<Address> wrapper) {
    return wrapper.wrap(generate());
  }

  @Override
  public Address generate() {
    assertInitialized();
    City city = cityGenerator.generate();
    Country country = city.getCountry();
    Street street = new Street(city,
        streetNameGenerator.generateForCountryAndLocale(
            country.getIsoCode(), city.getLanguage()));
    String[] data = street.generateHouseNumberWithPostalCode();
    String houseNumber = data[0];
    String postalCode = data[1];
    PhoneNumber privatePhone = generatePhoneNumber(city);
    PhoneNumber officePhone = generatePhoneNumber(city);
    PhoneNumber mobilePhone = country.generateMobileNumber(city);
    PhoneNumber fax = generatePhoneNumber(city);
    return new Address(street.getName(), houseNumber, postalCode, city,
        city.getState(), country,
        privatePhone, officePhone, mobilePhone, fax);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + dataset + ']';
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void initMembers(GeneratorContext context) {
    CountryGenerator countryGenerator =
        registerComponent(new CountryGenerator(dataset));
    countryGenerator.init(context);
    cityGenerator = registerComponent(new CityGenerator(dataset));
    cityGenerator.init(context);
    streetNameGenerator =
        registerComponent(new StreetNameGenerator(dataset));
    streetNameGenerator.init(context);
    localPhoneNumberGenerator = BeneratorFactory.getInstance()
        .createVarLengthStringGenerator("[0-9]", 10, 10);
    localPhoneNumberGenerator.init(context);
  }

  private PhoneNumber generatePhoneNumber(City city) {
    int localPhoneNumberLength = 10 - city.getAreaCode().length();
    String localCode = localPhoneNumberGenerator
        .generateWithLength(localPhoneNumberLength);
    return new PhoneNumber(city.getCountry().getPhoneCode(),
        city.getAreaCode(), localCode);
  }

}
