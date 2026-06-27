/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */
package com.rapiddweller.benerator.distribution;

import com.rapiddweller.common.exception.IllegalArgumentError;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** Tests {@link FeatureWeight}: weights an object by a numeric bean property. */
public class FeatureWeightTest {
  public static class Bean {
    private final double population;
    public Bean(double population) { this.population = population; }
    public double getPopulation() { return population; }
  }

  @Test
  public void testWeighsByFeatureProperty() {
    FeatureWeight w = new FeatureWeight("population");
    assertEquals("population", w.getWeightFeature());
    assertEquals(1000.0, w.weight(new Bean(1000)), 0.0);
    assertEquals(5.0, w.weight(new Bean(5)), 0.0);
  }

  @Test(expected = IllegalArgumentError.class)
  public void testMissingFeatureIsRejected() {
    new FeatureWeight("population").weight(new Object());
  }
}
