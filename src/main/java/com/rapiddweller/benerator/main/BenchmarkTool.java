/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.benchmark.BenchmarkToolConfig;
import com.rapiddweller.benerator.benchmark.BenchmarkToolReport;
import com.rapiddweller.benerator.benchmark.BenchmarkRunner;
import com.rapiddweller.benerator.benchmark.CSVResultExporter;
import com.rapiddweller.benerator.benchmark.Benchmark;
import com.rapiddweller.benerator.benchmark.TextTableResultExporter;
import com.rapiddweller.benerator.benchmark.XLSBenchmarkExporter;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.cli.CommandLineParser;
import com.rapiddweller.common.file.FilePrintStream;
import com.rapiddweller.common.ui.ConsolePrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.rapiddweller.benerator.BeneratorUtil.isEEAvailable;

/**
 * Performs benchmark tests on Benerator.<br/><br/>
 * Created: 13.09.2021 22:21:59
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class BenchmarkTool {

  private static final Logger logger = LoggerFactory.getLogger(BenchmarkTool.class);

  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    if (logger.isInfoEnabled()) {
      logger.info("benerator-benchmark {}", CommandLineParser.formatArgs(args));
    }
    BenchmarkToolConfig config = parseCommandLineConfig(args);
    BenchmarkToolReport result = BenchmarkRunner.runBenchmarks(config);
    exportResult(config, result);
  }


  // constructor -----------------------------------------------------------------------------------------------------

  private BenchmarkTool() {
    // private constructor to prevent instantiation of this utility class
  }


  // run methods -----------------------------------------------------------------------------------------------------

  // private helpers -------------------------------------------------------------------------------------------------

  private static void printHelp() {
    ConsolePrinter.printStandard(
        "Usage: benerator-benchmark [options] [name]",
        "",
        "Example: benerator-benchmark --ce --minDurationSecs 30",
        "",
        "Options:",
        "--ce              run on Benerator Community Edition (default on CE)",
        "--ee              run on Benerator Enterprise Edition (default on EE,",
        "                  only available on Enterprise Edition)",
        "--env <spec>      Runs the tests applicable to the specified system(s). ",
        "                  <spec> may be an environment name, a system (denoted by",
        "                  environment#system) or a comma-separated list of these",
        "                  (without whitespace)",
        "                  environment#system identifiers or a single env/system",
        "--minSecs n       Choose generation count to have a test execution time",
        "                  of at least n seconds (default: 10)",
        "--maxThreads k    Use only up to k cores for testing",
        "                  (default: slightly more than the number of cores)",
        "--mode <spec>     activates Benerator mode strict, lenient or ",
        "                  turbo (default: lenient)",
        "--csv <file>      Exports benchmark results to a CSV file of the specified name",
        "--csvSep c        Uses c as separator character in the generated CSV file",
        "--xls <file>      Exports benchmark results to an XLS file of the specified name",
        "--txt <file>      Exports benchmark results to a text file of the specified name",
        "--help            print this help",
        "--list            lists the available benchmark tests",
        "[name]            is an optional name of a benchmark test to execute.",
        "                  By default, all tests that fit the other settings ",
        "                  are executed."
    );
  }

  static BenchmarkToolConfig parseCommandLineConfig(String... args) {
    CommandLineParser p = new CommandLineParser();
    p.addFlag("ce", "--ce", null);
    p.addFlag("ee", "--ee", null);
    p.addFlag("list", "--list", null);
    p.addFlag("uiResult", "--uiResult", null);
    p.addOption("mode", "--mode", "-m");
    p.addOption("minSecs", "--minSecs", null);
    p.addOption("maxThreads", "--maxThreads", null);
    p.addOption("systemsSpec", "--env", null);
    p.addOption("csv", "--csv", null);
    p.addOption("csvSep", "--csvSep", null);
    p.addOption("xls", "--xls", null);
    p.addOption("txt", "--txt", null);
    p.addArgument("name", false);
    BenchmarkToolConfig config = new BenchmarkToolConfig();
    p.parse(config, args);

    // check help and version requests
    if (config.isHelp()) {
      printHelp();
      System.exit(0);
    }
    if (config.isVersion()) {
      ConsolePrinter.printStandard(BeneratorFactory.getInstance().getVersionInfo(false));
      System.exit(0);
    }

    if (config.isList()) {
      listTests();
      System.exit(0);
    }

    // check editions
    if (config.isEe() && !isEEAvailable()) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Benerator Enterprise Edition is not available on this installation");
    }

    // maxThreads
    int reportedCores = Runtime.getRuntime().availableProcessors();
    if (config.getMaxThreads() == 0) {
      config.setMaxThreads(reportedCores * 3 / 2);
    }

    config.prepareExecutionModes();
    return config;
  }

  private static void listTests() {
    System.out.println("Available Benchmark tests:");
    for (Benchmark setup : Benchmark.getInstances()) {
      System.out.println(setup.getName() + ": " + setup.getDescription());
    }
  }

  private static void exportResult(BenchmarkToolConfig config, BenchmarkToolReport result) throws IOException {
    new TextTableResultExporter(System.out).export(result);
    if (config.isUiResult()) {
      CSVResultExporter.forUiResult().export(result);
    }
    if (config.getCsv() != null) {
      CSVResultExporter.forFile(config.getCsv(), config.getCsvSep()).export(result);
    }
    if (config.getXls() != null) {
      new XLSBenchmarkExporter(config.getXls()).export(result);
    }
    if (config.getTxt() != null) {
      File file = new File(config.getTxt());
      FilePrintStream printer = new FilePrintStream(file);
      new TextTableResultExporter(printer).export(result);
      printer.close();
    }
  }

}
