/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.time.DateDuration;
import com.rapiddweller.model.data.Uniqueness;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Generates {@link ZonedDateTime} data with configurable date and time distributions in a fixed time zone.<br/><br/>
 * Created: 28.07.2022 08:07:03
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ZonedDateTimeGenerator extends CompositeGenerator<ZonedDateTime> implements NonNullGenerator<ZonedDateTime> {

	LocalDate minDate;
	LocalDate maxDate;
	DateDuration dateGranularity;
	Distribution dateDistribution;

	long minTime;
	long maxTime;
	long timeGranularity;
	Distribution timeDistribution;

	ZoneId zone;

	private LocalDateGenerator dateGenerator;
	private Generator<Long> timeOffsetGenerator;
	private final WrapperProvider<Long> timeWrapperProvider = new WrapperProvider<>();

	public ZonedDateTimeGenerator() {
		this(
			LocalDate.now().minusYears(1),
			LocalDate.now(),
			LocalTime.of(9, 0),
			LocalTime.of(17, 0),
			ZoneId.systemDefault());
	}

	public ZonedDateTimeGenerator(LocalDate minDate, LocalDate maxDate, LocalTime minTime, LocalTime maxTime, ZoneId zone) {
		super(ZonedDateTime.class);
		setMinDate(minDate);
		setMaxDate(maxDate);
		setMinTime(minTime);
		setMaxTime(maxTime);
		setDateDistribution(SequenceManager.RANDOM_SEQUENCE);
		setTimeDistribution(SequenceManager.RANDOM_SEQUENCE);
		setDateGranularity(DateDuration.of("0-0-1"));
		setTimeGranularity(LocalTime.of(0, 1));
		setZone(zone);
	}

	// properties ------------------------------------------------------------------------------------------------------

	public void setMinDate(LocalDate minDate) {
		this.minDate = minDate;
	}

	public void setMaxDate(LocalDate maxDate) {
		this.maxDate = maxDate;
	}

	public void setDateGranularity(DateDuration dateGranularity) {
		this.dateGranularity = dateGranularity;
	}

	public void setDateDistribution(Distribution distribution) {
		this.dateDistribution = distribution;
	}

	public void setMinTime(LocalTime minTime) {
		this.minTime = minTime.toNanoOfDay();
	}

	public void setMaxTime(LocalTime maxTime) {
		this.maxTime = maxTime.toNanoOfDay();
	}

	public void setTimeGranularity(LocalTime timeGranularity) {
		this.timeGranularity = timeGranularity.toNanoOfDay();
	}

	public void setTimeDistribution(Distribution distribution) {
		this.timeDistribution = distribution;
	}

	public void setZone(ZoneId zone) {
		this.zone = zone;
	}

	// Generator interface ---------------------------------------------------------------------------------------------

	@Override
	public void init(GeneratorContext context) {
		assertNotInitialized();
		this.dateGenerator = registerComponent(
			new LocalDateGenerator(minDate, maxDate, dateGranularity, dateDistribution, false));
		dateGenerator.setGranularity(dateGranularity);
		this.dateGenerator.init(context);
		this.timeOffsetGenerator = registerComponent(context.getGeneratorFactory().createNumberGenerator(
			Long.class, minTime, true, maxTime, true, timeGranularity, timeDistribution, Uniqueness.NONE));
		this.timeOffsetGenerator.init(context);
		super.init(context);
	}

	@Override
	public ProductWrapper<ZonedDateTime> generate(ProductWrapper<ZonedDateTime> wrapper) {
		ZonedDateTime result = generate();
		return (result != null ? wrapper.wrap(result) : null);
	}

	@Override
	public ZonedDateTime generate() {
		assertInitialized();
		LocalDate dateGeneration = dateGenerator.generate();
		if (dateGeneration == null) {
			return null;
		}
		ProductWrapper<Long> timeWrapper = timeOffsetGenerator.generate(timeWrapperProvider.get());
		if (timeWrapper == null) {
			return null;
		}
		long nanoOfDay = timeWrapper.unwrap();
		LocalTime time = LocalTime.ofNanoOfDay(nanoOfDay);
		return ZonedDateTime.of(dateGeneration, time, zone);
	}

}
