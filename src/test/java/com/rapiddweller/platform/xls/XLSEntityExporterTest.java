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

import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link XLSEntityExporter}.<br/>
 * <br/>
 * Created at 14.03.2009 07:27:34
 *
 * @author Volker Bergmann
 * @since 0.5.8
 */
public class XLSEntityExporterTest extends XLSTest {

  private static final boolean CLEAN_UP = true;

  /**
   * The constant STANDARD_FILE.
   */
  protected static final File STANDARD_FILE = new File("export.xls");
  /**
   * The constant CUSTOM_FILE.
   */
  protected static final File CUSTOM_FILE = new File("target", XLSEntityExporterTest.class.getSimpleName() + ".xls");

  // tests -----------------------------------------------------------------------------------------------------------

  /**
   * Test empty standard.
   */
  @Test
  public void testEmptyStandard() {
    try {
      XLSEntityExporter exporter = new XLSEntityExporter();
      exporter.close();
      assertTrue(STANDARD_FILE.exists());
    } finally {
      if (CLEAN_UP) {
        FileUtil.deleteIfExists(STANDARD_FILE);
      }
    }
  }

  /**
   * Test single entity.
   *
   * @throws Exception the exception
   */
  @Test
  public void testSingleEntity() throws Exception {
    try {
      XLSEntityExporter exporter = new XLSEntityExporter(CUSTOM_FILE.getAbsolutePath());
      consumeProducts(exporter);
      exporter.close();
      assertFullContent(CUSTOM_FILE);
    } finally {
      if (CLEAN_UP) {
        FileUtil.deleteIfExists(CUSTOM_FILE);
      }
    }

  }

  /**
   * Test two entities.
   *
   * @throws Exception the exception
   */
  @Test
  public void testTwoEntities() throws Exception {
    try {
      XLSEntityExporter exporter = new XLSEntityExporter(CUSTOM_FILE.getAbsolutePath());
      consumeProducts(exporter);
      consumePersons(exporter);
      exporter.close();
      assertFullContent(CUSTOM_FILE);
    } finally {
      if (CLEAN_UP) {
        FileUtil.deleteIfExists(CUSTOM_FILE);
      }
    }
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  private static void consumeProducts(XLSEntityExporter exporter) {
    exporter.startProductConsumption(PROD1);
    exporter.finishProductConsumption(PROD1);
    exporter.startProductConsumption(PROD2);
    exporter.finishProductConsumption(PROD2);
  }

  private static void consumePersons(XLSEntityExporter exporter) {
    exporter.startProductConsumption(PERSON1);
    exporter.finishProductConsumption(PERSON1);
  }

  private static void assertFullContent(File file) throws IOException {
    assertTrue(file.exists());
    HSSFSheet sheet = readFirstSheetOf(CUSTOM_FILE);
    checkCells(sheet.getRow(0), "ean", "price", "date", "avail", "updated", null);
    checkCells(sheet.getRow(1), EAN1, PRICE1, DATE1, AVAIL1, UPDATED1, null);
    checkCells(sheet.getRow(2), EAN2, PRICE2, DATE2, AVAIL2, UPDATED2, null);
    checkCells(sheet.getRow(3));
  }

  private static HSSFSheet readFirstSheetOf(File file) throws IOException {
    HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
    return wb.getSheetAt(0);
  }

  private static void checkCells(HSSFRow row, Object... values) {
    if (ArrayUtil.isEmpty(values)) {
      assertNull(row);
    }
    for (int i = 0; i < values.length; i++) {
      assert row != null;
      HSSFCell cell = row.getCell(i);
      Object expectedContent = values[i];
      if (expectedContent == null) {
        assertNull(cell);
      } else if (expectedContent instanceof String) {
        assertEquals(CellType.STRING, cell.getCellType());
        assertEquals(expectedContent, cell.getStringCellValue());
      } else if (expectedContent instanceof Number) {
        assertEquals(CellType.NUMERIC, cell.getCellType());
        assertEquals(((Number) expectedContent).doubleValue(), cell.getNumericCellValue(), 0);
      } else if (expectedContent instanceof Boolean) {
        assertEquals(CellType.BOOLEAN, cell.getCellType());
        assertEquals(expectedContent, cell.getBooleanCellValue());
      } else if (expectedContent instanceof Date) {
        assertEquals(CellType.NUMERIC, cell.getCellType());
        assertEquals(((Date) expectedContent).getTime() / 1000, cell.getDateCellValue().getTime() / 1000); // cut off milliseconds
      } else {
        throw new RuntimeException("Type not supported: " + expectedContent.getClass());
      }
    }
  }

}
