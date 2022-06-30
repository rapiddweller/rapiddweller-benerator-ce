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

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.RandomProvider;
import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;
import org.apache.commons.math3.util.Precision;

/**
 * Double Generator that implements a 'random' Double Sequence.<br/><br/>
 * Created: 11.06.2006 07:55:54
 * @author Volker Bergmann
 * @since 0.1
 */
public class RandomDoubleGenerator extends AbstractNonNullNumberGenerator<Double> {

  private final int decimalPlaces;
  private RandomProvider random;

  public RandomDoubleGenerator() {
    this(Double.MIN_VALUE, Double.MAX_VALUE);
  }

  public RandomDoubleGenerator(double min, double max) {
    this(min, max, 1);
  }

  public RandomDoubleGenerator(double min, double max, double granularity) {
    super(Double.class, min, max, granularity);
    String granularityText = Double.toString(Math.abs(granularity));
    int integerPlaces = granularityText.indexOf('.');
    decimalPlaces = granularityText.length() - integerPlaces - 1;
    this.random = BeneratorFactory.getInstance().getRandomProvider();
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public Double generate() {
    // generate an offset value which will include 'max' if a granularity is defined.
    // Otherwise granularity is 0 and its addition does not matter.
    double offset = random.randomDouble() * (max - min + granularity);
    if (granularity > 0) {
      // if generation is granular, then apply it to the offset (not the base!)
      offset = Math.floor(offset / granularity) * granularity;
    }
    return Precision.round(min + offset, decimalPlaces);
  }

}
