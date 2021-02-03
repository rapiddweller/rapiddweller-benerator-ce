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

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.converter.NumberToNumberConverter;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.WeightedSample;

/**
 * {@link Sequence} implementation that provides values specified in a comma-separated value list,
 * use like "new PredefinedSequence('A', 'B', 'C')" or "new PredefinedSequence(5, 7, 11)".<br/><br/>
 * Created: 03.06.2010 08:40:27
 *
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class LiteralSequence extends Sequence {

  private Number[] numbers;

  /**
   * Instantiates a new Literal sequence.
   */
  protected LiteralSequence() {
    this(null);
  }

  /**
   * Instantiates a new Literal sequence.
   *
   * @param spec the spec
   */
  protected LiteralSequence(String spec) {
    setSpec(spec);
  }

  private void setSpec(String spec) {
    this.numbers = parseSpec(spec);
  }

  private static Number[] parseSpec(String spec) {
    if (StringUtil.isEmpty(spec)) {
      return new Number[0];
    }
    WeightedSample<?>[] samples = DatabeneScriptParser.parseWeightedLiteralList(spec);
    Number[] result = new Number[samples.length];
    for (int i = 0; i < samples.length; i++) {
      result[i] = (Number) samples[i].getValue();
    }
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity,
                                                                      boolean unique) {
    Number[] ts = new Number[numbers.length];
    NumberToNumberConverter<Number, T> converter = new NumberToNumberConverter<>(Number.class, numberType);
    for (int i = 0; i < numbers.length; i++) {
      ts[i] = converter.convert(numbers[i]);
    }
    return WrapperFactory.asNonNullGenerator(new PredefinedSequenceGenerator<>((T[]) ts));
  }

}
