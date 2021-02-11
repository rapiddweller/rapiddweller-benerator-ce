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
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.sample.NonNullSampleGenerator;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;
import com.rapiddweller.common.LocaleUtil;
import com.rapiddweller.common.SyntaxError;
import com.rapiddweller.format.regex.RegexParser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Generates Character values from a character set or a regular expression.<br/>
 * <br/>
 * Created: 09.06.2006 20:34:55
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class CharacterGenerator extends NonNullGeneratorProxy<Character> {

  /**
   * The regular exception
   */
  private String pattern;

  /**
   * The locale
   */
  private Locale locale;

  /**
   * The set of characters to generate from
   */
  private Set<Character> values;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * initializes the generator to use letters of the fallback locale.
   *
   * @see com.rapiddweller.common.LocaleUtil#getFallbackLocale() com.rapiddweller.common.LocaleUtil#getFallbackLocale()
   */
  public CharacterGenerator() {
    this("\\w");
  }

  /**
   * initializes the generator to create character that match a regular expressions and the fallback locale.
   *
   * @param pattern the pattern
   * @see com.rapiddweller.common.LocaleUtil#getFallbackLocale() com.rapiddweller.common.LocaleUtil#getFallbackLocale()
   */
  public CharacterGenerator(String pattern) {
    this(pattern, LocaleUtil.getFallbackLocale());
  }

  /**
   * initializes the generator to create character that match a regular expressions and a locale.
   *
   * @param pattern the pattern
   * @param locale  the locale
   * @see com.rapiddweller.common.LocaleUtil#getFallbackLocale() com.rapiddweller.common.LocaleUtil#getFallbackLocale()
   */
  public CharacterGenerator(String pattern, Locale locale) {
    super(Character.class);
    this.pattern = pattern;
    this.locale = locale;
    this.values = new HashSet<>();
  }

  /**
   * initializes the generator to create characters from a character collection.
   *
   * @param values the values
   * @see com.rapiddweller.common.LocaleUtil#getFallbackLocale() com.rapiddweller.common.LocaleUtil#getFallbackLocale()
   */
  public CharacterGenerator(Collection<Character> values) {
    super(Character.class);
    this.pattern = null;
    this.locale = LocaleUtil.getFallbackLocale();
    this.values = new HashSet<>(values);
  }

  // config properties -----------------------------------------------------------------------------------------------

  /**
   * Returns the regular expression to match
   *
   * @return the pattern
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Sets the regular expression to match
   *
   * @param pattern the pattern
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * Returns the {@link Locale} of which letters are taken
   *
   * @return the locale
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Sets the {@link Locale} of which letters are taken
   *
   * @param locale the locale
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * Returns the available values
   *
   * @return the values
   */
  public Set<Character> getValues() {
    return values;
  }

  // source interface ------------------------------------------------------------------------------------------------

  @Override
  public Class<Character> getGeneratedType() {
    return Character.class;
  }

  /**
   * Initializes the generator's state.
   */
  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    try {
      if (pattern != null) {
        values = new RegexParser(locale).parseSingleChar(pattern).getCharSet().getSet();
      }
      setSource(new NonNullSampleGenerator<>(Character.class, values));
      super.init(context);
    } catch (SyntaxError e) {
      throw new IllegalGeneratorStateException(e);
    }
  }

  @Override
  public Character generate() {
    assertInitialized();
    return generateFromNotNullSource();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + values;
  }

}
