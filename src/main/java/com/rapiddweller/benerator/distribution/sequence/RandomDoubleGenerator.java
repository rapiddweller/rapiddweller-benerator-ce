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

import com.rapiddweller.benerator.primitive.number.AbstractNonNullNumberGenerator;
import com.rapiddweller.benerator.util.RandomUtil;

/**
 * Double Generator that implements a 'random' Double Sequence.<br/>
 * <br/>
 * Created: 11.06.2006 07:55:54
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class RandomDoubleGenerator extends AbstractNonNullNumberGenerator<Double> {

  /**
   * Instantiates a new Random double generator.
   */
  public RandomDoubleGenerator() {
    this(Double.MIN_VALUE, Double.MAX_VALUE);
  }

  /**
   * Instantiates a new Random double generator.
   *
   * @param min the min
   * @param max the max
   */
  public RandomDoubleGenerator(double min, double max) {
    this(min, max, 1);
  }

  /**
   * Instantiates a new Random double generator.
   *
   * @param min         the min
   * @param max         the max
   * @param granularity the granularity
   */
  public RandomDoubleGenerator(double min, double max, double granularity) {
    super(Double.class, min, max, granularity);
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public Double generate() {
    if (granularity == 0) {
      return min + Math.random() * (max - min);
    }
    int n = (int) ((max - min) / granularity);
    return min + RandomUtil.randomInt(0, n) * granularity;
  }

}
