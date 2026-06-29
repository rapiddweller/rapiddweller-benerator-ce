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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the {@link Profiler}.<br/><br/>
 * @author Alexander Kell
 */
public class ProfilerTest {

  @Test
  public void testSamplingAndSummary() {
    Profiler profiler = new Profiler("test", 1);
    assertNotNull(profiler.getRootProfile());
    // samples down a two-level path create/accumulate nested profiles
    profiler.addSample(Arrays.asList("group", "leaf"), 100);
    profiler.addSample(Arrays.asList("group", "leaf"), 200);
    profiler.addSample(Arrays.asList("group"), 50);
    assertNotNull(profiler.getRootProfile().getSubProfiles());
    profiler.printSummary(); // exercises the recursive printer
  }

  @Test
  public void testDefaultInstance() {
    assertNotNull(Profiler.defaultInstance());
    assertSame(Profiler.defaultInstance(), Profiler.defaultInstance());
  }

}
