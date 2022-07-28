/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link CurrentZonedDateTimeGenerator}.<br/><br/>
 * Created: 05.07.2022 11:12:17
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CurrentZonedDateTimeGeneratorTest extends GeneratorTest {

	@Test
	public void testDefault() {
		ZonedDateTime startDt = ZonedDateTime.now();
		CurrentZonedDateTimeGenerator generator = new CurrentZonedDateTimeGenerator();
		generator.init(context);
		ZonedDateTime generatedDt = generator.generate();
		assertFalse(startDt.isAfter(generatedDt));
		ZonedDateTime toleratedLimit = startDt.plusSeconds(2);
		assertTrue(generatedDt.isBefore(toleratedLimit));
	}

	@Test
	public void testLondon() {
		testInZone("Europe/London");
	}

	@Test
	public void testBerlin() {
		testInZone("Europe/Berlin");
	}

	@Test
	public void testChicago() {
		testInZone("America/Chicago");
	}

	public void testInZone(String zoneIdId) {
		ZoneId zoneId = ZoneId.of(zoneIdId);
		ZonedDateTime startDt = ZonedDateTime.now(zoneId);
		CurrentZonedDateTimeGenerator generator = new CurrentZonedDateTimeGenerator(zoneIdId);
		generator.init(context);
		ZonedDateTime generatedDt = generator.generate();
		assertFalse(startDt.isAfter(generatedDt));
		ZonedDateTime toleratedLimit = startDt.plusSeconds(2);
		assertTrue(generatedDt.isBefore(toleratedLimit));
	}

}
