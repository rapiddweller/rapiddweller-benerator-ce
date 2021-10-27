/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.engine.BeneratorRootContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorFactory;
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.ObjectNotFoundException;
import com.rapiddweller.common.OrderedMap;
import com.rapiddweller.common.TextUtil;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.cli.CommandLineParser;
import com.rapiddweller.common.format.Alignment;
import com.rapiddweller.common.log.LoggingInfoPrinter;
import com.rapiddweller.common.ui.ConsoleInfoPrinter;
import com.rapiddweller.common.version.VersionInfo;
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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

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

  static final Setup[] SETUPS = {
      new Setup("gen-string", false, V200, 100000, "Generation of random strings "),
      new Setup("gen-big-entity", false, V200, 100000, "Generation of big entities (323 attributes)"),
      new Setup("gen-person-showcase", false, V200, 80000, "Generation of real-looking person data"),
      new Setup("anon-person-showcase", false, V200, 100000, "Anonymization with real-looking person data"),
      new Setup("anon-person-regex", false, V200, 1500000, "Anonymization with regular expressions"),
      new Setup("anon-person-hash", false, V200, 1500000, "Anonymization with hashes of the original values"),
      new Setup("anon-person-random", false, V200, 1500000, "Anonymization with random data"),
      new Setup("anon-person-constant", false, V200, 8000000, "Anonymization with constant data"),
      new Setup("db-smalltable", false, V200, 15000, "Reading/writing small database tables (10 columns)"),
      new Setup("db-bigtable", false, V200, 5000, "Reading/writing big database tables (323 columns)"),
      new Setup("file-csv", false, V210, 1000000, "Reading/writing CSV files"),
      new Setup("file-dbunit", false, V210, 1000000, "Reading/writing DbUnit files"),
      new Setup("file-json", true, V210, 15000, "Reading/writing JSON files"),
      new Setup("file-fixedwidth", false, V210, 500000, "Reading/writing fixed-width-files"),
      new Setup("file-out-xml", false, V210, 500000, "Writing XML files")
  };

  public static final DecimalFormat FORMAT_1 = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
  public static final DecimalFormat FORMAT_0 = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));
  public static final long ONE_GIGABYTE = 1000000000L;


  // main ------------------------------------------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    BenchmarkConfig config = parseCommandLineConfig(args);
    Benchmark benchmark = new Benchmark(config);
    benchmark.run();
  }


  // attributes ------------------------------------------------------------------------------------------------------

  private final BenchmarkConfig config;

  // constructor -----------------------------------------------------------------------------------------------------

  public Benchmark(BenchmarkConfig config) {
    // apply configuration settings
    this.config = config;
    Benerator.setMode(config.getMode());
    // log configuration settings
    logger.debug("Min. duration: {} s", config.getMinSecs());
    logger.debug("Max threads: {}", config.getMaxThreads());
  }


  // run methods -----------------------------------------------------------------------------------------------------

  public void run() throws IOException {
    String[] title = createAndPrintTitle();
    Threading[] threadings = chooseThreadCounts();
    // perform tests
    for (Setup setup : config.getSetups()) {
      String[] environments = config.getEnvironments();
      if (environments.length > 0) {
        runOnEnvironments(setup, environments, threadings);
      } else {
        if (setup.isDbSetup()) {
          logger.info("Skipping DB test since no environment was specified");
        } else {
          runSetup(setup, null, threadings);
        }
      }
    }
    // Pretty-print results in a text table
    printResults(title, threadings);
  }

  private void runOnEnvironments(
      Setup setup, String[] environments, Threading[] threadings)
      throws IOException {
    if (setup.isDbSetup()) {
      for (String environment : environments) {
        runSetup(setup, environment, threadings);
      }
    } else {
      logger.info("Skipping plain test since running in DB test mode");
    }
  }

  void runSetup(Setup setup, String environment, Threading[] threadings) throws IOException {
    logger.info("------------------------------------------------------------" +
        "------------------------------------------------------------");
    logger.info("Running {}", setup.fileName);
    SetupApplication application = new SetupApplication(environment);
    setup.applications.add(application);
    long initialCount = setup.count;
    for (int iT = 0; iT < threadings.length; iT++) {
      Threading threading = threadings[iT];
      if (!threading.ee && setup.reqEE) {
        // ee feature in ce -> no go
        application.setResult(iT, null);
      } else {
        // feature is available
        List<SensorResult> results = runUntilMinDuration(setup.fileName, environment, config.getMinSecs(), initialCount, threading);
        for (SensorResult result : results) {
          application.setResult(iT, result);
        }
        initialCount = results.get(0).count;
      }
    }
  }

  private List<SensorResult> runUntilMinDuration(
      String fileName, String environment, long minDurationSecs, long countBase, Threading threading) throws IOException {
    long count = countBase;
    long minDurationMillis = minDurationSecs * 1000;
    do {
      AtomicLong maxFileSize = new AtomicLong(0);
      List<SensorResult> executions = runFile(fileName, environment, count, threading, maxFileSize);
      int actualMinDuration = minDurationOf(executions);
      if (actualMinDuration >= minDurationMillis) {
        return executions;
      }
      if (maxFileSize.get() > ONE_GIGABYTE) {
        logger.warn("Restricting benchmark execution due to an excessive file size of {} MB",
            maxFileSize.get() / 1000000);
        return executions;
      }
      count = scaleToMinDuration(count, minDurationMillis, actualMinDuration);
    } while (true);
  }

  private List<SensorResult> runFile(String fileName, String environment,
      long count, Threading threading, AtomicLong maxFileSize) throws IOException {
    if (threading.ee) {
      BeneratorFactory.setInstance((BeneratorFactory) BeanUtil.newInstance(EE_BENERATOR_FACTORY));
    } else {
      BeneratorFactory.setInstance(new DefaultBeneratorFactory());
    }
    logger.debug("Testing {} with count {} and {} thread(s)", fileName, count, threading);
    if (config.getMinSecs() == 0) { // unit test setup should run quickly
      count = 1;
    }
    File envFile = prepareEnvFile(environment);
    String filename = prepareXml(fileName, environment, count, threading.threads);
    CounterRepository.getInstance().clear();
    BeneratorUtil.checkSystem(new LoggingInfoPrinter(getClass()));
    BeneratorRootContext context = BeneratorFactory.getInstance().createRootContext(IOUtil.getParentUri(filename));
    File[] generatedFiles;
    try (DescriptorRunner runner = new DescriptorRunner(filename, context)) {
      runner.run();
      generatedFiles = getGeneratedFiles();
      for (File generatedFile : generatedFiles) {
        if (generatedFile.length() > maxFileSize.get()) {
          maxFileSize.set(generatedFile.length());
        }
      }
    }
    deleteArtifacts(filename, envFile, generatedFiles);
    return evaluateSensors(fileName, count, threading.threads);
  }


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

  private void printResults(String[] title, Threading[] threadings) {
    Object[][] table = createTable(threadings);
    printTable(title, table);
  }

  private Object[][] createTable(Threading[] threadings) {
    // create table
    ArrayBuilder<Object[]> table1 = new ArrayBuilder<>(Object[].class);
    // format table header
    Object[] header = createColumnHeaders(threadings);
    table1.add(header);
    ArrayBuilder<Object[]> table = table1;
    for (Setup setup : SETUPS) {
      for (SetupApplication application : setup.applications) {
        Collection<String> sensors = application.getSensors();
        for (String sensor : sensors) {
          Object[] row = newRow(threadings.length + 1, table);
          row[0] = rowHeader(setup, application.environment, sensor, sensors.size());
          List<SensorResult> results = application.sensorMeasurements.get(sensor);
          for (int i = 0; i < results.size(); i++) {
            SensorResult result = results.get(i);
            row[1 + i] = (result != null ? format(result.entitiesPerHour()) : "N/A");
          }
        }
      }
    }
    return table.toArray();
  }

  private Object[] createColumnHeaders(Threading[] threadings) {
    Object[] header  = new Object[threadings.length + 1];
    header[0] = "Benchmark";
    for (int i = 0; i < threadings.length; i++) {
      Threading threading = threadings[i];
      if (!threading.ee) {
        header[i + 1] = "CE";
      } else if (threading.threads > 1) {
        header[i + 1] = "EE\n" + threading.threads + " Threads";
      } else {
        header[i + 1] = "EE\n1 Thread";
      }
    }
    return header;
  }

  private void printTable(String[] title, Object[][] cells) {
    int cols = cells[0].length;
    Alignment[] alignments = new Alignment[cols];
    alignments[0] = Alignment.LEFT;
    for (int i = 1; i < cols; i++) {
      alignments[i] = Alignment.RIGHT;
    }
    System.out.println(TextUtil.formatLinedTable(title, cells, alignments, true));
  }

  private String[] createAndPrintTitle() {
    ArrayBuilder<String> builder = new ArrayBuilder<>(String.class);
    builder.addAll(new String[] {
        "Benchmark throughput of Benerator " + getVersionInfo().getVersion(),
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
    for (String line : title) {
      logger.debug("{}", line);
    }
    return title;
  }

  private VersionInfo getVersionInfo() {
    return VersionInfo.getInfo("benerator");
  }

  private Object[] newRow(int cellCount, ArrayBuilder<Object[]> table) {
    Object[] tableRow = new Object[cellCount];
    table.add(tableRow);
    return tableRow;
  }

  private Threading[] chooseThreadCounts() {
    int maxThreads = config.getMaxThreads();
    if (!config.isEe() || (!config.isCe() && maxThreads == 1)) {
      return new Threading[] { new Threading(config.isEe(), 1) };
    } else {
      // determine thread counts for EE
      TreeSet<Integer> set = new TreeSet<>();
      for (int i = 1; i < maxThreads; i *= 2) {
        set.add(i);
      }
      set.add(maxThreads);
      // create result object
      // add an element if a CE run is requested additionally to the EE runs
      int ceRun = (config.isCe() ? 1 : 0);
      Threading[] result = new Threading[set.size() + ceRun];
      // insert the CE run first...
      if (ceRun == 1) {
        result[0] = new Threading(false, 1);
      }
      // ...and then the EE runs with creasing thread count
      int i = ceRun;
      for (int n : set) {
        result[i++] = new Threading(true, n);
      }
      return result;
    }
  }

  private long scaleToMinDuration(long count, long minDurationMillis, int actualMinDuration) {
    double factor = (double) minDurationMillis / actualMinDuration;
    if (factor > 5) {
      factor = 5;
    } else {
      factor *= 1.1;
    }
    count = (long) (factor * count + 500) / 1000 * 1000;
    return count;
  }

  private String rowHeader(Setup setup, String environment, String sensor, int sensorCount) {
    String label = setup.fileName;
    if (sensorCount > 1) {
      label += " " + sensor;
    }
    if (environment != null) {
      label += " @ " + environment;
    }
    return label;
  }

  private static int minDurationOf(List<SensorResult> sensorResults) {
    if (CollectionUtil.isEmpty(sensorResults)) {
      throw new ConfigurationError("No sensors found");
    }
    int result = sensorResults.get(0).duration;
    for (int i = 1; i < sensorResults.size(); i++) {
      int tmp = sensorResults.get(i).duration;
      if (tmp < result) {
        result = tmp;
      }
    }
    return result;
  }

  private File prepareEnvFile(String environment) throws IOException {
    if ("h2".equals(environment) || "hsqlmem".equals(environment)) {
      String envFileName = environment + ".env.properties";
      IOUtil.copyFile("com/rapiddweller/benerator/benchmark/" + envFileName, envFileName);
      return new File(envFileName);
    } else {
      return null;
    }
  }

  private String prepareXml(String fileName, String environment, long count, int threads) throws IOException {
    String xml = IOUtil.getContentOfURI("com/rapiddweller/benerator/benchmark/" + fileName);
    xml = applyEnvironment(environment, xml);
    xml = xml.replace("{count}", String.valueOf(count));
    xml = xml.replace("{threads}", String.valueOf(threads));
    String filename = "__benchmark.ben.xml";
    IOUtil.writeTextFile(filename, xml);
    return filename;
  }

  private static void deleteArtifacts(String benFile, File envFile, File[] generatedFiles) {
    FileUtil.deleteIfExists(new File(benFile));
    if (envFile != null) {
      FileUtil.deleteIfExists(envFile);
    }
    for (File generatedFile : generatedFiles) {
      FileUtil.deleteIfExists(generatedFile);
    }
  }

  private static File[] getGeneratedFiles() {
    return new File(".").listFiles((dir, name) -> name.startsWith("__benchmark.out"));
  }

  private static List<SensorResult> evaluateSensors(String fileName, long count, int threads) {
    List<SensorResult> result = new ArrayList<>();
    Set<Map.Entry<String, LatencyCounter>> counters = CounterRepository.getInstance().getCounters();
    for (Map.Entry<String, LatencyCounter> counter : counters) {
      String key = counter.getKey();
      if (key.startsWith("benchmark.")) {
        LatencyCounter value = counter.getValue();
        Assert.equals(1, (int) value.sampleCount());
        long latency = (value.totalLatency() > 0 ? value.totalLatency() : 1);
        long eps = count * 1000 / latency;
        if (logger.isInfoEnabled()) {
          logger.info("{}: {} entities / {} ms, throughput {} E/s - {} ME/h, {} thread{}", key, count,
              value.totalLatency(), eps, format(eps * 3600. / 1000000.), threads, (threads > 1 ? "s" : ""));
        }
        String sensor = "[" + (key.substring("benchmark.".length())) + "]";
        result.add(new SensorResult(fileName, sensor, count, threads, (int) value.totalLatency()));
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
    p.addFlag("list", "--list", null);
    p.addOption("mode", "--mode", "-m");
    p.addOption("minSecs", "--minSecs", null);
    p.addOption("maxThreads", "--maxThreads", null);
    p.addOption("environmentSpec", "--env", null);
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
    return config;
  }

  private static void listTests() {
    System.out.println("Available Benchmark tests:");
    for (Setup setup : SETUPS) {
      System.out.println(setup.name + ": " + setup.description);
    }
  }

  public static Setup getSetup(String setupName) {
    for (Benchmark.Setup setup : Benchmark.SETUPS) {
      if (setup.name.equals(setupName)) {
        return setup;
      }
    }
    throw new ObjectNotFoundException("Found no setup of name '" + setupName + "'");
  }

  public static class Setup {
    public final String name;
    public final String fileName;
    public final boolean reqEE;
    public final VersionNumber requiredVersion;
    public final int count;
    public final List<SetupApplication> applications;
    public final String description;

    public Setup(String name, boolean reqEE,  String requiredVersion, int count, String description) {
      this.name = name;
      this.fileName = name + ".ben.xml";
      this.reqEE = reqEE;
      this.requiredVersion = new VersionNumberParser().parse(requiredVersion);
      this.count = count;
      this.description = description;
      this.applications = new ArrayList<>();
    }

    public boolean isDbSetup() {
      return name.startsWith("db-");
    }
  }

  public static class SetupApplication {
    final String environment;
    public OrderedMap<String, List<SensorResult>> sensorMeasurements;

    public SetupApplication(String environment) {
      this.environment = environment;
      this.sensorMeasurements = new OrderedMap<>();
    }

    public void setResult(int iT, SensorResult result) {
      if (result != null) {
        List<SensorResult> results = sensorMeasurements.computeIfAbsent(result.sensor, k -> new ArrayList<>());
        while (results.size() < iT + 1) {
          results.add(null);
        }
        results.set(iT, result);
      }
    }

    public Collection<String> getSensors() {
      return sensorMeasurements.keySet();
    }
  }

  public static class Threading {
    boolean ee;
    int threads;

    public Threading(boolean ee, int threads) {
      this.ee = ee;
      this.threads = threads;
    }
  }

  public static class SensorResult {
    public final String filename;
    public final String sensor;
    public final long count;
    public final int threads;
    public final int duration;

    public SensorResult(String filename, String sensor, long count, int threads, int duration) {
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
