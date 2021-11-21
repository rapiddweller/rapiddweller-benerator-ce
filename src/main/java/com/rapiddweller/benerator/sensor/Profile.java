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

import com.rapiddweller.stat.LatencyCounter;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses a {@link LatencyCounter} to collect profile information and manages sub profiles.<br/><br/>
 * Created: 19.05.2011 09:08:27
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class Profile {
	
	private final String name;
	private final Profile parent;
	private final Map<String, Profile> subProfiles;
	private final LatencyCounter counter;
	private final DecimalFormat nf = new DecimalFormat("0");
	private final DecimalFormat df = new DecimalFormat("0.0");

	public Profile(String name, Profile parent) {
		this.parent = parent;
		this.name = name;
		this.counter = new LatencyCounter(name);
		this.subProfiles = new HashMap<>();
	}
	
	public String getName() {
		return name;
	}
	
	public Profile getParent() {
		return parent;
	}
	
	public Collection<Profile> getSubProfiles() {
		return subProfiles.values();
	}

	public Profile getOrCreateSubProfile(String name) {
		Profile result = subProfiles.get(name);
		if (result == null)
			result = createSubProfile(name);
		return result;
	}

	private Profile createSubProfile(String name) {
		Profile result = new Profile(name, this);
		subProfiles.put(name, result);
		return result;
	}

	public void addSample(int duration) {
		counter.addSample(duration);
	}

	public long getInvocationCount() {
		return counter.sampleCount();
	}
	
	public long getTotalLatency() {
		return counter.totalLatency();
	}

	public double getAverageLatency() {
		return counter.averageLatency();
	}

	@Override
	public String toString() {
		return "[" + nf.format(getInvocationCount()) + " inv., " +
				"avg: " + df.format(getAverageLatency()) + ", " +
				"total: " + nf.format(getTotalLatency()) + "]: " + 
				name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Profile that = (Profile) obj;
		return this.name.equals(that.name);
	}
	
}
