/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.time.DateDuration;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests the {@link LocalDateGenerator}.<br/><br/>
 * Created: 28.07.2022 09:11:36
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class LocalDateGeneratorTest extends GeneratorClassTest {

	public LocalDateGeneratorTest() {
		super(LocalDateGenerator.class);
	}

	@Test
	public void test() {
		LocalDate min = LocalDate.of(2022, 7, 28);
		LocalDate max = LocalDate.of(2022, 12, 31);
		LocalDateGenerator g = new LocalDateGenerator(min, max, DateDuration.of(0, 0, 7));
		g.init(context);
		for (int i = 0; i < 1000; i++) {
			LocalDate date = g.generate();
			assertFalse(date.isBefore(min));
			assertFalse(date.isAfter(max));
			assertEquals(DayOfWeek.THURSDAY, date.getDayOfWeek());
		}
	}

}
