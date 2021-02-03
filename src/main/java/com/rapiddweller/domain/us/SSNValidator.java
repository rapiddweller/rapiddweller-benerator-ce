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

package com.rapiddweller.domain.us;

import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates US Social Security Numbers.<br/>
 * <br/>
 * Created at 17.11.2008 07:08:34
 *
 * @author Volker Bergmann
 * @see "http://en.wikipedia.org/wiki/Social_security_number"
 * @see "http://www.socialsecurity.gov/history/ssn/geocard.html"
 * @see "http://www.socialsecurity.gov/employer/stateweb.htm"
 * @see "http://www.socialsecurity.gov/employer/ssnvhighgroup.htm"
 * @since 0.5.6
 */
public class SSNValidator
    implements ConstraintValidator<SSN, String>, Validator<String> {

  private int maxAreaCode;

  /**
   * Instantiates a new Ssn validator.
   */
  public SSNValidator() {
    this(SSN.DEFAULT_MAX_AREA_CODE);
  }

  /**
   * Instantiates a new Ssn validator.
   *
   * @param maxAreaCode the max area code
   */
  public SSNValidator(int maxAreaCode) {
    this.maxAreaCode = maxAreaCode;
  }

  @Override
  public void initialize(SSN parameters) {
    this.maxAreaCode = parameters.maxAreaCode();
  }

  @Override
  public boolean isValid(String ssn, ConstraintValidatorContext context) {
    return valid(ssn);
  }

  @Override
  public boolean valid(String ssn) {
    if (ssn == null || ssn.length() != 11) {
      return false;
    }
    String[] tokens = StringUtil.tokenize(ssn, '-');
    if (tokens.length != 3) {
      return false;
    }
    try {
      // validate area number
      if (tokens[0].length() != 3) {
        return false;
      }
      int areaNumber = Integer.parseInt(tokens[0]);
      // Currently, a valid SSN cannot have an area number between 734 and 749, or above 772,
      // the highest area number which the Social Security Administration has allocated
      if (areaNumber < 1 || areaNumber == 666 ||
          areaNumber > maxAreaCode ||
          (areaNumber > 733 && areaNumber < 750)) {
        return false;
      }

      // validate group number
      if (tokens[1].length() != 2) {
        return false;
      }
      int groupNumber = Integer.parseInt(tokens[1]);
      if (groupNumber < 1) {
        return false;
      }

      // validate serial number
      if (tokens[2].length() != 4) {
        return false;
      }
      int serialNumber = Integer.parseInt(tokens[2]);
      if (serialNumber < 1) {
        return false;
      }
      // Numbers from 987-65-4320 to 987-65-4329 are reserved for use in advertisements
      if (areaNumber == 987 && areaNumber == 65 &&
          (serialNumber >= 4320 && serialNumber <= 4329)) {
        return false;
      }
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

}
