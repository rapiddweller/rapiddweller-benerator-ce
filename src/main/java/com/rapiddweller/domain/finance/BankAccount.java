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

package com.rapiddweller.domain.finance;

/**
 * Represents a bank account.<br/><br/>
 * Created at 23.06.2008 11:08:18
 *
 * @author Volker Bergmann
 * @since 0.5.4
 */
public class BankAccount {

  private final Bank bank;
  private final String accountNumber;
  private final String iban;

  /**
   * Instantiates a new Bank account.
   *
   * @param bank          the bank
   * @param accountNumber the account number
   * @param iban          the iban
   */
  public BankAccount(Bank bank, String accountNumber, String iban) {
    this.bank = bank;
    this.accountNumber = accountNumber;
    this.iban = iban;
  }

  /**
   * Gets bank code.
   *
   * @return the bank code
   */
  public String getBankCode() {
    return bank.getBankCode();
  }

  /**
   * Gets bank name.
   *
   * @return the bank name
   */
  public String getBankName() {
    return bank.getName();
  }

  /**
   * Gets account number.
   *
   * @return the account number
   */
  public String getAccountNumber() {
    return accountNumber;
  }

  /**
   * Gets bic.
   *
   * @return the bic
   */
  public String getBic() {
    return bank.getBic();
  }

  /**
   * Gets iban.
   *
   * @return the iban
   */
  public String getIban() {
    return iban;
  }

  @Override
  public String toString() {
    return "Account #" + accountNumber + " at " + bank + ", IBAN=" + iban;
  }
}
