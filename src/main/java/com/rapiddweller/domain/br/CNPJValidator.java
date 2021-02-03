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

package com.rapiddweller.domain.br;

import com.rapiddweller.common.MathUtil;
import com.rapiddweller.common.validator.bean.AbstractConstraintValidator;

import javax.validation.ConstraintValidatorContext;

/**
 * Validates Brazilian CNPJ numbers.
 * CNPJ stands for <i>Cadastro Nacional da Pessoa Jurídica</i>
 * and is a tax payer number assigned to a
 * legal person (Pessoa Jurídica).
 * Created: 17.10.2009 08:24:23
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class CNPJValidator
    extends AbstractConstraintValidator<CNPJ, CharSequence> {

  private boolean acceptingFormattedNumbers = true;

  /**
   * Instantiates a new Cnpj validator.
   */
  public CNPJValidator() {
    this(false);
  }

  /**
   * Instantiates a new Cnpj validator.
   *
   * @param acceptingFormattedNumbers the accepting formatted numbers
   */
  public CNPJValidator(boolean acceptingFormattedNumbers) {
    this.acceptingFormattedNumbers = acceptingFormattedNumbers;
  }

  /**
   * Is accepting formatted numbers boolean.
   *
   * @return the boolean
   */
  public boolean isAcceptingFormattedNumbers() {
    return acceptingFormattedNumbers;
  }

  /**
   * Sets accepting formatted numbers.
   *
   * @param acceptingFormattedNumbers the accepting formatted numbers
   */
  public void setAcceptingFormattedNumbers(
      boolean acceptingFormattedNumbers) {
    this.acceptingFormattedNumbers = acceptingFormattedNumbers;
  }

  @Override
  public void initialize(CNPJ params) {
    super.initialize(params);
    acceptingFormattedNumbers = params.formatted();
  }

  @Override
  public boolean isValid(CharSequence number,
                         ConstraintValidatorContext context) {
    // do simple checks first
    if (number == null) {
      return false;
    }
    int length = number.length();
    boolean formattedNumber = (acceptingFormattedNumbers && length == 18);
    if (length != 14 && !formattedNumber) {
      return false;
    }
    if (formattedNumber) {
      // check grouping characters
      if (number.charAt(2) != '.' || number.charAt(6) != '.' ||
          number.charAt(10) != '/' || number.charAt(15) != '-') {
        return false;
      }
      // remove grouping
      number = "" + number.subSequence(0, 2) + number.subSequence(3, 6) +
          number.subSequence(7, 10) +
          number.subSequence(11, 15) + number.subSequence(16, 18);
    }
    // compute 1st verification digit
    int v1 = MathUtil.weightedSumOfDigits(number, 0, 5, 4, 3, 2, 9, 8, 7, 6,
        5, 4, 3, 2);
    v1 = 11 - v1 % 11;
    if (v1 >= 10) {
      v1 = 0;
    }

    // Check 1st verification digit
    if (v1 != number.charAt(12) - '0') {
      return false;
    }

    // compute 2nd verification digit
    int v2 = MathUtil.weightedSumOfDigits(number, 0, 6, 5, 4, 3, 2, 9, 8, 7,
        6, 5, 4, 3);
    v2 += 2 * v1;
    v2 = 11 - v2 % 11;
    if (v2 >= 10) {
      v2 = 0;
    }

    // Check 2nd verification digit
    return (v2 == number.charAt(13) - '0');
  }

}
