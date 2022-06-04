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

import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.StringUtil;

import java.util.Objects;

/**
 * Represents a phone number.<br/><br/>
 * Created: 13.06.2006 07:19:26
 * @author Volker Bergmann
 * @since 0.1
 */
public class PhoneNumber {

  private String countryCode;
  private String areaCode;
  private String localNumber;

  private boolean mobile;

  // constructors ----------------------------------------------------------------------------------------------------

  public PhoneNumber() {
    this("", "", "");
  }

  public PhoneNumber(String phoneNumber) {
    this(coc(phoneNumber), cic(phoneNumber), loc(phoneNumber));
  }

  public PhoneNumber(String countryCode, String cityCode,
                     String localNumber) {
    this(countryCode, cityCode, localNumber, false);
  }

  public PhoneNumber(String countryCode, String cityCode, String localNumber,
                     boolean mobile) {
    this.countryCode = countryCode;
    this.areaCode = cityCode;
    this.localNumber = localNumber;
    this.mobile = mobile;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getAreaCode() {
    return areaCode;
  }

  public void setAreaCode(String cityCode) {
    this.areaCode = cityCode;
  }

  public String getLocalNumber() {
    return localNumber;
  }

  public void setLocalNumber(String localNumber) {
    this.localNumber = localNumber;
  }

  public boolean isMobile() {
    return mobile;
  }

  public void setMobile(boolean mobile) {
    this.mobile = mobile;
  }

  private static String coc(String phoneNumber) {
    return StringUtil.split(phoneNumber, ' ')[0];
  }

  private static String cic(String phoneNumber) {
    return StringUtil.split(phoneNumber, ' ')[1];
  }

  private static String loc(String phoneNumber) {
    StringBuilder result = new StringBuilder();
    String[] tokens = StringUtil.split(phoneNumber, ' ');
    for (int i = 2; i < tokens.length; i++) {
      if (i > 2) {
        result.append(' ');
      }
      result.append(tokens[i]);
    }
    return result.toString();
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return "+" + countryCode + '-' + areaCode + '-' + localNumber;
  }

  @Override
  public int hashCode() {
    return Objects.hash(countryCode, areaCode, localNumber);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final PhoneNumber that = (PhoneNumber) obj;
    if (!this.areaCode.equals(that.areaCode)) {
      return false;
    }
    if (!NullSafeComparator.equals(this.countryCode, that.countryCode)) {
      return false;
    }
    return (this.localNumber.equals(that.localNumber));
  }

}
