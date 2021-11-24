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

package com.rapiddweller.benerator.distribution;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.sample.SampleGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.ConverterManager;

import java.util.List;
import java.util.Random;

/**
 * {@link Distribution} implementation which uses the inverse of a probability function integral
 * for efficiently generating numbers with a given probability distribution.
 * See <a href="http://www.stat.wisc.edu/~larget/math496/random2.html">Random
 * Number Generation from Non-uniform Distributions</a>.<br/><br/>
 * Created: 12.03.2010 13:31:16
 * @author Volker Bergmann
 * @since 0.6.0
 */
public abstract class CumulativeDistributionFunction extends AbstractDistribution {

  public abstract double cumulativeProbability(double value);

   public abstract double inverse(double probability);

  @Override
  public <T extends Number> NonNullGenerator<T> createNumberGenerator(
      Class<T> numberType, T min, T max, T granularity, boolean unique) {
    if (unique) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument(this + " cannot generate unique values");
    }
    return new IPINumberGenerator<>(this, numberType, min, max, granularity);
  }

  @Override
  public boolean isApplicationDetached() {
    return true;
  }

  @Override
  public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
    if (unique) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument(this + " is not designed to generate unique values");
    }
    List<T> allProducts = GeneratorUtil.allProducts(source);
    if (allProducts.size() == 1) {
      return new ConstantGenerator<>(allProducts.get(0));
    }
    return new SampleGenerator<>(source.getGeneratedType(), this, unique, allProducts);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  /**
   * Generates numbers according to an {@link CumulativeDistributionFunction}.<br/><br/>
   * Created: 12.03.2010 14:37:33
   * @param <E> the type of generated numbers
   * @author Volker Bergmann
   * @since 0.6.0
   */
  public static class IPINumberGenerator<E extends Number> extends AbstractNonNullNumberGenerator<E> {

    private final CumulativeDistributionFunction fcn;
    private final Random random = new Random();
    private final Converter<Double, E> converter;
    private final double minProb;
    private final double probScale;
    private final double minD;
    private double maxD;
    private final double granularityD;

    public IPINumberGenerator(CumulativeDistributionFunction fcn, Class<E> targetType, E min, E max, E granularity) {
      super(targetType, min, max, granularity);
      this.fcn = fcn;
      this.minD = (min != null ? min.doubleValue() : (max != null ? maxD - 9 : 0));
      this.maxD = (max != null ? max.doubleValue() : (min != null ? minD + 9 : 0));
      this.granularityD = granularity.doubleValue();
      this.minProb = fcn.cumulativeProbability(minD);
      this.probScale = fcn.cumulativeProbability(maxD + granularityD) - this.minProb;
      this.converter = ConverterManager.getInstance().createConverter(Double.class, targetType);
    }

    @Override
    public E generate() {
      double tmp;
      double prob = minProb + random.nextDouble() * probScale;
      tmp = fcn.inverse(prob);
      tmp = Math.floor((tmp - minD) / granularityD) * granularityD + minD;
      return converter.convert(tmp);
    }

  }

}
