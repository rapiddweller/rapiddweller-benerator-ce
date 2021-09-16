/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.TextUtil;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.common.version.VersionNumberParser;
import org.hsqldb.lib.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @since 1.2.0
 */
public class Benchmark {

  // constants -------------------------------------------------------------------------------------------------------

  private static final Logger logger = LoggerFactory.getLogger(Benchmark.class);

  public static final int DEFAULT_MIN_DURATION_SECS = 30;

  public static final String EE_BENERATOR = "com.rapiddweller.benerator_ee.main.EEBenerator";

  private static final Setup[] DEFAULT_SETUPS = {
      new Setup("gen-string.ben.xml", "1.2_.0", 100000),
      new Setup("gen-person.ben.xml", "1.2.0", 80000),
      new Setup("anon-person-showcase.ben.xml", "1.2.0", 100000),
      new Setup("anon-person-regex.ben.xml", "1.2.0", 1500000),
      new Setup("anon-person-hash.ben.xml", "1.2.0", 1500000),
      new Setup("anon-person-random.ben.xml", "1.2.0", 1500000),
      new Setup("anon-person-constant.ben.xml", "1.2.0", 8000000)
  };

  private static final NumberFormat PF = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));


  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    if (ArrayUtil.indexOf("--help", args) >= 0 || ArrayUtil.indexOf("-h", args) >= 0) {
      printHelp();
      System.exit(0);
    }
    // parse edition
    boolean ee;
    if (ArrayUtil.indexOf("--ee", args) >= 0) {
      if (!isEEAvailable()) {
        System.err.println("Benerator Enterprice Edition is not avaliable on this installation");
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
    new Benchmark(DEFAULT_SETUPS, mainClassName, minDurationSecs, maxThreads).run();
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

  public void run() throws IOException {
    String[] title = createAndPrintTitle();
    int[] threadCounts = chooseThreadCounts();
    Object[][] table = createTable(threadCounts);
    // perform tests
    for (int iS = 0; iS < setups.length; iS++) {
      Object[] tableRow = table[iS + 1];
      printHorizontalLine();
      runSetup(setups[iS], threadCounts, tableRow);
    }
    printHorizontalLine();
    // Pretty-print results in a text table
    System.out.println();
    System.out.print(TextUtil.formatLinedTable(title, table));
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
        "                  of at least n seconds (default: 30)",
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

  private String[] createAndPrintTitle() {
    printHorizontalLine();
    String[] title = {
        "Benchmark throughput of " + benerator.getVersion(),
        "on a " + BeneratorUtil.getOsWithCoresInfo(),
        "Java version " + VMInfo.getJavaVersion(),
        "" + BeneratorUtil.getJVMInfo(),
        "Date/Time: " + ZonedDateTime.now(),
        "",
        "Numbers are million entities generated per hour"
    };
    for (String line : title)
      System.out.println(line);
    for (String line : title)
      logger.debug("{}", line);
    return title;
  }

  private static void printHorizontalLine() {
    System.out.println("--------------------------------------------------------------------------");
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

  private void runSetup(Setup setup, int[] threadCounts, Object[] tableRow) throws IOException {
    System.out.println("Running " + setup.fileName);
    tableRow[0] = setup.fileName;
    for (int iT = 0; iT < threadCounts.length; iT++) {
      if (versionNumber.compareTo(setup.requiredVersion) >= 0) {
        int threads = threadCounts[iT];
        Execution execution = runWithMinDuration(setup.fileName, minDurationSecs, setup.count, threads, benerator);
        double eps = (double) execution.count / execution.duration * 1000.;
        double meph = 3600. * eps / 1000000.;
        System.out.print(execution.filename + " with " + threads + (threads > 1 ? " threads: " : " thread:  "));
        System.out.print(execution.count + " E / " + execution.duration + " ms ");
        System.out.println(" -> " + PF.format(meph) + " ME/h");
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
    logger.debug("Testing " + fileName + " with count " + count + " and " + threads + " thread(s)");
    String xml = IOUtil.getContentOfURI("com/rapiddweller/benerator/benchmark/" + fileName);
    xml = xml.replace("{count}", "count=\"" + count + "\"");
    xml = xml.replace("{threads}", (Benerator.isCommunityEdition() ? "" : "threads=\"" + threads + "\""));
    String filename = "benchmark.ben.xml";
    IOUtil.writeTextFile(filename, xml);
    long t0 = System.currentTimeMillis();
    benerator.main(new String[] {filename});
    long t1 = System.currentTimeMillis();
    FileUtil.delete(filename);
    return new Execution(fileName, count, threads, t1 - t0);
  }

  public static class Setup {
    public String fileName;
    public VersionNumber requiredVersion;
    public int count;

    public Setup(String fileName, String requiredVersion, int count) {
      this.fileName = fileName;
      this.requiredVersion = new VersionNumberParser().parse(requiredVersion);
      this.count = count;
    }
  }

  public static class Execution {
    public String filename;
    public int count;
    public int threads;
    public long duration;

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
