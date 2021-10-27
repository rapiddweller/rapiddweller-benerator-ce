/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.common.ConfigurationError;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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

  @Test
  public void testFullWithFile() {
    BenchmarkConfig config = Benchmark.parseCommandLineConfig(
        "--ce", "--mode", "turbo", "--minSecs", "123", "--maxThreads", "17", "--env", "h2,hsqlmem", "db-bigtable");
    assertTrue(config.isCe());
    assertFalse(config.isEe());
    assertEquals(BeneratorMode.TURBO, config.getMode());
    assertEquals(123, config.getMinSecs());
    assertEquals(17, config.getMaxThreads());
    assertEqualArrays(new String[] { "h2", "hsqlmem" }, config.getEnvironments());
    assertEquals("db-bigtable", config.getName());
  }

  @Test
  public void testFileOnly() {
    BenchmarkConfig config = Benchmark.parseCommandLineConfig("gen-string");
    assertTrue(config.isCe());
    assertFalse(config.isEe());
    assertEquals(BeneratorMode.STRICT, config.getMode());
    assertEquals("gen-string", config.getName());
  }

  @Test(expected = ConfigurationError.class)
  public void testEeFlagOnCe() {
    Benchmark.parseCommandLineConfig("--ee");
  }

  @Test
  public void testSetupCount() {
    assertEquals(15, Benchmark.SETUPS.length);
  }

  @Test
  public void testGenerationSetups() throws IOException {
    runSetup("gen-string");
    runSetup("gen-big-entity");
    runSetup("gen-person-showcase");
  }

  @Test
  public void testAnonymizationSetups() throws IOException {
    runSetup("anon-person-showcase");
    runSetup("anon-person-regex");
    runSetup("anon-person-hash");
    runSetup("anon-person-random");
    runSetup("anon-person-constant");
  }

  @Test
  public void testDatabaseSetups() throws IOException {
    runSetup("db-smalltable", "h2");
    runSetup("db-smalltable", "hsqlmem");
    runSetup("db-bigtable", "h2");
    runSetup("db-bigtable", "hsqlmem");
  }

  @Test
  public void testFileSetups() throws IOException {
    runSetup("file-csv");
    runSetup("file-dbunit");
    runSetup("file-json");
    runSetup("file-fixedwidth");
    runSetup("file-out-xml");
  }

  private void runSetup(String setupName) throws IOException {
    runSetup(setupName, null);
  }

  private void runSetup(String setupName, String environment) throws IOException {
    Benchmark.Setup setup = Benchmark.getSetup(setupName);
    assertNotNull(setup);
    BenchmarkConfig config = new BenchmarkConfig();
    config.setMinSecs(0);
    Benchmark.Threading[] threadings = {new Benchmark.Threading(false, 1)};
    new Benchmark(config).runSetup(setup, environment, threadings);
  }

}
