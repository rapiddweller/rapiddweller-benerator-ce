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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;

/**
 * This forwards a source generator's products.
 * Iterates through the products of another generator with a variable step width.
 * This is intended mainly for use with importing generators that provide data
 * volumes too big to keep in RAM.<br/>
 * <br/>
 * Created: 26.08.2006 16:16:04
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.1
 */
public class SkipGeneratorProxy<E> extends CardinalGenerator<E, E> {

  /**
   * The constant DEFAULT_MIN_INCREMENT.
   */
  public static final int DEFAULT_MIN_INCREMENT = 1;
  /**
   * The constant DEFAULT_MAX_INCREMENT.
   */
  public static final int DEFAULT_MAX_INCREMENT = 1;

  private final int minIncrement;
  private final int maxIncrement;
  private int count;
  private final Integer limit;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Skip generator proxy.
   */
  public SkipGeneratorProxy() {
    this(null);
  }

  /**
   * Initializes the generator to iterate with increment 1
   *
   * @param source the source
   */
  public SkipGeneratorProxy(Generator<E> source) {
    this(source, DEFAULT_MIN_INCREMENT, DEFAULT_MAX_INCREMENT);
  }

  /**
   * Instantiates a new Skip generator proxy.
   *
   * @param minIncrement the min increment
   * @param maxIncrement the max increment
   */
  public SkipGeneratorProxy(Integer minIncrement, Integer maxIncrement) {
    this(null, minIncrement, maxIncrement);
  }

  /**
   * Initializes the generator to use a random increment of uniform distribution
   *
   * @param source       the source
   * @param minIncrement the min increment
   * @param maxIncrement the max increment
   */
  public SkipGeneratorProxy(Generator<E> source, Integer minIncrement, Integer maxIncrement) {
    this(source, minIncrement, maxIncrement, SequenceManager.RANDOM_SEQUENCE, null);
  }

  /**
   * Initializes the generator to use a random increment of uniform distribution
   *
   * @param source                the source
   * @param minIncrement          the min increment
   * @param maxIncrement          the max increment
   * @param incrementDistribution the increment distribution
   * @param limit                 the limit
   */
  public SkipGeneratorProxy(Generator<E> source, int minIncrement, int maxIncrement,
                            Distribution incrementDistribution, Integer limit) {
    super(source, false, minIncrement, maxIncrement, 1, incrementDistribution);
    this.minIncrement = minIncrement;
    this.maxIncrement = maxIncrement;
    this.count = 0;
    this.limit = limit;
  }

  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  public Class<E> getGeneratedType() {
    return getSource().getGeneratedType();
  }

  @Override
  public void init(GeneratorContext context) {
    // check values
    if (minIncrement < 0) {
      throw new InvalidGeneratorSetupException("minIncrement is less than zero");
    }
    if (maxIncrement < 0) {
      throw new InvalidGeneratorSetupException("maxIncrement is less than zero");
    }
    if (minIncrement > maxIncrement) {
      throw new InvalidGeneratorSetupException("minIncrement (" + minIncrement + ") is larger than maxIncrement (" + maxIncrement + ")");
    }
    super.init(context);
  }

  @Override
  public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    Integer increment = generateCardinal();
    if (increment == null) {
      return null;
    }
    for (long i = 0; i < increment - 1; i++) {
      generateFromSource();
    }
    count += increment;
    if (limit != null && count > limit) {
      return null;
    }
    return getSource().generate(wrapper);
  }

  @Override
  public void reset() {
    super.reset();
    count = 0;
  }

}
