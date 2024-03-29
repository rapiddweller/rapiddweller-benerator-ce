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

/**
 * Encapsulates access to Profiling configuration.<br/><br/>
 * Created: 21.07.2011 08:28:43
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class Profiling {

	/** private constructor to prevent instantiation of this utility class. */
	private Profiling() {
		// private constructor to prevent instantiation of this utility class
	}

	public static boolean isEnabled() {
		String config = System.getProperty("profile");
		return (config != null && !"false".equals(config));
	}
	
}
