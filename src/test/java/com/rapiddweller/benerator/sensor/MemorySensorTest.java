/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.sensor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link MemorySensor}.<br/><br/>
 * Created: 23.12.2021 11:10:20
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class MemorySensorTest {

	@Test
	public void testInstantiation() {
		MemorySensor sensor = MemorySensor.getInstance();
		sensor.measure();
		assertEquals(60000, sensor.getInterval());
	}

	@Test
	public void testInterval() {
		MemorySensor sensor = MemorySensor.getInstance();
		assertEquals(60000, sensor.getInterval());
		sensor.setInterval(30000);
		assertEquals(30000, sensor.getInterval());
	}

	@Test
	public void testAccess() {
		MemorySensor sensor = MemorySensor.getInstance();
		sensor.measure();
		assertTrue(sensor.getMaxCommittedHeapSize() >= 0);
		assertTrue(sensor.getMaxUsedHeapSize() >= 0);
	}

	@Test
	public void testReset() {
		MemorySensor sensor = MemorySensor.getInstance();
		sensor.measure();
		sensor.reset();
		sensor.measure();
		assertTrue(sensor.getMaxCommittedHeapSize() >= 0);
		assertTrue(sensor.getMaxUsedHeapSize() >= 0);
	}

}
