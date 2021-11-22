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

import com.rapiddweller.common.ThreadUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

/**
 * Tracks the consumed heap size.<br/><br/>
 * Created: 13.12.2012 13:31:56
 * @since 2.3.1
 * @author Volker Bergmann
 */
public class MemorySensor {
	
	private static final int DEFAULT_INTERVAL = 60000;
	
	private static final MemorySensor INSTANCE = new MemorySensor();
	
	public static MemorySensor getInstance() {
		return INSTANCE;
	}
	
	private MeasurementThread thread;
	private long maxUsedHeapSize;
	private long maxCommittedHeapSize;
	
	public MemorySensor() {
		startThread(DEFAULT_INTERVAL);
		reset();
	}

	public int getInterval() {
		return thread.getInterval();
	}
	
	public void setInterval(int interval) {
		if (interval != getInterval()) {
			thread.cancel();
			startThread(interval);
		}
	}
	
	public long getMaxUsedHeapSize() {
		return maxUsedHeapSize;
	}
	
	public long getMaxCommittedHeapSize() {
		return maxCommittedHeapSize;
	}
	
	public void reset() {
		maxUsedHeapSize = 0;
		maxCommittedHeapSize = 0;
		measure();
	}
	
	public void measure() {
		MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		this.maxUsedHeapSize = Math.max(maxUsedHeapSize, heapMemoryUsage.getUsed());
		this.maxCommittedHeapSize = Math.max(maxCommittedHeapSize, heapMemoryUsage.getCommitted());
	}
	
	private void startThread(int interval) {
		this.thread = new MeasurementThread(interval);
		thread.start();
	}
	
	class MeasurementThread extends Thread {
		
		private final int interval;
		
		public MeasurementThread(int interval) {
			super("Benerator-Memory-Sensor-Thread");
			this.interval = interval;
			setDaemon(true);
		}

		public int getInterval() {
			return interval;
		}

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					measure();
					ThreadUtil.sleepWithException(interval);
				}
			} catch (InterruptedException e) {
				// makes the thread leave the loop and finish
				if (Thread.interrupted()) {
					Thread.currentThread().interrupt();
				}
			}
		}
		
		public void cancel() {
			interrupt();
		}
		
	}
	
}
