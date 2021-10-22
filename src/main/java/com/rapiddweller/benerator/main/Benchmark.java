/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.engine.DefaultBeneratorFactory;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.TextUtil;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.cli.CommandLineParser;
import com.rapiddweller.common.format.Alignment;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.common.ui.InfoPrinter;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.common.version.VersionNumberParser;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.jdbacl.DatabaseDialect;
import com.rapiddweller.jdbacl.EnvironmentUtil;
import com.rapiddweller.stat.CounterRepository;
import com.rapiddweller.stat.LatencyCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.rapiddweller.benerator.BeneratorUtil.EE_BENERATOR;
import static com.rapiddweller.benerator.BeneratorUtil.EE_BENERATOR_FACTORY;
import static com.rapiddweller.benerator.BeneratorUtil.isEEAvailable;
import static com.rapiddweller.jdbacl.EnvironmentUtil.getDialect;

/**
 * Performs benchmark tests on Benerator.<br/><br/>
 * Created: 13.09.2021 22:21:59
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class Benchmark {

  // constants -------------------------------------------------------------------------------------------------------

  private static final Logger logger = LoggerFactory.getLogger(Benchmark.class);

  private static final String V200 = "2.0.0";
  private static final String V210 = "2.1.0";

  private static final Setup[] DEFAULT_SETUPS = {
      new Setup("gen-string.ben.xml", false, V200, 100000),
      new Setup("gen-big-entity.ben.xml", false, V200, 100000),
      new Setup("gen-person-showcase.ben.xml", false, V200, 80000),
      new Setup("anon-person-showcase.ben.xml", false, V200, 100000),
      new Setup("anon-person-regex.ben.xml", false, V200, 1500000),
      new Setup("anon-person-hash.ben.xml", false, V200, 1500000),
      new Setup("anon-person-random.ben.xml", false, V200, 1500000),
      new Setup("anon-person-constant.ben.xml", false, V200, 8000000),
      new Setup("db-smalltable.ben.xml", false, V200, 15000),
      new Setup("db-bigtable.ben.xml", false, V200, 5000),
      // TODO limit file generation to 1 GB file size
      new Setup("file-csv.ben.xml", false, V210, 1000000),
      new Setup("file-dbunit.ben.xml", false, V210, 1000000),
      new Setup("file-json.ben.xml", true, V210, 10000),
      new Setup("file-fixedwidth.ben.xml", false, V210, 500000),
      new Setup("file-out-xml.ben.xml", false, V210, 500000)
  };

  public static final DecimalFormat FORMAT_1 = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
  public static final DecimalFormat FORMAT_0 = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));


  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    InfoPrinter printer = new ConsoleInfoPrinter();
    BenchmarkConfig config = parseCommandLineConfig(args);
    Benchmark benchmark = new Benchmark(DEFAULT_SETUPS, config);
    benchmark.run(printer);
  }


  // attributes ------------------------------------------------------------------------------------------------------

  private final Setup[] setups;
  private final BenchmarkConfig config;
  private final Benerator benerator;


  // constructor -----------------------------------------------------------------------------------------------------

  public Benchmark(Setup[] setups, BenchmarkConfig config) {
    // apply configuration settings
    this.setups = setups;
    this.config = config;
    String mainClassName;
    if (config.isEe()) {
      mainClassName = EE_BENERATOR;
      BeneratorFactory.setInstance((BeneratorFactory) BeanUtil.newInstance(EE_BENERATOR_FACTORY));
    } else {
      mainClassName = Benerator.class.getName();
      BeneratorFactory.setInstance(new DefaultBeneratorFactory());
    }
    this.benerator = (Benerator) BeanUtil.newInstance(mainClassName);
    Benerator.setMode(config.getMode());
    // log configuration settings
    logger.debug("Main class {}", mainClassName);
    logger.debug("Min. duration: {} s", config.getMinSecs());
    logger.debug("Max threads: {}", config.getMaxThreads());
  }


  // interface -------------------------------------------------------------------------------------------------------

  public void run(InfoPrinter printer) throws IOException {
    String[] title = createAndPrintTitle(printer);
    int[] threadCounts = chooseThreadCounts();
    ArrayBuilder<Object[]> table = createTable(threadCounts);
    // perform tests
    for (Setup setup : setups) {
      printHorizontalLine(printer);
      String[] environments = config.getEnvironments();
      if (environments.length > 0) {
        runOnEnvironments(setup, environments, threadCounts, table, printer);
      } else {
        if (setup.isDbSetup()) {
          logger.info("Skipping DB test since no environment was specified");
        } else {
          runSetup(setup, null, threadCounts, table, printer);
        }
      }
    }
    printHorizontalLine(printer);
    // Pretty-print results in a text table
    printer.printLines("");
    Object[][] t = table.toArray();
    int cols = t[0].length;
    Alignment[] alignments = new Alignment[cols];
    alignments[0] = Alignment.LEFT;
    for (int i = 1; i < cols; i++) {
      alignments[i] = Alignment.RIGHT;
    }
    printer.printLines(TextUtil.formatLinedTable(title, t, alignments));
  }

  private void runOnEnvironments(
      Setup setup, String[] environments, int[] threadCounts, ArrayBuilder<Object[]> table, InfoPrinter printer)
      throws IOException {
    if (setup.isDbSetup()) {
      for (String environment : environments) {
        runSetup(setup, environment, threadCounts, table, printer);
      }
    } else {
      logger.info("Skipping plain test since running in DB test mode");
    }
  }

  private Object[] newRow(int cellCount, ArrayBuilder<Object[]> table) {
    Object[] tableRow = new Object[cellCount];
    table.add(tableRow);
    return tableRow;
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
        "                  only available on Enterprise Edition)",
        "--env x[,y]       runs only database tests on the environments listed",
        "--minSecs n       Choose generation count to have a test execution time",
        "                  of at least n seconds (default: 10)",
        "--maxThreads k    Use only up to k cores for testing",
        "                  (default: slightly more than the number of cores)",
        "--mode <spec>     activates Benerator mode strict, lenient or " +
        "                  turbo (default: lenient)",
        "--help            print this help"
    );
  }

  private ArrayBuilder<Object[]> createTable(int[] threadCounts) {
    // create table
    ArrayBuilder<Object[]> table = new ArrayBuilder<>(Object[].class);
    // format table header
    Object[] header  = new Object[threadCounts.length + 1];
    header[0] = "Benchmark";
    for (int i = 0; i < threadCounts.length; i++) {
      header[i + 1] = threadCounts[i] + (threadCounts[i] > 1 ? " Threads" : " Thread");
    }
    table.add(header);
    return table;
  }

  private String[] createAndPrintTitle(InfoPrinter printer) {
    printHorizontalLine(printer);
    ArrayBuilder<String> builder = new ArrayBuilder<>(String.class);
    builder.addAll(new String[] {
        "Benchmark throughput of " + benerator.getVersion(),
        "in " + Benerator.getMode().getCode() + " mode",
        "on a " + BeneratorUtil.getOsInfo(),
        "with " + BeneratorUtil.getCpuAndMemInfo(),
        "Java version " + VMInfo.getJavaVersion(),
        BeneratorUtil.getJVMInfo(),
        "Date/Time: " + ZonedDateTime.now(),
    });
    String[] environments = config.getEnvironments();
    if (!ArrayUtil.isEmpty(environments)) {
      if (environments.length == 1) {
        builder.add("Database: " + EnvironmentUtil.getProductDescription(environments[0]));
      } else {
        builder.add("Databases:");
        for (String environment : environments) {
          builder.add("- " + EnvironmentUtil.getProductDescription(environment));
        }
      }
    }
    builder.addAll(new String[] {
      "",
      "Numbers are reported in million entities generated per hour"
    });
    String[] title = builder.toArray();
    printer.printLines(title);
    for (String line : title)
      logger.debug("{}", line);
    return title;
  }

  private static void printHorizontalLine(InfoPrinter printer) {
    printer.printLines("--------------------------------------------------------------------------");
  }

  private int[] chooseThreadCounts() {
    int maxThreads = config.getMaxThreads();
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

  private void runSetup(Setup setup, String environment, int[] threadCounts, ArrayBuilder<Object[]> table, InfoPrinter printer) throws IOException {
    printer.printLines("Running " + setup.fileName);
    Object[][] sensorRows = null;
    long initialCount = setup.count;
    for (int iT = 0; iT < threadCounts.length; iT++) {
      if (versionAndEditionMatch(setup)) {
        int threads = threadCounts[iT];
        List<Execution> executions = runWithMinDuration(setup.fileName, environment, config.getMinSecs(), initialCount, threads, benerator);
        initialCount = executions.get(0).count;
        if (sensorRows == null) {
          sensorRows = createSensorRows(setup, environment, threadCounts, table, executions);
        }
        for (int iS = 0; iS < executions.size(); iS++) {
          sensorRows[iS][iT + 1] = format(executions.get(iS).entitiesPerHour());
          sensorRows[iS][0] = rowHeader(setup, environment, executions, iS);
        }
      } else { // version too low or EE feature in CE
        sensorRows = createNaRow(setup, environment, threadCounts, table, sensorRows, iT);
      }
    }
  }

  private boolean versionAndEditionMatch(Setup setup) {
    VersionNumber versionNumber = benerator.getVersionNumber();
    return versionNumber.compareTo(setup.requiredVersion) >= 0 && (!setup.reqEE || !benerator.isCommunityEdition());
  }

  private Object[][] createSensorRows(Setup setup, String environment, int[] threadCounts, ArrayBuilder<Object[]> table, List<Execution> executions) {
    Object[][] sensorRows;
    sensorRows = new Object[executions.size()][];
    for (int iS = 0; iS < executions.size(); iS++) {
      sensorRows[iS] = newRow(threadCounts.length + 1, table);
      sensorRows[iS][0] = rowHeader(setup, environment, executions, iS);
    }
    return sensorRows;
  }

  private Object[][] createNaRow(Setup setup, String environment, int[] threadCounts, ArrayBuilder<Object[]> table, Object[][] sensorRows, int iT) {
    if (sensorRows == null) {
      sensorRows = new Object[1][];
      sensorRows[0] = newRow(threadCounts.length + 1, table);
      sensorRows[0][0] = rowHeader(setup, environment, null, 0);
    }
    sensorRows[0][iT + 1] = "N/A";
    return sensorRows;
  }

  private String rowHeader(Setup setup, String environment, List<Execution> executions, int iS) {
    String label = setup.fileName;
    if (executions != null && executions.size() > 1) {
      label += " " + executions.get(iS).sensor;
    }
    if (environment != null) {
      label += " @ " + environment;
    }
    return label;
  }

  private static List<Execution> runWithMinDuration(String fileName, String environment,
        long minDurationSecs, long countBase, int threads, Benerator benerator) throws IOException {
    long count = countBase;
    long minDurationMillis = minDurationSecs * 1000;
    do {
      List<Execution> executions = trySetting(fileName, environment, count, threads, benerator);
      int actualMinDuration = minDuration(executions);
      if (actualMinDuration >= minDurationMillis)
        return executions;
      double factor = (double) minDurationMillis / actualMinDuration;
      if (factor > 5) {
        factor = 5;
      } else {
        factor *= 1.1;
      }
      count = (long) (factor * count + 500) / 1000 * 1000;
    } while (true);
  }

  private static int minDuration(List<Execution> executions) {
    if (CollectionUtil.isEmpty(executions)) {
      throw new ConfigurationError("No sensors found");
    }
    int result = executions.get(0).duration;
    for (int i = 1; i < executions.size(); i++) {
      int tmp = executions.get(i).duration;
      if (tmp < result) {
        result = tmp;
      }
    }
    return result;
  }

  private static List<Execution> trySetting(String fileName, String environment, long count, int threads, Benerator benerator) throws IOException {
    logger.debug("Testing {} with count {} and {} thread(s)", fileName, count, threads);
    String xml = IOUtil.getContentOfURI("com/rapiddweller/benerator/benchmark/" + fileName);
    xml = applyEnvironment(environment, xml);
    xml = xml.replace("{count}", String.valueOf(count));
    xml = xml.replace("{threads}", String.valueOf(threads));
    String filename = "__benchmark.ben.xml";
    IOUtil.writeTextFile(filename, xml);
    CounterRepository repo = CounterRepository.getInstance();
    repo.clear();
    benerator.runFile(filename);
    FileUtil.deleteIfExists(new File(filename));
    File[] files = new File(".").listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().startsWith("__benchmark.out")) {
          FileUtil.deleteIfExists(file);
        }
      }
    }
    FileUtil.deleteIfExists(new File(filename));
    List<Execution> result = new ArrayList<>();
    Set<Map.Entry<String, LatencyCounter>> counters = repo.getCounters();
    for (Map.Entry<String, LatencyCounter> counter : counters) {
      String key = counter.getKey();
      if (key.startsWith("benchmark.")) {
        LatencyCounter value = counter.getValue();
        Assert.equals(1, (int) value.sampleCount());
        long eps = count * 1000 / value.totalLatency();
        logger.info("{}: {} entities / {} ms, throughput {} E/s - {} ME/h, {} thread{}",
            key, count, value.totalLatency(), eps, eps * 3600. / 1000000., threads, (threads > 1 ? "s" : ""));
        String sensor = "[" + (key.substring("benchmark.".length())) + "]";
        result.add(new Execution(fileName, sensor, count, threads, (int) value.totalLatency()));
      }
    }
    return result;
  }

  private static String applyEnvironment(String environment, String xml) {
    if (environment != null) {
      xml = xml.replace("{environment}", environment);
      DatabaseDialect dialect = getDialect(environment);
      Set<String> types = CollectionUtil.toSet(
          "varchar", "char", "string", "date", "time", "timestamp", "binary", "boolean",
          "byte", "short", "int", "long", "big_integer", "float", "double", "big_decimal");
      for (String type : types) {
        String specialType = dialect.getSpecialType(type);
        xml = xml.replace('{' + type + '}', specialType);
      }
    }
    return xml;
  }

  private static String format(double number) {
    return (number < 10 ? FORMAT_1 : FORMAT_0).format(number);
  }

  static BenchmarkConfig parseCommandLineConfig(String... args) {
    CommandLineParser p = new CommandLineParser();
    p.addFlag("ce", "--ce", null);
    p.addFlag("ee", "--ee", null);
    p.addOption("mode", "--mode", "-m");
    p.addOption("minSecs", "--minSecs", null);
    p.addOption("maxThreads", "--maxThreads", null);
    p.addOption("environmentSpec", "--env", null);
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

    // check editions
    if (config.isEe() && !isEEAvailable()) {
      throw new ConfigurationError("Benerator Enterprise Edition is not available on this installation");
    }
    if (config.isCe() && config.isEe()) {
      throw new ConfigurationError("The flags --ee and --ce exclude each other");
    }
    if (config.isEe()) {
      System.setProperty(BeneratorFactory.BENERATOR_FACTORY_PROPERTY, EE_BENERATOR);
    }

    // maxThreads
    int reportedCores = Runtime.getRuntime().availableProcessors();
    if (config.getMaxThreads() == 0) {
      config.setMaxThreads(reportedCores * 3 / 2);
    }
    return config;
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

    public boolean isDbSetup() {
      return fileName.startsWith("db-");
    }
  }

  public static class Execution {
    public final String filename;
    public final String sensor;
    public final long count;
    public final int threads;
    public final int duration;

    public Execution(String filename, String sensor, long count, int threads, int duration) {
      this.filename = filename;
      this.sensor = sensor;
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
