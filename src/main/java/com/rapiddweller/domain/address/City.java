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

import com.rapiddweller.benerator.primitive.RandomVarLengthStringGenerator;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.Escalator;
import com.rapiddweller.common.LoggerEscalator;
import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.StringUtil;

import java.util.Locale;

/**
 * Represents a city.<br/><br/>
 * Created: 11.06.2006 08:19:23
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class City {

  private static final Escalator escalator = new LoggerEscalator();
  private static final RandomVarLengthStringGenerator localNumberGenerator;

  static {
    localNumberGenerator =
        new RandomVarLengthStringGenerator("\\d", 7, 8, 1);
    localNumberGenerator.init(null);
  }

  private final String name;
  private String nameExtension;
  private String[] postalCodes;
  private String areaCode;
  private State state;
  private Locale language;
  private int population;

  /**
   * Instantiates a new City.
   *
   * @param state       the state
   * @param name        the name
   * @param addition    the addition
   * @param postalCodes the postal codes
   * @param areaCode    the area code
   */
  public City(State state, String name, String addition, String[] postalCodes,
              String areaCode) {
    if (areaCode == null) {
      throw new IllegalArgumentException("Area Code is null for " + name);
    }
    this.state = state;
    this.name = name;
    this.nameExtension = addition;
    this.postalCodes = (postalCodes != null ? postalCodes : new String[0]);
    this.areaCode = areaCode;
  }

  /**
   * Gets name extension.
   *
   * @return the name extension
   */
  public String getNameExtension() {
    return nameExtension;
  }

  /**
   * Sets name extension.
   *
   * @param nameExtension the name extension
   */
  public void setNameExtension(String nameExtension) {
    this.nameExtension = nameExtension;
  }

  /**
   * Get postal codes string [ ].
   *
   * @return the string [ ]
   */
  public String[] getPostalCodes() {
    return postalCodes;
  }

  /**
   * Sets postal codes.
   *
   * @param postalCodes the postal codes
   */
  public void setPostalCodes(String[] postalCodes) {
    this.postalCodes = postalCodes;
  }

  /**
   * Add postal code.
   *
   * @param postalCode the postal code
   */
  public void addPostalCode(String postalCode) {
    postalCodes = ArrayUtil.append(postalCode, postalCodes);
  }

  /**
   * Get zip codes string [ ].
   *
   * @return the string [ ]
   * @deprecated use property postalCodes
   */
  @Deprecated
  public String[] getZipCodes() {
    escalator.escalate(
        "property City.zipCode is deprecated, use City.postalCode instead",
        City.class, "Invoked getZipCodes()");
    return getPostalCodes();
  }

  /**
   * Sets zip codes.
   *
   * @param zipCodes the zip codes
   * @deprecated use property postalCodes
   */
  @Deprecated
  public void setZipCodes(String[] zipCodes) {
    escalator.escalate(
        "property City.zipCode is deprecated, use City.postalCode instead",
        City.class, "Invoked setZipCodes()");
    this.postalCodes = zipCodes;
  }

  /**
   * Add zip code.
   *
   * @param zipCode the zip code
   * @deprecated use property postalCodes
   */
  @Deprecated
  public void addZipCode(String zipCode) {
    escalator.escalate(
        "property City.zipCode is deprecated, use City.postalCode instead",
        City.class, "Invoked addZipCode()");
    postalCodes = ArrayUtil.append(zipCode, postalCodes);
  }

  /**
   * Gets area code.
   *
   * @return the area code
   */
  public String getAreaCode() {
    return areaCode;
  }

  /**
   * Sets area code.
   *
   * @param phoneCode the phone code
   */
  public void setAreaCode(String phoneCode) {
    this.areaCode = phoneCode;
  }

  /**
   * Gets state.
   *
   * @return the state
   */
  public State getState() {
    return state;
  }

  /**
   * Sets state.
   *
   * @param state the state
   */
  public void setState(State state) {
    this.state = state;
  }

  /**
   * Gets country.
   *
   * @return the country
   */
  public Country getCountry() {
    return (state != null ? state.getCountry() : null);
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets language.
   *
   * @return the language
   */
  public Locale getLanguage() {
    if (language != null) {
      return language;
    }
    if (state != null) {
      return state.getDefaultLanguageLocale();
    }
    Country country = getCountry();
    return (country != null ? country.getDefaultLanguageLocale() : null);
  }

  /**
   * Sets language.
   *
   * @param language the language
   */
  public void setLanguage(Locale language) {
    this.language = language;
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
   * Sets population.
   *
   * @param population the population
   */
  public void setPopulation(int population) {
    this.population = population;
  }

  /**
   * Generate mobile number phone number.
   *
   * @return the phone number
   */
  public PhoneNumber generateMobileNumber() {
    return getCountry().generateMobileNumber(this);
  }

  /**
   * Generate landline number phone number.
   *
   * @return the phone number
   */
  public PhoneNumber generateLandlineNumber() {
    return new PhoneNumber(getCountry().getPhoneCode(), areaCode,
        localNumberGenerator.generate());
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return name + (StringUtil.isEmpty(nameExtension) ? "" :
        (Character.isLetter(nameExtension.charAt(0)) ? " " : "") +
            nameExtension);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final City that = (City) o;
    if (!this.name.equals(that.name)) {
      return false;
    }
    if (!NullSafeComparator
        .equals(this.nameExtension, that.nameExtension)) {
      return false;
    }
    return NullSafeComparator.equals(this.state, that.state);
  }

  @Override
  public int hashCode() {
    int result;
    result = name.hashCode();
    result = 29 * result + NullSafeComparator.hashCode(nameExtension);
    result = 29 * result + NullSafeComparator.hashCode(state);
    return result;
  }

}
