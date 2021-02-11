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

package com.rapiddweller.benerator.demo;

import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.factory.StochasticGeneratorFactory;

import static com.rapiddweller.benerator.util.GeneratorUtil.*;

import com.rapiddweller.benerator.util.UnsafeNonNullGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.model.data.Uniqueness;

/**
 * Demonstrates definition and use of the custom Sequence 'odd'
 * by an example that generates a sequence of odd numbers:
 * 3, 5, 7, ...<br/>
 * <br/>
 * Created: 13.09.2006 20:27:54
 *
 * @author Volker Bergmann
 */
public class CustomSequenceDemo {

  /**
   * Defines the Sequence 'odd', creates an Integer generator that acceses it and invokes the generator 10 times
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    Sequence odd = new OddNumberSequence();
    Generator<Integer> generator = new StochasticGeneratorFactory()
        .createNumberGenerator(Integer.class, 3, true,
            Integer.MAX_VALUE, true, 2, odd, Uniqueness.NONE);
    init(generator);
    for (int i = 0; i < 10; i++) {
      System.out.println(generateNonNull(generator));
    }
    close(generator);
  }

  /**
   * The custom Sequence implementation
   */
  public static class OddNumberSequence extends Sequence {

    /**
     * Create number generator non null generator.
     *
     * @param <T>         the type parameter
     * @param numberType  the number type
     * @param min         the min
     * @param max         the max
     * @param granularity the granularity
     * @param unique      the unique
     * @return the non null generator
     */
    public <T extends Number> NonNullGenerator<T> createNumberGenerator(
        Class<T> numberType, T min, T max, T granularity,
        boolean unique) {
      OddNumberGenerator doubleGenerator =
          new OddNumberGenerator(min.doubleValue(),
              max.doubleValue());
      return WrapperFactory
          .asNonNullNumberGeneratorOfType(numberType, doubleGenerator,
              min, granularity);
    }
  }

  /**
   * The type Odd number generator.
   */
  public static class OddNumberGenerator
      extends UnsafeNonNullGenerator<Double> {

    private double min;
    private double max;
    private double granularity;

    private double next;

    /**
     * Instantiates a new Odd number generator.
     *
     * @param min the min
     * @param max the max
     */
    public OddNumberGenerator(double min, double max) {
      this(min, max, null);
    }

    /**
     * Instantiates a new Odd number generator.
     *
     * @param min         the min
     * @param max         the max
     * @param granularity the granularity
     */
    public OddNumberGenerator(double min, double max, Double granularity) {
      this.min = min;
      this.max = max;
      this.granularity = (granularity != null ? granularity : 2);
      this.next = min;
    }

    // Generator interface implementation --------------------------------------------------------------------------

    /**
     * Gets generated type.
     *
     * @return the generated type
     */
    public Class<Double> getGeneratedType() {
      return Double.class;
    }

    /**
     * Generate double.
     *
     * @return the double
     */
    @Override
    public Double generate() {
      if (next >= max) {
        return null;
      }
      double result = next;
      next += granularity;
      return result;
    }

    /**
     * Reset.
     */
    @Override
    public void reset() {
      next = min;
    }

  }

}
