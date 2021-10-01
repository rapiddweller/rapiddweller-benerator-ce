/*
 * (c) Copyright 2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.common.Validator;

/**
 * Generator proxy that uses another generator for creating values and filters out invalid ones.<br/><br/>
 * Created: 01.10.2021 09:25:24
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ValidatingNonNullGeneratorProxy<E> extends ValidatingGeneratorProxy<E> implements NonNullGenerator<E> {

  /** Constructor with the source generator and the validator to use */
  public ValidatingNonNullGeneratorProxy(NonNullGenerator<E> source, Validator<E> validator) {
    super(source, validator);
  }

  // Generator & ValidatingGenerator implementation ------------------------------------------------------------------

  /** Generator implementation that calls generateImpl() to generate values
   *  and validator.validate() in order to validate them.
   *  Consecutive invalid values are counted. If this count reaches the
   *  WARNING_THRESHOLD value, a warning is logged, if the count reaches the
   *  ERROR_THRESHOLD, an exception is raised. */
  @Override
  public E generate() {
    boolean valid;
    int count = 0;
    E product;
    do {
      product = ((NonNullGenerator<E>) source).generate();
      if (product == null) {
        return null;
      }
      valid = validator.valid(product);
      count++;
      if (count >= ERROR_THRESHOLD) {
        throw new IllegalGeneratorStateException("Aborting generation, because of " + ERROR_THRESHOLD
            + " consecutive invalid generations. Validator is: " + validator +
            ". Last attempt was: " + product);
      }
    } while (!valid);
    if (count >= WARNING_THRESHOLD) {
      logger.warn("Inefficient generation: needed {} tries to generate a valid value. ", count);
    }
    return product;
  }

}
