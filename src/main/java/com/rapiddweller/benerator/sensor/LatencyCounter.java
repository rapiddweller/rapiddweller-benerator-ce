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

import com.rapiddweller.contiperf.clock.SystemClock;

import java.io.PrintWriter;

/**
 * Counts latencies and calculates performance-related statistics.<br/><br/>
 * Created: Created: 14.12.2006 18:11:58
 * @since 1.0
 * @author Volker Bergmann
 */
public final class LatencyCounter {
	
	private final String name;
	private final String clockName;
	
    private int minLatency;
    private int maxLatency;
    private long[] latencyCounts;

    private boolean running;
    private long startTime;
    private long endTime;
    private long sampleCount;
    private long totalLatency;

    public LatencyCounter(String name) {
        this(name, SystemClock.NAME, 1000);
    }

    public LatencyCounter(String name, String clockName, int expectedMaxLatency) {
    	this.name = name;
    	this.clockName = clockName;
        this.latencyCounts = new long[1 + expectedMaxLatency];
        this.sampleCount = 0;
        this.totalLatency = 0;
        this.minLatency = -1;
        this.maxLatency = -1;
        this.startTime = -1;
        this.endTime = -1;
    }
    
	public String getName() {
		return name;
	}
	
	public String getClockName() {
		return clockName;
	}
	
    // interface -------------------------------------------------------------------------------------------------------

    public void start() {
    	if (running)
    		throw new IllegalStateException(this + " has already been started");
    	this.startTime = System.currentTimeMillis();
    	this.running = true;
    }
    
    public synchronized void addSample(int latency) {
        if (latency >= latencyCounts.length)
            resize(latency);
        latencyCounts[latency]++;
        sampleCount++;
        totalLatency += latency;
        if (minLatency == -1 || latency < minLatency)
            minLatency = latency;
        if (latency > maxLatency)
            maxLatency = latency;
    }

    public void stop() {
    	if (!running)
    		throw new IllegalStateException("Stopping " + this + " which is not running");
    	this.running = false;
    	this.endTime = System.currentTimeMillis();
    }
    
	public boolean isRunning() {
		return running;
	}
    
	public long getStartTime() {
	    return startTime;
    }

    public long getLatencyCount(long latency) {
        if (latency < latencyCounts.length)
            return latencyCounts[(int) latency];
        else
            return 0;
    }

    public long totalLatency() {
        return totalLatency;
    }

    public double averageLatency() {
        return (double) totalLatency / sampleCount;
    }

    public long minLatency() {
        return Math.max(minLatency, 0);
    }

    public long maxLatency() {
        return Math.max(maxLatency, 0);
    }

    public long sampleCount() {
        return sampleCount;
    }

    public void setSampleCount(long sampleCount) {
      this.sampleCount = sampleCount;
    }

    public long percentileLatency(int percentile) {
        long targetCount = percentile * sampleCount / 100;
        long count = 0;
        for (long value = minLatency(); value <= maxLatency; value++) {
            count += getLatencyCount(value);
            if (count >= targetCount)
                return value;
        }
        return maxLatency;
    }
    
    public double percentileAboveLatency(int latency) {
        long count = 0;
        for (long value = (long) latency + 1; value <= maxLatency; value++)
            count += getLatencyCount(value);
        return (count * 100.) / sampleCount;
    }
    
    public double throughput() {
    	if (startTime == -1 || endTime == -1)
    		throw new IllegalArgumentException("Invalid setup: Use start() and stop() to indicate test start and end!");
    	return 1000. * sampleCount / duration();
    }

	public long duration() {
	    return endTime - startTime;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private void resize(int requestedIndex) {
        int sizingFactor = (requestedIndex + latencyCounts.length) / latencyCounts.length;
        int newLength = sizingFactor * latencyCounts.length;
        long[] newLatencyCounts = new long[newLength];
        System.arraycopy(latencyCounts, 0, newLatencyCounts, 0, latencyCounts.length);
        latencyCounts = newLatencyCounts;
    }

	public void printSummary(PrintWriter out, int... percentiles) {
    	out.println("samples: " + sampleCount);
    	out.println("max:     " + maxLatency());
    	out.println("average: " + averageLatency());
    	out.println("median:  " + percentileLatency(50));
    	for (int percentile : percentiles)
    		out.println(percentile + "%:     " + percentileLatency(percentile));
    	out.flush();
    }
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
