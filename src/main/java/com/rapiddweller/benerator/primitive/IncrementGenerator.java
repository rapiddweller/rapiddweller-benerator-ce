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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;
import com.rapiddweller.common.ConfigurationError;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates long values by continuously incrementing a base (min) value.
 *
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class IncrementGenerator extends ThreadSafeNonNullGenerator<Long> {

  private static final long DEFAULT_MIN = 1;
  private static final long DEFAULT_MAX = Long.MAX_VALUE - 1;

  // attributes ------------------------------------------------------------------------------------------------------

  private long min;
  private long max;
  private long increment;

  /**
   * The Cursor.
   */
  @SuppressWarnings("CanBeFinal")
  volatile AtomicLong cursor = new AtomicLong();

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Increment generator.
   */
  public IncrementGenerator() {
    this(DEFAULT_MIN);
  }

  /**
   * Instantiates a new Increment generator.
   *
   * @param min the min
   */
  public IncrementGenerator(long min) {
    this(min, 1);
  }

  /**
   * Instantiates a new Increment generator.
   *
   * @param min       the min
   * @param increment the increment
   */
  public IncrementGenerator(long min, long increment) {
    this(min, increment, DEFAULT_MAX);
  }

  /**
   * Instantiates a new Increment generator.
   *
   * @param min       the min
   * @param increment the increment
   * @param max       the max
   */
  public IncrementGenerator(long min, long increment, long max) {
    setMin(min);
    setIncrement(increment);
    setMax(max);
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets min.
   *
   * @return the min
   */
  public Long getMin() {
    return min;
  }

  /**
   * Sets min.
   *
   * @param min the min
   */
  public void setMin(Long min) {
    this.min = min;
    this.cursor.set(min);
  }

  /**
   * Gets max.
   *
   * @return the max
   */
  public long getMax() {
    return max;
  }

  /**
   * Sets max.
   *
   * @param max the max
   */
  public void setMax(long max) {
    this.max = max;
  }

  /**
   * Gets increment.
   *
   * @return the increment
   */
  public long getIncrement() {
    return increment;
  }

  /**
   * Sets increment.
   *
   * @param increment the increment
   */
  public void setIncrement(long increment) {
    if (increment < 1) {
      throw new ConfigurationError("increment must be a positive number, but was " + increment);
    }
    this.increment = increment;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public Class<Long> getGeneratedType() {
    return Long.class;
  }

  @Override
  public Long generate() {
    if (cursor.get() <= max) {
      return cursor.getAndAdd(increment);
    } else {
      return null;
    }
  }

  @Override
  public void reset() {
    this.cursor.set(min);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + cursor + ']';
  }

}
