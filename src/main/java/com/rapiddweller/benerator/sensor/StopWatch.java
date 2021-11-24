/*
 * Copyright (C) 2011-2021 Volker Bergmann (volker.bergmann@bergmann-it.de).
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

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.stat.CounterRepository;
import com.rapiddweller.stat.LatencyCounter;

/**
 * Stopwatch-style access to ContiPerf's {@link LatencyCounter} features.
 * A StopWach is created with a name and immediately starts measuring time.
 * When calling stop(), the elapsed time is registered at a central latency 
 * counter identified by the stopwatch's name.
 * <pre>
 *     StopWatch watch = new StopWatch("mytest");
 *     Thread.sleep(delay);
 *     watch.stop();
 * </pre>
 * You can use a stop watch only a single time, so you have to create a new 
 * instance for each measurement you are performing.
 * After the desired number of invocations, you can query the associated 
 * {@link LatencyCounter} from the CounterRepository and query its features, 
 * e.g.
 * <pre>
 *     LatencyCounter counter = CounterRepository.getInstance("mytest");
 *     System.out.println("avg:" + counter.averageLatency + ", max:" + counter.maxLatency())
 * </pre>
 * <br/><br/>
 * Created: 14.01.2011 11:17:30
 * @since 2.0.0
 * @author Volker Bergmann
 * @see CounterRepository
 * @see LatencyCounter
 */
public class StopWatch {

	private final String name;
	private long startTime;
	
	public StopWatch(String name) {
		this.name = name;
		this.startTime = System.nanoTime();
	}

	public String getName() {
		return name;
	}

	public int elapsedTime() {
		return (startTime == -1 ? -1 : (int) ((System.nanoTime() - startTime) / 1000000L));
	}

	public boolean isRunning() {
		return (startTime != -1);
	}

	public int stop() {
		if (startTime == -1)
			throw BeneratorExceptionFactory.getInstance().illegalOperation(
					"Called stop() on StopWatch '" + name + "' which has already been stopped");
		int latency = (int) ((System.nanoTime() - startTime) / 1000000L);
		startTime = -1;
		CounterRepository.getInstance().addSample(name, latency);
		return latency;
	}
	
}
