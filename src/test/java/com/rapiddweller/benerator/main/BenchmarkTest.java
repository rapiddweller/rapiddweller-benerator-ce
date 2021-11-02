/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.benchmark.BenchmarkConfig;
import com.rapiddweller.benerator.benchmark.Environment;
import com.rapiddweller.benerator.benchmark.BenchmarkDefinition;
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
    assertEqualArrays(new String[0], config.getDbs());
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
    assertEqualArrays(new String[] { "h2", "hsqlmem" }, config.getDbs());
  }

  @Test
  public void testFullWithFile() {
    BenchmarkConfig config = Benchmark.parseCommandLineConfig(
        "--ce", "--mode", "turbo", "--minSecs", "123", "--maxThreads", "17", "--env", "h2,hsqlmem", "db-big-table");
    assertTrue(config.isCe());
    assertFalse(config.isEe());
    assertEquals(BeneratorMode.TURBO, config.getMode());
    assertEquals(123, config.getMinSecs());
    assertEquals(17, config.getMaxThreads());
    assertEqualArrays(new String[] { "h2", "hsqlmem" }, config.getDbs());
    assertEquals("db-big-table", config.getName());
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
    assertEquals(17, BenchmarkDefinition.getInstances().length);
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
    runSetup("db-small-table", Environment.ofDb("h2"));
    runSetup("db-small-table", Environment.ofDb("hsqlmem"));
    runSetup("db-big-table", Environment.ofDb("h2"));
    runSetup("db-big-table", Environment.ofDb("hsqlmem"));
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

  private void runSetup(String setupName, Environment environment) throws IOException {
    BenchmarkDefinition setup = BenchmarkDefinition.getInstance(setupName);
    assertNotNull(setup);
    Benchmark.main(new String[] { "--ce", "--maxThreads", "1", "--minSecs", "0" });
  }

}

