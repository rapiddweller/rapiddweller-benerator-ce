/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests the integration of {@link ZonedDateTimeGenerator}.<br/><br/>
 * Created: 28.07.2022 12:17:51
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ZonedDateTimeGeneratorIntegrationTest extends AbstractBeneratorIntegrationTest {

	private static final String XML = "<setup xmlns=\"https://www.benerator.de/schema/3.0.0\">\n" +
		"    <bean id=\"zdtGen\" class=\"ZonedDateTimeGenerator\">\n" +
		"        <property name='minDate' value='2020-01-02'/>\n" +
		"        <property name='maxDate' value='2020-11-30'/>\n" +
		"        <property name='dateGranularity' value='0-0-7'/>\n" +
		"        <property name='minTime' value='09:00'/>\n" +
		"        <property name='maxTime' value='12:00'/>\n" +
		"        <property name='timeGranularity' value='00:05:00'/>\n" +
		"        <property name='zone' value='America/Chicago'/>\n" +
		"    </bean>\n" +
		"\n" +
		"    <memstore id='mem'/>\n" +
		"\n" +
		"    <generate type='x' count='1000' consumer='mem'>'\n" +
		"        <attribute name='y' type='zoneddatetime' generator='zdtGen'/>\n" +
		"    </generate>\n" +
		"</setup>";

	@Test
	public void test() {
		BeneratorContext context = parseAndExecuteXmlString(XML);
		MemStore mem = (MemStore) context.get("mem");
		List<Entity> products = mem.getEntities("x");
		assertEquals(1000, products.size());
		LocalDate minDate = LocalDate.of(2020, 1, 2);
		LocalDate maxDate = LocalDate.of(2020, 11, 30);
		ZoneId zone = ZoneId.of("America/Chicago");
		LocalTime minTime = LocalTime.of( 9, 0);
		LocalTime maxTime = LocalTime.of(12, 0);
		for (Entity product : products) {
			ZonedDateTime dateTime = (ZonedDateTime) product.get("y");
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
