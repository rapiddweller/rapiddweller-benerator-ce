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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.wrapper.LengthGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.format.regex.RegexParser;

import java.util.Locale;
import java.util.Set;

/**
 * Generates {@link String}s composed of numerical digits.<br/><br/>
 * Created: 16.10.2009 07:31:16
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class RandomVarLengthStringGenerator extends LengthGenerator<Character, String>
    implements VarLengthStringGenerator {

  private String pattern;
  private Locale locale;
  private Set<Character> chars;

  /**
   * Instantiates a new Random var length string generator.
   */
  public RandomVarLengthStringGenerator() {
    this("[0-9]", 8);
  }

  /**
   * Instantiates a new Random var length string generator.
   *
   * @param pattern the pattern
   * @param length  the length
   */
  public RandomVarLengthStringGenerator(String pattern, int length) {
    this(pattern, length, length, 1);
  }

  /**
   * Instantiates a new Random var length string generator.
   *
   * @param pattern           the pattern
   * @param minLength         the min length
   * @param maxLength         the max length
   * @param lengthGranularity the length granularity
   */
  public RandomVarLengthStringGenerator(String pattern, int minLength, int maxLength, int lengthGranularity) {
    this(pattern, minLength, maxLength, lengthGranularity, null);
  }

  /**
   * Instantiates a new Random var length string generator.
   *
   * @param pattern            the pattern
   * @param minLength          the min length
   * @param maxLength          the max length
   * @param lengthGranularity  the length granularity
   * @param lengthDistribution the length distribution
   */
  public RandomVarLengthStringGenerator(String pattern, int minLength, int maxLength,
                                        int lengthGranularity, Distribution lengthDistribution) {
    super(null, true, minLength, maxLength, lengthGranularity, lengthDistribution);
    this.pattern = pattern;
  }

  /**
   * Instantiates a new Random var length string generator.
   *
   * @param chars              the chars
   * @param minLength          the min length
   * @param maxLength          the max length
   * @param lengthGranularity  the length granularity
   * @param lengthDistribution the length distribution
   */
  public RandomVarLengthStringGenerator(Set<Character> chars, int minLength, int maxLength,
                                        int lengthGranularity, Distribution lengthDistribution) {
    super(null, true, minLength, maxLength, lengthGranularity, lengthDistribution);
    this.chars = chars;
  }


  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets pattern.
   *
   * @return the pattern
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Sets pattern.
   *
   * @param charSet the char set
   */
  public void setPattern(String charSet) {
    this.pattern = charSet;
    this.chars = null;
  }

  /**
   * Gets locale.
   *
   * @return the locale
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Sets locale.
   *
   * @param locale the locale
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * Gets chars.
   *
   * @return the chars
   */
  public Set<Character> getChars() {
    return chars;
  }

  /**
   * Sets chars.
   *
   * @param chars the chars
   */
  public void setChars(Set<Character> chars) {
    this.chars = chars;
    this.pattern = null;
  }


  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public Class<String> getGeneratedType() {
    return String.class;
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

  @Override
  public void init(GeneratorContext context) {
    if (pattern != null) {
      this.chars = new RegexParser(locale).parseSingleChar(pattern).getCharSet().getSet();
    }
    setSource(new CharacterGenerator(chars));
    super.init(context);
  }

  @Override
  public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
    return wrapper.wrap(generate());
  }

  @Override
  public String generate() {
    return generateWithLength(generateCardinal());
  }

  @Override
  public String generateWithLength(int length) {
    if (length > 1000) {
      length = 1000;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      builder.append(generateFromSource().unwrap());
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[chars=" + chars + ", minLength=" + getMinLength() + ", " +
        "maxLength=" + getMaxLength() + ", lengthGranularity=" + getLengthGranularity() + ", " +
        "lengthDistribution=" + getLengthDistribution() + "]";
  }

}
