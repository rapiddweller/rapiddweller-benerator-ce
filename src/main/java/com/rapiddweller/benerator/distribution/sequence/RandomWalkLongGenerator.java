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
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

/**
 * Long Generator that implements a 'randomWalk' Long Sequence.<br/>
 * <br/>
 * Created: 13.06.2006 07:36:45
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class RandomWalkLongGenerator extends AbstractNonNullNumberGenerator<Long> {

  /**
   * The Min increment.
   */
  final long minIncrement;
  /**
   * The Max increment.
   */
  final long maxIncrement;
  /**
   * The Increment distribution.
   */
  final Distribution incrementDistribution;

  private long initial;
  private long next;

  private NonNullGenerator<Long> incrementGenerator;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Random walk long generator.
   */
  public RandomWalkLongGenerator() {
    this(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  /**
   * Instantiates a new Random walk long generator.
   *
   * @param min the min
   * @param max the max
   */
  public RandomWalkLongGenerator(long min, long max) {
    this(min, max, 1, 2);
  }

  /**
   * Instantiates a new Random walk long generator.
   *
   * @param min          the min
   * @param max          the max
   * @param minIncrement the min increment
   * @param maxIncrement the max increment
   */
  public RandomWalkLongGenerator(long min, long max, long minIncrement, long maxIncrement) {
    this(min, max, 1, min, minIncrement, maxIncrement, SequenceManager.RANDOM_SEQUENCE);
  }

  /**
   * Instantiates a new Random walk long generator.
   *
   * @param min          the min
   * @param max          the max
   * @param granularity  the granularity
   * @param initial      the initial
   * @param minIncrement the min increment
   * @param maxIncrement the max increment
   */
  public RandomWalkLongGenerator(long min, long max, long granularity, long initial, long minIncrement, long maxIncrement) {
    this(min, max, granularity, initial, minIncrement, maxIncrement, SequenceManager.RANDOM_SEQUENCE);
  }

  /**
   * Instantiates a new Random walk long generator.
   *
   * @param min                   the min
   * @param max                   the max
   * @param granularity           the granularity
   * @param initial               the initial
   * @param minIncrement          the min increment
   * @param maxIncrement          the max increment
   * @param incrementDistribution the increment distribution
   */
  public RandomWalkLongGenerator(long min, long max, long granularity, long initial,
                                 long minIncrement, long maxIncrement, Distribution incrementDistribution) {
    super(Long.class, min, max, granularity);
    this.minIncrement = minIncrement;
    this.maxIncrement = maxIncrement;
    this.incrementDistribution = incrementDistribution;
    this.initial = initial;
  }

  // config properties -----------------------------------------------------------------------------------------------

  /**
   * Gets next.
   *
   * @return the next
   */
  public long getNext() {
    return next;
  }

  /**
   * Sets next.
   *
   * @param next the next
   */
  public void setNext(long next) {
    this.next = next;
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    incrementGenerator = incrementDistribution.createNumberGenerator(
        Long.class, minIncrement, maxIncrement, granularity, false);
    if (minIncrement < 0 && maxIncrement <= 0) {
      initial = max;
    } else if (minIncrement >= 0 && maxIncrement > 0) {
      initial = min;
    } else {
      initial = (min + max) / 2;
    }
    next = initial;
    incrementGenerator.init(context);
    super.init(context);
  }

  @Override
  public synchronized Long generate() {
    assertInitialized();
    long value = next;
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
    super.reset();
    next = initial;
  }

  @Override
  public synchronized void close() {
    super.close();
    next = initial;
  }

}
