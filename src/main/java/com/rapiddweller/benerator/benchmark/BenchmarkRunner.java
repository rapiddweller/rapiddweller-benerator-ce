/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.engine.BeneratorRootContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorFactory;
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.benerator.environment.EnvironmentUtil;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.log.LoggingInfoPrinter;
import com.rapiddweller.jdbacl.DatabaseDialect;
import com.rapiddweller.stat.CounterRepository;
import com.rapiddweller.stat.LatencyCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.rapiddweller.benerator.BeneratorUtil.EE_BENERATOR_FACTORY;

/**
 * Runs all test configurations as defined in a {@link BenchmarkToolConfig}.<br/><br/>
 * Created: 02.11.2021 09:38:50
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BenchmarkRunner {

  private static final Logger logger = LoggerFactory.getLogger(BenchmarkRunner.class);

  public static final long ONE_GIGABYTE = 1000000000L;
  public static final String TMP_FILENAME = "__benchmark.ben.xml";

  private BenchmarkRunner() {
    // private constructor to prevent instantiation of this utility class
  }

  public static BenchmarkToolReport runBenchmarks(BenchmarkToolConfig config) throws IOException {
    // perform tests
    BenchmarkToolReport result = new BenchmarkToolReport(config);
    Benerator.setMode(config.getMode());
    for (Benchmark benchmark : config.getBenchmarks()) {
      runBenchmark(benchmark, result);
    }
    return result.stop();
  }

  private static void runBenchmark(Benchmark benchmark, BenchmarkToolReport report) throws IOException {
    if (benchmark.isDb()) {
      SystemRef[] dbs = report.getSystems("db");
      if (dbs.length > 0) {
        runBenchmarkOnEnvironments(benchmark, dbs, report);
      } else {
        logger.info("Skipping DB test since no database was specified");
      }
    } else if (benchmark.isKafka()) {
      SystemRef[] kafkas = report.getSystems("kafka");
      if (kafkas.length > 0) {
        runBenchmarkOnEnvironments(benchmark, kafkas, report);
      } else {
        logger.info("Skipping Kafka test since no Kafka cluster was specified");
      }
    } else if (report.getSystems().length == 0) {
      runBenchmarkOnEnvironment(benchmark, null, report);
    }
  }

  private static void runBenchmarkOnEnvironments(Benchmark benchmark, SystemRef[] systems,
                                                 BenchmarkToolReport summary) throws IOException {
    if (benchmark.isDb()) {
      for (SystemRef system : systems) {
        if (system.isDb()) {
          runBenchmarkOnEnvironment(benchmark, system, summary);
        }
      }
    } else if (benchmark.isKafka()) {
      for (SystemRef system : systems) {
        if (system.isKafka()) {
          runBenchmarkOnEnvironment(benchmark, system, summary);
        }
      }
    } else {
      logger.info("Skipping plain test since running in environment test mode");
    }
  }

  public static void runBenchmarkOnEnvironment(Benchmark benchmark, SystemRef environment, BenchmarkToolReport summary) throws IOException {
    logger.info("Running {}", benchmark.getFileName());
    BenchmarkResult benchmarkResult = new BenchmarkResult(benchmark, environment);
    summary.addResult(benchmarkResult);
    long initialCount = benchmark.getInitialCount();
    ExecutionMode[] executionModes = summary.getExecutionModes();
    for (ExecutionMode executionMode : executionModes) {
      if (executionMode.isEe() || !benchmark.isReqEE()) {
        String filePath = summary.getProjectFolder() + SystemInfo.getFileSeparator() + benchmark.getFileName();
        List<SensorResult> results = runUntilMinDuration(filePath, environment, summary.getMinSecs(), initialCount, executionMode);
        for (SensorResult result : results) {
          benchmarkResult.addResult(result);
        }
        initialCount = results.get(0).getCount();
      }
    }
  }

  private static List<SensorResult> runUntilMinDuration(
      String filePath, SystemRef system, long minDurationSecs, long countBase, ExecutionMode executionMode) throws IOException {
    if (minDurationSecs == 0) {
      // this indicates a unit test, so call it that each thread creates only one product
      return runFile(filePath, system, executionMode.getThreadCount(), executionMode, new AtomicLong());
    }
    // normal test execution
    long count = countBase;
    long minDurationMillis = minDurationSecs * 1000;
    do {
      AtomicLong maxFileSize = new AtomicLong(0);
      List<SensorResult> measurements = runFile(filePath, system, count, executionMode, maxFileSize);
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

  private static List<SensorResult> runFile(String filePath, SystemRef system,
      long count, ExecutionMode executionMode, AtomicLong maxFileSize) throws IOException {
    logger.info("------------------------------------------------------------" +
        "------------------------------------------------------------");
    logger.info("Running {}", filePath);
    if (executionMode.isEe()) {
      BeneratorFactory.setInstance((BeneratorFactory) BeanUtil.newInstance(EE_BENERATOR_FACTORY));
    } else {
      BeneratorFactory.setInstance(new DefaultBeneratorFactory());
    }
    logger.debug("Testing {} with count {} and {} thread(s)", filePath, count, executionMode);
    File envFile = prepareEnvFile(system);
    String tmpFileName = prepareXml(filePath, system, count, executionMode.getThreadCount());
    CounterRepository.getInstance().clear();
    BeneratorUtil.checkSystem(new LoggingInfoPrinter(BenchmarkRunner.class));
    BeneratorRootContext context = BeneratorFactory.getInstance().createRootContext(IOUtil.getParentUri(tmpFileName));
    File[] generatedFiles;
    try (DescriptorRunner runner = new DescriptorRunner(tmpFileName, context)) {
      runner.run();
      generatedFiles = getGeneratedFiles();
      for (File generatedFile : generatedFiles) {
        logger.info("Generated file {} has length {}", generatedFile, generatedFile.length());
        if (generatedFile.length() > maxFileSize.get()) {
          maxFileSize.set(generatedFile.length());
        }
      }
    }
    deleteArtifacts(tmpFileName, envFile, generatedFiles);
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

  private static File prepareEnvFile(SystemRef system) throws IOException {
    if (system != null && system.isDb() && "builtin".equals(system.getEnvironment().getName())) {
      String envFileName = EnvironmentUtil.fileName(system.getEnvironment().getName());
      IOUtil.copyFile("com/rapiddweller/benerator/benchmark/" + envFileName, envFileName);
      return new File(envFileName);
    }
    return null;
  }

  private static String prepareXml(String filePath, SystemRef system, long count, int threads) throws IOException {
    String xml = IOUtil.getContentOfURI(filePath);
    xml = xml.replace("{count}", String.valueOf(count));
    xml = xml.replace("{threads}", String.valueOf(threads));
    if (system != null) {
      xml = applyEnvironment(system, xml);
      if (system.isDb()) {
        xml = xml.replace("{writeCount}", String.valueOf(count));
        xml = xml.replace("{readCount}", String.valueOf(2 * count));
      }
    }
    String filename = TMP_FILENAME;
    IOUtil.writeTextFile(filename, xml);
    return filename;
  }

  private static String applyEnvironment(SystemRef system, String xml) {
    if (system != null) {
      xml = xml.replace("{environment}", system.getEnvironment().getName());
      xml = xml.replace("{system}", system.getName());
      if (system.isDb()) {
        DatabaseDialect dialect = EnvironmentUtil.getDbDialect(system);
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
              latencyCount.totalLatency(), eps, PerformanceFormatter.format(eps * 3600. / 1000000.),
              threads, (threads > 1 ? "s" : ""));
        }
        result.add(new SensorResult(sensor, countUsed, executionMode, (int) latencyCount.totalLatency()));
      }
    }
    return result;
  }

}
