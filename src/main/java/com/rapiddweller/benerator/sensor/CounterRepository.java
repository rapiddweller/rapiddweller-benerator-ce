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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Repository that binds {@link LatencyCounter}s to a name and makes them available to clients.<br/><br/>
 * Created: 14.01.2011 11:26:09
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class CounterRepository {
	
	private final Map<String, LatencyCounter> counters;



	// construction and singleton management ---------------------------------------------------------------------------
	
	private static final CounterRepository INSTANCE = new CounterRepository();
	
	private CounterRepository() {
		counters = new HashMap<>();
	}

	public static CounterRepository getInstance() {
		return INSTANCE;
	}



	// CounterRepository interface -------------------------------------------------------------------------------------
	
	public void addSample(String name, int latency) {
		LatencyCounter counter = getOrCreateCounter(name);
		counter.addSample(latency);
	}

	public LatencyCounter getCounter(String name) {
		return counters.get(name);
	}
	
	public Set<Map.Entry<String, LatencyCounter>> getCounters() {
		return counters.entrySet();
	}
	
	public void clear() {
		counters.clear();
	}

	public void printSummary() {
		DecimalFormat df = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
		List<String[]> list = new ArrayList<>(counters.size());
		List<LatencyCounter> sortedCounters = new ArrayList<>(counters.values());
		sortedCounters.sort((c1, c2) -> -Long
				.compare(c1.totalLatency(), c2.totalLatency()));
		for (LatencyCounter counter : sortedCounters) {
			list.add(new String[] {
					counter.getName() + ":", 
					counter.totalLatency() + " ms total,", 
					counter.sampleCount() + " inv,", 
					df.format(counter.averageLatency()) + " ms/inv (avg.)" });
		}
		printSummaryTable(list);
	}



	// helper methods --------------------------------------------------------------------------------------------------
	
	private LatencyCounter getOrCreateCounter(String name) {
		LatencyCounter counter = getCounter(name);
		if (counter == null)
			counter = createCounter(name);
		return counter;
	}

	private synchronized LatencyCounter createCounter(String name) {
		LatencyCounter counter = getCounter(name);
		if (counter == null) {
			counter = new LatencyCounter(name);
			counters.put(name, counter);
		}
		return counter;
	}

	private static void printSummaryTable(List<String[]> list) {
		// determine column widths
		int[] widths = new int[4];
		for (int col = 0; col < 4; col++) {
			int width = 0;
			for (String[] strings : list)
				width = Math.max(width, strings[col].length());
			widths[col] = width;
		}
		// print rows
		for (String[] strings : list) {
			for (int col = 0; col < 4; col++) {
				String text = strings[col];
				if (col > 0) {
					pad(widths[col] - text.length());
					System.out.print(text);
				} else {
					System.out.print(text);
					pad(widths[col] - text.length());
				}
				if (col < 3)
					System.out.print(' ');
			}
			System.out.println();
		}
	}

	private static void pad(int count) {
		for (int i = 0; i < count; i++)
			System.out.print(' ');
	}

}
