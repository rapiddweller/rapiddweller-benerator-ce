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
import com.rapiddweller.benerator.util.RandomUtil;
import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Generates random {@link BigDecimal}s with a uniform distribution.
 * <br/>
 * Created at 23.06.2009 23:36:15
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class RandomBigDecimalGenerator extends ThreadSafeNonNullGenerator<BigDecimal> {

  private static final BigDecimal DEFAULT_MIN = BigDecimal.valueOf(Double.MIN_VALUE);
  private static final BigDecimal DEFAULT_MAX = BigDecimal.valueOf(Double.MAX_VALUE);
  private static final BigDecimal DEFAULT_GRANULARITY = BigDecimal.valueOf(1);

  private final BigDecimal min;
  private final BigDecimal max;
  private final BigDecimal granularity;
  private final BigDecimal range;

  /**
   * Instantiates a new Random big decimal generator.
   */
  public RandomBigDecimalGenerator() {
    this(DEFAULT_MIN, DEFAULT_MAX);
  }

  /**
   * Instantiates a new Random big decimal generator.
   *
   * @param min the min
   * @param max the max
   */
  public RandomBigDecimalGenerator(BigDecimal min, BigDecimal max) {
    this(min, max, DEFAULT_GRANULARITY);
  }

  /**
   * Instantiates a new Random big decimal generator.
   *
   * @param min         the min
   * @param max         the max
   * @param granularity the granularity
   */
  public RandomBigDecimalGenerator(BigDecimal min, BigDecimal max, BigDecimal granularity) {
    this.min = min;
    this.max = max;
    this.granularity = granularity;
    BigDecimal tmp = max.subtract(min).divide(granularity);
    tmp = tmp.setScale(0, RoundingMode.DOWN);
    this.range = tmp.multiply(granularity);
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public Class<BigDecimal> getGeneratedType() {
    return BigDecimal.class;
  }

  @Override
  public synchronized void init(GeneratorContext context) {
    if (BigDecimal.ONE.compareTo(granularity) == 0) {
      throw new InvalidGeneratorSetupException(getClass().getSimpleName() + ".granularity may not be 0");
    }
    super.init(context);
  }

  @Override
  public BigDecimal generate() {
    long n = range.divide(granularity).longValue();
    BigDecimal i = BigDecimal.valueOf(RandomUtil.randomLong(0, n));
    return min.add(i.multiply(granularity));
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets min.
   *
   * @return the min
   */
  public BigDecimal getMin() {
    return min;
  }

  /**
   * Gets max.
   *
   * @return the max
   */
  public BigDecimal getMax() {
    return max;
  }

  /**
   * Gets granularity.
   *
   * @return the granularity
   */
  public BigDecimal getGranularity() {
    return granularity;
  }

}
