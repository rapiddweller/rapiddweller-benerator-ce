/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Service provider interface for custom implementation of random functions.<br/><br/>
 * Created: 12.09.2021 08:54:13
 * @author Volker Bergmann
 * @since 2.0.0
 */
public interface RandomProvider {

  /** Returns a random, uniformly distributed double value between 0.0 (inclusive) and 1.0 (exclusive). */
  double randomDouble();

  /** Returns a random, uniformly distributed long value between minInclusive and maxInclusive. */
  long randomLong(long minInclusive, long maxInclusive);

  /** Returns a random, uniformly distributed long value between 0 (inclusive) and maxExclusive. */
  long randomLong(long maxExclusive);

  /** Returns a random, uniformly distributed int value between minInclusive and maxInclusive. */
  int randomInt(int minInclusive, int maxInclusive);

  /** Returns a random, uniformly distributed int value between 0 (inclusive) and maxExclusive. */
  int randomInt(int maxExclusive);

  /** Returns a random, uniformly distributed int value to be used as index of the collection. */
  int randomIndex(Collection<?> collection);

  /** Returns a random, uniformly distributed element of the array. */
  <T> T randomElement(T[] array);

  /** Returns a random, uniformly distributed element of the list. */
  <T> T randomElement(List<T> list);

  float randomProbability();
  char randomDigit(int min);
  Date randomDate(Date min, Date max);
  Object randomFromWeightLiteral(String literal);
  boolean randomBoolean();

}
