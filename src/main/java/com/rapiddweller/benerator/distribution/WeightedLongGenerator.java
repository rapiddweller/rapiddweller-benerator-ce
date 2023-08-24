/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
 * Long Generator that supports a weight function.<br/><br/>
 * Created: 18.06.2006 15:00:41
 * @author Volker Bergmann
 * @since 0.1
 */
public class WeightedLongGenerator extends AbstractNonNullNumberGenerator<Long> {

  private WeightFunction function;

  private final Random randomizer;
  private float[] probSum;

  // constructors ----------------------------------------------------------------------------------------------------

  public WeightedLongGenerator() {
    this(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  public WeightedLongGenerator(long min, long max) {
    this(min, max, 1);
  }

  public WeightedLongGenerator(long min, long max, long granularity) {
    this(min, max, granularity, new ConstantFunction(1));
  }

  public WeightedLongGenerator(long min, long max, WeightFunction function) {
    this(min, max, 1, function);
  }

  public WeightedLongGenerator(long min, long max, long granularity, WeightFunction function) {
    super(Long.class, min, max, granularity);
    this.function = function;
    this.randomizer = new Random();
  }

  // properties ------------------------------------------------------------------------------------------------------

  public Distribution getDistribution() {
    return function;
  }

  public void setDistribution(Distribution distribution) {
    if (!(distribution instanceof WeightFunction)) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("Function expected, found: " + distribution);
    }
    this.function = (WeightFunction) distribution;
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    normalize();
    super.init(context);
  }

  @Override
  public Long generate() {
    assertInitialized();
    float random = randomizer.nextFloat();
    long n = intervalNoOfRandom(random);
    return min + n * granularity;
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private long intervalNoOfRandom(float random) {
    int i = Arrays.binarySearch(probSum, random);
    if (i < 0) {
      i = -i - 1;
    }
    if (i >= probSum.length) {
      return probSum.length - 1L;
    }
    return i;
  }

  private void normalize() {
    int sampleCount = (int) ((max - min) / granularity) + 1;
    if (sampleCount > 100000) {
      logger.warn("granularity too small, resulting in a set of {} samples", sampleCount);
    }
    probSum = new float[sampleCount];
    if (sampleCount == 1) {
      probSum[0] = 1;
    } else {
      double sum = 0;
      for (int i = 0; i < sampleCount; i++) {
        long dx = (max - min) / (sampleCount - 1);
        long x = min + i * dx;
        sum += function.value(x);
        probSum[i] = (float) sum;
      }
      if (sum == 0) {
        sum = 1;
        float avgProp = (float) 1. / sampleCount;
        for (int i = 0; i < sampleCount; i++) {
          probSum[i] = (i + 1) * avgProp;
        }
      } else if (sum < 0) {
        throw BeneratorExceptionFactory.getInstance().illegalGeneratorState(
            "Invalid WeightFunction: Sum is negative (" + sum + ") for " + function);
      }
      for (int i = 0; i < sampleCount; i++) {
        probSum[i] /= (float) sum;
      }
    }
  }

}
