/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.common.Assert;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.format.xls.XLSUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * Writes benchmark results to an XLS file.<br/><br/>
 * Created: 16.11.2021 12:28:08
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class XLSBenchmarkExporter extends AbstractBenchmarkResultExporter {

  private final String path;

  private HSSFCellStyle infoTopStyle;
  private HSSFCellStyle infoCenterStyle;
  private HSSFCellStyle infoBottomStyle;

  private HSSFCellStyle colHeadStyle;
  private HSSFCellStyle rowHeadStyle;
  private HSSFCellStyle bigCellStyle;
  private HSSFCellStyle smallCellStyle;

  public XLSBenchmarkExporter(String path) {
    this.path = Assert.notNull(path, "path");
  }

  @Override
  public void export(BenchmarkToolReport result) throws IOException {
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFDataFormat format = workbook.createDataFormat();
    createStyles(workbook, format);
    HSSFSheet sheet = workbook.createSheet("Results");
    exportInfo(result, sheet);
    exportPerformance(result, sheet);
    XLSUtil.autoSizeColumns(workbook);
    saveWorkbook(workbook);
  }

  private void createStyles(HSSFWorkbook workbook, HSSFDataFormat format) {
    infoTopStyle = workbook.createCellStyle();
    infoTopStyle.setBorderLeft(BorderStyle.THIN);
    infoTopStyle.setBorderTop(BorderStyle.THIN);
    infoTopStyle.setBorderRight(BorderStyle.THIN);

    infoCenterStyle = workbook.createCellStyle();
    infoCenterStyle.setBorderLeft(BorderStyle.THIN);
    infoCenterStyle.setBorderRight(BorderStyle.THIN);

    infoBottomStyle = workbook.createCellStyle();
    infoBottomStyle.setBorderLeft(BorderStyle.THIN);
    infoBottomStyle.setBorderBottom(BorderStyle.THIN);
    infoBottomStyle.setBorderRight(BorderStyle.THIN);

    short bigFormat = format.getFormat("#,##0");
    bigCellStyle = workbook.createCellStyle();
    bigCellStyle.setDataFormat(bigFormat);
    addBorders(bigCellStyle);

    short smallFormat = format.getFormat("0.#");
    smallCellStyle = workbook.createCellStyle();
    smallCellStyle.setDataFormat(smallFormat);
    addBorders(smallCellStyle);

    colHeadStyle = workbook.createCellStyle();
    addBorders(colHeadStyle);
    colHeadStyle.setAlignment(HorizontalAlignment.CENTER);
    colHeadStyle.setWrapText(true);

    rowHeadStyle = workbook.createCellStyle();
    addBorders(rowHeadStyle);
  }

  private void addBorders(HSSFCellStyle style) {
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
  }

  private void saveWorkbook(HSSFWorkbook workbook) throws IOException {
    File file = new File(path);
    FileUtil.ensureDirectoryExists(file.getParentFile());
    FileOutputStream out = new FileOutputStream(file);
    workbook.write(out);
    out.close();
  }

  private void exportInfo(BenchmarkToolReport result, HSSFSheet sheet) {
    int colCount = result.getExecutionModes().length + 1;
    String[] infos = formatInfo(result);
    for (int i = 0; i < infos.length; i++) {
      HSSFCellStyle style;
      if (i == 0) {
        style = infoTopStyle;
      } else if (i < infos.length - 1) {
        style = infoCenterStyle;
      } else {
        style = infoBottomStyle;
      }
      String info = infos[i];
      addInfoRow(info, style, colCount, sheet);
    }
    addInfoRow("", null, colCount, sheet);
  }

  private void exportPerformance(BenchmarkToolReport result, HSSFSheet sheet) {
    // export headers
    ExecutionMode[] executionModes = result.getExecutionModes();
    Object[] headers = formatColumnHeaders(executionModes, false);
    addRow(headers, colHeadStyle, sheet);
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
          row[i] = (sensorResult != null ? sensorResult.entitiesPerHour() : "N/A");
          i++;
        }
        addRow(row, rowHeadStyle, sheet);
      }
    }
  }

  private static void addInfoRow(String content, HSSFCellStyle style, int colCount, HSSFSheet sheet) {
    HSSFRow row = addRow(sheet);
    HSSFCell cell = row.createCell(0);
    cell.setCellValue(content);
    if (style != null) {
      cell.setCellStyle(style);
      for (int i = 1; i < colCount; i++) {
        row.createCell(i).setCellStyle(style);
      }
    }
    int rowNum = row.getRowNum();
    CellRangeAddress mergedRegion = new CellRangeAddress(rowNum, rowNum, 0, colCount - 1);
    sheet.addMergedRegion(mergedRegion);
  }

  private void addRow(Object[] cells, HSSFCellStyle style, HSSFSheet sheet) {
    HSSFRow row = addRow(sheet);
    for (int i = 0; i < cells.length; i++) {
      HSSFCell cell = row.createCell(i);
      Object value = cells[i];
      if (value instanceof Number) {
        double d = ((Number) value).doubleValue();
        if (d < 10) {
          cell.setCellStyle(smallCellStyle);
        } else {
          cell.setCellStyle(bigCellStyle);
        }
        cell.setCellValue(d);
      } else {
        cell.setCellValue(String.valueOf(value));
        if (style != null) {
          cell.setCellStyle(style);
        }
      }
    }
  }

  private static HSSFRow addRow(HSSFSheet sheet) {
    return sheet.createRow(sheet.getLastRowNum() + 1);
  }

}
