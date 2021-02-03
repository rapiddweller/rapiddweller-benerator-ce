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

import java.util.Date;
import java.util.Locale;

/**
 * Represents a natural person.<br/><br/>
 * Created: 09.06.2006 21:51:25
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class Person {

  private String givenName;
  private String secondGivenName;
  private String familyName;
  private Gender gender;
  private String salutation;
  private String academicTitle;
  private String nobilityTitle;
  private Date birthDate;
  private String email;
  private final Locale locale;

  /**
   * Instantiates a new Person.
   *
   * @param locale the locale
   */
  public Person(Locale locale) {
    this.locale = locale;
  }

  /**
   * Gets salutation.
   *
   * @return the salutation
   */
  public String getSalutation() {
    return salutation;
  }

  /**
   * Sets salutation.
   *
   * @param salutation the salutation
   */
  public void setSalutation(String salutation) {
    this.salutation = salutation;
  }

  /**
   * Gets title.
   *
   * @return the title
   */
  public String getTitle() {
    return getAcademicTitle();
  }

  /**
   * Sets title.
   *
   * @param title the title
   */
  public void setTitle(String title) {
    this.setAcademicTitle(title);
  }

  /**
   * Gets academic title.
   *
   * @return the academic title
   */
  public String getAcademicTitle() {
    return academicTitle;
  }

  /**
   * Sets academic title.
   *
   * @param academicTitle the academic title
   */
  public void setAcademicTitle(String academicTitle) {
    this.academicTitle = academicTitle;
  }

  /**
   * Gets nobility title.
   *
   * @return the nobility title
   */
  public String getNobilityTitle() {
    return nobilityTitle;
  }

  /**
   * Sets nobility title.
   *
   * @param nobilityTitle the nobility title
   */
  public void setNobilityTitle(String nobilityTitle) {
    this.nobilityTitle = nobilityTitle;
  }

  /**
   * Gets gender.
   *
   * @return the gender
   */
  public Gender getGender() {
    return gender;
  }

  /**
   * Sets gender.
   *
   * @param gender the gender
   */
  public void setGender(Gender gender) {
    this.gender = gender;
  }

  /**
   * Gets given name.
   *
   * @return the given name
   */
  public String getGivenName() {
    return givenName;
  }

  /**
   * Sets given name.
   *
   * @param givenName the given name
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  /**
   * Gets second given name.
   *
   * @return the second given name
   */
  public String getSecondGivenName() {
    return secondGivenName;
  }

  /**
   * Sets second given name.
   *
   * @param secondGivenName the second given name
   */
  public void setSecondGivenName(String secondGivenName) {
    this.secondGivenName = secondGivenName;
  }

  /**
   * Gets family name.
   *
   * @return the family name
   */
  public String getFamilyName() {
    return familyName;
  }

  /**
   * Sets family name.
   *
   * @param familyName the family name
   */
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  /**
   * Gets birth date.
   *
   * @return the birth date
   */
  public Date getBirthDate() {
    return birthDate;
  }

  /**
   * Sets birth date.
   *
   * @param birthDate the birth date
   */
  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  /**
   * Gets email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets email.
   *
   * @param email the email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public synchronized String toString() {
    return PersonFormatter.getInstance(locale).format(this);
  }

}
