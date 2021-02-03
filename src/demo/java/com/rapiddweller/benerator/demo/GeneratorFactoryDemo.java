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

package com.rapiddweller.benerator.demo;

import java.util.Arrays;

import static com.rapiddweller.benerator.util.GeneratorUtil.*;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.factory.EquivalenceGeneratorFactory;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.factory.SourceFactory;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.model.data.Uniqueness;

/**
 * Demonstrates usages of the {@link GeneratorFactory}.<br/><br/>
 * Created: 08.03.2011 17:39:20
 *
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class GeneratorFactoryDemo {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    GeneratorFactory factory = new EquivalenceGeneratorFactory();
    generateByRegex(factory);
    generateByWeightedLiteralList(factory);
    iterateCsv(factory);
  }

  private static void iterateCsv(GeneratorFactory generatorFactory) {
    Generator<String[]> generator = SourceFactory.createCSVLineGenerator(
        "com/rapiddweller/benerator/products.csv", ';', Encodings.UTF_8,
        true);
    init(generator);
    String[] row;
    while ((row = generateNonNull(generator)) !=
        null) // null signals that the generator is used up
    {
      System.out.println(Arrays.toString(row));
    }
    close(generator);
  }

  private static void generateByWeightedLiteralList(
      GeneratorFactory generatorFactory) {
    String valueSpec = "'Alpha'^4,'Bravo'^1";
    Generator<String> generator =
        generatorFactory.createFromWeightedLiteralList(
            valueSpec, String.class, null, false);
    init(generator);
    for (int i = 0; i < 10; i++) {
      System.out.println(generateNonNull(generator));
    }
    close(generator);
  }

  private static void generateByRegex(GeneratorFactory generatorFactory) {
    // generating German phone numbers
    String pattern = "\\+49\\-[1-9]{2,5}\\-[1-9][0-9]{3,9}";
    Generator<String> generator = generatorFactory
        .createRegexStringGenerator(pattern, 8, 20, Uniqueness.NONE);
    init(generator);
    for (int i = 0; i < 10; i++) {
      System.out.println(generateNonNull(generator));
    }
    close(generator);
  }

}
