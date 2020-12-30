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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.io.IOException;

/**
 * Represents a country and provides constants for most bigger countries.
 * Country information is read from the file com/rapiddweller/domain/address/country.csv.<br/><br/>
 * Created: 11.06.2006 08:15:37
 * @since 0.1
 * @author Volker Bergmann
 */
public class Country {

    private final String isoCode;
    private final String name;
    private final String phoneCode;
	private final boolean mobilePhoneCityRelated;
	private final RegexStringGenerator mobilePrefixGenerator;
    private final RandomVarLengthStringGenerator localNumberGenerator;
    private final Locale countryLocale;
    private final Locale defaultLanguageLocale;
    private Map<String, State> states;
    private final int population;

	private CityGenerator cityGenerator;


    private Country(String isoCode, String defaultLanguage, int population, String phoneCode, String mobileCodePattern, 
    		String name) {
        this.isoCode = isoCode;
        this.defaultLanguageLocale = LocaleUtil.getLocale(defaultLanguage);
        this.phoneCode = phoneCode;
        this.countryLocale = new Locale(LocaleUtil.getLocale(defaultLanguage).getLanguage(), isoCode);
        this.mobilePhoneCityRelated = "BR".equals(isoCode.toUpperCase()); // TODO v1.0 make configuration generic
        this.mobilePrefixGenerator = new RegexStringGenerator(mobileCodePattern);
        this.mobilePrefixGenerator.init(null);
        this.localNumberGenerator = new RandomVarLengthStringGenerator("\\d", 7);
        this.localNumberGenerator.init(null);
        this.name = (name != null ? name : countryLocale.getDisplayCountry(Locale.US));
        this.population = population;
        importStates();
        instances.put(isoCode, this);
    }

    private void importStates() {
        this.states = new OrderedNameMap<>();
        String filename = "/com/rapiddweller/domain/address/state_" + isoCode + ".csv";
        if (!IOUtil.isURIAvailable(filename)) {
        	LOGGER.debug("No states defined for {}", this);
        	return;
        }
		ComplexTypeDescriptor stateDescriptor = (ComplexTypeDescriptor) new BeanDescriptorProvider().getTypeDescriptor(State.class.getName());
		CSVEntitySource source = new CSVEntitySource(filename, stateDescriptor, Encodings.UTF_8);
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

    private static void mapProperty(String propertyName, Entity source, State target, boolean required) {
    	String propertyValue = String.valueOf(source.get(propertyName));
    	if (required)
    		Assert.notNull(propertyValue, propertyName);
    	BeanUtil.setPropertyValue(target, propertyName, propertyValue);
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
        return countryLocale.getDisplayCountry(new Locale(defaultLanguageLocale.getLanguage()));
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
    	for (State state : states.values())
            cities.addAll(state.getCities());
    	return cities;
    }
    
	public City generateCity() {
	    return getCityGenerator().generate();
    }

	public PhoneNumber generatePhoneNumber() {
		if (RandomUtil.randomInt(0, 2) < 2) // generate land line numbers in 66% of the cases
			return generateLandlineNumber();
		else
			return generateMobileNumber();
    }

	public PhoneNumber generateLandlineNumber() {
		return generateCity().generateLandlineNumber();
    }

	public PhoneNumber generateMobileNumber() {
		if (mobilePhoneCityRelated)
			return generateCity().generateMobileNumber();
		else
			return generateMobileNumber(null);
    }

	public PhoneNumber generateMobileNumber(City city) {
		String localNumber = localNumberGenerator.generate();
		String mobilePrefix = mobilePrefixGenerator.generate();
		if (mobilePhoneCityRelated)
			return new PhoneNumber(phoneCode, 
					city != null ? city.getAreaCode() : null,
					mobilePrefix + localNumber.substring(mobilePrefix.length()));
		else
			return new PhoneNumber(phoneCode, 
					mobilePrefix, 
					localNumber);
    }

    private CityGenerator getCityGenerator() {
    	if (cityGenerator == null) {
    		cityGenerator = new CityGenerator(this.getIsoCode());
    		cityGenerator.init(null);
    	}
	    return cityGenerator;
    }

	public static Collection<Country> getInstances() {
        return instances.values();
    }

    /**
     * Retrieves a country from the country configuration file.
     * @param isoCode the ISO code of the country to retrieve
     * @return if it is a predfined country, an instance with the configured data is returned,
     * else one with the specified ISO code and default settings, e.g. phoneCode 'UNKNOWN'.
     */
    public static Country getInstance(String isoCode) {
        return getInstance(isoCode, true);
    }

    /**
     * Retrieves a country from the country configuration file.
     * @param isoCode the ISO code of the country to retrieve
     * @return if it is a predfined country, an instance with the configured data is returned,
     * else one with the specified ISO code and default settings, e.g. phoneCode 'UNKNOWN'.
     */
    public static Country getInstance(String isoCode, boolean create) {
        Country country = instances.get(isoCode.toUpperCase());
        if (country == null && create)
            country = new Country(isoCode, Locale.getDefault().getLanguage(), 1000000, DEFAULT_PHONE_CODE, DEFAULT_MOBILE_PHONE_PATTERN, null);
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
	
    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Country other = (Country) obj;
		return isoCode.equals(other.isoCode);
	}

	// constants -------------------------------------------------------------------------------------------------------
	
	private static final Logger LOGGER = LogManager.getLogger(Country.class);

	private static final String DEFAULT_PHONE_CODE = "[2-9][0-9][0-9]";

	private static final String DEFAULT_MOBILE_PHONE_PATTERN = "[1-9][0-9][0-9]";

    private static final Map<String, Country> instances = new HashMap<>(250);

    static {
        parseConfigFile();
    }

    // German speaking countries
    public static final Country GERMANY = getInstance("DE");
    public static final Country AUSTRIA = getInstance("AT");
    public static final Country SWITZERLAND = getInstance("CH");
    public static final Country LIECHTENSTEIN = getInstance("LI");

    // BeNeLux
    public static final Country BELGIUM = getInstance("BE");
    public static final Country NETHERLANDS = getInstance("NL");
    public static final Country LUXEMBURG = getInstance("LU");

    // Northern Europe
    public static final Country DENMARK = getInstance("DK");
    public static final Country FINLAND = getInstance("FI");
    public static final Country IRELAND = getInstance("IE");
    public static final Country ICELAND = getInstance("IS");
    public static final Country NORWAY = getInstance("NO");
    public static final Country SWEDEN = getInstance("SE");
    public static final Country UNITED_KINGDOM = getInstance("GB");
    public static final Country GREAT_BRITAIN = getInstance("GB");

    // Southern Europe
    public static final Country ITALY = getInstance("IT");
    public static final Country SAN_MARINO = getInstance("SM");
    public static final Country MALTA = getInstance("MT");
    public static final Country FRANCE = getInstance("FR");
    public static final Country MONACO = getInstance("MC");
    public static final Country ANDORRA = getInstance("AD");
    public static final Country SPAIN = getInstance("ES");
    public static final Country PORTUGAL = getInstance("PT");

    // South-East Europe
    public static final Country GREECE = getInstance("GR");
    public static final Country CYPRUS = getInstance("CY");
    public static final Country TURKEY = getInstance("TR");

    // Eastern Europe
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

    // Near East
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
    
    // Africa
    public static final Country ALGERIA = getInstance("AL");
    public static final Country EGYPT = getInstance("EG");
    public static final Country GHANA = getInstance("GH");
    public static final Country KENYA = getInstance("KE");
    public static final Country SOUTH_AFRICA = getInstance("ZA");
    
    // North America
    public static final Country USA = getInstance("US");
    public static final Country US = USA;
    public static final Country CANADA = getInstance("CA");
    
    // Central America
    public static final Country BAHAMAS = getInstance("BS");
    public static final Country MEXICO = getInstance("MX");
    
    // South America
    public static final Country ARGENTINA = getInstance("AR");
    public static final Country BRAZIL = getInstance("BR");
    public static final Country CHILE = getInstance("CL");
    public static final Country ECUADOR = getInstance("EC");
    
    // Asia
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

    // Australia
    public static final Country NEW_ZEALAND = getInstance("NZ");
    public static final Country AUSTRALIA = getInstance("AU");

    private static Country defaultCountry;

    // initialization --------------------------------------------------------------------------------------------------
    
    static {
        defaultCountry = Country.getInstance(LocaleUtil.getDefaultCountryCode());
    }

    private static void parseConfigFile() {
        CSVLineIterator iterator = null;
        try {
            String FILE_NAME = "/com/rapiddweller/domain/address/country.csv";
            iterator = new CSVLineIterator(FILE_NAME, ',', true);
            LOGGER.debug("Parsing country setup file {}", FILE_NAME);
            DataContainer<String[]> container = new DataContainer<>();
            while ((container = iterator.next(container)) != null) {
                String[] cells = container.getData();
                String isoCode = cells[0];
                String defaultLocale = (cells.length > 1 && !StringUtil.isEmpty(cells[1]) ? cells[1].trim() : "en");
                String phoneCode = (cells.length > 2 && !StringUtil.isEmpty(cells[2]) ? cells[2].trim() : null);
                String mobilCodePattern = (cells.length > 3 && !StringUtil.isEmpty(cells[3]) ? cells[3].trim() : DEFAULT_MOBILE_PHONE_PATTERN);
                String name = (cells.length > 4 ? cells[4].trim() : null);
                int population = (cells.length > 5 ? Integer.parseInt(cells[5].trim()) : 1000000);
                Country country = new Country(isoCode, defaultLocale, population, phoneCode, mobilCodePattern, name);
                LOGGER.debug("Parsed country {}", country);
            }
        } catch (IOException e) {
            throw new ConfigurationError("Country definition file could not be processed. ", e);
        } finally {
            if (iterator != null)
                iterator.close();
        }
    }

    private boolean citiesInitialized = false;
    
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
