/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.primitive.RegexStringGenerator;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
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
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
 * @author Volker Bergmann
 * @since 0.1
 */
public class Country {

  private static final Logger logger = LoggerFactory.getLogger(Country.class);

  private static final String COUNTRY_CSV = "/com/rapiddweller/domain/address/country.csv";
  private static final RandomProvider RANDOM = BeneratorFactory.getInstance().getRandomProvider();
  private static final String DEFAULT_PHONE_CODE = "[2-9][0-9][0-9]";
  private static final String DEFAULT_MOBILE_PHONE_PATTERN = "[1-9][0-9][0-9]";
  private static final Map<String, Country> instances = new HashMap<>(250);

  static {
    parseConfigFile();
  }

  public static final Country GERMANY = getInstance("DE");
  public static final Country AUSTRIA = getInstance("AT");
  public static final Country SWITZERLAND = getInstance("CH");
  public static final Country LIECHTENSTEIN = getInstance("LI");
  // BeNeLux
  public static final Country BELGIUM = getInstance("BE");
  public static final Country NETHERLANDS = getInstance("NL");
  public static final Country LUXEMBURG = getInstance("LU");
  public static final Country DENMARK = getInstance("DK");
  public static final Country FINLAND = getInstance("FI");
  public static final Country IRELAND = getInstance("IE");
  public static final Country ICELAND = getInstance("IS");
  public static final Country NORWAY = getInstance("NO");
  public static final Country SWEDEN = getInstance("SE");
  public static final Country UNITED_KINGDOM = getInstance("GB");
  public static final Country GREAT_BRITAIN = getInstance("GB");
  public static final Country ITALY = getInstance("IT");
  public static final Country SAN_MARINO = getInstance("SM");
  public static final Country MALTA = getInstance("MT");
  public static final Country FRANCE = getInstance("FR");
  public static final Country MONACO = getInstance("MC");
  public static final Country ANDORRA = getInstance("AD");
  public static final Country SPAIN = getInstance("ES");
  public static final Country PORTUGAL = getInstance("PT");
  public static final Country GREECE = getInstance("GR");
  public static final Country CYPRUS = getInstance("CY");
  public static final Country TURKEY = getInstance("TR");
  public static final Country ALBANIA = getInstance("AL");
  public static final Country BOSNIA_AND_HERZEGOVINA = getInstance("BA");
  public static final Country BULGARIA = getInstance("BG");
  public static final Country BELARUS = getInstance("BY");
  public static final Country CZECH_REPUBLIC = getInstance("CZ");
  public static final Country ESTONIA = getInstance("EE");
  public static final Country CROATIA = getInstance("HR");
  public static final Country HUNGARY = getInstance("HU");
  public static final Country LITHUANIA = getInstance("LT");
  public static final Country LATVIA = getInstance("LV");
  public static final Country POLAND = getInstance("PL");
  public static final Country ROMANIA = getInstance("RO");
  public static final Country RUSSIA = getInstance("RU");
  public static final Country SERBIA = getInstance("RS");
  public static final Country SLOVENIA = getInstance("SI");
  public static final Country SLOVAKIA = getInstance("SK");
  public static final Country UKRAINE = getInstance("UA");
  public static final Country UNITED_ARAB_EMIRATES = getInstance("AE");
  public static final Country AFGHANISTAN = getInstance("AF");
  public static final Country BAHRAIN = getInstance("BH");
  public static final Country ISRAEL = getInstance("IL");
  public static final Country IRAN = getInstance("IR");
  public static final Country IRAQ = getInstance("IQ");
  public static final Country JORDAN = getInstance("JO");
  public static final Country KAZAKHSTAN = getInstance("KZ");
  public static final Country PAKISTAN = getInstance("PK");
  public static final Country QATAR = getInstance("QA");
  public static final Country SAUDI_ARABIA = getInstance("SA");
  public static final Country ALGERIA = getInstance("AL");
  public static final Country EGYPT = getInstance("EG");
  public static final Country GHANA = getInstance("GH");
  public static final Country KENYA = getInstance("KE");
  public static final Country SOUTH_AFRICA = getInstance("ZA");
  public static final Country USA = getInstance("US");
  public static final Country US = USA;
  public static final Country CANADA = getInstance("CA");
  public static final Country BAHAMAS = getInstance("BS");
  public static final Country MEXICO = getInstance("MX");
  public static final Country ARGENTINA = getInstance("AR");
  public static final Country BRAZIL = getInstance("BR");
  public static final Country CHILE = getInstance("CL");
  public static final Country ECUADOR = getInstance("EC");
  public static final Country CHINA = getInstance("CN");
  public static final Country INDONESIA = getInstance("ID");
  public static final Country INDIA = getInstance("IN");
  public static final Country JAPAN = getInstance("JP");
  public static final Country KOREA_PR = getInstance("KP");
  public static final Country KOREA_R = getInstance("KR");
  public static final Country MALAYSIA = getInstance("MY");
  public static final Country SINGAPORE = getInstance("SG");
  public static final Country THAILAND = getInstance("TH");
  public static final Country TAIWAN = getInstance("TW");
  public static final Country VIETNAM = getInstance("VN");
  public static final Country NEW_ZEALAND = getInstance("NZ");
  public static final Country AUSTRALIA = getInstance("AU");

  private static Country defaultCountry;

  static {
    defaultCountry = Country.getInstance(LocaleUtil.getDefaultCountryCode());
  }

  private final String isoCode;
  private final String name;
  private final String phoneCode;
  private final boolean mobilePhoneCityRelated;
  private final RegexStringGenerator mobilePrefixGenerator;
  private final Generator<String> localNumberGenerator;
  private final Locale countryLocale;
  private final Locale defaultLanguageLocale;
  private final int population;
  private Map<String, State> states;
  private CityGenerator cityGenerator;
  private volatile boolean citiesInitialized = false;
  private final WrapperProvider<String> swp = new WrapperProvider<>();

  private Country(String isoCode, String defaultLanguage, int population,
                  String phoneCode, String mobileCodePattern,
                  String name) {
    this.isoCode = isoCode;
    this.defaultLanguageLocale = LocaleUtil.getLocale(defaultLanguage);
    this.phoneCode = phoneCode;
    this.countryLocale =
        new Locale(LocaleUtil.getLocale(defaultLanguage).getLanguage(),
            isoCode);
    this.mobilePhoneCityRelated = "BR".equalsIgnoreCase(isoCode); // TODO make configuration generic
    this.mobilePrefixGenerator = new RegexStringGenerator(mobileCodePattern);
    this.mobilePrefixGenerator.init(null);
    this.localNumberGenerator = BeneratorFactory.getInstance()
        .createVarLengthStringGenerator("[0-9]", 7, 7, 1, null);
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

  public static Collection<Country> getInstances() {
    return instances.values();
  }

  /** Retrieves a country from the country configuration file.
   *  @param isoCode the ISO code of the country to retrieve
   *  @return if it is a predefined country, an instance with the configured data is returned, else one with the specified ISO code and default settings
   * , e.g. phoneCode 'UNKNOWN'. */
  public static Country getInstance(String isoCode) {
    return getInstance(isoCode, true);
  }

  /** Retrieves a country from the country configuration file.
   *  @param isoCode the ISO code of the country to retrieve
   *  @param create  the create
   *  @return if it is a predfined country, an instance with the configured data is returned, else one with the specified ISO code and default settings
   *  , e.g. phoneCode 'UNKNOWN'. */
  public static Country getInstance(String isoCode, boolean create) {
    Country country = instances.get(isoCode.toUpperCase());
    if (country == null && create) {
      country = new Country(isoCode, Locale.getDefault().getLanguage(),
          1000000, DEFAULT_PHONE_CODE, DEFAULT_MOBILE_PHONE_PATTERN,
          null);
    }
    return country;
  }

  public static boolean hasInstance(String isoCode) {
    return (instances.get(isoCode.toUpperCase()) != null);
  }

  public static Country getDefault() {
    return Country.defaultCountry;
  }

  public static void setDefault(Country country) {
    Country.defaultCountry = country;
  }

  public static Country getFallback() {
    return Country.US;
  }

  private static void parseConfigFile() {
    try (CSVLineIterator iterator = new CSVLineIterator(COUNTRY_CSV, ',', true)) {
      logger.debug("Parsing country setup file {}", COUNTRY_CSV);
      DataContainer<String[]> container = new DataContainer<>();
      while ((container = iterator.next(container)) != null) {
        String[] cells = container.getData();
        String isoCode = cells[0];
        String defaultLocale = cellValueOrDefault(cells, 1, "en");
        String phoneCode = cellValueOrDefault(cells, 2, null);
        String mobilCodePattern = cellValueOrDefault(cells, 3, DEFAULT_MOBILE_PHONE_PATTERN);
        String name = cellValueOrDefault(cells, 4, null);
        int population = Integer.parseInt(cellValueOrDefault(cells, 5, "1000000"));
        Country country = new Country(isoCode, defaultLocale, population, phoneCode, mobilCodePattern, name);
        logger.debug("Parsed country {}", country);
      }
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().componentInitializationFailed("Error processing Country definition file. ", e);
    }
  }

  private static String cellValueOrDefault(String[] cells, int index, String defaultValue) {
    return (cells.length > index && !StringUtil.isEmpty(cells[index]) ? cells[index].trim() : defaultValue);
  }

  private void importStates() {
    this.states = new OrderedNameMap<>();
    String filename = "/com/rapiddweller/domain/address/state_" + isoCode + ".csv";
    if (!IOUtil.isURIAvailable(filename)) {
      logger.debug("No states defined for {}", this);
      return;
    }
    ComplexTypeDescriptor stateDescriptor =
        (ComplexTypeDescriptor) new BeanDescriptorProvider().getTypeDescriptor(State.class.getName());
    CSVEntitySource source = new CSVEntitySource(filename, stateDescriptor, Encodings.UTF_8);
    source.setContext(new DefaultBeneratorContext());
    try (DataIterator<Entity> iterator = source.iterator()) {
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
    }
  }

  public String getIsoCode() {
    return isoCode;
  }

  /** Returns the English name */
  public String getName() {
    return name;
  }

  /** Returns the name in the user's {@link Locale} */
  public String getDisplayName() {
    return countryLocale.getDisplayCountry(Locale.getDefault());
  }

  /** Returns the name in the country's own {@link Locale} */
  public String getLocalName() {
    return countryLocale.getDisplayCountry(
        new Locale(defaultLanguageLocale.getLanguage()));
  }

  public Locale getDefaultLanguageLocale() {
    return defaultLanguageLocale;
  }

  public int getPopulation() {
    return population;
  }

  public String getPhoneCode() {
    return phoneCode;
  }

  public State getState(String stateId) {
    return states.get(stateId);
  }

  public Collection<State> getStates() {
    return states.values();
  }

  public void addState(State state) {
    state.setCountry(this);
    states.put(state.getId(), state);
  }

  public boolean isMobilePhoneCityRelated() {
    return mobilePhoneCityRelated;
  }

  public List<City> getCities() {
    List<City> cities = new ArrayList<>();
    for (State state : states.values()) {
      cities.addAll(state.getCities());
    }
    return cities;
  }

  public City generateCity() {
    return getCityGenerator().generate();
  }

  public PhoneNumber generatePhoneNumber() {
    if (RANDOM.randomInt(0, 2) < 2) {
      // generate land line numbers in 66% of the cases
      return generateLandlineNumber();
    } else {
      return generateMobileNumber();
    }
  }

  public PhoneNumber generateLandlineNumber() {
    return generateCity().generateLandlineNumber();
  }

  public PhoneNumber generateMobileNumber() {
    if (mobilePhoneCityRelated) {
      return generateCity().generateMobileNumber();
    } else {
      return generateMobileNumber(null);
    }
  }

  public PhoneNumber generateMobileNumber(City city) {
    String mobilePrefix = mobilePrefixGenerator.generate();
    String localNumber = generateString(localNumberGenerator);
    if (localNumber == null)
      return null;
    if (mobilePhoneCityRelated) {
      String cityCode = city != null ? city.getAreaCode() : null;
      localNumber = mobilePrefix + localNumber.substring(mobilePrefix.length());
      return new PhoneNumber(phoneCode, cityCode, localNumber);
    } else {
      return new PhoneNumber(phoneCode, mobilePrefix, localNumber);
    }
  }

  private String generateString(Generator<String> generator) {
    ProductWrapper<String> wrapper = generator.generate(swp.get());
    return (wrapper != null ? wrapper.unwrap() : null);
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
