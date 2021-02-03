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
import com.rapiddweller.benerator.sample.NonNullSampleGenerator;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.ThreadAware;
import com.rapiddweller.common.converter.CaseConverter;
import com.rapiddweller.common.converter.ConverterChain;
import com.rapiddweller.domain.net.DomainGenerator;
import com.rapiddweller.format.text.DelocalizingConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;

/**
 * Generates email addresses for a random domain by a given person name.<br/><br/>
 * Created: 22.02.2010 12:16:11
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class EMailAddressBuilder implements ThreadAware {

  private final DomainGenerator domainGenerator;
  private final CaseConverter caseConverter;
  private final Converter<String, String> nameConverter;
  private final NonNullSampleGenerator<Character> joinGenerator;

  // constructor -----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new E mail address builder.
   *
   * @param dataset the dataset
   */
  public EMailAddressBuilder(String dataset) {
    // Logger is not static in order to adopt sub classes
    Logger logger = LogManager.getLogger(getClass());
    logger.debug("Creating instance of {} for dataset {}", getClass(),
        dataset);
    this.domainGenerator = new DomainGenerator(dataset);
    this.caseConverter = new CaseConverter(false);
    try {
      this.nameConverter = new ConverterChain<>(
          new DelocalizingConverter(),
          caseConverter);
    } catch (IOException e) {
      throw new ConfigurationError("Error in Converter setup", e);
    }
    this.joinGenerator =
        new NonNullSampleGenerator<>(Character.class, '_', '.', '0',
            '1');
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Sets dataset.
   *
   * @param datasetName the dataset name
   */
  public void setDataset(String datasetName) {
    domainGenerator.setDataset(datasetName);
  }

  /**
   * Sets locale.
   *
   * @param locale the locale
   */
  public void setLocale(Locale locale) {
    caseConverter.setLocale(locale);
  }

  // generator-like interface ----------------------------------------------------------------------------------------

  /**
   * Init.
   *
   * @param context the context
   */
  public void init(GeneratorContext context) {
    domainGenerator.init(context);
    joinGenerator.init(context);
  }

  /**
   * Generate string.
   *
   * @param givenName  the given name
   * @param familyName the family name
   * @return the string
   */
  public String generate(String givenName, String familyName) {
    String given = nameConverter.convert(givenName);
    String family = nameConverter.convert(familyName);
    String domain = domainGenerator.generate();
    Character join = joinGenerator.generate();
    switch (join) {
      case '.':
        return given + '.' + family + '@' + domain;
      case '_':
        return given + '_' + family + '@' + domain;
      case '0':
        return given + family + '@' + domain;
      case '1':
        return given.charAt(0) + family + '@' + domain;
      default:
        throw new ConfigurationError("Invalid join strategy: " + join);
    }
  }

  // ThreadAware interface implementation ----------------------------------------------------------------------------

  @Override
  public boolean isParallelizable() {
    return domainGenerator.isParallelizable()
        && caseConverter.isParallelizable()
        && nameConverter.isParallelizable()
        && joinGenerator.isParallelizable();
  }

  @Override
  public boolean isThreadSafe() {
    return domainGenerator.isThreadSafe()
        && caseConverter.isThreadSafe()
        && nameConverter.isThreadSafe()
        && joinGenerator.isThreadSafe();
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return BeanUtil.toString(this);
  }

}
