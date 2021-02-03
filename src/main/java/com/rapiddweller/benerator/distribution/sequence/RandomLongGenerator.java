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
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.PropertyMessage;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;
import com.rapiddweller.benerator.util.RandomUtil;

/**
 * Long Generator that implements a 'random' Long Sequence.<br/>
 * <br/>
 * Created: 03.09.2006 09:53:01
 *
 * @author Volker Bergmann
 */
public class RandomLongGenerator extends AbstractNonNullNumberGenerator<Long> {

  /**
   * The constant DEFAULT_MIN.
   */
  public static final long DEFAULT_MIN = Long.MIN_VALUE / 2 + 1;
  /**
   * The constant DEFAULT_MAX.
   */
  public static final long DEFAULT_MAX = Long.MAX_VALUE / 2 - 1;
  private static final long DEFAULT_GRANULARITY = 1;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Random long generator.
   */
  public RandomLongGenerator() {
    this(DEFAULT_MIN, DEFAULT_MAX);
  }

  /**
   * Instantiates a new Random long generator.
   *
   * @param min the min
   * @param max the max
   */
  public RandomLongGenerator(long min, Long max) {
    this(min, max, DEFAULT_GRANULARITY);
  }

  /**
   * Instantiates a new Random long generator.
   *
   * @param min         the min
   * @param max         the max
   * @param granularity the granularity
   */
  public RandomLongGenerator(long min, Long max, long granularity) {
    super(Long.class, min, max, granularity);
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    if (granularity == 0L) {
      throw new InvalidGeneratorSetupException(getClass().getSimpleName() + ".granularity may not be 0");
    }
    if (min > max) {
      throw new InvalidGeneratorSetupException(
          new PropertyMessage("min", "greater than max"),
          new PropertyMessage("max", "less than min"));
    }
    super.init(context);
  }

  @Override
  public synchronized Long generate() {
    return generate(min, max, granularity);
  }

  // public convenience method ---------------------------------------------------------------------------------------

  /**
   * Generate long.
   *
   * @param min         the min
   * @param max         the max
   * @param granularity the granularity
   * @return the long
   */
  public static long generate(long min, long max, long granularity) {
    if (min == max) {
      return min;
    }
    long range = (max - min) / granularity;
    return min + RandomUtil.randomLong(0, range) * granularity;
  }

}
