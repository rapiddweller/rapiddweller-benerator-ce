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
  long randomLong(long min, long maxInclusive);
  long randomLong(long maxExclusive);
  int randomInt(int min, int maxInclusive);
  int randomInt(int maxExclusive);
  int randomIndex(Collection<?> collection);
  <T> T randomElement(T[] array);
  <T> T randomElement(List<T> list);
  float randomProbability();
  char randomDigit(int min);
  Date randomDate(Date min, Date max);
  Object randomFromWeightLiteral(String literal);
  double randomDouble();
}
