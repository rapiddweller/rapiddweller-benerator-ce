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
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.util.LuhnUtil;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;
import com.rapiddweller.common.StringUtil;

/**
 * Generates numbers that pass a Luhn test.<br/><br/>
 * Created: 18.10.2009 10:08:09
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class LuhnGenerator extends NonNullGeneratorProxy<String> {

  /**
   * The Prefix.
   */
  protected String prefix;
  /**
   * The Min length.
   */
  protected int minLength;
  /**
   * The Max length.
   */
  protected int maxLength;
  /**
   * The Length granularity.
   */
  protected final int lengthGranularity;
  /**
   * The Length distribution.
   */
  protected final Distribution lengthDistribution;

  /**
   * Instantiates a new Luhn generator.
   */
  public LuhnGenerator() {
    this("", 16);
  }

  /**
   * Instantiates a new Luhn generator.
   *
   * @param prefix the prefix
   * @param length the length
   */
  public LuhnGenerator(String prefix, int length) {
    this(prefix, length, length, 1, null);
  }

  /**
   * Instantiates a new Luhn generator.
   *
   * @param prefix             the prefix
   * @param minLength          the min length
   * @param maxLength          the max length
   * @param lengthGranularity  the length granularity
   * @param lengthDistribution the length distribution
   */
  public LuhnGenerator(String prefix, int minLength, int maxLength, int lengthGranularity, Distribution lengthDistribution) {
    super(String.class);
    this.prefix = prefix;
    this.minLength = minLength;
    this.maxLength = maxLength;
    this.lengthGranularity = 1;
    this.lengthDistribution = lengthDistribution;
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
   * Sets min length.
   *
   * @param minLength the min length
   */
  public void setMinLength(int minLength) {
    this.minLength = minLength;
  }

  /**
   * Sets max length.
   *
   * @param maxLength the max length
   */
  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
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
  public synchronized void init(GeneratorContext context) {
    super.setSource(new RandomVarLengthStringGenerator("\\d", minLength, maxLength, lengthGranularity, lengthDistribution));
    super.init(context);
  }

  @Override
  public String generate() throws IllegalGeneratorStateException {
    String number = super.generate();
    if (!StringUtil.isEmpty(prefix)) {
      number = prefix + number.substring(prefix.length());
    }
    char checkDigit = LuhnUtil.requiredCheckDigit(number);
    if (StringUtil.lastChar(number) == checkDigit) {
      return number;
    } else {
      return number.substring(0, number.length() - 1) + checkDigit;
    }
  }

}
