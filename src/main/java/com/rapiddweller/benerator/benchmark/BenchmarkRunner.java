/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.engine.BeneratorRootContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorFactory;
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.log.LoggingInfoPrinter;
import com.rapiddweller.jdbacl.DatabaseDialect;
import com.rapiddweller.stat.CounterRepository;
import com.rapiddweller.stat.LatencyCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.rapiddweller.benerator.BeneratorUtil.EE_BENERATOR_FACTORY;
import static com.rapiddweller.jdbacl.EnvironmentUtil.getDialect;

/**
 * Runs all test configurations as defined in a {@link BenchmarkConfig}.<br/><br/>
 * Created: 02.11.2021 09:38:50
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BenchmarkRunner {

  private static final Logger logger = LoggerFactory.getLogger(BenchmarkRunner.class);

  public static final DecimalFormat FORMAT_1 = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
  public static final DecimalFormat FORMAT_0 = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));
  public static final long ONE_GIGABYTE = 1000000000L;
  private static final Set<String> IN_PROCESS_DBS = CollectionUtil.toSet("h2", "hsqlmem");

  private BenchmarkRunner() {
    // private constructor to prevent instantiation of this utility class
  }

  public static BenchmarkSummary runBenchmarks(BenchmarkConfig config) throws IOException {
    BenchmarkSummary result = new BenchmarkSummary(config);
    // perform tests
    Benerator.setMode(config.getMode());
    for (BenchmarkDefinition benchmark : config.getSetups()) {
      runBenchmark(benchmark, result);
    }
    return result.stop();
  }

  private static void runBenchmark(BenchmarkDefinition benchmark, BenchmarkSummary summary) throws IOException {
    if (benchmark.isDb()) {
      Environment[] environments = summary.getEnvironments(EnvironmentType.DB);
      if (environments.length > 0) {
        runBenchmarkOnEnvironments(benchmark, environments, summary);
      } else {
        logger.info("Skipping DB test since no database was specified");
      }
    } else if (benchmark.isKafka()) {
      Environment[] environments = summary.getEnvironments(EnvironmentType.KAFKA);
      if (environments.length > 0) {
        runBenchmarkOnEnvironments(benchmark, environments, summary);
      } else {
        logger.info("Skipping Kafka test since no Kafka cluster was specified");
      }
    } else if (summary.getEnvironments().length == 0) {
      runBenchmarkOnEnvironment(benchmark, null, summary);
    }
  }

  private static void runBenchmarkOnEnvironments(BenchmarkDefinition benchmark, Environment[] environments,
                                                 BenchmarkSummary summary) throws IOException {
    if (benchmark.isDb()) {
      for (Environment environment : environments) {
        if (environment.isDb()) {
          runBenchmarkOnEnvironment(benchmark, environment, summary);
        }
      }
    } else if (benchmark.isKafka()) {
      for (Environment environment : environments) {
        if (environment.isKafka()) {
          runBenchmarkOnEnvironment(benchmark, environment, summary);
        }
      }
    } else {
      logger.info("Skipping plain test since running in environment test mode");
    }
  }

  public static void runBenchmarkOnEnvironment(BenchmarkDefinition benchmark, Environment environment, BenchmarkSummary summary) throws IOException {
    logger.info("Running {}", benchmark.getFileName());
    BenchmarkResult benchmarkResult = new BenchmarkResult(benchmark, environment);
    summary.addResult(benchmarkResult);
    long initialCount = benchmark.getInitialCount();
    ExecutionMode[] executionModes = summary.getExecutionModes();
    for (ExecutionMode executionMode : executionModes) {
      if (executionMode.isEe() || !benchmark.isReqEE()) {
        List<SensorResult> results = runUntilMinDuration(benchmark.getFileName(), environment, summary.getMinSecs(), initialCount, executionMode);
        for (SensorResult result : results) {
          benchmarkResult.addResult(result);
        }
        initialCount = results.get(0).getCount();
      }
    }
  }

  private static List<SensorResult> runUntilMinDuration(
      String fileName, Environment environment, long minDurationSecs, long countBase, ExecutionMode executionMode) throws IOException {
    if (minDurationSecs == 0) {
      // this indicates a unit test, so call it that each thread creates only one product
      return runFile(fileName, environment, executionMode.getThreadCount(), executionMode, new AtomicLong());
    }
    // normal test execution
    long count = countBase;
    long minDurationMillis = minDurationSecs * 1000;
    do {
      AtomicLong maxFileSize = new AtomicLong(0);
      List<SensorResult> measurements = runFile(fileName, environment, count, executionMode, maxFileSize);
      int actualMinDuration = minDurationOf(measurements);
      if (actualMinDuration >= minDurationMillis) {
        return measurements;
      }
      if (maxFileSize.get() > ONE_GIGABYTE) {
        logger.warn("Restricting benchmark execution due to an excessive file size of {} MB",
            maxFileSize.get() / 1000000);
        return measurements;
      }
      count = scaleToMinDuration(count, minDurationMillis, actualMinDuration, maxFileSize.get());
      if (count == 0) {
        return measurements;
      }
    } while (true);
  }

  private static List<SensorResult> runFile(String fileName, Environment environment,
      long count, ExecutionMode executionMode, AtomicLong maxFileSize) throws IOException {
    logger.info("------------------------------------------------------------" +
        "------------------------------------------------------------");
    logger.info("Running {}", fileName);
    if (executionMode.isEe()) {
      BeneratorFactory.setInstance((BeneratorFactory) BeanUtil.newInstance(EE_BENERATOR_FACTORY));
    } else {
      BeneratorFactory.setInstance(new DefaultBeneratorFactory());
    }
    logger.debug("Testing {} with count {} and {} thread(s)", fileName, count, executionMode);
    File envFile = prepareEnvFile(environment);
    String filename = prepareXml(fileName, environment, count, executionMode.getThreadCount());
    CounterRepository.getInstance().clear();
    BeneratorUtil.checkSystem(new LoggingInfoPrinter(BenchmarkRunner.class));
    BeneratorRootContext context = BeneratorFactory.getInstance().createRootContext(IOUtil.getParentUri(filename));
    File[] generatedFiles;
    try (DescriptorRunner runner = new DescriptorRunner(filename, context)) {
      runner.run();
      generatedFiles = getGeneratedFiles();
      for (File generatedFile : generatedFiles) {
        logger.info("Generated file {} has length {}", generatedFile, generatedFile.length());
        if (generatedFile.length() > maxFileSize.get()) {
          maxFileSize.set(generatedFile.length());
        }
      }
    }
    deleteArtifacts(filename, envFile, generatedFiles);
    return evaluateSensors(executionMode);
  }

  // helper methods --------------------------------------------------------------------------------------------------

  private static int minDurationOf(List<SensorResult> sensorResults) {
    if (CollectionUtil.isEmpty(sensorResults)) {
      throw new ConfigurationError("No sensors found");
    }
    int result = sensorResults.get(0).getDuration();
    for (int i = 1; i < sensorResults.size(); i++) {
      int tmp = sensorResults.get(i).getDuration();
      if (tmp < result) {
        result = tmp;
      }
    }
    return result;
  }

  private static long scaleToMinDuration(
      long count, long minDurationMillis, int actualMinDuration, long recentFileSize) {
    double factor = (double) minDurationMillis / actualMinDuration;
    if (factor > 8) {
      factor = 8;
    } else {
      factor *= 1.1;
    }
    long result = (long) (factor * count + 500) / 1000 * 1000;
    if (result == 0) {
      result = 1000;
    }
    if (recentFileSize * factor > ONE_GIGABYTE) {
      factor = ONE_GIGABYTE * 1.1 / recentFileSize;
      result = (long) (count * factor);
      if (factor < 1.3) {
        return 0;
      }
    }
    return result;
  }

  private static File prepareEnvFile(Environment environment) throws IOException {
    if (environment != null && environment.isDb() && IN_PROCESS_DBS.contains(environment.getName())) {
      String envFileName = environment.getName() + ".env.properties";
      IOUtil.copyFile("com/rapiddweller/benerator/benchmark/" + envFileName, envFileName);
      return new File(envFileName);
    }
    return null;
  }

  private static String prepareXml(String fileName, Environment environment, long count, int threads) throws IOException {
    String xml = IOUtil.getContentOfURI("com/rapiddweller/benerator/benchmark/" + fileName);
    xml = applyEnvironment(environment, xml);
    xml = xml.replace("{count}", String.valueOf(count));
    if (environment != null && environment.isDb()) {
      xml = xml.replace("{writeCount}", String.valueOf(count));
      xml = xml.replace("{readCount}", String.valueOf(2 * count));
    }
    xml = xml.replace("{threads}", String.valueOf(threads));
    String filename = "__benchmark.ben.xml";
    IOUtil.writeTextFile(filename, xml);
    return filename;
  }

  private static String applyEnvironment(Environment environment, String xml) {
    if (environment != null) {
      xml = xml.replace("{environment}", environment.getName());
      if (environment.isDb()) {
        DatabaseDialect dialect = getDialect(environment.getName());
        Set<String> types = CollectionUtil.toSet(
            "varchar", "char", "string", "date", "time", "timestamp", "binary", "boolean",
            "byte", "short", "int", "long", "big_integer", "float", "double", "big_decimal");
        for (String type : types) {
          String specialType = dialect.getSpecialType(type);
          xml = xml.replace('{' + type + '}', specialType);
        }
      }
    }
    return xml;
  }

  private static File[] getGeneratedFiles() {
    return new File(".").listFiles((dir, name) -> name.startsWith("__benchmark.out"));
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

  private static List<SensorResult> evaluateSensors(ExecutionMode executionMode) {
    List<SensorResult> result = new ArrayList<>();
    Set<Map.Entry<String, LatencyCounter>> counters = CounterRepository.getInstance().getCounters();
    for (Map.Entry<String, LatencyCounter> entry : counters) {
      String key = entry.getKey();
      if (key.startsWith("benchmark.")) {
        String sensor = "[" + (key.substring("benchmark.".length())) + "]";
        LatencyCounter latencyCount = entry.getValue();
        long latency = (latencyCount.totalLatency() > 0 ? latencyCount.totalLatency() : 1);
        long countUsed = latencyCount.sampleCount();
        long eps = countUsed * 1000 / latency;
        if (logger.isInfoEnabled()) {
          int threads = executionMode.getThreadCount();
          logger.info("{}: {} entities / {} ms, throughput {} E/s - {} ME/h, {} thread{}", key, countUsed,
              latencyCount.totalLatency(), eps, format(eps * 3600. / 1000000.), threads, (threads > 1 ? "s" : ""));
        }
        result.add(new SensorResult(sensor, countUsed, executionMode, (int) latencyCount.totalLatency()));
      }
    }
    return result;
  }

  private static String format(double number) {
    return (number < 10 ? FORMAT_1 : FORMAT_0).format(number);
  }

}
