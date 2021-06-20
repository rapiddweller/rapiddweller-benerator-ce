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

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.sample.WeightedCSVSampleGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Encodings;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generates Brazilian CNPJ numbers.
 * CNPJ stands for <i>Cadastro Nacional da Pessoa Jurídica</i>
 * and is a tax payer number assigned to a
 * legal person (Pessoa Jurídica).
 *
 * @author Eric Chaves
 * @see "http://en.wikipedia.org/wiki/Cadastro_de_Pessoas_F%C3%ADsicas"
 */
public class CNPJGenerator extends WeightedCSVSampleGenerator<String>
    implements NonNullGenerator<String> {

  private static final String LOCAL =
      "/com/rapiddweller/domain/br/cnpj_sufix.csv";

  /**
   * flag indicating should return CPF in numeric or formatted form. Defaults to true
   */
  private final boolean formatted;

  private final Random random;

  /**
   * Instantiates a new Cnpj generator.
   */
  public CNPJGenerator() {
    this(false);
  }

  /**
   * Instantiates a new Cnpj generator.
   *
   * @param formatted the formatted
   */
  public CNPJGenerator(boolean formatted) {
    super(LOCAL, Encodings.UTF_8, ',');
    this.random = new Random();
    this.formatted = formatted;
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  private static void addDigits(ArrayList<Integer> digits) {
    int sum = 0;
    sum = (5 * digits.get(0)) + (4 * digits.get(1)) + (3 * digits.get(2)) +
        (2 * digits.get(3))
        + (9 * digits.get(4)) + (8 * digits.get(5)) +
        (7 * digits.get(6)) + (6 * digits.get(7))
        + (5 * digits.get(8)) + (4 * digits.get(9)) +
        (3 * digits.get(10)) + (2 * digits.get(11));
    digits.add((sum % 11 < 2) ? 0 : 11 - (sum % 11));

    sum = (6 * digits.get(0)) + (5 * digits.get(1)) + (4 * digits.get(2)) +
        (3 * digits.get(3))
        + (2 * digits.get(4)) + (9 * digits.get(5)) +
        (8 * digits.get(6)) + (7 * digits.get(7))
        + (6 * digits.get(8)) + (5 * digits.get(9)) +
        (4 * digits.get(10)) + (3 * digits.get(11))
        + (2 * digits.get(12));
    digits.add((sum % 11 < 2) ? 0 : 11 - (sum % 11));
  }

  @Override
  public String generate() {
    return generate(getResultWrapper()).unwrap();
  }

  // private helpers -------------------------------------------------------------------------------------------------

  @Override
  public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
    String suffix = super.generate(wrapper).unwrap();
    if (suffix == null) {
      suffix = "0000";
    }
    return wrapper.wrap(generateCNPJ(suffix));
  }

  private String generateCNPJ(String sufix) {

    StringBuilder buf = new StringBuilder();
    ArrayList<Integer> digits = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      digits.add(random.nextInt(9));
    }
    for (int i = 0; i < 4; i++) {
      digits.add(Integer.parseInt(sufix.substring(i, i + 1)));
    }
    addDigits(digits);

    for (Integer digit : digits) {
      buf.append(digit);
    }
    if (this.formatted) {
      buf.insert(2, '.');
      buf.insert(6, '.');
      buf.insert(10, '/');
      buf.insert(15, '-');
    }
    return buf.toString();
  }

}
