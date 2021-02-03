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

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

/**
 * Double Generator that implements a 'randomWalk' Double Sequence.<br/>
 * <br/>
 * Created: 13.06.2006 07:36:45
 *
 * @author Volker Bergmann
 */
public class RandomWalkDoubleGenerator extends AbstractNonNullNumberGenerator<Double> {

  private double next;

  private final RandomDoubleGenerator incrementGenerator;

  /**
   * Instantiates a new Random walk double generator.
   */
  public RandomWalkDoubleGenerator() {
    this(Double.MIN_VALUE, Double.MAX_VALUE);
  }

  /**
   * Instantiates a new Random walk double generator.
   *
   * @param min the min
   * @param max the max
   */
  public RandomWalkDoubleGenerator(double min, double max) {
    this(min, max, 1, 1);
  }

  /**
   * Instantiates a new Random walk double generator.
   *
   * @param min          the min
   * @param max          the max
   * @param minIncrement the min increment
   * @param maxIncrement the max increment
   */
  public RandomWalkDoubleGenerator(double min, double max, double minIncrement, double maxIncrement) {
    super(Double.class, min, max, 1.);
    incrementGenerator = new RandomDoubleGenerator(minIncrement, maxIncrement);
  }

  /**
   * Instantiates a new Random walk double generator.
   *
   * @param min          the min
   * @param max          the max
   * @param granularity  the granularity
   * @param minIncrement the min increment
   * @param maxIncrement the max increment
   */
  public RandomWalkDoubleGenerator(double min, double max, double granularity, double minIncrement, double maxIncrement) {
    super(Double.class, min, max, granularity);
    incrementGenerator = new RandomDoubleGenerator(minIncrement, maxIncrement, granularity);
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Sets granularity.
   *
   * @param granularity the granularity
   */
  public void setGranularity(double granularity) {
    super.setGranularity(granularity);
    incrementGenerator.setGranularity(granularity);
  }

  /**
   * Gets next.
   *
   * @return the next
   */
  public double getNext() {
    return next;
  }

  /**
   * Sets next.
   *
   * @param next the next
   */
  public void setNext(double next) {
    this.next = next;
  }

  // Generator interface implementation ------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    resetMembers();
    super.init(context);
  }

  @Override
  public synchronized Double generate() {
    assertInitialized();
    double value = next;
    next += incrementGenerator.generate();
    if (next > max) {
      next = max;
    } else if (next < min) {
      next = min;
    }
    return value;
  }

  @Override
  public synchronized void reset() {
    resetMembers();
  }

  private void resetMembers() {
    double minIncrement = incrementGenerator.getMin();
    double maxIncrement = incrementGenerator.getMax();
    if (minIncrement < 0 && maxIncrement <= 0) {
      next = max;
    } else if (minIncrement >= 0 && maxIncrement > 0) {
      next = min;
    } else {
      next = (min + max) / 2;
    }
  }

}
