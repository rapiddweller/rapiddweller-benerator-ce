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
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;

/**
 * Long Generator that implements a 'cumulated' Long Sequence.
 * Uniqueness cannot be supported since it contradicts the
 * purpose of this generator.<br/>
 * <br/>
 * Created: 07.06.2006 19:33:37
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class CumulatedLongGenerator extends AbstractNonNullNumberGenerator<Long> {

  /**
   * The constant DEFAULT_MAX.
   */
  public static final long DEFAULT_MAX = Long.MAX_VALUE / 10;
  /**
   * The constant DEFAULT_MIN.
   */
  public static final long DEFAULT_MIN = Long.MIN_VALUE / 10;

  /**
   * The Base gen.
   */
  RandomLongGenerator baseGen;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Cumulated long generator.
   */
  public CumulatedLongGenerator() {
    this(DEFAULT_MIN, DEFAULT_MAX);
  }

  /**
   * Instantiates a new Cumulated long generator.
   *
   * @param min the min
   * @param max the max
   */
  public CumulatedLongGenerator(long min, long max) {
    this(min, max, 1);
  }

  /**
   * Instantiates a new Cumulated long generator.
   *
   * @param min         the min
   * @param max         the max
   * @param granularity the granularity
   */
  public CumulatedLongGenerator(long min, long max, long granularity) {
    super(Long.class, min, max, granularity);
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Average float.
   *
   * @return the float
   */
  public float average() {
    return (float) (max + min) / 2;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public synchronized Long generate() {
    long index = (baseGen.generate() + baseGen.generate() + baseGen.generate() +
        baseGen.generate() + baseGen.generate() + 2) / 5L;
    return min + index * granularity;
  }

  @Override
  public void init(GeneratorContext context) {
    if (granularity.compareTo(0L) == 0) {
      throw new InvalidGeneratorSetupException(getClass().getSimpleName() + ".granularity may not be 0");
    }
    baseGen = new RandomLongGenerator(0, (max - min) / granularity);
    baseGen.init(context);
    super.init(context);
  }

}
