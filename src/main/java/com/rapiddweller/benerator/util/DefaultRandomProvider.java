/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.RandomProvider;
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

  public long randomLong(long min, long maxInclusive) {
    if (min > maxInclusive) {
      throw new IllegalArgumentException("min (" + min + ") > max (" + maxInclusive + ")");
    }
    return min + randomLong(maxInclusive - min + 1);
  }

  public long randomLong(long maxExclusive) {
    Assert.notNegative(maxExclusive, "maxExclusive");
    long result = random.nextLong() % maxExclusive;
    if (result < 0) {
      result += maxExclusive;
    }
    return result;
  }

  public int randomInt(int min, int maxInclusive) {
    if (min > maxInclusive) {
      throw new IllegalArgumentException("min (" + min + ") > max (" + maxInclusive + ")");
    }
    return min + randomInt(maxInclusive - min + 1);
  }

  public int randomInt(int maxExclusive) {
    Assert.notNegative(maxExclusive, "maxExclusive");
    int result = random.nextInt() % maxExclusive;
    if (result < 0) {
      result += maxExclusive;
    }
    return result;
  }

  public double randomDouble() {
    return random.nextDouble();
  }

  public <T> T randomElement(T... values) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Cannot choose random value from an empty array");
    }
    return values[randomIndex(values)];
  }

  public int randomIndex(Object[] values) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Cannot create random index for an empty array");
    }
    return randomInt(values.length);
  }

  public <T> T randomElement(List<T> values) {
    return values.get(randomIndex(values));
  }

  public int randomIndex(Collection<?> values) {
    if (values.size() == 0) {
      throw new IllegalArgumentException("Cannot create random index for an empty array");
    }
    return randomInt(values.size());
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
        throw new IllegalArgumentException("Negative weight in literal: " + literal);
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
