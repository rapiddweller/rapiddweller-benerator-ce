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
 * @author Volker Bergmann
 * @since 0.1
 */
public class Address {

  private static final Escalator escalator = new LoggerEscalator();

  private String street;
  private String houseNumber;
  private String street2;
  private String postalCode;
  private City city;
  private State state;
  private Country country;

  private PhoneNumber privatePhone;
  private PhoneNumber officePhone;
  private PhoneNumber mobilePhone;
  private PhoneNumber fax;

  private String organization;
  private String department;

  public Address() {
    this(null, null, null, null, null, null, null, null, null, null);
  }

  public Address(String street, String houseNumber, String postalCode,
                 City city, State state, Country country,
                 PhoneNumber privatePhone, PhoneNumber officePhone,
                 PhoneNumber mobilePhone, PhoneNumber fax) {
    this.street = street;
    this.houseNumber = houseNumber;
    this.street2 = null;
    this.postalCode = postalCode;
    this.city = city;
    this.state = state;
    this.country = country;
    this.privatePhone = privatePhone;
    this.officePhone = officePhone;
    this.mobilePhone = mobilePhone;
    this.fax = fax;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  public String getStreet2() {
    return street2;
  }

  public void setStreet2(String street2) {
    this.street2 = street2;
  }

  /** @deprecated Property 'zipCode' is deprecated and replaced with 'postalCode' */
  @Deprecated
  public String getZipCode() {
    escalator.escalate(
        "Property 'zipCode' is deprecated and replaced with 'postalCode'",
        getClass(), "zipCode");
    return getPostalCode();
  }

  /** @deprecated Property 'zipCode' is deprecated and replaced with 'postalCode' */
  @Deprecated
  public void setZipCode(String zipCode) {
    escalator.escalate(
        "Property 'zipCode' is deprecated and replaced with 'postalCode'",
        getClass(), "zipCode");
    setPostalCode(zipCode);
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public City getCity() {
    return city;
  }

  public void setCity(City city) {
    this.city = city;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public PhoneNumber getPrivatePhone() {
    return privatePhone;
  }

  public void setPrivatePhone(PhoneNumber privatePhone) {
    this.privatePhone = privatePhone;
  }

  public PhoneNumber getOfficePhone() {
    return officePhone;
  }

  public void setOfficePhone(PhoneNumber officePhone) {
    this.officePhone = officePhone;
  }

  public PhoneNumber getMobilePhone() {
    return mobilePhone;
  }

  public void setMobilePhone(PhoneNumber mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  public PhoneNumber getFax() {
    return fax;
  }

  public void setFax(PhoneNumber fax) {
    this.fax = fax;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  // Override methods from java.lang.Object --------------------------------------------------------------------------

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
        postalCode, street, houseNumber, city, organization, department, mobilePhone, officePhone, privatePhone);
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
    if (!NullSafeComparator.equals(this.city, that.city)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.organization, that.organization)) {
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
