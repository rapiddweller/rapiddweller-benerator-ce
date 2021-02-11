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

import java.util.List;

import static com.rapiddweller.benerator.util.GeneratorUtil.*;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.common.CollectionUtil;

/**
 * Generates salutations using different salutation words for greeting different persons.
 *
 * @author Volker Bergmann
 */
public class HelloWorldDemo {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    // first create a context
    BeneratorContext context = new DefaultBeneratorContext();

    // create and initialize the salutation generator
    GeneratorFactory generatorFactory = context.getGeneratorFactory();
    List<String> salutations =
        CollectionUtil.toList("Hi", "Hello", "Howdy");
    Generator<String> salutationGenerator = generatorFactory
        .createSampleGenerator(salutations, String.class, false);
    salutationGenerator.init(context);

    // create and initialize the name generator
    List<String> names = CollectionUtil.toList("Alice", "Bob", "Charly");
    Generator<String> nameGenerator = generatorFactory
        .createSampleGenerator(names, String.class, false);
    init(nameGenerator, context);

    // use the generators
    for (int i = 0; i < 5; i++) {
      String salutation = generateNonNull(salutationGenerator);
      String name = generateNonNull(nameGenerator);
      System.out.println(salutation + " " + name);
    }

    // in the end, close the generators
    close(salutationGenerator);
    close(nameGenerator);
  }

}
