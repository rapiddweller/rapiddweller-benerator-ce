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

import static com.rapiddweller.benerator.util.GeneratorUtil.*;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.model.data.Uniqueness;

/**
 * Demonstrates the use of the regular expression generator
 * by generating phone numbers and email addresses.<br/>
 * <br/>
 * Created: 07.09.2006 21:01:53
 *
 * @author Volker Bergmann
 */
public class RegexDemo {

  private static final String PHONE_PATTERN =
      "\\+[1-9][0-9]{1,2}/[1-9][0-9]{0,4}/[1-9][0-9]{4,8}";
  private static final String EMAIL_PATTERN =
      "[a-z][a-z0-9\\.]{3,12}[a-z0-9]@[a-z0-9]{3,12}\\.com";

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    BeneratorContext context = new DefaultBeneratorContext();
    GeneratorFactory generatorFactory = context.getGeneratorFactory();

    Generator<String> phoneGenerator = generatorFactory
        .createRegexStringGenerator(PHONE_PATTERN, 1, 16,
            Uniqueness.NONE);
    phoneGenerator.init(context);
    for (int i = 0; i < 5; i++) {
      System.out.println(generateNonNull(phoneGenerator));
    }
    close(phoneGenerator);

    Generator<String> emailGenerator = generatorFactory
        .createRegexStringGenerator(EMAIL_PATTERN, 1, 100,
            Uniqueness.NONE);
    emailGenerator.init(context);
    for (int i = 0; i < 5; i++) {
      System.out.println(generateNonNull(emailGenerator));
    }
    close(emailGenerator);
  }

}
