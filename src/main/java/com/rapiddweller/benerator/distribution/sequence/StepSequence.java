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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.wrapper.SkipGeneratorProxy;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.NumberUtil;

import java.math.BigDecimal;

import static com.rapiddweller.common.NumberUtil.toDouble;
import static com.rapiddweller.common.NumberUtil.toInteger;
import static com.rapiddweller.common.NumberUtil.toLong;

/**
 * Creates numbers by continuously incrementing a base value by a constant amount.<br/>
 * <br/>
 * Created at 30.06.2009 09:55:20
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class StepSequence extends Sequence {

  private BigDecimal delta;
  private final BigDecimal initial;
  private final BigDecimal limit;

  /**
   * Instantiates a new Step sequence.
   */
  public StepSequence() {
    this(null); // when using null, the granularity parameter will be used to set the increment in createGenerator
  }

  /**
   * Instantiates a new Step sequence.
   *
   * @param delta the increment to choose for created generators.              When using null, the granularity parameter will be used to set the increment              in {@link #createNumberGenerator(Class, Number, Number, Number, boolean)}
   */
  public StepSequence(BigDecimal delta) {
    this(delta, null);
  }

  /**
   * Instantiates a new Step sequence.
   *
   * @param delta   the delta
   * @param initial the initial
   */
  public StepSequence(BigDecimal delta, BigDecimal initial) {
    this(delta, initial, null);
  }

  /**
   * Instantiates a new Step sequence.
   *
   * @param delta   the delta
   * @param initial the initial
   * @param limit   the limit
   */
  public StepSequence(BigDecimal delta, BigDecimal initial, BigDecimal limit) {
    this.delta = delta;
    this.initial = initial;
    this.limit = limit;
  }

  /**
   * Sets delta.
   *
   * @param delta the delta
   */
  public void setDelta(BigDecimal delta) {
    this.delta = delta;
  }

  /**
   * Gets delta.
   *
   * @return the delta
   */
  public BigDecimal getDelta() {
    return delta;
  }

  /**
   * Gets initial.
   *
   * @return the initial
   */
  public BigDecimal getInitial() {
    return initial;
  }

  @Override
  public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
    int deltaToUse = (delta != null ? toInteger(delta) : 1);
    if (delta != null && delta.longValue() < 0) {
      return super.applyTo(source, unique);
    } else {
      return new SkipGeneratorProxy<>(source, deltaToUse, deltaToUse,
          SequenceManager.RANDOM_SEQUENCE, toInteger(limit));
    }
  }

  @Override
  public <T extends Number> NonNullGenerator<T> createNumberGenerator(
      Class<T> numberType, T min, T max, T granularity, boolean unique) {
    Number deltaToUse = deltaToUse(granularity);
    if (unique && deltaToUse.doubleValue() == 0) {
      throw new InvalidGeneratorSetupException("Can't generate unique numbers with an increment of 0.");
    }
    NonNullGenerator<? extends Number> base;
    if (BeanUtil.isIntegralNumberType(numberType)) {
      if (max == null) {
        max = NumberUtil.maxValue(numberType);
      }
      base = new StepLongGenerator(
          toLong(min), toLong(max), toLong(deltaToUse), toLong(initial));
    } else {
      base = new StepDoubleGenerator(
          toDouble(min), toDouble(max), toDouble(deltaToUse), toDouble(initial));
    }
    return WrapperFactory.asNonNullNumberGeneratorOfType(numberType, base, min, granularity);
  }

  private <T extends Number> Number deltaToUse(T granularity) {
    return (delta != null ? delta : (granularity != null ? granularity : 1));
  }

  @Override
  public String toString() {
    return BeanUtil.toString(this);
  }

}
