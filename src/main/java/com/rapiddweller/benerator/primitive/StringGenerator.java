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
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;
import com.rapiddweller.common.Filter;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.filter.FilterUtil;
import com.rapiddweller.format.regex.RegexParser;
import com.rapiddweller.model.data.Uniqueness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * {@link String} {@link Generator} which offers a wide range of options for generating strings.<br/><br/>
 * Created: 31.07.2011 07:15:05
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class StringGenerator extends NonNullGeneratorProxy<String> {

  private String charSet;
  private Locale locale;
  private boolean unique;
  private boolean ordered;
  private String prefix;
  /**
   * The Min initial.
   */
  Character minInitial;
  private String suffix;
  private int minLength;
  private int maxLength;
  private int lengthGranularity;
  private Distribution lengthDistribution;

  private NonNullGenerator<Character> minInitialGenerator;

  /**
   * Instantiates a new String generator.
   */
  public StringGenerator() {
    this("\\w", LocaleUtil.getFallbackLocale(), false, false, null, null, null, 1, 8, 1, null);
  }

  /**
   * Instantiates a new String generator.
   *
   * @param charSet            the char set
   * @param locale             the locale
   * @param unique             the unique
   * @param ordered            the ordered
   * @param prefix             the prefix
   * @param minInitial         the min initial
   * @param suffix             the suffix
   * @param minLength          the min length
   * @param maxLength          the max length
   * @param lengthGranularity  the length granularity
   * @param lengthDistribution the length distribution
   */
  public StringGenerator(String charSet, Locale locale, boolean unique,
                         boolean ordered, String prefix, Character minInitial,
                         String suffix, int minLength, int maxLength, int lengthGranularity,
                         Distribution lengthDistribution) {
    super(String.class);
    this.charSet = charSet;
    this.locale = locale;
    this.unique = unique;
    this.ordered = ordered;
    this.prefix = prefix;
    this.minInitial = minInitial;
    this.suffix = suffix;
    this.minLength = minLength;
    this.maxLength = maxLength;
    this.lengthGranularity = lengthGranularity;
    this.lengthDistribution = lengthDistribution;
  }

  /**
   * Gets char set.
   *
   * @return the char set
   */
  public String getCharSet() {
    return charSet;
  }

  /**
   * Sets char set.
   *
   * @param charSet the char set
   */
  public void setCharSet(String charSet) {
    this.charSet = charSet;
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
   * Is unique boolean.
   *
   * @return the boolean
   */
  public boolean isUnique() {
    return unique;
  }

  /**
   * Sets unique.
   *
   * @param unique the unique
   */
  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  /**
   * Is ordered boolean.
   *
   * @return the boolean
   */
  public boolean isOrdered() {
    return ordered;
  }

  /**
   * Sets ordered.
   *
   * @param ordered the ordered
   */
  public void setOrdered(boolean ordered) {
    this.ordered = ordered;
  }

  /**
   * Gets prefix.
   *
   * @return the prefix
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * Sets prefix.
   *
   * @param prefix the prefix
   */
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  /**
   * Gets min initial.
   *
   * @return the min initial
   */
  public Character getMinInitial() {
    return minInitial;
  }

  /**
   * Sets min initial.
   *
   * @param minInitial the min initial
   */
  public void setMinInitial(Character minInitial) {
    this.minInitial = minInitial;
  }

  /**
   * Gets suffix.
   *
   * @return the suffix
   */
  public String getSuffix() {
    return suffix;
  }

  /**
   * Sets suffix.
   *
   * @param suffix the suffix
   */
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  /**
   * Gets min length.
   *
   * @return the min length
   */
  public int getMinLength() {
    return minLength;
  }

  /**
   * Sets min length.
   *
   * @param minLength the min length
   */
  public void setMinLength(int minLength) {
    this.minLength = minLength;
  }

  /**
   * Gets max length.
   *
   * @return the max length
   */
  public int getMaxLength() {
    return maxLength;
  }

  /**
   * Sets max length.
   *
   * @param maxLength the max length
   */
  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  /**
   * Gets length granularity.
   *
   * @return the length granularity
   */
  public int getLengthGranularity() {
    return lengthGranularity;
  }

  /**
   * Sets length granularity.
   *
   * @param lengthGranularity the length granularity
   */
  public void setLengthGranularity(int lengthGranularity) {
    this.lengthGranularity = lengthGranularity;
  }

  /**
   * Gets length distribution.
   *
   * @return the length distribution
   */
  public Distribution getLengthDistribution() {
    return lengthDistribution;
  }

  /**
   * Sets length distribution.
   *
   * @param lengthDistribution the length distribution
   */
  public void setLengthDistribution(Distribution lengthDistribution) {
    this.lengthDistribution = lengthDistribution;
  }

  @Override
  public boolean isParallelizable() {
    return super.isParallelizable() && (minInitialGenerator == null || minInitialGenerator.isParallelizable());
  }

  @Override
  public boolean isThreadSafe() {
    return super.isThreadSafe() && (minInitialGenerator == null || minInitialGenerator.isThreadSafe());
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    Set<Character> chars = new RegexParser(locale).parseSingleChar(charSet).getCharSet().getSet();
    GeneratorFactory factory = context.getGeneratorFactory();
    if (minInitial != null) {
      Filter<Character> initialFilter = candidate -> (candidate >= minInitial);
      Set<Character> initialSet = new HashSet<>(FilterUtil.filter(new ArrayList<>(chars), initialFilter));
      this.minInitialGenerator = factory.createCharacterGenerator(initialSet);
      this.minInitialGenerator.init(context);
    }
    Generator<String> source = factory.createStringGenerator(chars, minLength, maxLength,
        lengthGranularity, lengthDistribution, Uniqueness.instance(unique, ordered));
    setSource(source);
    super.init(context);
  }

  @Override
  public String generate() {
    assertInitialized();
    StringBuilder builder = new StringBuilder();
    String base = super.generate();
    if (base == null) {
      return null;
    }
    if (!StringUtil.isEmpty(prefix)) {
      builder.append(prefix);
      base = base.substring(prefix.length());
    }
    if (minInitialGenerator != null) {
      builder.append(minInitialGenerator.generate());
      base = base.substring(1);
    }
    if (!StringUtil.isEmpty(suffix)) {
      base = base.substring(0, base.length() - suffix.length());
      builder.append(base).append(suffix);
    } else {
      builder.append(base);
    }
    return builder.toString();
  }

  @Override
  public void reset() {
    if (minInitialGenerator != null) {
      minInitialGenerator.reset();
    }
    super.reset();
  }

}
