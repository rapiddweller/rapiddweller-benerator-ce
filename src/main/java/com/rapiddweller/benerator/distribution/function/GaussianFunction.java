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

package com.rapiddweller.benerator.distribution.function;

import com.rapiddweller.benerator.distribution.AbstractWeightFunction;

/**
 * Gaussian Function. 1/(deviation*sqrt(2PI)) * e^(-(x - average)^2/(4 * deviation^2)).<br/>
 * <br/>
 * Created: 10.06.2006 05:37:56
 */
public class GaussianFunction extends AbstractWeightFunction {

  /**
   * the average value
   */
  private final double average;

  /**
   * the deviation
   */
  private final double deviation;

  /**
   * a constant scale factor of the function
   */
  private final double scale;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Fully Initializes the function
   *
   * @param average   the average
   * @param deviation the deviation
   */
  public GaussianFunction(double average, double deviation) {
    this.average = average;
    this.deviation = deviation;
    this.scale = 1. / deviation / Math.sqrt(2 * Math.PI);
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * returns the average
   *
   * @return the average
   */
  public double getAverage() {
    return average;
  }

  /**
   * returns the deviation
   *
   * @return the deviation
   */
  public double getDeviation() {
    return deviation;
  }

  // WeightFunction implementation -----------------------------------------------------------------------------------------

  /**
   * calculates the value
   */
  @Override
  public double value(double param) {
    double x = (param - average) / deviation;
    return scale * Math.exp(-0.5 * x * x);
  }

  /**
   * Creates a String representation of the function
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[1. / (" + deviation + "*sqrt(2*PI)) * e^(-" + (average != 0 ? "(x - " + average + ")" : "x") + "^2/" +
        (2 * deviation * deviation) + ")]";
  }
}
