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

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Validator;

/**
 * Provides an abstract implementation of a generator that validates its generated values.<br/><br/>
 * Created: 23.09.2006 00:03:04
 * @param <P> the type parameter
 * @author Volker Bergmann
 * @since 0.1
 */
public abstract class ValidatingGenerator<P> extends AbstractGenerator<P> {

  /** The number of invalid consecutive generations that causes a warning */
  public static final int WARNING_THRESHOLD = 100;

  /** The number of invalid consecutive generations that causes an exception */
  public static final int ERROR_THRESHOLD = 1000;

  /** The validator used for validation */
  protected final Validator<P> validator;

  /** Constructor that takes the validator */
  protected ValidatingGenerator(Validator<P> validator) {
    this.validator = validator;
  }

  /** Generator implementation that calls generateImpl() to generate values
   *  and validator.validate() in order to validate them.
   *  Consecutive invalid values are counted. If this count reaches the
   *  WARNING_THRESHOLD value, a warning is logged, if the count reaches the
   *  ERROR_THRESHOLD, an exception is raised. */
  @Override
  public ProductWrapper<P> generate(ProductWrapper<P> wrapper) {
    boolean valid;
    int count = 0;
    P product;
    do {
      wrapper = doGenerate(wrapper);
      if (wrapper == null) {
        return null;
      }
      product = wrapper.unwrap();
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
    return wrapper.wrap(product);
  }

  /** Callback method that does the job of creating values.
   *  This is to be implemented by child classes */
  protected abstract ProductWrapper<P> doGenerate(ProductWrapper<P> wrapper);

}
