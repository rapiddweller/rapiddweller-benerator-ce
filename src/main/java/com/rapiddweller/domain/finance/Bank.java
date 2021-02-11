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
 * Represents a bank.<br/><br/>
 * Created at 24.06.2008 08:31:56
 *
 * @author Volker Bergmann
 * @since 0.5.4
 */
public class Bank {

  /**
   * The Bank Identification Number
   */
  private final String bin;

  /**
   * The name of the bank
   */
  private final String name;

  /**
   * The national bank code
   */
  private final String bankCode;

  /**
   * The international Bank Identifier Code
   */
  private final String bic;

  // Constructor -----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Bank.
   *
   * @param name     the name
   * @param bankCode the bank code
   * @param bic      the bic
   * @param bin      the bin
   */
  public Bank(String name, String bankCode, String bic, String bin) {
    this.name = name;
    this.bankCode = bankCode;
    this.bic = bic;
    this.bin = bin;
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets name.
   *
   * @return the name of the bank
   */
  public String getName() {
    return name;
  }

  /**
   * Gets bank code.
   *
   * @return the national bank code
   */
  public String getBankCode() {
    return bankCode;
  }

  /**
   * Gets bic.
   *
   * @return the international Bank Identifier Code
   */
  public String getBic() {
    return bic;
  }

  /**
   * Gets bin.
   *
   * @return the Bank Identification Number
   */
  public String getBin() {
    return bin;
  }

  @Override
  public String toString() {
    return bankCode + ' ' + name + "(BIC:" + bic + ')';
  }

}
