/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.TextUtil;
import com.rapiddweller.common.format.Alignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.Collection;

/**
 * Prints benchmark results to the consolse.<br/><br/>
 * Created: 16.11.2021 10:21:29
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class TextTableResultExporter extends AbstractBenchmarkResultExporter {

  private static final Logger logger = LoggerFactory.getLogger(TextTableResultExporter.class);

  private final PrintStream printer;

  public TextTableResultExporter(PrintStream printer) {
    this.printer = printer;
  }

  public void export(BenchmarkToolReport result) {
    String[] title = createAndPrintInfos(result);
    Object[][] table = createTable(result);
    printTable(title, table);
  }

  private static String[] createAndPrintInfos(BenchmarkToolReport result) {
    ArrayBuilder<String> builder = new ArrayBuilder<>(String.class);
    builder.addAll(formatInfo(result));
    String[] title = builder.toArray();
    for (String line : title) {
      logger.debug("{}", line);
    }
    return title;
  }

  private static Object[][] createTable(BenchmarkToolReport result) {
    // create table
    ArrayBuilder<Object[]> table = new ArrayBuilder<>(Object[].class);
    // format table header
    ExecutionMode[] executionModes = result.getExecutionModes();
    Object[] header = formatColumnHeaders(executionModes, true);
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
          row[i] = (sensorResult != null ? PerformanceFormatter.format(sensorResult.entitiesPerHour()) : "N/A");
          i++;
        }
      }
    }
    return table.toArray();
  }

  private void printTable(String[] title, Object[][] cells) {
    int cols = cells[0].length;
    Alignment[] alignments = new Alignment[cols];
    alignments[0] = Alignment.LEFT;
    for (int i = 1; i < cols; i++) {
      alignments[i] = Alignment.RIGHT;
    }
    printer.println(TextUtil.formatLinedTable(title, cells, alignments, true));
  }

  private static Object[] newRow(int cellCount, ArrayBuilder<Object[]> table) {
    Object[] tableRow = new Object[cellCount];
    table.add(tableRow);
    return tableRow;
  }

}
