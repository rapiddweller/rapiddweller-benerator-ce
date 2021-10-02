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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.factory.StochasticGeneratorFactory;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;
import com.rapiddweller.model.data.Uniqueness;

import java.util.Locale;

/**
 * Generates Strings that comply to a regular expression.<br/><br/>
 * Created: 18.07.2006 19:32:52
 * @author Volker Bergmann
 * @since 0.1
 */
public class RegexStringGenerator extends NonNullGeneratorProxy<String> {

  /** Optional String representation of a regular expression */
  private String pattern;

  private boolean unique;

  private boolean ordered;

  /** The locale from which to choose letters */
  private Locale locale;

  private int minLength;

  private final int maxLength;

  // constructors ----------------------------------------------------------------------------------------------------

  /** Initializes the generator to an empty regular expression, a maxQuantity of 30 and the fallback locale. */
  public RegexStringGenerator() {
    this(30);
  }

  /** Initializes the generator to an empty regular expression and the fallback locale. */
  public RegexStringGenerator(int maxLength) {
    this(null, maxLength);
  }

  /** Initializes the generator to a maxQuantity of 30 and the fallback locale. */
  public RegexStringGenerator(String pattern) {
    this(pattern, 30);
  }

  /** Initializes the generator to the fallback locale. */
  public RegexStringGenerator(String pattern, int maxLength) {
    this(pattern, maxLength, false);
  }

  /** Initializes the generator with the String representation of a regular expression. */
  public RegexStringGenerator(String pattern, Integer maxLength, boolean unique) {
    super(String.class);
    this.pattern = pattern;
    this.maxLength = maxLength;
    this.unique = unique;
    this.ordered = false;
  }

  // config properties -----------------------------------------------------------------------------------------------

  /** Sets the String representation of the regular expression. */
  public String getPattern() {
    return pattern;
  }

  /** Returns the String representation of the regular expression. */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  public boolean isOrdered() {
    return ordered;
  }

  public void setOrdered(boolean ordered) {
    this.ordered = ordered;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public int getMinLength() {
    return minLength;
  }

  public void setMinLength(int minLength) {
    this.minLength = minLength;
  }

  public int getMaxLength() {
    return maxLength;
  }

  // Generator interface ---------------------------------------------------------------------------------------------


  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return !isUnique();
  }

  /** Ensures consistency of the generator's state. */
  @Override
  public void init(GeneratorContext context) {
    Generator<String> tmp = getGeneratorFactory(context).createRegexStringGenerator(
        pattern, locale, minLength, maxLength, Uniqueness.instance(unique, ordered));
    try {
      setSource(tmp);
      super.init(context);
    } catch (Exception e) {
      throw new InvalidGeneratorSetupException("Illegal regular expression: ", e);
    }
  }

  protected GeneratorFactory getGeneratorFactory(GeneratorContext context) {
    return (context != null ? context.getGeneratorFactory() : new StochasticGeneratorFactory());
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + (unique ? "unique '" : "'") + pattern + "']";
  }

}
