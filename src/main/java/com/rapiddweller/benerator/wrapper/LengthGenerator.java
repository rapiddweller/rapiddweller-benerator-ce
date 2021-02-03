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
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;

/**
 * Abstract parent class for {@link Generator}s that generate objects of a variable length.<br/><br/>
 * Created: 01.08.2011 11:34:58
 *
 * @param <S> the type parameter
 * @param <P> the type parameter
 * @author Volker Bergmann
 * @since 0.7.0
 */
public abstract class LengthGenerator<S, P> extends CardinalGenerator<S, P> {

  /**
   * Instantiates a new Length generator.
   *
   * @param source                   the source
   * @param resettingLengthGenerator the resetting length generator
   */
  public LengthGenerator(Generator<S> source, boolean resettingLengthGenerator) {
    super(source, resettingLengthGenerator);
  }

  /**
   * Instantiates a new Length generator.
   *
   * @param source          the source
   * @param resettingLength the resetting length
   * @param lengthGenerator the length generator
   */
  public LengthGenerator(Generator<S> source, boolean resettingLength, NonNullGenerator<Integer> lengthGenerator) {
    super(source, resettingLength, lengthGenerator);
  }

  /**
   * Instantiates a new Length generator.
   *
   * @param source                   the source
   * @param resettingLengthGenerator the resetting length generator
   * @param minLength                the min length
   * @param maxLength                the max length
   * @param lengthGranularity        the length granularity
   * @param lengthDistribution       the length distribution
   */
  public LengthGenerator(Generator<S> source,
                         boolean resettingLengthGenerator, int minLength, int maxLength,
                         int lengthGranularity, Distribution lengthDistribution) {
    super(source, resettingLengthGenerator, minLength, maxLength, lengthGranularity, lengthDistribution);
  }

  /**
   * Gets min length.
   *
   * @return the min length
   */
  public int getMinLength() {
    return minCardinal;
  }

  /**
   * Sets min length.
   *
   * @param minLength the min length
   */
  public void setMinLength(int minLength) {
    this.minCardinal = minLength;
  }

  /**
   * Gets max length.
   *
   * @return the max length
   */
  public int getMaxLength() {
    return maxCardinal;
  }

  /**
   * Sets max length.
   *
   * @param maxLength the max length
   */
  public void setMaxLength(int maxLength) {
    this.maxCardinal = maxLength;
  }

  /**
   * Gets length granularity.
   *
   * @return the length granularity
   */
  public int getLengthGranularity() {
    return cardinalGranularity;
  }

  /**
   * Sets length granularity.
   *
   * @param lengthGranularity the length granularity
   */
  public void setLengthGranularity(int lengthGranularity) {
    this.cardinalGranularity = lengthGranularity;
  }

  /**
   * Gets length distribution.
   *
   * @return the length distribution
   */
  public Distribution getLengthDistribution() {
    return cardinalDistribution;
  }

  /**
   * Sets length distribution.
   *
   * @param lengthDistribution the length distribution
   */
  public void setLengthDistribution(Distribution lengthDistribution) {
    this.cardinalDistribution = lengthDistribution;
  }

}
