/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.benchmark.BenchmarkToolConfig;
import com.rapiddweller.benerator.benchmark.Benchmark;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.FileUtil;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link BenchmarkTool} class.<br/><br/>
 * Created: 21.10.2021 17:57:21
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class BenchmarkToolTest extends ModelTest {

  private static final String[] BUILTIN_DBS = new String[] { "builtin#h2" };

  @AfterClass
  public static void cleanUp() {
    FileUtil.deleteIfExists(new File("benchmark.csv"));
    FileUtil.deleteIfExists(new File("benchmark.xls"));
    FileUtil.deleteIfExists(new File("benchmark.txt"));
    FileUtil.deleteIfExists(new File("results/1_Performance.csv"));
    FileUtil.deleteIfExists(new File("results/2_Info.csv"));
    FileUtil.deleteDirectory(new File("results"));
  }

  @Test
  public void testEmpty() {
    BenchmarkToolConfig config = BenchmarkTool.parseCommandLineConfig();
    assertEquals(BeneratorMode.STRICT, config.getMode());
    assertEqualArrays(new String[0], config.getSystems());
  }

  @Test
  public void testFull() {
    BenchmarkToolConfig config = BenchmarkTool.parseCommandLineConfig(
        "--ce", "--mode", "turbo", "--minSecs", "123", "--maxThreads", "17", "--env", "builtin#h2");
    assertTrue(config.isCe());
    assertFalse(config.isEe());
    assertEquals(BeneratorMode.TURBO, config.getMode());
    assertEquals(123, config.getMinSecs());
    assertEquals(17, config.getMaxThreads());
    assertBuiltinDbs(config.getSystems());
  }

  @Test
  public void testFullWithFile() {
    BenchmarkToolConfig config = BenchmarkTool.parseCommandLineConfig(
        "--ce", "--mode", "turbo", "--minSecs", "123", "--maxThreads", "17", "--env", "builtin#h2", "db-big-table");
    assertTrue(config.isCe());
    assertFalse(config.isEe());
    assertEquals(BeneratorMode.TURBO, config.getMode());
    assertEquals(123, config.getMinSecs());
    assertEquals(17, config.getMaxThreads());
    assertBuiltinDbs(config.getSystems());
    assertEquals("db-big-table", config.getName());
  }

  @Test
  public void testFileOnly() {
    BenchmarkToolConfig config = BenchmarkTool.parseCommandLineConfig("gen-string");
    assertTrue(config.isCe());
    assertFalse(config.isEe());
    assertEquals(BeneratorMode.STRICT, config.getMode());
    assertEquals("gen-string", config.getName());
  }

  @Test(expected = ConfigurationError.class)
  public void testEeFlagOnCe() {
    BenchmarkTool.parseCommandLineConfig("--ee");
  }

  @Test
  public void testBenchmarkCount() {
    assertEquals(17, Benchmark.getInstances().length);
  }

  @Test
  public void testGenerationBenchmarks() throws IOException {
    runBenchmark("gen-string");
    runBenchmark("gen-big-entity");
    runBenchmark("gen-person-showcase");
  }

  @Test
  public void testAnonymizationBenchmarks() throws IOException {
    runBenchmark("anon-person-showcase");
    runBenchmark("anon-person-regex");
    runBenchmark("anon-person-hash");
    runBenchmark("anon-person-random");
    runBenchmark("anon-person-constant");
  }

  @Test
  public void testDatabaseBenchmarks_system() throws IOException {
    runBenchmark("db-small-table", "builtin#h2");
    runBenchmark("db-big-table", "builtin#h2");
  }

  @Test
  public void testDatabaseBenchmarks_environment() throws IOException {
    runBenchmark("db-small-table", "builtin");
    runBenchmark("db-big-table", "builtin");
  }

  @Test
  public void testFileBenchmarks() throws IOException {
    runBenchmark("file-csv");
    runBenchmark("file-dbunit");
    runBenchmark("file-json");
    runBenchmark("file-fixedwidth");
    runBenchmark("file-out-xml");
  }

  @Test
  public void testRealBenchmarkRun() throws IOException {
    runBenchmark("gen-string", null, 1);
  }

  @Test
  public void testExport() throws IOException {
    BenchmarkTool.main(new String[] { "--minSecs", "0",
            "--csv", "benchmark.csv", "--xls", "benchmark.xls", "--txt", "benchmark.txt", "--uiResult", "gen-string" });
    assertTrue(new File("benchmark.csv").exists());
    assertTrue(new File("benchmark.xls").exists());
    assertTrue(new File("benchmark.txt").exists());
    assertTrue(new File("results/1_Performance.csv").exists());
    assertTrue(new File("results/2_Info.csv").exists());
  }


  // helper methods --------------------------------------------------------------------------------------------------

  private void runBenchmark(String benchmarkName) throws IOException {
    runBenchmark(benchmarkName, null, 0);
  }

  private void runBenchmark(String benchmarkName, String systemId) throws IOException {
    runBenchmark(benchmarkName, systemId, 0);
  }

  private void runBenchmark(String benchmarkName, String systemId, int minSecs) throws IOException {
    Benchmark setup = Benchmark.getInstance(benchmarkName);
    assertNotNull(setup);
    ArrayBuilder<String> builder = new ArrayBuilder<>(String.class);
    builder.addAll(new String[] { "--ce", "--maxThreads", "1", "--minSecs", String.valueOf(minSecs) });
    if (systemId != null) {
      builder.add("--env").add(systemId);
    }
    builder.add(benchmarkName);
    BenchmarkTool.main(builder.toArray());
  }

  private void assertBuiltinDbs(SystemRef[] systems) {
    assertEquals(BUILTIN_DBS.length, systems.length);
    for (int i = 0; i < systems.length; i++) {
      assertEquals(BUILTIN_DBS[i], systems[i].toString());
    }
  }

}

