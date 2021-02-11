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
import java.util.regex.Pattern;

/**
 * Verifies Brazilian CPF numbers.
 * CPF stands for 'Cadastro de Pessoa Fisica'
 * and is a tax payer number assigned to an
 * individual person (Pessoa Fisica).
 * <br/><br/>
 * Created: 17.10.2009 08:24:12
 *
 * @author Volker Bergmann
 * @see "http://en.wikipedia.org/wiki/Cadastro_de_Pessoas_F%C3%ADsicas"
 * @since 0.6.0
 */
public class CPFValidator extends AbstractConstraintValidator<CPF, String> {

  private static final Pattern pattern =
      Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");

  private boolean acceptingFormattedNumbers = true;

  /**
   * Instantiates a new Cpf validator.
   */
  public CPFValidator() {
    this(false);
  }

  /**
   * Instantiates a new Cpf validator.
   *
   * @param acceptingFormattedNumbers the accepting formatted numbers
   */
  public CPFValidator(boolean acceptingFormattedNumbers) {
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
  public void initialize(CPF params) {
    super.initialize(params);
    acceptingFormattedNumbers = params.formatted();
  }

  @Override
  public boolean isValid(String number, ConstraintValidatorContext context) {
    // do simple checks first
    if (number == null) {
      return false;
    }

    if (number.length() == 14) {
      if (acceptingFormattedNumbers &&
          pattern.matcher(number).matches()) {
        number = number.substring(0, 3) + number.substring(4, 7) +
            number.substring(8, 11) + number.substring(12, 14);
      } else {
        return false;
      }
    }

    if (number.length() != 11) {
      return false;
    }

    // compute 1st verification digit
    int v1 =
        MathUtil.weightedSumOfDigits(number, 0, 10, 9, 8, 7, 6, 5, 4, 3,
            2);
    v1 = 11 - v1 % 11;
    if (v1 >= 10) {
      v1 = 0;
    }

    // Check 1st verification digit
    if (v1 != number.charAt(9) - '0') {
      return false;
    }

    // compute 2nd verification digit
    int v2 = MathUtil.weightedSumOfDigits(number, 0, 11, 10, 9, 8, 7, 6, 5,
        4, 3);
    v2 += 2 * v1;
    v2 = 11 - v2 % 11;
    if (v2 >= 10) {
      v2 = 0;
    }

    // Check 2nd verification digit
    return (v2 == number.charAt(10) - '0');
  }

}
