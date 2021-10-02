/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.platform.xls;

import com.rapiddweller.benerator.consumer.FileExporter;
import com.rapiddweller.benerator.consumer.FormattingConsumer;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.format.xls.XLSUtil;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.script.PrimitiveType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Exports entities to Excel sheets.<br/><br/>
 * Created at 07.05.2008 13:31:15
 * @author Volker Bergmann
 * @since 0.5.3
 */
public class XLSEntityExporter extends FormattingConsumer implements FileExporter {

  private static final Logger logger = LoggerFactory.getLogger(XLSEntityExporter.class);

  // defaults --------------------------------------------------------------------------------------------------------

  private static final String DEFAULT_URI = "export.xls";

  // attributes ------------------------------------------------------------------------------------------------------

  private String uri;
  private HSSFWorkbook workbook;

  // constructors ----------------------------------------------------------------------------------------------------

  public XLSEntityExporter() {
    this(DEFAULT_URI);
  }

  public XLSEntityExporter(String uri) {
    this.uri = uri;
    setDatePattern("m/d/yy");
    setDecimalPattern("#,##0.##");
    setIntegralPattern("0");
    setTimePattern("h:mm:ss");
    setTimestampPattern("m/d/yy h:mm");
  }

  // properties ------------------------------------------------------------------------------------------------------

  private static Set<Entry<String, Object>> getComponents(Entity entity) {
    return entity.getComponents().entrySet();
  }

  @Override
  public String getUri() {
    return uri;
  }

  // Consumer interface ----------------------------------------------------------------------------------------------

  public void setUri(String uri) {
    this.uri = uri;
  }

  @Override
  public void startProductConsumption(Object object) {
    logger.debug("exporting {}", object);
    if (!(object instanceof Entity)) {
      throw new IllegalArgumentException("Expecting Entity");
    }
    Entity entity = (Entity) object;
    HSSFSheet sheet = getOrCreateSheet(entity);
    HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
    int i = 0;
    for (Map.Entry<String, Object> component : getComponents(entity)) {
      render(row, i++, component.getValue());
    }
  }

  // private helpers -------------------------------------------------------------------------------------------------

  @Override
  public void close() {
    FileOutputStream out = null;
    try {
      if (workbook == null) {
        workbook =
            new HSSFWorkbook(); // if no data was added, create an empty Excel document
      } else {
        XLSUtil.autoSizeColumns(workbook);
      }

      File directory = new File(uri);
      // check if path exists, if not make sure it exists
      if (directory.getParent() != null
          && !directory.isDirectory()
          && !directory.getParentFile().exists()) {
        boolean result = directory.getParentFile().mkdirs();
        if (!result) {
          throw new ConfigurationError("filepath does not exists and can not be created ...");
        }
      }

      // Write the output to a file
      out = new FileOutputStream(uri);


      workbook.write(out);
    } catch (FileNotFoundException e) {
      throw new ConfigurationError(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      IOUtil.close(out);
    }
  }

  private HSSFSheet getOrCreateSheet(Entity entity) {
    // create file
    if (workbook == null) {
      createWorkbook();
    }
    String sheetName = entity.type();
    HSSFSheet sheet = workbook.getSheet(sheetName);
    if (sheet == null) {
      sheet = workbook.createSheet(sheetName);
      writeHeaderRow(entity, sheet);
    }
    return sheet;
  }

  private void createWorkbook() {
    this.workbook = new HSSFWorkbook();
    HSSFCellStyle dateCellStyle = workbook.createCellStyle();
    HSSFDataFormat format = workbook.createDataFormat();
    short dateFormat = format.getFormat(getDatePattern());
    dateCellStyle.setDataFormat(dateFormat);
  }

  private void writeHeaderRow(Entity entity, HSSFSheet sheet) {
    HSSFRow headerRow = sheet.createRow(0);
    int colnum = 0;
    for (Map.Entry<String, Object> component : getComponents(entity)) {
      String componentName = component.getKey();
      headerRow.createCell(colnum)
          .setCellValue(new HSSFRichTextString(componentName));
      ComponentDescriptor cd =
          entity.descriptor().getComponent(componentName);
      PrimitiveType primitiveType;
      if (cd.getTypeDescriptor() instanceof SimpleTypeDescriptor) {
        primitiveType = ((SimpleTypeDescriptor) cd.getTypeDescriptor())
            .getPrimitiveType();
      } else {
        throw new UnsupportedOperationException(
            "Can only export simple type attributes, " +
                "failed to export " + entity.type() + '.' +
                cd.getName());
      }
      Class<?> javaType =
          (primitiveType != null ? primitiveType.getJavaType() :
              String.class);
      String formatString = null;
      if (BeanUtil.isIntegralNumberType(javaType)) {
        formatString = getIntegralPattern();
      } else if (BeanUtil.isDecimalNumberType(javaType)) {
        formatString = getDecimalPattern();
      } else if (Time.class.isAssignableFrom(javaType)) {
        formatString = getTimePattern();
      } else if (Timestamp.class.isAssignableFrom(javaType)) {
        formatString = getTimestampPattern();
      } else if (Date.class.isAssignableFrom(javaType)) {
        formatString = getDatePattern();
      }
      if (formatString != null) {
        HSSFDataFormat dataFormat = workbook.createDataFormat();
        CellStyle columnStyle = workbook.createCellStyle();
        columnStyle.setDataFormat(dataFormat.getFormat(formatString));
        sheet.setDefaultColumnStyle(colnum, columnStyle);
      }
      colnum++;
    }
  }

  private void render(HSSFRow row, int column, Object value) {
    HSSFCell cell = row.createCell(column);
    if (value instanceof Number) {
      cell.setCellValue(((Number) value).doubleValue());
    } else if (value instanceof Date) {
      cell.setCellValue((Date) value);
    } else if (value instanceof Boolean) {
      cell.setCellValue((Boolean) value);
    } else {
      String s = plainConverter.convert(value);
      cell.setCellValue(new HSSFRichTextString(s));
    }
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + '(' + uri + ")";
  }

  @Override
  public int hashCode() {
    return uri.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    XLSEntityExporter that = (XLSEntityExporter) obj;
    return (this.uri.equals(that.uri));
  }

}
