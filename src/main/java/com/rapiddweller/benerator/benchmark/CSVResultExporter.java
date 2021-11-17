/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.common.Assert;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.format.csv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Saves Benchmark results in CSV files.<br/><br/>
 * Created: 16.11.2021 10:34:11
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class CSVResultExporter extends AbstractBenchmarkResultExporter {

  public static CSVResultExporter forFile(String csvPath, char separator) {
    return new CSVResultExporter(csvPath, csvPath, separator);
  }

  public static CSVResultExporter forUiResult() {
    return new CSVResultExporter("results/2_Info.csv", "results/1_Performance.csv", '|');
  }

  private final String performancePath;
  private final String infoPath;
  private final char separator;

  public CSVResultExporter(String infoFilePath, String performanceFilePath, char separator) {
    this.infoPath = Assert.notNull(infoFilePath, "info file path");
    this.performancePath = Assert.notNull(performanceFilePath, "performance file path");
    this.separator = separator;
  }

  @Override
  public void export(BenchmarkToolReport result) throws IOException {
    CSVWriter writer = createCSVWriter(infoPath);
    exportInfo(result, writer);
    if (!infoPath.equals(performancePath)) {
      writer.close();
      writer = createCSVWriter(performancePath);
    }
    exportPerformance(result, writer);
    writer.close();
  }

  private CSVWriter createCSVWriter(String path) throws IOException {
    File file = new File(path);
    FileUtil.ensureDirectoryExists(file.getParentFile());
    return new CSVWriter(new FileWriter(file), separator, false);
  }

  private void exportInfo(BenchmarkToolReport result, CSVWriter writer) throws IOException {
    if (!performancePath.equals(infoPath)) {
      writer.writeRow(new Object [] { "Execution Info" });
    }
    for (String info : formatInfo(result)) {
      writer.writeRow(new Object [] { info });
    }
  }

  private void exportPerformance(BenchmarkToolReport result, CSVWriter writer) throws IOException {
    // export headers
    ExecutionMode[] executionModes = result.getExecutionModes();
    writer.writeRow(formatColumnHeaders(executionModes, false));
    // export performance values
    for (BenchmarkResult benchmarkResult : result.getResults()) {
      Collection<String> sensors = benchmarkResult.getSensors();
      for (String sensor : sensors) {
        Object[] row = new Object[executionModes.length + 1];
        row[0] = rowHeader(benchmarkResult, sensor, sensors.size());
        SensorSummary sensorSummary = benchmarkResult.getSensorSummary(sensor);
        int i = 1;
        for (ExecutionMode mode : executionModes) {
          SensorResult sensorResult = sensorSummary.getResult(mode);
          row[i] = (sensorResult != null ? PerformanceFormatter.format(sensorResult.entitiesPerHour()) : "N/A");
          i++;
        }
        writer.writeRow(row);
      }
    }
  }

}
