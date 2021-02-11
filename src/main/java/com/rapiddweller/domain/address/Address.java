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

import com.rapiddweller.common.Escalator;
import com.rapiddweller.common.LoggerEscalator;
import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.bean.HashCodeBuilder;

/**
 * Represents an address with phone numbers.<br/><br/>
 * Created: 11.06.2006 08:05:00
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class Address {

  private static final Escalator escalator = new LoggerEscalator();

  private String street;
  private String houseNumber;
  private String postalCode;
  private City city;
  private State state;
  private Country country;

  private PhoneNumber privatePhone;
  private PhoneNumber officePhone;
  private PhoneNumber mobilePhone;
  private PhoneNumber fax;

  // TODO v0.8 generate the following attributes
  private String organization;
  private String department;
  private String building;
  private String co;
  private String poBox;

  /**
   * Instantiates a new Address.
   */
  public Address() {
    this(null, null, null, null, null, null, null, null, null, null);
  }

  /**
   * Instantiates a new Address.
   *
   * @param street       the street
   * @param houseNumber  the house number
   * @param postalCode   the postal code
   * @param city         the city
   * @param state        the state
   * @param country      the country
   * @param privatePhone the private phone
   * @param officePhone  the office phone
   * @param mobilePhone  the mobile phone
   * @param fax          the fax
   */
  public Address(String street, String houseNumber, String postalCode,
                 City city, State state, Country country,
                 PhoneNumber privatePhone, PhoneNumber officePhone,
                 PhoneNumber mobilePhone, PhoneNumber fax) {
    this.street = street;
    this.houseNumber = houseNumber;
    this.postalCode = postalCode;
    this.city = city;
    this.state = state;
    this.country = country;
    this.privatePhone = privatePhone;
    this.officePhone = officePhone;
    this.mobilePhone = mobilePhone;
    this.fax = fax;
  }

  /**
   * Gets organization.
   *
   * @return the organization
   */
  public String getOrganization() {
    return organization;
  }

  /**
   * Sets organization.
   *
   * @param organization the organization
   */
  public void setOrganization(String organization) {
    this.organization = organization;
  }

  /**
   * Gets street.
   *
   * @return the street
   */
  public String getStreet() {
    return street;
  }

  /**
   * Sets street.
   *
   * @param street the street
   */
  public void setStreet(String street) {
    this.street = street;
  }

  /**
   * Gets house number.
   *
   * @return the house number
   */
  public String getHouseNumber() {
    return houseNumber;
  }

  /**
   * Sets house number.
   *
   * @param houseNumber the house number
   */
  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  /**
   * Gets zip code.
   *
   * @return the zip code
   */
  @Deprecated
  public String getZipCode() {
    escalator.escalate(
        "Property 'zipCode' is deprecated and replaced with 'postalCode'",
        getClass(), "zipCode");
    return getPostalCode();
  }

  /**
   * Sets zip code.
   *
   * @param zipCode the zip code
   */
  @Deprecated
  public void setZipCode(String zipCode) {
    escalator.escalate(
        "Property 'zipCode' is deprecated and replaced with 'postalCode'",
        getClass(), "zipCode");
    setPostalCode(zipCode);
  }

  /**
   * Gets postal code.
   *
   * @return the postal code
   */
  public String getPostalCode() {
    return postalCode;
  }

  /**
   * Sets postal code.
   *
   * @param postalCode the postal code
   */
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * Gets city.
   *
   * @return the city
   */
  public City getCity() {
    return city;
  }

  /**
   * Sets city.
   *
   * @param city the city
   */
  public void setCity(City city) {
    this.city = city;
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
    return country;
  }

  /**
   * Sets country.
   *
   * @param country the country
   */
  public void setCountry(Country country) {
    this.country = country;
  }

  /**
   * Gets private phone.
   *
   * @return the private phone
   */
  public PhoneNumber getPrivatePhone() {
    return privatePhone;
  }

  /**
   * Sets private phone.
   *
   * @param privatePhone the private phone
   */
  public void setPrivatePhone(PhoneNumber privatePhone) {
    this.privatePhone = privatePhone;
  }

  /**
   * Gets office phone.
   *
   * @return the office phone
   */
  public PhoneNumber getOfficePhone() {
    return officePhone;
  }

  /**
   * Sets office phone.
   *
   * @param officePhone the office phone
   */
  public void setOfficePhone(PhoneNumber officePhone) {
    this.officePhone = officePhone;
  }

  /**
   * Gets mobile phone.
   *
   * @return the mobile phone
   */
  public PhoneNumber getMobilePhone() {
    return mobilePhone;
  }

  /**
   * Sets mobile phone.
   *
   * @param mobilePhone the mobile phone
   */
  public void setMobilePhone(PhoneNumber mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  /**
   * Gets fax.
   *
   * @return the fax
   */
  public PhoneNumber getFax() {
    return fax;
  }

  /**
   * Sets fax.
   *
   * @param fax the fax
   */
  public void setFax(PhoneNumber fax) {
    this.fax = fax;
  }

  @Override
  public String toString() {
    AddressFormat format = AddressFormat.getInstance(country.getIsoCode());
    if (format == null) {
      format = AddressFormat.DE;
    }
    return format.format(this);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.hashCode(
        postalCode, street, houseNumber, poBox, city,
        organization, building, co, department,
        mobilePhone, officePhone, privatePhone);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Address that = (Address) obj;
    if (!NullSafeComparator.equals(this.postalCode, that.postalCode)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.street, that.street)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.houseNumber, that.houseNumber)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.poBox, that.poBox)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.city, that.city)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.organization, that.organization)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.building, that.building)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.co, that.co)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.department, that.department)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.fax, that.fax)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.mobilePhone, that.mobilePhone)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.officePhone, that.officePhone)) {
      return false;
    }
    return NullSafeComparator.equals(this.privatePhone, that.privatePhone);
  }


}
