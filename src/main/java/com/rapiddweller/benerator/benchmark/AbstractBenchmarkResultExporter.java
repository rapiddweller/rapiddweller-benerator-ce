/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.benerator.environment.EnvironmentUtil;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.HF;

/**
 * Abstract parent class to use for {@link BenchmarkResultExporter} implementations.<br/><br/>
 * Created: 16.11.2021 10:43:36
 * @author Volker Bergmann
 * @since 3.0.0
 */
public abstract class AbstractBenchmarkResultExporter implements BenchmarkResultExporter {

  protected AbstractBenchmarkResultExporter() { }

  protected static String[] formatInfo(BenchmarkToolReport result) {
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
    return builder.toArray();
  }

  protected static Object[] formatColumnHeaders(ExecutionMode[] executionModes, boolean insertLf) {
    Object[] header  = new Object[executionModes.length + 1];
    header[0] = "Benchmark";
    for (int i = 0; i < executionModes.length; i++) {
      ExecutionMode executionMode = executionModes[i];
      if (!executionMode.isEe()) {
        header[i + 1] = "CE";
      } else if (executionMode.getThreadCount() > 1) {
        header[i + 1] = "EE" + (insertLf ? '\n' : ' ') + executionMode.getThreadCount() + " Threads";
      } else {
        header[i + 1] = "EE" + (insertLf ? '\n' : ' ') + "1 Thread";
      }
    }
    return header;
  }

  protected static String environmentName(BenchmarkResult result) {
    SystemRef system = result.getSystem();
    return (system != null ? system.toString() : null);
  }

  protected static Object rowHeader(BenchmarkResult benchmarkResult, String sensor, int sensorCount) {
    return rowHeader(benchmarkResult.getBenchmark(), environmentName(benchmarkResult), sensor, sensorCount);
  }

  protected static String rowHeader(Benchmark benchmark, String environment, String sensor, int sensorCount) {
    String label = benchmark.getName();
    if (sensorCount > 1) {
      label += " " + sensor;
    }
    if (environment != null) {
      label += " @ " + environment;
    }
    return label;
  }

}
