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

package com.rapiddweller.benerator.main;

import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.exception.IllegalArgumentError;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.After;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.sql.Connection;

import static com.rapiddweller.common.SystemInfo.isLinux;
import static org.junit.Assert.*;

/**
 * Tests the DBSnapshotTool.<br/><br/>
 * Created at 03.05.2008 11:39:01
 * @author Volker Bergmann
 * @since 0.5.3
 */
public class DBSnaphotToolTest {

  // testing framework -----------------------------------------------------------------------------------------------

  private static final String CREATION_SCRIPT = "com/rapiddweller/benerator/main/create_tables.hsql.sql";
  private static final String DBUNIT_SNAPSHOT_FILE = "target/test.snapshot.dbunit.xml";
  private static final String XLS_SNAPSHOT_FILE = "target/test.snapshot.xls";
  private static final String ENCODING = "iso-8859-15";

  private static final String defaultEncoding = SystemInfo.getFileEncoding();

  private final String dbName = getClass().getSimpleName();

  @After
  public void clearSystemProperties() {
    System.clearProperty(DBSnapshotTool.DB_URL);
    System.clearProperty(DBSnapshotTool.DB_DRIVER);
    System.clearProperty(DBSnapshotTool.DB_USER);
    System.clearProperty(DBSnapshotTool.DB_PASSWORD);
    System.clearProperty(DBSnapshotTool.DB_SCHEMA);
    System.clearProperty(DBSnapshotTool.FORMAT);
    SystemInfo.setFileEncoding(defaultEncoding);
  }

  /** Kind of a @Before method, but only for tests against a valid setup */
  private void setUpSystemProperties(String format) {
    System.setProperty(DBSnapshotTool.DB_URL, HSQLUtil.IN_MEMORY_URL_PREFIX + dbName);
    System.setProperty(DBSnapshotTool.DB_DRIVER, HSQLUtil.DRIVER);
    System.setProperty(DBSnapshotTool.DB_USER, HSQLUtil.DEFAULT_USER);
    System.clearProperty(DBSnapshotTool.DB_PASSWORD);
    System.setProperty(DBSnapshotTool.DB_SCHEMA, HSQLUtil.DEFAULT_SCHEMA);
    System.setProperty(DBSnapshotTool.FORMAT, format);
    System.setProperty("file.encoding", ENCODING);
  }

  // tests -----------------------------------------------------------------------------------------------------------

  @Test(expected = IllegalArgumentError.class)
  public void testMissingUrl() {
    System.setProperty(DBSnapshotTool.DB_URL, "");
    System.setProperty(DBSnapshotTool.DB_DRIVER, HSQLUtil.DRIVER);
    DBSnapshotTool.main(new String[0]);
  }

  @Test(expected = IllegalArgumentError.class)
  public void testMissingDriver() {
    System.setProperty(DBSnapshotTool.DB_URL, HSQLUtil.IN_MEMORY_URL_PREFIX + "benerator");
    System.setProperty(DBSnapshotTool.DB_DRIVER, "");
    DBSnapshotTool.main(new String[0]);
  }

  @Test
  public void testHsqlDbUnitSnapshot() throws Exception {
    Assume.assumeTrue(isLinux());
    // prepare DB
    Connection connection = HSQLUtil.connectInMemoryDB(dbName);
    DBUtil.executeScriptFile(CREATION_SCRIPT, ENCODING, connection, true, new ErrorHandler(getClass()));
    // prepare snapshot
    setUpSystemProperties(DBSnapshotTool.DBUNIT_FORMAT);
    // create snapshot
    DBSnapshotTool.main(new String[] {DBUNIT_SNAPSHOT_FILE});
    File file = new File(DBUNIT_SNAPSHOT_FILE);
    assertTrue(file.exists());
    Document document = XMLUtil.parse(DBUNIT_SNAPSHOT_FILE);
    assertTrue(ENCODING.equalsIgnoreCase(document.getXmlEncoding()));
    Element root = document.getDocumentElement();
    assertEquals("dataset", root.getNodeName());
    assertEquals(1, XMLUtil.getChildElements(root).length);
    Element child = XMLUtil.getChildElement(root, false, true, "T1");
    assertEquals("1", child.getAttribute("ID"));
    assertEquals("R&B", child.getAttribute("NAME"));
  }

  @Test
  public void testHsqlXlsSnapshot() throws Exception {
    Assume.assumeTrue(isLinux());
    // prepare DB
    Connection connection = HSQLUtil.connectInMemoryDB(dbName);
    DBUtil.executeScriptFile(CREATION_SCRIPT, ENCODING, connection, true, new ErrorHandler(getClass()));
    // prepare snapshot
    setUpSystemProperties(DBSnapshotTool.XLS_FORMAT);
    // create snapshot
    DBSnapshotTool.main(new String[] {XLS_SNAPSHOT_FILE});
    File file = new File(XLS_SNAPSHOT_FILE);
    assertTrue("Snapshot file was not created: " + file, file.exists());

    HSSFWorkbook workbook = new HSSFWorkbook(IOUtil.getInputStreamForURI(XLS_SNAPSHOT_FILE));
    HSSFSheet sheet = workbook.getSheet("T1");
    assertNotNull("Sheet T1 not found", sheet);
    HSSFRow headerRow = sheet.getRow(0);
    assertEquals("ID", headerRow.getCell(0).getStringCellValue());
    assertEquals("NAME", headerRow.getCell(1).getStringCellValue());
    HSSFRow dataRow1 = sheet.getRow(1);
    assertEquals(1., dataRow1.getCell(0).getNumericCellValue(), 0);
    assertEquals("R&B", dataRow1.getCell(1).getStringCellValue());
    clearSystemProperties();
  }

}
