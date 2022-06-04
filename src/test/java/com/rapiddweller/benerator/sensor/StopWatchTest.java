/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.sensor;

import com.rapiddweller.common.ThreadUtil;
import com.rapiddweller.common.exception.IllegalOperationError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the StopWatch.<br/><br/>
 * Created: 04.06.2022 13:43:06
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class StopWatchTest {

	@Test
	public void test_regular_life_cycle() throws InterruptedException {
		StopWatch w = new StopWatch("test_reg");
		assertEquals("test_reg", w.getName());
		assertTrue(w.isRunning());
		Thread.sleep(100);
		assertTrue("Elapsed time " + w.elapsedTime() + " is less than expected", w.elapsedTime() > 80);
		Thread.sleep(100);
		int t = w.stop();
		assertTrue(t > 160);
	}

	@Test(expected = IllegalOperationError.class)
	public void test_stop_twice() {
		StopWatch w = new StopWatch("test_err");
		w.stop();
		w.stop();
	}

}
