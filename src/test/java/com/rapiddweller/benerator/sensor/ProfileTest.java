/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.sensor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link Profile}.<br/><br/>
 * Created: 02.06.2022 17:08:05
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ProfileTest {

	@Test
	public void test() {
		Profile profile = new Profile("profile", null);
		assertNull(profile.getParent());
		assertEquals("profile", profile.getName());
		assertTrue(profile.getSubProfiles().isEmpty());
		profile.addSample(10);
		profile.addSample(20);
		assertEquals(2, profile.getInvocationCount());
		assertEquals(30, profile.getTotalLatency());
		assertEquals(15, profile.getAverageLatency(), 0.001);
		assertEquals("profile[2 inv., avg lat: 15,0, total lat: 30]", profile.toString());
		assertEquals(-309425751, profile.hashCode());
		assertNotEquals(null, profile);
		assertNotEquals(profile, new Object());
		assertNotEquals(profile, new Profile("xxx", null));
		assertEquals(profile, profile);
		assertEquals(profile, new Profile("profile", null));
		Profile sub = profile.getOrCreateSubProfile("sub");
		assertEquals(profile, sub.getParent());
	}

}
