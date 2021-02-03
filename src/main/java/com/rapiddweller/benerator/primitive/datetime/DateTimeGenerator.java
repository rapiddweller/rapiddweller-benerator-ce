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

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.model.data.Uniqueness;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Creates DateTimes with separate date and time distribution characteristics.<br/><br/>
 * Created: 29.02.2008 18:19:55
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class DateTimeGenerator extends CompositeGenerator<Date> implements NonNullGenerator<Date> {

  private DayGenerator dateGenerator;
  private Generator<Long> timeOffsetGenerator;

  /**
   * The Min date.
   */
  Date minDate;
  /**
   * The Max date.
   */
  Date maxDate;
  /**
   * The Date granularity.
   */
  String dateGranularity;
  /**
   * The Date distribution.
   */
  Distribution dateDistribution;

  /**
   * The Min time.
   */
  long minTime;
  /**
   * The Max time.
   */
  long maxTime;
  /**
   * The Time granularity.
   */
  long timeGranularity;
  /**
   * The Time distribution.
   */
  Distribution timeDistribution;
  private final WrapperProvider<Long> timeWrapperProvider = new WrapperProvider<>();

  /**
   * Instantiates a new Date time generator.
   */
  public DateTimeGenerator() {
    this(
        TimeUtil.add(TimeUtil.today(), Calendar.YEAR, -1),
        TimeUtil.today(),
        TimeUtil.time(9, 0),
        TimeUtil.time(17, 0));
  }

  /**
   * Instantiates a new Date time generator.
   *
   * @param minDate the min date
   * @param maxDate the max date
   * @param minTime the min time
   * @param maxTime the max time
   */
  public DateTimeGenerator(Date minDate, Date maxDate, Time minTime, Time maxTime) {
    super(Date.class);
    setMinDate(minDate);
    setMaxDate(maxDate);
    setMinTime(minTime);
    setMaxTime(maxTime);
    setDateDistribution(SequenceManager.RANDOM_SEQUENCE);
    setTimeDistribution(SequenceManager.RANDOM_SEQUENCE);
    setDateGranularity("00-00-01");
    setTimeGranularity(TimeUtil.time(0, 1));
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Sets min date.
   *
   * @param minDate the min date
   */
  public void setMinDate(Date minDate) {
    this.minDate = minDate;
  }

  /**
   * Sets max date.
   *
   * @param maxDate the max date
   */
  public void setMaxDate(Date maxDate) {
    this.maxDate = maxDate;
  }

  /**
   * Sets date granularity.
   *
   * @param dateGranularity the date granularity
   */
  public void setDateGranularity(String dateGranularity) {
    this.dateGranularity = dateGranularity;
  }

  /**
   * Sets date distribution.
   *
   * @param distribution the distribution
   */
  public void setDateDistribution(Distribution distribution) {
    this.dateDistribution = distribution;
  }

  /**
   * Sets min time.
   *
   * @param minTime the min time
   */
  public void setMinTime(Time minTime) {
    this.minTime = TimeUtil.millisSinceOwnEpoch(minTime);
  }

  /**
   * Sets max time.
   *
   * @param maxTime the max time
   */
  public void setMaxTime(Time maxTime) {
    this.maxTime = TimeUtil.millisSinceOwnEpoch(maxTime);
  }

  /**
   * Sets time granularity.
   *
   * @param timeGranularity the time granularity
   */
  public void setTimeGranularity(Time timeGranularity) {
    this.timeGranularity = TimeUtil.millisSinceOwnEpoch(timeGranularity);
  }

  /**
   * Sets time distribution.
   *
   * @param distribution the distribution
   */
  public void setTimeDistribution(Distribution distribution) {
    this.timeDistribution = distribution;
  }

  // Generator interface ---------------------------------------------------------------------------------------------

  @Override
  public void init(GeneratorContext context) {
    assertNotInitialized();
    this.dateGenerator = registerComponent(
        new DayGenerator(minDate, maxDate, dateDistribution, false));
    dateGenerator.setGranularity(dateGranularity);
    this.dateGenerator.init(context);
    this.timeOffsetGenerator = registerComponent(context.getGeneratorFactory().createNumberGenerator(
        Long.class, minTime, true, maxTime, true, timeGranularity, timeDistribution, Uniqueness.NONE));
    this.timeOffsetGenerator.init(context);
    super.init(context);
  }

  @Override
  public ProductWrapper<Date> generate(ProductWrapper<Date> wrapper) {
    Date result = generate();
    return (result != null ? wrapper.wrap(result) : null);
  }

  @Override
  public Date generate() {
    assertInitialized();
    Date dateGeneration = dateGenerator.generate();
    if (dateGeneration == null) {
      return null;
    }
    ProductWrapper<Long> timeWrapper = timeOffsetGenerator.generate(timeWrapperProvider.get());
    if (timeWrapper == null) {
      return null;
    }
    long timeOffsetGeneration = timeWrapper.unwrap();
    return new Date(dateGeneration.getTime() + timeOffsetGeneration);
  }

}
