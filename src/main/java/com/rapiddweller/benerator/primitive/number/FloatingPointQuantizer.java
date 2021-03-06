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

import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.NumberToNumberConverter;
import com.rapiddweller.common.converter.ThreadSafeConverter;

/**
 * Quantizes floating point numbers ({@link Double} or {@link Float})
 * to be <code>min</code> plus an integral multiple of <code>granularity</code>.<br/><br/>
 * Created: 15.03.2010 15:35:05
 *
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class FloatingPointQuantizer<E extends Number> extends ThreadSafeConverter<E, E> {

  private final Double min;
  private final double granularity;
  private final NumberToNumberConverter<Double, E> converter;

  /**
   * Instantiates a new Floating point quantizer.
   *
   * @param numberType  the number type
   * @param min         the min
   * @param granularity the granularity
   */
  public FloatingPointQuantizer(Class<E> numberType, Double min, double granularity) {
    super(numberType, numberType);
    this.min = (min != null ? min : 0.);
    this.granularity = granularity;
    this.converter = new NumberToNumberConverter<>(Double.class, numberType);
  }

  @Override
  public E convert(E sourceValue) throws ConversionException {
    double l = Math.floor((sourceValue.doubleValue() - min) / granularity) * granularity + min;
    return converter.convert(l);
  }

}
