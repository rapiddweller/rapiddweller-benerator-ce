/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.benchmark.BenchmarkToolConfig;
import com.rapiddweller.benerator.benchmark.BenchmarkToolReport;
import com.rapiddweller.benerator.benchmark.BenchmarkRunner;
import com.rapiddweller.benerator.benchmark.PerformanceFormatter;
import com.rapiddweller.benerator.benchmark.SensorResult;
import com.rapiddweller.benerator.benchmark.Benchmark;
import com.rapiddweller.benerator.benchmark.BenchmarkResult;
import com.rapiddweller.benerator.benchmark.ExecutionMode;
import com.rapiddweller.benerator.benchmark.SensorSummary;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.HF;
import com.rapiddweller.common.TextUtil;
import com.rapiddweller.common.cli.CommandLineParser;
import com.rapiddweller.common.format.Alignment;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.benerator.environment.EnvironmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

import static com.rapiddweller.benerator.BeneratorUtil.isEEAvailable;

/**
 * Performs benchmark tests on Benerator.<br/><br/>
 * Created: 13.09.2021 22:21:59
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class BenchmarkTool {

  // constants -------------------------------------------------------------------------------------------------------

  private static final Logger logger = LoggerFactory.getLogger(BenchmarkTool.class);

  private static final String PROJECT_FOLDER = "com/rapiddweller/benerator/benchmark";


  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    BenchmarkToolConfig config = parseCommandLineConfig(args);
    BenchmarkToolReport result = BenchmarkRunner.runBenchmarks(config);
    printResult(result);
  }


  // constructor -----------------------------------------------------------------------------------------------------

  private BenchmarkTool() {
    // private constructor to prevent instantiation of this utility class
  }


  // run methods -----------------------------------------------------------------------------------------------------

  // private helpers -------------------------------------------------------------------------------------------------

  private static void printHelp() {
    ConsoleInfoPrinter.printHelp(
        "Usage: benerator-benchmark [options] [name]",
        "",
        "Example: benerator-benchmark --ce --minDurationSecs 30 --halfCores",
        "",
        "Options:",
        "--ce              run on Benerator Community Edition (default on CE)",
        "--ee              run on Benerator Enterprise Edition (default on EE,",
        "                  only available on Enterprise Edition)",
        "--env x[,y]       runs only database tests on the environments listed",
        "--kafka x[,y]     runs only Kafka tests on the environments listed",
        "--minSecs n       Choose generation count to have a test execution time",
        "                  of at least n seconds (default: 10)",
        "--maxThreads k    Use only up to k cores for testing",
        "                  (default: slightly more than the number of cores)",
        "--mode <spec>     activates Benerator mode strict, lenient or " +
        "                  turbo (default: lenient)",
        "--help            print this help",
        "--list            lists the available benchmark tests",
        "[name]            is an optional name of a benchmark test to execute." +
        "                  By default, all tests that fit the other settings " +
        "                  are executed."
    );
  }

  private static void printResult(BenchmarkToolReport result) {
    String[] title = createAndPrintTitle(result);
    printResults(title, result);
  }

  private static void printResults(String[] title, BenchmarkToolReport result) {
    Object[][] table = createTable(result);
    printTable(title, table);
  }

  private static Object[][] createTable(BenchmarkToolReport result) {
    // create table
    ArrayBuilder<Object[]> table = new ArrayBuilder<>(Object[].class);
    // format table header
    ExecutionMode[] executionModes = result.getExecutionModes();
    Object[] header = createColumnHeaders(executionModes);
    table.add(header);
    for (BenchmarkResult benchmarkResult : result.getResults()) {
      Collection<String> sensors = benchmarkResult.getSensors();
      for (String sensor : sensors) {
        Object[] row = newRow(executionModes.length + 1, table);
        row[0] = rowHeader(benchmarkResult.getBenchmark(), environmentName(benchmarkResult), sensor, sensors.size());
        SensorSummary sensorSummary = benchmarkResult.getSensorSummary(sensor);
        int i = 1;
        for (ExecutionMode mode : executionModes) {
          SensorResult sensorResult = sensorSummary.getResult(mode);
          row[i] = (sensorResult != null ? PerformanceFormatter.format(sensorResult.entitiesPerHour()) : "N/A");
          i++;
        }
      }
    }
    return table.toArray();
  }

  private static String environmentName(BenchmarkResult result) {
    SystemRef system = result.getSystem();
    return (system != null ? system.toString() : null);
  }

  private static Object[] createColumnHeaders(ExecutionMode[] threadings) {
    Object[] header  = new Object[threadings.length + 1];
    header[0] = "Benchmark";
    for (int i = 0; i < threadings.length; i++) {
      ExecutionMode executionMode = threadings[i];
      if (!executionMode.isEe()) {
        header[i + 1] = "CE";
      } else if (executionMode.getThreadCount() > 1) {
        header[i + 1] = "EE\n" + executionMode.getThreadCount() + " Threads";
      } else {
        header[i + 1] = "EE\n1 Thread";
      }
    }
    return header;
  }

  private static void printTable(String[] title, Object[][] cells) {
    int cols = cells[0].length;
    Alignment[] alignments = new Alignment[cols];
    alignments[0] = Alignment.LEFT;
    for (int i = 1; i < cols; i++) {
      alignments[i] = Alignment.RIGHT;
    }
    System.out.println(TextUtil.formatLinedTable(title, cells, alignments, true));
  }

  private static String[] createAndPrintTitle(BenchmarkToolReport result) {
    ArrayBuilder<String> builder = new ArrayBuilder<>(String.class);
    builder.addAll(new String[] {
        "Benchmark throughput of Benerator " + result.getVersionInfo().getVersion(),
        "in " + result.getMode().getCode() + " mode",
        "on a " + result.getOsInfo(),
        "with " + result.getCpuAndMemInfo(),
        "Java version " + result.getJavaVersion(),
        result.getJVMInfo(),
        "Started: " + result.getStartDateTime(),
        "Duration: " + HF.formatDurationSec(result.getDurationSecs()),
    });
    SystemRef[] dbs = result.getSystems("db");
    if (dbs.length > 0) {
      if (dbs.length == 1) {
        builder.add("Database: " + EnvironmentUtil.getDbProductDescription(dbs[0]));
      } else {
        builder.add("Databases:");
        for (SystemRef system : dbs) {
          builder.add("- " + EnvironmentUtil.getDbProductDescription(system));
        }
      }
    }
    builder.addAll(new String[] {
      "",
      "Numbers are million entities generated per hour"
    });
    String[] title = builder.toArray();
    for (String line : title) {
      logger.debug("{}", line);
    }
    return title;
  }

  private static Object[] newRow(int cellCount, ArrayBuilder<Object[]> table) {
    Object[] tableRow = new Object[cellCount];
    table.add(tableRow);
    return tableRow;
  }

  private static String rowHeader(Benchmark benchmark, String environment, String sensor, int sensorCount) {
    String label = benchmark.getName();
    if (sensorCount > 1) {
      label += " " + sensor;
    }
    if (environment != null) {
      label += " @ " + environment;
    }
    return label;
  }

  static BenchmarkToolConfig parseCommandLineConfig(String... args) {
    CommandLineParser p = new CommandLineParser();
    p.addFlag("ce", "--ce", null);
    p.addFlag("ee", "--ee", null);
    p.addFlag("list", "--list", null);
    p.addOption("mode", "--mode", "-m");
    p.addOption("minSecs", "--minSecs", null);
    p.addOption("maxThreads", "--maxThreads", null);
    p.addOption("systemsSpec", "--env", null);
    p.addArgument("name", false);
    BenchmarkToolConfig config = new BenchmarkToolConfig(PROJECT_FOLDER);
    p.parse(config, args);

    // check help and version requests
    if (config.isHelp()) {
      printHelp();
      System.exit(0);
    }
    if (config.isVersion()) {
      BeneratorUtil.printVersionInfo(false, new ConsoleInfoPrinter());
      System.exit(0);
    }

    if (config.isList()) {
      listTests();
      System.exit(0);
    }

    // check editions
    if (config.isEe() && !isEEAvailable()) {
      throw new ConfigurationError("Benerator Enterprise Edition is not available on this installation");
    }

    // maxThreads
    int reportedCores = Runtime.getRuntime().availableProcessors();
    if (config.getMaxThreads() == 0) {
      config.setMaxThreads(reportedCores * 3 / 2);
    }

    config.prepareThreadings();
    return config;
  }

  private static void listTests() {
    System.out.println("Available Benchmark tests:");
    for (Benchmark setup : Benchmark.getInstances()) {
      System.out.println(setup.getName() + ": " + setup.getDescription());
    }
  }

}
