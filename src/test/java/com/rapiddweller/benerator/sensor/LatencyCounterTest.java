/*
 * Copyright (C) 2011-2014 Volker Bergmann (volker.bergmann@bergmann-it.de).
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rapiddweller.benerator.sensor;

import com.rapiddweller.common.ThreadUtil;
import com.rapiddweller.common.exception.IllegalOperationError;
import com.rapiddweller.common.exception.ProgrammerStateError;
import com.rapiddweller.common.ui.ConsolePrinter;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link com.rapiddweller.stat.LatencyCounter}.<br/><br/>
 * Created: 26.02.2012 18:31:16
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class LatencyCounterTest {

	@Test
	public void testNameAndClock() {
		LatencyCounter counter = new LatencyCounter("test");
		assertEquals("test", counter.getName());
		assertEquals("system", counter.getClockName());
	}

	@Test
	public void testIsRunning() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		assertNotEquals(counter.getStartTime(), 0L);
		assertTrue(counter.isRunning());
		counter.stop();
		assertFalse(counter.isRunning());
	}

	@Test(expected = IllegalOperationError.class)
	public void testThroughputWithoutStart() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.throughput();
	}

	@Test(expected = IllegalOperationError.class)
	public void testStartTwice() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		counter.start();
	}

	@Test(expected = ProgrammerStateError.class)
	public void testStopTwice() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		counter.stop();
		counter.stop();
	}

	@Test
	public void testGetLatencyCount() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		for (int i = 25; i <= 1025; i += 25) {
			counter.addSample(i);
		}
		counter.stop();
		assertEquals(41, counter.sampleCount());
		assertEquals(0, counter.getLatencyCount(24));
		assertEquals(1, counter.getLatencyCount(25));
		assertEquals(1, counter.getLatencyCount(1025));
		assertEquals(0, counter.getLatencyCount(1026));
		assertEquals(0, counter.getLatencyCount(50000));
	}

	@Test
	public void testPercentileAboveLatency() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		for (int i = 25; i <= 125; i += 25)
			counter.addSample(i);
		ThreadUtil.sleepIgnoringException(500);
		counter.stop();
		assertEquals(100., counter.percentileAboveLatency(0), 0.);
		assertEquals(80., counter.percentileAboveLatency(25), 0.);
		assertEquals(40., counter.percentileAboveLatency(99), 0.);
		assertEquals(20., counter.percentileAboveLatency(100), 0.);
		assertEquals(20., counter.percentileAboveLatency(124), 0.);
		assertEquals(0., counter.percentileAboveLatency(125), 0.);
		assertEquals(0., counter.percentileAboveLatency(126), 0.);
		assertTrue(counter.throughput() > 0);
		assertTrue(counter.duration() >= 500);
	}

	@Test
	public void testSetSampleCount() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		for (int i = 10; i <= 13; i++)
			counter.addSample(i);
		counter.stop();
		counter.setSampleCount(1500);
		assertEquals(1500, counter.sampleCount());
	}

	@Test
	public void testAvgAndTotalLatency() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		for (int i = 10; i <= 13; i++)
			counter.addSample(i);
		counter.stop();
		assertEquals(46, counter.totalLatency());
		assertEquals(11.5, counter.averageLatency(), 0.001);
		assertEquals(10, counter.minLatency());
		assertEquals(13, counter.maxLatency());
	}

	@Test
	public void testPercentileLatency() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		for (int i = 10; i <= 13; i++)
			counter.addSample(i);
		counter.stop();
		assertEquals(10, counter.percentileLatency(0));
		assertEquals(11, counter.percentileLatency(50));
		assertEquals(13, counter.percentileLatency(100));
		assertEquals(13, counter.percentileLatency(110));
	}

	@Test
	public void testPrintSummary() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		for (int i = 10; i <= 13; i++)
			counter.addSample(i);
		counter.stop();
		StringWriter sw = new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		counter.printSummary(out, 50);
		String text = sw.toString();
		ConsolePrinter.printStandard(text);
		assertEquals("samples: 4\nmax:     13\naverage: 11.5\nmedian:  11\n50%:     11\n", text);
	}

}
