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

package com.rapiddweller.domain.person;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.csv.LocalCSVGenerator;
import com.rapiddweller.benerator.util.RandomUtil;
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Encodings;

import java.util.Locale;

/**
 * Creates nobility titles at a defined quota.
 * Titles are defined in the files 'com/rapiddweller/domain/person/nobTitle_*_*.csv'.
 * See the Wikipedia articles on <a href="http://en.wikipedia.org/wiki/Royal_and_noble_ranks">Royal
 * and noble ranks</a> and <a href="http://en.wikipedia.org/wiki/Nobility">Nobility</a> for further
 * information on the domain.<br/><br/>
 * Created: 11.02.2010 12:04:01
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class NobilityTitleGenerator extends GeneratorProxy<String> {

  private final static String BASE_NAME =
      "/com/rapiddweller/domain/person/nobTitle_";

  private Gender gender;
  private Locale locale;
  private float nobleQuota = 0.005f;

  /**
   * Instantiates a new Nobility title generator.
   */
  public NobilityTitleGenerator() {
    this(Gender.MALE, Locale.getDefault());
  }

  /**
   * Instantiates a new Nobility title generator.
   *
   * @param gender the gender
   * @param locale the locale
   */
  public NobilityTitleGenerator(Gender gender, Locale locale) {
    super(String.class);
    this.gender = gender;
    this.locale = locale;
  }

  // properties ------------------------------------------------------------------------------------------------------

  private static LocalCSVGenerator<String> createCSVGenerator(Gender gender,
                                                              Locale locale) {
    return new LocalCSVGenerator<>(String.class, baseName(gender), locale,
        ".csv", Encodings.UTF_8);
  }

  private static String baseName(Gender gender) {
    if (gender == Gender.FEMALE) {
      return BASE_NAME + "female";
    } else if (gender == Gender.MALE) {
      return BASE_NAME + "male";
    } else {
      throw new IllegalArgumentException("Gender: " + gender);
    }
  }

  /**
   * Sets gender.
   *
   * @param gender the gender
   */
  public void setGender(Gender gender) {
    this.gender = gender;
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

  // Generator interface implementation ------------------------------------------------------------------------------

  /**
   * Gets noble quota.
   *
   * @return the noble quota
   */
  public double getNobleQuota() {
    return nobleQuota;
  }

  /**
   * Sets noble quota.
   *
   * @param nobleQuota the noble quota
   */
  public void setNobleQuota(double nobleQuota) {
    this.nobleQuota = (float) nobleQuota;
  }

  // helper methods --------------------------------------------------------------------------------------------------

  @Override
  public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
    if (RandomUtil.randomProbability() < getNobleQuota()) {
      return super.generate(wrapper);
    } else {
      return wrapper.wrap("");
    }
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    setSource(createCSVGenerator(gender, locale));
    super.init(context);
  }

}
