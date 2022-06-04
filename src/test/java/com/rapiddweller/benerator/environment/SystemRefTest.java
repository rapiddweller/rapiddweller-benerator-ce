/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.environment;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link SystemRef} class.<br/><br/>
 * Created: 04.06.2022 14:35:38
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SystemRefTest {

	@Test
	public void test() {
		Environment environment = new Environment("testenv");
		Map<String, String> properties = new HashMap<>();
		properties.put("x", "y");
		SystemRef system = new SystemRef(environment, "sys", "db", properties);
		assertSame(environment, system.getEnvironment());
		assertEquals("sys", system.getName());
		assertEquals("db", system.getType());
		assertSame(properties, system.getProperties());
		assertEquals("y", system.getProperty("x"));
		assertTrue(system.isDb());
		assertFalse(system.isKafka());
		assertEquals("testenv#sys", system.toString());
		assertEquals(-1171138454, system.hashCode());
		// testing equals method
		assertEquals(system, system);
		assertNotEquals(system, null);
		assertNotEquals(system, "string");
		assertNotEquals(system, new SystemRef(environment, "xxx", "db", new HashMap<>()));
		assertNotEquals(system, new SystemRef(environment, "sys", "kafka", new HashMap<>()));
	}

}
