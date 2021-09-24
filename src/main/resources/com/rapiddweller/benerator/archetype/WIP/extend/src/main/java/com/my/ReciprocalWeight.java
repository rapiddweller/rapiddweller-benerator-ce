package com.my;

import com.rapiddweller.benerator.distribution.AbstractWeightFunction;

/**
 * The type Reciprocal weight.
 */
public class ReciprocalWeight extends AbstractWeightFunction {

  public double value(double x) {
    return 100 / (x + 1);
  }

}
