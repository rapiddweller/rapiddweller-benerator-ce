/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.time.DateDuration;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests the {@link ZonedDateTimeGenerator}.<br/><br/>
 * Created: 28.07.2022 11:40:06
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ZonedDateTimeGeneratorTest extends GeneratorClassTest {

	public ZonedDateTimeGeneratorTest() {
		super(ZonedDateTimeGenerator.class);
	}

	@Test
	public void test() {
		LocalDate minDate = LocalDate.of(2022, 7, 28);
		LocalDate maxDate = LocalDate.of(2022, 12, 31);
		ZoneId zone = ZoneId.of("America/Chicago");
		LocalTime minTime = LocalTime.of(12, 0);
		LocalTime maxTime = LocalTime.of(13, 0);
		ZonedDateTimeGenerator g = new ZonedDateTimeGenerator(minDate, maxDate, minTime, maxTime, zone);
		g.setDateGranularity(DateDuration.of(0, 0, 7));
		g.setTimeGranularity(LocalTime.of(0, 5, 0));
		g.init(context);
		for (int i = 0; i < 1000; i++) {
			ZonedDateTime dateTime = g.generate();
			LocalDate date = dateTime.toLocalDate();
			assertFalse(date.isBefore(minDate));
			assertFalse(date.isAfter(maxDate));
			assertEquals(DayOfWeek.THURSDAY, dateTime.getDayOfWeek());
			LocalTime time = dateTime.toLocalTime();
			assertFalse(time.isBefore(minTime));
			assertFalse(time.isAfter(maxTime));
			assertEquals(0, time.getMinute() % 5);
			assertEquals(zone, dateTime.getZone());
		}
	}

}
