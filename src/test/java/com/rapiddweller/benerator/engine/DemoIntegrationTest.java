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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.rapiddweller.common.SystemInfo.isLinux;

/**
 * Integration test for Benerator's Demo Files.<br/><br/>
 * <p>
 * Created at 30.12.2020
 *
 * @author Alexander Kell
 * @since 1.1.0
 */
public class DemoIntegrationTest extends AbstractBeneratorIntegrationTest {
  private static final Logger logger = LogManager.getLogger(DemoIntegrationTest.class);

  /**
   * The Root.
   */
  String ROOT = "src/demo/resources/";

  private void parseAndExecute() throws IOException {
    for (File file : Objects.requireNonNull(new File(ROOT, context.getContextUri()).listFiles())) {
      String filename = file.getPath();
      if (FileUtil.isXMLFile(filename)) {
        logger.info(filename);
        parseAndExecuteFile(filename);
      }
    }
  }

  /**
   * Demo files postprocess.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesPostprocess() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/postprocess-import.ben.xml");
  }

  /**
   * Demo files import fixed width.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesImportFixedWidth() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/import_fixed_width.ben.xml");
  }

  /**
   * Demo files greeting csv.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesGreetingCSV() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/greetings_csv.ben.xml");
  }

  /**
   * Demo files csvio.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesCSVIO() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/csv_io.ben.xml");
  }

  /**
   * Demo files xml by script.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesXMLByScript() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/create_xml_by_script.ben.xml");
  }

  /**
   * Demo files create xml.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesCreateXML() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/create_xml.ben.xml");
  }

  /**
   * Demo files create xlsl.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesCreateXLSL() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/create_xls.ben.xml");
  }


  /**
   * Demo files create dates.
   */
  @Test
  public void DemoFilesCreateDates() {
    try {
      context.setContextUri("/demo/file");
      parseAndExecuteFile("/demo/file/create_dates.ben.xml");
    } catch (Exception e) {
      logger.info(e);
    }
  }

  /**
   * Demo files create csv.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesCreateCSV() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/create_csv.ben.xml");
  }

  /**
   * Demo files xls demo.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesXLSDemo() throws IOException {
    context.setContextUri("/demo/file");
    parseAndExecuteFile("/demo/file/xls-demo.ben.xml");
  }

  /**
   * Demo mass test.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoMassTest() throws IOException {
    context.setContextUri("/demo/db");
    parseAndExecuteFile("/demo/db/hsqlmem.masstest.ben.xml");
  }

  /**
   * Demo h 2 multi schema.
   *
   * @throws IOException the io exception
   */
  @Ignore
  @Test
  public void DemoH2MultiSchema() throws IOException {
    context.setContextUri("/demo/db");
    parseAndExecuteFile("/demo/db/h2.multischema.ben.xml");
  }

  /**
   * Demo postgres multi schema.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoPostgresMultiSchema() throws IOException {
    context.setContextUri("/demo/shop");
    parseAndExecuteFile("/demo/shop/postgres.multischema.ben.xml");
  }

  /**
   * Demo postgres multi schema with table with same name and different columns and foreign key in different schema
   *
   * @throws IOException the io exception
   */
  @Test(expected = RuntimeException.class)
  public void DemoPostgresMultiSchemaDuplicatedTableInBenCtx() throws IOException {
    context.setContextUri("/demo/shop");
    parseAndExecuteFile("/demo/shop/postgres.multischema_duplicated_table.ben.xml");
  }

  /**
   * Demo Mssql Shop
   *
   * @throws IOException the io exception
   */
//  @Ignore
  @Test
  public void DemoMssqlShop() throws IOException {
    context.setContextUri("/demo/shop");
    parseAndExecuteFile("/demo/shop/shop-mssql.ben.xml");

  }

  /**
   * Demo Mysql Shop
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoMysqlShop() throws IOException {
    context.setContextUri("/demo/shop");
    parseAndExecuteFile("/demo/shop/shop-mysql.ben.xml");

  }

  /**
   * Demo postgres shop.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoPostgresShop() throws IOException {
    Assume.assumeTrue(isLinux());
    context.setContextUri("/demo/shop");
    parseAndExecuteFile("/demo/shop/shop-postgres.ben.xml");
  }

  /**
   * Demo oracle shop.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoOracleShop() throws IOException {
    Assume.assumeTrue(isLinux());
    context.setContextUri("/demo/shop");
    parseAndExecuteFile("/demo/shop/shop-oracle.ben.xml");
  }

  /**
   * Debugging
   *
   * @throws IOException the io exception
   */
  @Ignore
  @Test
  public void PostgresDebugging() throws IOException {
    Assume.assumeTrue(isLinux());
    context.setContextUri("/demo/WIP");
    parseAndExecuteFile("/demo/WIP/benerator.xml");
  }


  /**
   * Demo db composite key.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoDbCompositeKey() throws IOException {
    context.setContextUri("/demo/db");
    parseAndExecuteFile("/demo/db/compositekey.ben.xml");
  }

  /**
   * Demo script code.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoScriptCode() throws IOException {
    context.setContextUri("/demo/script");
    parseAndExecuteFile("/demo/script/scriptcode.ben.xml");

  }

  /**
   * Demo script file.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoScriptFile() throws IOException {
    context.setContextUri("/demo/script");
    parseAndExecuteFile("/demo/script/scriptfile.ben.xml");
  }


  /**
   * Shop script hsql mem.
   *
   * @throws IOException the io exception
   */
  @Test
  public void ShopScriptHSQLMem() throws IOException {
    context.setContextUri("/demo/shop");
    parseAndExecuteFile("/demo/shop/shop-hsqlmem.ben.xml");
  }

  /**
   * Shop script h 2 mem.
   *
   * @throws IOException the io exception
   */
  @Test
  public void ShopScriptH2Mem() throws IOException {
    context.setContextUri("/demo/shop");
    parseAndExecuteFile("/demo/shop/shop-h2.ben.xml");
  }


}
