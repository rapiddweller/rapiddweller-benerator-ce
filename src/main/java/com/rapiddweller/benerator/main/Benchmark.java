/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.TextUtil;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.format.Alignment;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.common.ui.InfoPrinter;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.common.version.VersionNumberParser;
import com.rapiddweller.common.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Performs benchmark tests on Benerator.<br/><br/>
 * Created: 13.09.2021 22:21:59
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class Benchmark {

  // constants -------------------------------------------------------------------------------------------------------

  private static final Logger logger = LoggerFactory.getLogger(Benchmark.class);

  public static final int DEFAULT_MIN_DURATION_SECS = 10;

  private static final String EE_BENERATOR = "com.rapiddweller.benerator_ee.main.EEBenerator";

  private static final String V200 = "2.0.0";
  private static final String V210 = "2.1.0";

  private static final Setup[] DEFAULT_SETUPS = {
      new Setup("gen-string.ben.xml", false, V200, 100000),
      new Setup("gen-person-showcase.ben.xml", false, V200, 80000),
      new Setup("anon-person-showcase.ben.xml", false, V200, 100000),
      new Setup("anon-person-regex.ben.xml", false, V200, 1500000),
      new Setup("anon-person-hash.ben.xml", false, V200, 1500000),
      new Setup("anon-person-random.ben.xml", false, V200, 1500000),
      new Setup("anon-person-constant.ben.xml", false, V200, 8000000),
      // TODO 2.1.0 measure in/out performance
      new Setup("file-out-csv.ben.xml", false, V210, 1000000),
      new Setup("file-out-json.ben.xml", true, V210, 1000000),
      new Setup("file-out-dbunit.ben.xml", false, V210, 1000000),
      new Setup("file-out-fixedwidth.ben.xml", false, V210, 500000),
      new Setup("file-out-xml.ben.xml", false, V210, 500000)
  };

  private static final NumberFormat PF = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));


  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    InfoPrinter printer = new ConsoleInfoPrinter();
    if (ArrayUtil.indexOf("--help", args) >= 0 || ArrayUtil.indexOf("-h", args) >= 0) {
      printHelp();
      System.exit(0);
    }
    // parse edition
    boolean ee;
    if (ArrayUtil.indexOf("--ee", args) >= 0) {
      if (!isEEAvailable()) {
        printer.printLines("Benerator Enterprice Edition is not avaliable on this installation");
        System.exit(-1);
      }
      ee = true;
    } else if (ArrayUtil.indexOf("--ce", args) >= 0) {
      ee = false;
    } else {
      ee = isEEAvailable();
    }
    String mainClassName = (ee ? EE_BENERATOR : Benerator.class.getName());

    // parse min duration (secs)
    int minDurationSecs = DEFAULT_MIN_DURATION_SECS;
    int minDurIndex = ArrayUtil.indexOf("--minSecs", args);
    if (minDurIndex >= 0 && args.length > minDurIndex + 1)
      minDurationSecs = Integer.parseInt(args[minDurIndex + 1]);

    // parse 'half cores' spec
    int maxThreads = 0;
    int maxThreadsIndex = ArrayUtil.indexOf("--maxThreads", args);
    if (maxThreadsIndex >= 0 && args.length > maxThreadsIndex + 1)
      maxThreads = Integer.parseInt(args[maxThreadsIndex + 1]);

    // run
    new Benchmark(DEFAULT_SETUPS, mainClassName, minDurationSecs, maxThreads).run(printer);
  }


  // attributes ------------------------------------------------------------------------------------------------------

  private final Benerator benerator;
  private final VersionNumber versionNumber;
  private final Setup[] setups;
  private final int minDurationSecs;
  private final int maxThreads;


  // constructor -----------------------------------------------------------------------------------------------------

  public Benchmark(Setup[] setups, String mainClassName, int minDurationSecs, int maxThreads) {
    // apply configuration settings
    this.setups = setups;
    this.benerator = (Benerator) BeanUtil.newInstance(mainClassName);
    this.versionNumber = benerator.getVersionNumber();
    this.minDurationSecs = minDurationSecs;
    int reportedCores = Runtime.getRuntime().availableProcessors();
    this.maxThreads = (maxThreads > 0 ? maxThreads : reportedCores * 3 / 2);
    // log configuration settings
    logger.debug("Main class {}", mainClassName);
    logger.debug("Min. duration: {} s", minDurationSecs);
    logger.debug("Max threads: {}", maxThreads);
  }


  // interface -------------------------------------------------------------------------------------------------------

  public void run(InfoPrinter printer) throws IOException {
    String[] title = createAndPrintTitle(printer);
    int[] threadCounts = chooseThreadCounts();
    Object[][] table = createTable(threadCounts);
    // perform tests
    for (int iS = 0; iS < setups.length; iS++) {
      Object[] tableRow = table[iS + 1];
      printHorizontalLine(printer);
      runSetup(setups[iS], threadCounts, tableRow, printer);
    }
    printHorizontalLine(printer);
    // Pretty-print results in a text table
    printer.printLines("");
    int cols = table[0].length;
    Alignment[] alignments = new Alignment[cols];
    alignments[0] = Alignment.LEFT;
    for (int i = 1; i < cols; i++)
      alignments[i] = Alignment.RIGHT;
    printer.printLines(TextUtil.formatLinedTable(title, table, alignments));
  }


  // private helpers -------------------------------------------------------------------------------------------------

  private static void printHelp() {
    ConsoleInfoPrinter.printHelp(
        "Usage: benerator-benchmark [options]",
        "",
        "Example: benerator-benchmark --ce --minDurationSecs 30 --halfCores",
        "",
        "Options:",
        "--ce              run on Benerator Community Edition (default on CE)",
        "--ee              run on Benerator Enterprise Edition (default on EE,",
        "                  only available on Enterprice Edition)",
        "--minSecs n       Choose generation count to have a test execution time",
        "                  of at least n seconds (default: 10)",
        "--maxThreads k    Use only up to k cores for testing",
        "                  (default: slightly more than the number of cores)",
        "--help            print this help"
    );
  }

  private static boolean isEEAvailable() {
    try {
      Class.forName(EE_BENERATOR);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  private Object[][] createTable(int[] threadCounts) {
    // create table
    Object[][] table = new Object[setups.length + 1][];
    // format table header
    for (int row = 0; row <= setups.length; row++) {
      table[row] = new Object[threadCounts.length + 1];
    }
    table[0][0] = "Benchmark";
    for (int i = 0; i < threadCounts.length; i++) {
      table[0][i + 1] = threadCounts[i] + (threadCounts[i] > 1 ? " Threads" : " Thread");
    }
    return table;
  }

  private String[] createAndPrintTitle(InfoPrinter printer) {
    printHorizontalLine(printer);
    String[] title = {
        "Benchmark throughput of " + benerator.getVersion(),
        "on a " + BeneratorUtil.getOsInfo(),
        "with " + BeneratorUtil.getCpuAndMemInfo(),
        "Java version " + VMInfo.getJavaVersion(),
        BeneratorUtil.getJVMInfo(),
        "Date/Time: " + ZonedDateTime.now(),
        "",
        "Numbers are reported in million entities generated per hour"
    };
    printer.printLines(title);
    for (String line : title)
      logger.debug("{}", line);
    return title;
  }

  private static void printHorizontalLine(InfoPrinter printer) {
    printer.printLines("--------------------------------------------------------------------------");
  }

  private int[] chooseThreadCounts() {
    if (benerator.isCommunityEdition() || maxThreads == 1) {
      return new int[] { 1 };
    }
    TreeSet<Integer> set = new TreeSet<>();
    for (int i = 1; i < maxThreads; i *= 2)
        set.add(i);
    set.add(maxThreads);
    int[] result = new int[set.size()];
    int i = 0;
    for (int n : set)
      result[i++] = n;
    return result;
  }

  private void runSetup(Setup setup, int[] threadCounts, Object[] tableRow, InfoPrinter printer) throws IOException {
    printer.printLines("Running " + setup.fileName);
    tableRow[0] = setup.fileName;
    for (int iT = 0; iT < threadCounts.length; iT++) {
      if (versionNumber.compareTo(setup.requiredVersion) >= 0
          && (!setup.reqEE || !benerator.isCommunityEdition())) {
        int threads = threadCounts[iT];
        Execution execution = runWithMinDuration(setup.fileName, minDurationSecs, setup.count, threads, benerator);
        double eps = (double) execution.count / execution.duration * 1000.;
        double meph = 3600. * eps / 1000000.;
        String message = execution.filename + " with " + threads + (threads > 1 ? " threads: " : " thread:  ");
        message += execution.count + " E / " + execution.duration + " ms ";
        message += " -> " + PF.format(meph) + " ME/h";
        printer.printLines(message);
        tableRow[iT + 1] = Math.floor(execution.entitiesPerHour());
      } else {
        tableRow[iT + 1] = "N/A";
      }
    }
  }

  private static Execution runWithMinDuration(String fileName, long minDurationSecs, int countBase, int threads, Benerator benerator) throws IOException {
    int count = countBase;
    long minDurationMillis = minDurationSecs * 1000;
    do {
      Execution execution = runTest(fileName, count, threads, benerator);
      if (execution.duration >= minDurationMillis)
        return execution;
      double factor = (double) minDurationMillis / execution.duration;
      if (factor > 5) {
        factor = 5;
      } else {
        factor *= 1.1;
      }
      count *= Math.ceil(factor);
    } while (true);
  }

  private static Execution runTest(String fileName, int count, int threads, Benerator benerator) throws IOException {
    logger.debug("Testing {} with count {} and {} thread(s)", fileName, count, threads);
    String xml = IOUtil.getContentOfURI("com/rapiddweller/benerator/benchmark/" + fileName);
    xml = xml.replace("{count}", String.valueOf(count));
    xml = xml.replace("{threads}", String.valueOf(threads));
    String filename = "__benchmark.ben.xml";
    IOUtil.writeTextFile(filename, xml);
    long t0 = System.currentTimeMillis();
    benerator.runFile(filename);
    long t1 = System.currentTimeMillis();
    FileUtil.deleteIfExists(new File(filename));
    FileUtil.deleteIfExists(new File("__benchmark.out"));
    return new Execution(fileName, count, threads, t1 - t0);
  }

  public static class Setup {
    public final String fileName;
    public final boolean reqEE;
    public final VersionNumber requiredVersion;
    public final int count;

    public Setup(String fileName, boolean reqEE,  String requiredVersion, int count) {
      this.fileName = fileName;
      this.reqEE = reqEE;
      this.requiredVersion = new VersionNumberParser().parse(requiredVersion);
      this.count = count;
    }
  }

  public static class Execution {
    public final String filename;
    public final int count;
    public final int threads;
    public final long duration;

    public Execution(String filename, int count, int threads, long duration) {
      this.filename = filename;
      this.count = count;
      this.threads = threads;
      this.duration = duration;
    }

    public double entitiesPerSecond() {
      return (double) count / duration * 1000.;
    }

    public double entitiesPerHour() {
      return 3600. * entitiesPerSecond() / 1000000.;
    }
  }

}
