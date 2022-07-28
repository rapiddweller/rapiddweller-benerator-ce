/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorWrapper;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.converter.DateString2DurationConverter;
import com.rapiddweller.common.time.DateDuration;

import java.time.LocalDate;

/**
 * Generates {@link LocalDate}s.<br/><br/>
 * Created: 28.07.2022 08:10:03
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class LocalDateGenerator extends NonNullGeneratorWrapper<Integer, LocalDate> {

	private final DateString2DurationConverter dateConverter = new DateString2DurationConverter();

	private LocalDate min;
	private LocalDate max;
	private DateDuration granularity;
	private Distribution distribution;
	private final boolean unique;

	// constructors ----------------------------------------------------------------------------------------------------

	/** Initializes the generator to create LocalDates within about the last 80 years with a one-day resolution */
	public LocalDateGenerator() {
		this(defaultMinDate(), defaultMaxDate(), DateDuration.of(0, 0, 1));
	}

	/** Initializes the generator to create LocalDates with a uniform distribution */
	public LocalDateGenerator(LocalDate min, LocalDate max, DateDuration granularity) {
		this(min, max, granularity, SequenceManager.RANDOM_SEQUENCE);
	}

	/** Initializes the generator to create LocalDates of a Sequence or WeightFunction */
	public LocalDateGenerator(LocalDate min, LocalDate max, DateDuration granularity, Distribution distribution) {
		this(min, max, granularity, distribution, false);
	}

	/** Initializes the generator to create LocalDates of a Sequence or WeightFunction */
	public LocalDateGenerator(LocalDate min, LocalDate max, DateDuration granularity, Distribution distribution, boolean unique) {
		super(null);
		this.distribution = distribution;
		this.min = (min != null ? min : defaultMinDate());
		this.max = (max != null ? max : defaultMaxDate());
		this.granularity = granularity;
		this.unique = unique;
	}

	// config properties -----------------------------------------------------------------------------------------------

	/** Sets the earliest date to generate */
	public void setMin(LocalDate min) {
		this.min = min;
	}

	/** Sets the latest date to generate */
	public void setMax(LocalDate max) {
		this.max = max;
	}

	/** Sets the date granularity in days */
	@SuppressWarnings("unused")
	public void setGranularity(DateDuration granularity) {
		this.granularity = granularity;
	}

	/** Sets the distribution to use */
	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	// source interface ---------------------------------------------------------------------------------------------

	@Override
	public Class<LocalDate> getGeneratedType() {
		return LocalDate.class;
	}

	@Override
	public void init(GeneratorContext context) {
		assertNotInitialized();
		int maxOffset = granularity.countBetween(min, max);
		setSource(distribution.createNumberGenerator(Integer.class, 0, maxOffset, 1, this.unique));
		super.init(context);
	}

	/** Generates a Date by creating a millisecond value from the source generator and wrapping it into a Date */
	@Override
	public LocalDate generate() {
		assertInitialized();
		ProductWrapper<Integer> tmp = generateFromSource();
		if (tmp == null) {
			return null;
		}
		return granularity.plusDurations(min, tmp.unwrap());
	}

	@Override
	public boolean isThreadSafe() {
		return super.isThreadSafe() && dateConverter.isThreadSafe();
	}

	@Override
	public boolean isParallelizable() {
		return super.isParallelizable() && dateConverter.isParallelizable();
	}

	// implementation --------------------------------------------------------------------------------------------------

	/** Returns the default start date as 80 years ago */
	private static LocalDate defaultMinDate() {
		return LocalDate.now().minusYears(80);
	}

	private static LocalDate defaultMaxDate() {
		return LocalDate.now();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + getSource() + ']';
	}

}
