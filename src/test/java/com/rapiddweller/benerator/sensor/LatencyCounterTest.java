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

import com.rapiddweller.common.exception.ProgrammerStateError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link com.rapiddweller.stat.LatencyCounter}.<br/><br/>
 * Created: 26.02.2012 18:31:16
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class LatencyCounterTest {
	
	@Test(expected = IllegalStateException.class)
	public void testStartTwice() {
		com.rapiddweller.stat.LatencyCounter counter = new com.rapiddweller.stat.LatencyCounter("test");
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
	public void testPercentileAboveLatency() {
		LatencyCounter counter = new LatencyCounter("test");
		counter.start();
		for (int i = 25; i <= 125; i += 25)
			counter.addSample(i);
		counter.stop();
		assertEquals(100., counter.percentileAboveLatency(0), 0.);
		assertEquals(80., counter.percentileAboveLatency(25), 0.);
		assertEquals(40., counter.percentileAboveLatency(99), 0.);
		assertEquals(20., counter.percentileAboveLatency(100), 0.);
		assertEquals(20., counter.percentileAboveLatency(124), 0.);
		assertEquals(0., counter.percentileAboveLatency(125), 0.);
		assertEquals(0., counter.percentileAboveLatency(126), 0.);
	}
	
}
