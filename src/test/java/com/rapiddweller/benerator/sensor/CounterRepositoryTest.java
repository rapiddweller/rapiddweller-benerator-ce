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

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the {@link com.rapiddweller.stat.CounterRepository}.<br/><br/>
 * Created: 14.01.2011 11:34:25
 * @since 1.08
 * @author Volker Bergmann
 */
public class CounterRepositoryTest {

	private static final String NAME = "CounterRepositoryTest";
	
	CounterRepository repository = CounterRepository.getInstance();

	@After
	public void tearDown() {
		repository.clear();
	}
	
	@Test
	public void testLifeCyle() {
		assertNull("Counter should not be defined yet", repository.getCounter(NAME));
		repository.addSample(NAME, 100);
		LatencyCounter counter = repository.getCounter(NAME);
		assertNotNull("Counter should have been defined after calling addSample()", counter);
        assertSame(
                "repository is expected to return the same counter instance on subsequent calls to getCounter()",
                counter, repository.getCounter(NAME));
		repository.clear();
		assertNull("After calling clear(), the repository should have no counters", repository.getCounter(NAME));
	}
	
}
