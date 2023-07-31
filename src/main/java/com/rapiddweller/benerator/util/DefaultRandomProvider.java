/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.WeightedSample;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Default implementation of the {@link RandomProvider} interface.<br/><br/>
 * Created: 12.09.2021 08:55:55
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class DefaultRandomProvider implements RandomProvider {

  private final Random random = new Random();

  /** Returns a random, uniformly distributed double value between 0.0 (inclusive) and 1.0 (exclusive). */
  public double randomDouble() {
    return random.nextDouble();
  }

  /** Returns a random, uniformly distributed long value between minInclusive and maxInclusive. */
  public long randomLong(long minInclusive, long maxInclusive) {
    if (minInclusive > maxInclusive) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("min (" + minInclusive + ") > max (" + maxInclusive + ")");
    }
    return minInclusive + randomLong(maxInclusive - minInclusive + 1);
  }

  /** Returns a random, uniformly distributed long value between 0 (inclusive) and maxExclusive. */
  public long randomLong(long maxExclusive) {
    Assert.notNegative(maxExclusive, "maxExclusive");
    long result = random.nextLong() % maxExclusive;
    if (result < 0) {
      result += maxExclusive;
    }
    return result;
  }

  /** Returns a random, uniformly distributed int value between minInclusive and maxInclusive. */
  public int randomInt(int minInclusive, int maxInclusive) {
    if (minInclusive > maxInclusive) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("min (" + minInclusive + ") > max (" + maxInclusive + ")");
    }
    return minInclusive + randomInt(maxInclusive - minInclusive + 1);
  }

  /** Returns a random, uniformly distributed int value between 0 (inclusive) and maxExclusive. */
  public int randomInt(int maxExclusive) {
    Assert.notNegative(maxExclusive, "maxExclusive");
    int result = random.nextInt() % maxExclusive;
    if (result < 0) {
      result += maxExclusive;
    }
    return result;
  }

  /** Returns a random, uniformly distributed int value to be used as index of the array. */
  public int randomIndex(Object[] values) {
    if (values.length == 0) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("Cannot create random index for an empty array");
    }
    return randomInt(values.length);
  }

  /** Returns a random, uniformly distributed element of the array. */
  public <T> T randomElement(T... values) {
    if (values.length == 0) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("Cannot choose random value from an empty array");
    }
    return values[randomIndex(values)];
  }

  /** Returns a random, uniformly distributed int value to be used as index of the collection. */
  public int randomIndex(Collection<?> values) {
    if (values.isEmpty()) {
      throw BeneratorExceptionFactory.getInstance().illegalArgument("Cannot create random index for an empty array");
    }
    return randomInt(values.size());
  }

  /** Returns a random, uniformly distributed element of the list. */
  public <T> T randomElement(List<T> values) {
    return values.get(randomIndex(values));
  }

  public char randomDigit(int min) {
    return (char) ('0' + min + random.nextInt(10 - min));
  }

  public float randomProbability() {
    return random.nextFloat();
  }

  public Date randomDate(Date min, Date max) {
    return new Date(randomLong(min.getTime(), max.getTime()));
  }

  public Object randomFromWeightLiteral(String literal) {
    if (StringUtil.isEmpty(literal)) {
      return null;
    }
    WeightedSample<?>[] samples = DatabeneScriptParser.parseWeightedLiteralList(literal);
    int sampleCount = samples.length;
    if (sampleCount == 1) {
      return samples[0];
    }

    // normalize weights
    double[] probSum = new double[sampleCount];
    double sum = 0;
    for (int i = 0; i < sampleCount; i++) {
      double weight = samples[i].getWeight();
      if (weight < 0) {
        throw BeneratorExceptionFactory.getInstance().illegalArgument("Negative weight in literal: " + literal);
      }
      sum += weight;
      probSum[i] = (float) sum;
    }
    if (sum == 0) {
      return samples[randomInt(sampleCount)]; // for unweighted values, use simple random
    }
    for (int i = 0; i < sampleCount; i++) {
      probSum[i] /= (float) sum;
    }

    // choose an item
    double probability = randomProbability();
    int i = Arrays.binarySearch(probSum, probability);
    if (i < 0) {
      i = -i - 1;
    }
    if (i >= probSum.length) {
      i = probSum.length - 1;
    }
    return samples[i].getValue();
  }

}
