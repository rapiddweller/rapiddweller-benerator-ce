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

package com.rapiddweller.benerator.primitive.number;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.util.AbstractNonNullGenerator;
import com.rapiddweller.common.comparator.NumberComparator;
import com.rapiddweller.common.converter.NumberToNumberConverter;

/**
 * Abstract parent class for all number generators.
 * It hosts a distribution and defines abstract properties to be implemented by child classes.<br/>
 * <br/>
 * Created: 10.09.2006 19:47:32
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.1
 */
public abstract class AbstractNonNullNumberGenerator<E extends Number> extends AbstractNonNullGenerator<E> {

  /**
   * The Generated type.
   */
  protected final Class<E> generatedType;

  /**
   * The Min.
   */
  protected E min;
  /**
   * The Max.
   */
  protected E max;
  /**
   * The Granularity.
   */
  protected E granularity;

  // constructors ----------------------------------------------------------------------------------------------------

  /**
   * Instantiates a new Abstract non null number generator.
   *
   * @param generatedType the generated type
   * @param min           the min
   * @param max           the max
   * @param granularity   the granularity
   */
  public AbstractNonNullNumberGenerator(Class<E> generatedType, E min, E max, E granularity) {
    this.generatedType = generatedType;
    setMin(min);
    setMax(max);
    setGranularity(granularity);
  }

  // config properties -----------------------------------------------------------------------------------------------

  /**
   * Gets min.
   *
   * @return the min
   */
  public E getMin() {
    return NumberToNumberConverter.convert(min, generatedType);
  }

  /**
   * Sets min.
   *
   * @param min the min
   */
  public void setMin(E min) {
    this.min = min;
  }

  /**
   * Gets max.
   *
   * @return the max
   */
  public E getMax() {
    return NumberToNumberConverter.convert(max, generatedType);
  }

  /**
   * Sets max.
   *
   * @param max the max
   */
  public void setMax(E max) {
    this.max = max;
  }

  /**
   * Gets granularity.
   *
   * @return the granularity
   */
  public E getGranularity() {
    return NumberToNumberConverter.convert(granularity, generatedType);
  }

  /**
   * Sets granularity.
   *
   * @param granularity the granularity
   */
  public void setGranularity(E granularity) {
    this.granularity = granularity;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public Class<E> getGeneratedType() {
    return generatedType;
  }

  @Override
  public boolean isThreadSafe() {
    return true;
  }

  @Override
  public boolean isParallelizable() {
    return true;
  }

  @Override
  public void init(GeneratorContext context) {
    if (min != null && max != null && NumberComparator.compareNumbers(min, max) > 0) {
      throw new InvalidGeneratorSetupException("min (" + min + ") is greater than max (" + max + ")");
    }
    super.init(context);
  }

}
