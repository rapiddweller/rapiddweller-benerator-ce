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

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.primitive.RandomVarLengthStringGenerator;
import com.rapiddweller.benerator.primitive.RegexStringGenerator;
import com.rapiddweller.benerator.util.RandomUtil;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.collection.OrderedNameMap;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.csv.CSVLineIterator;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.csv.CSVEntitySource;
import com.rapiddweller.platform.java.BeanDescriptorProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a country and provides constants for most bigger countries.
 * Country information is read from the file com/rapiddweller/domain/address/country.csv.<br/><br/>
 * Created: 11.06.2006 08:15:37
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class Country {

  private static final Logger LOGGER = LogManager.getLogger(Country.class);
  private static final String DEFAULT_PHONE_CODE = "[2-9][0-9][0-9]";
  private static final String DEFAULT_MOBILE_PHONE_PATTERN =
      "[1-9][0-9][0-9]";
  private static final Map<String, Country> instances = new HashMap<>(250);
  /**
   * The constant GERMANY.
   */
  public static final Country GERMANY = getInstance("DE");
  /**
   * The constant AUSTRIA.
   */
  public static final Country AUSTRIA = getInstance("AT");
  /**
   * The constant SWITZERLAND.
   */
  public static final Country SWITZERLAND = getInstance("CH");
  /**
   * The constant LIECHTENSTEIN.
   */
  public static final Country LIECHTENSTEIN = getInstance("LI");
  /**
   * The constant BELGIUM.
   */
  // BeNeLux
  public static final Country BELGIUM = getInstance("BE");
  /**
   * The constant NETHERLANDS.
   */
  public static final Country NETHERLANDS = getInstance("NL");
  /**
   * The constant LUXEMBURG.
   */
  public static final Country LUXEMBURG = getInstance("LU");
  /**
   * The constant DENMARK.
   */
  public static final Country DENMARK = getInstance("DK");
  /**
   * The constant FINLAND.
   */
  public static final Country FINLAND = getInstance("FI");
  /**
   * The constant IRELAND.
   */
  public static final Country IRELAND = getInstance("IE");
  /**
   * The constant ICELAND.
   */
  public static final Country ICELAND = getInstance("IS");
  /**
   * The constant NORWAY.
   */
  public static final Country NORWAY = getInstance("NO");
  /**
   * The constant SWEDEN.
   */
  public static final Country SWEDEN = getInstance("SE");
  /**
   * The constant UNITED_KINGDOM.
   */
  public static final Country UNITED_KINGDOM = getInstance("GB");
  /**
   * The constant GREAT_BRITAIN.
   */
  public static final Country GREAT_BRITAIN = getInstance("GB");
  /**
   * The constant ITALY.
   */
  public static final Country ITALY = getInstance("IT");
  /**
   * The constant SAN_MARINO.
   */
  public static final Country SAN_MARINO = getInstance("SM");
  /**
   * The constant MALTA.
   */
  public static final Country MALTA = getInstance("MT");
  /**
   * The constant FRANCE.
   */
  public static final Country FRANCE = getInstance("FR");
  /**
   * The constant MONACO.
   */
  public static final Country MONACO = getInstance("MC");
  /**
   * The constant ANDORRA.
   */
  public static final Country ANDORRA = getInstance("AD");
  /**
   * The constant SPAIN.
   */
  public static final Country SPAIN = getInstance("ES");
  /**
   * The constant PORTUGAL.
   */
  public static final Country PORTUGAL = getInstance("PT");
  /**
   * The constant GREECE.
   */
  public static final Country GREECE = getInstance("GR");
  /**
   * The constant CYPRUS.
   */
  public static final Country CYPRUS = getInstance("CY");
  /**
   * The constant TURKEY.
   */
  public static final Country TURKEY = getInstance("TR");
  /**
   * The constant ALBANIA.
   */
  public static final Country ALBANIA = getInstance("AL");
  /**
   * The constant BOSNIA_AND_HERZEGOVINA.
   */
  public static final Country BOSNIA_AND_HERZEGOVINA = getInstance("BA");
  /**
   * The constant BULGARIA.
   */
  public static final Country BULGARIA = getInstance("BG");
  /**
   * The constant BELARUS.
   */
  public static final Country BELARUS = getInstance("BY");
  /**
   * The constant CZECH_REPUBLIC.
   */
  public static final Country CZECH_REPUBLIC = getInstance("CZ");
  /**
   * The constant ESTONIA.
   */
  public static final Country ESTONIA = getInstance("EE");
  /**
   * The constant CROATIA.
   */
  public static final Country CROATIA = getInstance("HR");
  /**
   * The constant HUNGARY.
   */
  public static final Country HUNGARY = getInstance("HU");
  /**
   * The constant LITHUANIA.
   */
  public static final Country LITHUANIA = getInstance("LT");

  // java.lang.Object overrides --------------------------------------------------------------------------------------
  /**
   * The constant LATVIA.
   */
  public static final Country LATVIA = getInstance("LV");
  /**
   * The constant POLAND.
   */
  public static final Country POLAND = getInstance("PL");
  /**
   * The constant ROMANIA.
   */
  public static final Country ROMANIA = getInstance("RO");

  // constants -------------------------------------------------------------------------------------------------------
  /**
   * The constant RUSSIA.
   */
  public static final Country RUSSIA = getInstance("RU");
  /**
   * The constant SERBIA.
   */
  public static final Country SERBIA = getInstance("RS");
  /**
   * The constant SLOVENIA.
   */
  public static final Country SLOVENIA = getInstance("SI");
  /**
   * The constant SLOVAKIA.
   */
  public static final Country SLOVAKIA = getInstance("SK");
  /**
   * The constant UKRAINE.
   */
  public static final Country UKRAINE = getInstance("UA");
  /**
   * The constant UNITED_ARAB_EMIRATES.
   */
  public static final Country UNITED_ARAB_EMIRATES = getInstance("AE");
  /**
   * The constant AFGHANISTAN.
   */
  public static final Country AFGHANISTAN = getInstance("AF");
  /**
   * The constant BAHRAIN.
   */
  public static final Country BAHRAIN = getInstance("BH");
  /**
   * The constant ISRAEL.
   */
  public static final Country ISRAEL = getInstance("IL");
  /**
   * The constant IRAN.
   */
  public static final Country IRAN = getInstance("IR");
  /**
   * The constant IRAQ.
   */
  public static final Country IRAQ = getInstance("IQ");
  /**
   * The constant JORDAN.
   */
  public static final Country JORDAN = getInstance("JO");
  /**
   * The constant KAZAKHSTAN.
   */
  public static final Country KAZAKHSTAN = getInstance("KZ");
  /**
   * The constant PAKISTAN.
   */
  public static final Country PAKISTAN = getInstance("PK");
  /**
   * The constant QATAR.
   */
  public static final Country QATAR = getInstance("QA");
  /**
   * The constant SAUDI_ARABIA.
   */
  public static final Country SAUDI_ARABIA = getInstance("SA");
  /**
   * The constant ALGERIA.
   */
  public static final Country ALGERIA = getInstance("AL");
  /**
   * The constant EGYPT.
   */
  public static final Country EGYPT = getInstance("EG");
  /**
   * The constant GHANA.
   */
  public static final Country GHANA = getInstance("GH");
  /**
   * The constant KENYA.
   */
  public static final Country KENYA = getInstance("KE");
  /**
   * The constant SOUTH_AFRICA.
   */
  public static final Country SOUTH_AFRICA = getInstance("ZA");
  /**
   * The constant USA.
   */
  public static final Country USA = getInstance("US");
  /**
   * The constant US.
   */
  public static final Country US = USA;
  /**
   * The constant CANADA.
   */
  public static final Country CANADA = getInstance("CA");
  /**
   * The constant BAHAMAS.
   */
  public static final Country BAHAMAS = getInstance("BS");
  /**
   * The constant MEXICO.
   */
  public static final Country MEXICO = getInstance("MX");
  /**
   * The constant ARGENTINA.
   */
  public static final Country ARGENTINA = getInstance("AR");
  /**
   * The constant BRAZIL.
   */
  public static final Country BRAZIL = getInstance("BR");
  /**
   * The constant CHILE.
   */
  public static final Country CHILE = getInstance("CL");
  /**
   * The constant ECUADOR.
   */
  public static final Country ECUADOR = getInstance("EC");
  /**
   * The constant CHINA.
   */
  public static final Country CHINA = getInstance("CN");
  /**
   * The constant INDONESIA.
   */
  public static final Country INDONESIA = getInstance("ID");
  /**
   * The constant INDIA.
   */
  public static final Country INDIA = getInstance("IN");
  /**
   * The constant JAPAN.
   */
  public static final Country JAPAN = getInstance("JP");
  /**
   * The constant KOREA_PR.
   */
  public static final Country KOREA_PR = getInstance("KP");
  /**
   * The constant KOREA_R.
   */
  public static final Country KOREA_R = getInstance("KR");
  /**
   * The constant MALAYSIA.
   */
  public static final Country MALAYSIA = getInstance("MY");
  /**
   * The constant SINGAPORE.
   */
  public static final Country SINGAPORE = getInstance("SG");
  /**
   * The constant THAILAND.
   */
  public static final Country THAILAND = getInstance("TH");
  /**
   * The constant TAIWAN.
   */
  public static final Country TAIWAN = getInstance("TW");
  /**
   * The constant VIETNAM.
   */
  public static final Country VIETNAM = getInstance("VN");
  /**
   * The constant NEW_ZEALAND.
   */
  public static final Country NEW_ZEALAND = getInstance("NZ");
  /**
   * The constant AUSTRALIA.
   */
  public static final Country AUSTRALIA = getInstance("AU");
  private static Country defaultCountry;

  static {
    parseConfigFile();
  }

  static {
    defaultCountry =
        Country.getInstance(LocaleUtil.getDefaultCountryCode());
  }

  private final String isoCode;
  private final String name;
  private final String phoneCode;
  private final boolean mobilePhoneCityRelated;
  private final RegexStringGenerator mobilePrefixGenerator;
  private final RandomVarLengthStringGenerator localNumberGenerator;
  private final Locale countryLocale;
  private final Locale defaultLanguageLocale;
  private final int population;
  private Map<String, State> states;
  private CityGenerator cityGenerator;
  private boolean citiesInitialized = false;

  private Country(String isoCode, String defaultLanguage, int population,
                  String phoneCode, String mobileCodePattern,
                  String name) {
    this.isoCode = isoCode;
    this.defaultLanguageLocale = LocaleUtil.getLocale(defaultLanguage);
    this.phoneCode = phoneCode;
    this.countryLocale =
        new Locale(LocaleUtil.getLocale(defaultLanguage).getLanguage(),
            isoCode);
    this.mobilePhoneCityRelated = "BR".equalsIgnoreCase(isoCode); // TODO v1.0 make configuration generic
    this.mobilePrefixGenerator = new RegexStringGenerator(mobileCodePattern);
    this.mobilePrefixGenerator.init(null);
    this.localNumberGenerator = new RandomVarLengthStringGenerator("\\d", 7);
    this.localNumberGenerator.init(null);
    this.name = (name != null ? name :
        countryLocale.getDisplayCountry(Locale.US));
    this.population = population;
    importStates();
    instances.put(isoCode, this);
  }

  private static void mapProperty(String propertyName, Entity source,
                                  State target, boolean required) {
    String propertyValue = String.valueOf(source.get(propertyName));
    if (required) {
      Assert.notNull(propertyValue, propertyName);
    }
    BeanUtil.setPropertyValue(target, propertyName, propertyValue);
  }

  /**
   * Gets instances.
   *
   * @return the instances
   */
  public static Collection<Country> getInstances() {
    return instances.values();
  }

  /**
   * Retrieves a country from the country configuration file.
   *
   * @param isoCode the ISO code of the country to retrieve
   * @return if it is a predfined country, an instance with the configured data is returned, else one with the specified ISO code and default settings
   * , e.g. phoneCode 'UNKNOWN'.
   */
  public static Country getInstance(String isoCode) {
    return getInstance(isoCode, true);
  }

  /**
   * Retrieves a country from the country configuration file.
   *
   * @param isoCode the ISO code of the country to retrieve
   * @param create  the create
   * @return if it is a predfined country, an instance with the configured data is returned, else one with the specified ISO code and default settings
   * , e.g. phoneCode 'UNKNOWN'.
   */
  public static Country getInstance(String isoCode, boolean create) {
    Country country = instances.get(isoCode.toUpperCase());
    if (country == null && create) {
      country = new Country(isoCode, Locale.getDefault().getLanguage(),
          1000000, DEFAULT_PHONE_CODE, DEFAULT_MOBILE_PHONE_PATTERN,
          null);
    }
    return country;
  }

  /**
   * Has instance boolean.
   *
   * @param isoCode the iso code
   * @return the boolean
   */
  public static boolean hasInstance(String isoCode) {
    return (instances.get(isoCode.toUpperCase()) != null);
  }

  /**
   * Gets default.
   *
   * @return the default
   */
  public static Country getDefault() {
    return Country.defaultCountry;
  }

  /**
   * Sets default.
   *
   * @param country the country
   */
  public static void setDefault(Country country) {
    Country.defaultCountry = country;
  }

  /**
   * Gets fallback.
   *
   * @return the fallback
   */
  public static Country getFallback() {
    return Country.US;
  }

  private static void parseConfigFile() {
    CSVLineIterator iterator = null;
    try {
      String fileName = "/com/rapiddweller/domain/address/country.csv";
      iterator = new CSVLineIterator(fileName, ',', true);
      LOGGER.debug("Parsing country setup file {}", fileName);
      DataContainer<String[]> container = new DataContainer<>();
      while ((container = iterator.next(container)) != null) {
        String[] cells = container.getData();
        String isoCode = cells[0];
        String defaultLocale =
            (cells.length > 1 && !StringUtil.isEmpty(cells[1]) ?
                cells[1].trim() : "en");
        String phoneCode =
            (cells.length > 2 && !StringUtil.isEmpty(cells[2]) ?
                cells[2].trim() : null);
        String mobilCodePattern =
            (cells.length > 3 && !StringUtil.isEmpty(cells[3]) ?
                cells[3].trim() : DEFAULT_MOBILE_PHONE_PATTERN);
        String name = (cells.length > 4 ? cells[4].trim() : null);
        int population =
            (cells.length > 5 ? Integer.parseInt(cells[5].trim()) :
                1000000);
        Country country =
            new Country(isoCode, defaultLocale, population,
                phoneCode, mobilCodePattern, name);
        LOGGER.debug("Parsed country {}", country);
      }
    } catch (IOException e) {
      throw new ConfigurationError(
          "Country definition file could not be processed. ", e);
    } finally {
      if (iterator != null) {
        iterator.close();
      }
    }
  }

  private void importStates() {
    this.states = new OrderedNameMap<>();
    String filename =
        "/com/rapiddweller/domain/address/state_" + isoCode + ".csv";
    if (!IOUtil.isURIAvailable(filename)) {
      LOGGER.debug("No states defined for {}", this);
      return;
    }
    ComplexTypeDescriptor stateDescriptor =
        (ComplexTypeDescriptor) new BeanDescriptorProvider()
            .getTypeDescriptor(State.class.getName());
    CSVEntitySource source =
        new CSVEntitySource(filename, stateDescriptor, Encodings.UTF_8);
    source.setContext(new DefaultBeneratorContext());
    DataIterator<Entity> iterator = source.iterator();
    DataContainer<Entity> container = new DataContainer<>();
    while ((container = iterator.next(container)) != null) {
      Entity entity = container.getData();
      State state = new State();
      mapProperty("id", entity, state, true);
      mapProperty("name", entity, state, true);
      mapProperty("defaultLanguage", entity, state, false);
      state.setCountry(this);
      addState(state);
    }
    IOUtil.close(iterator);
  }

  /**
   * Gets iso code.
   *
   * @return the iso code
   */
  public String getIsoCode() {
    return isoCode;
  }

  /**
   * Returns the English name
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the name in the user's {@link Locale}
   *
   * @return the display name
   */
  public String getDisplayName() {
    return countryLocale.getDisplayCountry(Locale.getDefault());
  }

  /**
   * Returns the name in the country's own {@link Locale}
   *
   * @return the local name
   */
  public String getLocalName() {
    return countryLocale.getDisplayCountry(
        new Locale(defaultLanguageLocale.getLanguage()));
  }

  /**
   * Gets default language locale.
   *
   * @return the default language locale
   */
  public Locale getDefaultLanguageLocale() {
    return defaultLanguageLocale;
  }

  /**
   * Gets population.
   *
   * @return the population
   */
  public int getPopulation() {
    return population;
  }

  /**
   * Gets phone code.
   *
   * @return the phone code
   */
  public String getPhoneCode() {
    return phoneCode;
  }

  /**
   * Gets state.
   *
   * @param stateId the state id
   * @return the state
   */
  public State getState(String stateId) {
    return states.get(stateId);
  }

  /**
   * Gets states.
   *
   * @return the states
   */
  public Collection<State> getStates() {
    return states.values();
  }

  /**
   * Add state.
   *
   * @param state the state
   */
  public void addState(State state) {
    state.setCountry(this);
    states.put(state.getId(), state);
  }

  /**
   * Is mobile phone city related boolean.
   *
   * @return the boolean
   */
  public boolean isMobilePhoneCityRelated() {
    return mobilePhoneCityRelated;
  }

  /**
   * Gets cities.
   *
   * @return the cities
   */
  public List<City> getCities() {
    List<City> cities = new ArrayList<>();
    for (State state : states.values()) {
      cities.addAll(state.getCities());
    }
    return cities;
  }

  /**
   * Generate city city.
   *
   * @return the city
   */
  public City generateCity() {
    return getCityGenerator().generate();
  }

  /**
   * Generate phone number phone number.
   *
   * @return the phone number
   */
  public PhoneNumber generatePhoneNumber() {
    if (RandomUtil.randomInt(0, 2) <
        2) {
      // generate land line numbers in 66% of the cases

      return generateLandlineNumber();
    } else {
      return generateMobileNumber();
    }
  }

  /**
   * Generate landline number phone number.
   *
   * @return the phone number
   */
  public PhoneNumber generateLandlineNumber() {
    return generateCity().generateLandlineNumber();
  }

  /**
   * Generate mobile number phone number.
   *
   * @return the phone number
   */
  public PhoneNumber generateMobileNumber() {
    if (mobilePhoneCityRelated) {
      return generateCity().generateMobileNumber();
    } else {
      return generateMobileNumber(null);
    }
  }

  /**
   * Generate mobile number phone number.
   *
   * @param city the city
   * @return the phone number
   */
  public PhoneNumber generateMobileNumber(City city) {
    String localNumber = localNumberGenerator.generate();
    String mobilePrefix = mobilePrefixGenerator.generate();
    if (mobilePhoneCityRelated) {
      return new PhoneNumber(phoneCode,
          city != null ? city.getAreaCode() : null,
          mobilePrefix +
              localNumber.substring(mobilePrefix.length()));
    } else {
      return new PhoneNumber(phoneCode,
          mobilePrefix,
          localNumber);
    }
  }

  private CityGenerator getCityGenerator() {
    if (cityGenerator == null) {
      cityGenerator = new CityGenerator(this.getIsoCode());
      cityGenerator.init(null);
    }
    return cityGenerator;
  }

  // initialization --------------------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public int hashCode() {
    return isoCode.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Country other = (Country) obj;
    return isoCode.equals(other.isoCode);
  }

  /**
   * Check cities.
   */
  void checkCities() {
    if (!citiesInitialized) {
      synchronized (this) {
        if (!citiesInitialized) {
          citiesInitialized = true;
          CityManager.readCities(this);
        }
      }
    }
  }

}
