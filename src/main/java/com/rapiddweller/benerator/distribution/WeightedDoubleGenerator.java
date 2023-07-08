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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.distribution.function.ConstantFunction;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

import java.util.Arrays;
import java.util.Random;

/**
 * Double Generator that supports a weight function.<br/><br/>
 * Created: 11.06.2006 21:33:41
 * @author Volker Bergmann
 * @since 0.1
 */
public class WeightedDoubleGenerator extends AbstractNonNullNumberGenerator<Double> {

  private final WeightFunction function;
  private final Random random;

  private double[] value;
  private double[] probSum;

  // constructors ----------------------------------------------------------------------------------------------------

  public WeightedDoubleGenerator() {
    this(0, 0, 1, new ConstantFunction(1));
  }

  public WeightedDoubleGenerator(double min, double max, double granularity, WeightFunction function) {
    super(Double.class, min, max, granularity);
    this.function = function;
    this.random = new Random();
  }

  public Distribution getDistribution() {
    return function;
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    if (min > max) {
      throw new InvalidGeneratorSetupException("min (" + min + ") > max(" + max + ")");
    }
    if (granularity <= 0) {
      throw new InvalidGeneratorSetupException("granularity value not supported: " + granularity);
    }
    int sampleCount = (int) ((max - min) / granularity) + 1;
    if (sampleCount > 100000) {
      logger.warn("granularity too small, resulting in a set of {} samples", sampleCount);
    }
    probSum = new double[sampleCount];
    value = new double[sampleCount];
    if (sampleCount == 1) {
      value[0] = min;
      probSum[0] = 1;
    } else {
      double sum = 0;
      double dx = (max - min) / (sampleCount - 1);
      for (int i = 0; i < sampleCount; i++) {
        value[i] = min + i * dx;
        sum += function.value(value[i]);
        probSum[i] = sum;
      }
      if (sum <= 0) {
        throw BeneratorExceptionFactory.getInstance().illegalGeneratorState(
            "Invalid WeightFunction: Sum is not positive for " + function);
      }
      for (int i = 0; i < sampleCount; i++) {
        probSum[i] /= sum;
      }
    }
    super.init(context);
  }

  @Override
  public Double generate() {
    assertInitialized();
    double randomValue = random.nextDouble();
    int n = intervallNoOfRandom(randomValue);
    return value[n];
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private int intervallNoOfRandom(double random) {
    int i = Arrays.binarySearch(probSum, random);
    if (i < 0) {
      i = -i - 1;
    }
    if (i >= probSum.length) {
      return probSum.length - 1;
    }
    return i;
  }

}
