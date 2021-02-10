package com.my;

import com.rapiddweller.common.Validator;

/**
 * The type Credit card validator.
 */
public class CreditCardValidator implements Validator<String> {

  /**
   * valid
   *
   * @return true or false (boolean)
   */
  public boolean valid(String number) {
    if (number == null || number.length() < 13 || number.length() > 16) {
      return false;
    }
    int sum = 0;
    byte[] digits = number.getBytes();
    int parity = digits.length % 2;
    for (int i = digits.length - 1; i >= 0; i--) {
      int digit = digits[i] - '0';
      if (i % 2 == parity) {
        digit *= 2;
      }
      sum += digit > 9 ? digit - 9 : digit;
    }
    return sum % 10 == 0;
  }
}
