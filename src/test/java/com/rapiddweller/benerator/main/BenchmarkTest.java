/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.common.ConfigurationError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link Benchmark} class.<br/><br/>
 * Created: 21.10.2021 17:57:21
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BenchmarkTest extends ModelTest {

  @Test
  public void testEmpty() {
    BenchmarkConfig config = Benchmark.parseCommandLineConfig();
    assertEquals(BeneratorMode.STRICT, config.getMode());
    assertEqualArrays(new String[0], config.getEnvironments());
  }

  @Test
  public void testFull() {
    BenchmarkConfig config = Benchmark.parseCommandLineConfig(
        "--ce", "--mode", "turbo", "--minSecs", "123", "--maxThreads", "17", "--env", "h2,hsqlmem");
    assertTrue(config.isCe());
    assertFalse(config.isEe());
    assertEquals(BeneratorMode.TURBO, config.getMode());
    assertEquals(123, config.getMinSecs());
    assertEquals(17, config.getMaxThreads());
    assertEqualArrays(new String[] { "h2", "hsqlmem" }, config.getEnvironments());
  }

  @Test(expected = ConfigurationError.class)
  public void testEeFlagOnCe() {
    Benchmark.parseCommandLineConfig("--ee");
  }

}
