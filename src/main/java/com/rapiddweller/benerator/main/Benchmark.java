/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.benchmark.BenchmarkConfig;
import com.rapiddweller.benerator.benchmark.BenchmarkSummary;
import com.rapiddweller.benerator.benchmark.BenchmarkRunner;
import com.rapiddweller.benerator.benchmark.SensorResult;
import com.rapiddweller.benerator.benchmark.BenchmarkDefinition;
import com.rapiddweller.benerator.benchmark.BenchmarkResult;
import com.rapiddweller.benerator.benchmark.ExecutionMode;
import com.rapiddweller.benerator.benchmark.SensorSummary;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.HF;
import com.rapiddweller.common.TextUtil;
import com.rapiddweller.common.cli.CommandLineParser;
import com.rapiddweller.common.format.Alignment;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.jdbacl.EnvironmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static com.rapiddweller.benerator.BeneratorUtil.isEEAvailable;

/**
 * Performs benchmark tests on Benerator.<br/><br/>
 * Created: 13.09.2021 22:21:59
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class Benchmark {

  // constants -------------------------------------------------------------------------------------------------------

  private static final Logger logger = LoggerFactory.getLogger(Benchmark.class);

  public static final DecimalFormat FORMAT_1 = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
  public static final DecimalFormat FORMAT_0 = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));


  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    BenchmarkConfig config = parseCommandLineConfig(args);
    BenchmarkSummary result = BenchmarkRunner.runBenchmarks(config);
    printResult(result);
  }


  // constructor -----------------------------------------------------------------------------------------------------

  private Benchmark() {
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

  private static void printResult(BenchmarkSummary result) {
    String[] title = createAndPrintTitle(result);
    printResults(title, result);
  }

  private static void printResults(String[] title, BenchmarkSummary result) {
    Object[][] table = createTable(result);
    printTable(title, table);
  }

  private static Object[][] createTable(BenchmarkSummary result) {
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
          row[i] = (sensorResult != null ? format(sensorResult.entitiesPerHour()) : "N/A");
          i++;
        }
      }
    }
    return table.toArray();
  }

  private static String environmentName(BenchmarkResult application) {
    return (application.getEnvironment() != null ? application.getEnvironment().getName() : null);
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

  private static String[] createAndPrintTitle(BenchmarkSummary result) {
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
    List<String> dbs = result.getDbs();
    if (!dbs.isEmpty()) {
      if (dbs.size() == 1) {
        builder.add("Database: " + EnvironmentUtil.getProductDescription(dbs.get(0)));
      } else {
        builder.add("Databases:");
        for (String db : dbs) {
          builder.add("- " + EnvironmentUtil.getProductDescription(db));
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

  private static String rowHeader(BenchmarkDefinition benchmark, String environment, String sensor, int sensorCount) {
    String label = benchmark.getName();
    if (sensorCount > 1) {
      label += " " + sensor;
    }
    if (environment != null) {
      label += " @ " + environment;
    }
    return label;
  }

  static BenchmarkConfig parseCommandLineConfig(String... args) {
    CommandLineParser p = new CommandLineParser();
    p.addFlag("ce", "--ce", null);
    p.addFlag("ee", "--ee", null);
    p.addFlag("list", "--list", null);
    p.addOption("mode", "--mode", "-m");
    p.addOption("minSecs", "--minSecs", null);
    p.addOption("maxThreads", "--maxThreads", null);
    p.addOption("dbsSpec", "--env", null);
    p.addOption("kafkasSpec", "--kafka", null);
    p.addArgument("name", false);
    BenchmarkConfig config = new BenchmarkConfig();
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
    for (BenchmarkDefinition setup : BenchmarkDefinition.getInstances()) {
      System.out.println(setup.getName() + ": " + setup.getDescription());
    }
  }

  private static String format(double number) {
    return (number < 10 ? FORMAT_1 : FORMAT_0).format(number);
  }

}
