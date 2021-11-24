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

package com.rapiddweller.benerator.primitive.number;

import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.util.GeneratingConverter;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.math.ArithmeticEngine;

/**
 * {@link Converter} implementation that transforms numbers
 * inducing relative or absolute numerical noise based on a {@link Distribution}.<br/><br/>
 * Created: 06.10.2010 17:14:46
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class NoiseInducer extends GeneratingConverter<Number, Number, Number> {

  private double minNoise;
  private double maxNoise;
  private double noiseGranularity;
  private Distribution noiseDistribution;
  private boolean relative;

  private Class<? extends Number> numberType;
  private ArithmeticEngine arithmetic;

  public NoiseInducer() {
    this(-0.1, 0.1, 0.001);
  }

  public NoiseInducer(double minNoise, double maxNoise, double noiseGranularity) {
    super(Number.class, Number.class, null);
    this.minNoise = minNoise;
    this.maxNoise = maxNoise;
    this.noiseGranularity = noiseGranularity;
    this.noiseDistribution = SequenceManager.CUMULATED_SEQUENCE;
    this.relative = true;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public double getMinNoise() {
    return minNoise;
  }

  public void setMinNoise(double minNoise) {
    this.minNoise = minNoise;
  }

  public double getMaxNoise() {
    return maxNoise;
  }

  public void setMaxNoise(double maxNoise) {
    this.maxNoise = maxNoise;
  }

  public double getNoiseGranularity() {
    return noiseGranularity;
  }

  public void setNoiseGranularity(double noiseGranularity) {
    this.noiseGranularity = noiseGranularity;
  }

  public Distribution getNoiseDistribution() {
    return noiseDistribution;
  }

  public void setNoiseDistribution(Distribution noiseDistribution) {
    this.noiseDistribution = noiseDistribution;
  }

  public boolean isRelative() {
    return relative;
  }

  public void setRelative(boolean relative) {
    this.relative = relative;
  }

  // Converter interface implementation ------------------------------------------------------------------------------

  @Override
  protected Number doConvert(Number sourceValue) {
    if (sourceValue == null) {
      return null;
    }
    if (numberType == null) {
      initialize(sourceValue);
    }
    ProductWrapper<Number> wrapper = generate();
    if (wrapper == null) {
      throw BeneratorExceptionFactory.getInstance().illegalGeneratorState("Noise generator unavailable: " + generator);
    }
    Number delta = wrapper.unwrap();
    Number result;
    if (relative) {
      result = (Number) arithmetic.multiply(sourceValue, arithmetic.subtract(1, delta));
    } else {
      result = (Number) arithmetic.add(sourceValue, delta);
    }
    return result;
  }

  public Number convert(Number sourceValue, Number minValue, Number maxValue) {
    if (sourceValue == null) {
      return null;
    }
    Number result = convert(sourceValue);
    double rd = result.doubleValue();
    if (rd < minValue.doubleValue()) {
      return minValue;
    }
    if (rd > maxValue.doubleValue()) {
      return maxValue;
    }
    return result;
  }

  // private helpers -------------------------------------------------------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  protected void initialize(Number sourceValue) {
    this.numberType = (relative ? Double.class : sourceValue.getClass());
    Converter<Number, ? extends Number> converter = ConverterManager.getInstance().createConverter(Number.class, numberType);
    arithmetic = new ArithmeticEngine();
    generator = context.getGeneratorFactory().createNumberGenerator(
        (Class<Number>) numberType,
        converter.convert(minNoise), true,
        converter.convert(maxNoise), true,
        converter.convert(noiseGranularity),
        noiseDistribution, Uniqueness.NONE);
    super.initialize(sourceValue);
  }

}
