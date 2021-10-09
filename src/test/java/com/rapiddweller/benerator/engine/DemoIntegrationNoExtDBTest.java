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
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Integration test for Benerator's Demo Files.<br/><br/>
 * <p>
 * Created at 30.12.2020
 *
 * @author Alexander Kell
 * @since 1.1.0
 */
public class DemoIntegrationNoExtDBTest extends AbstractBeneratorIntegrationTest {
  private static final Logger logger = LoggerFactory.getLogger(DemoIntegrationNoExtDBTest.class);

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
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/postprocess-import.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files import fixed width.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesImportFixedWidth() throws IOException {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/import_fixed_width.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files greeting csv.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesGreetingCSV() throws IOException {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/greetings_csv.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files csvio.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesCSVIO() throws IOException {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/csv_io.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files xml by script.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesXMLByScript() throws IOException {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_xml_by_script.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files create xml.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesCreateXML() throws IOException {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_xml.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files create xlsl.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesCreateXLSL() throws IOException {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_xls.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }


  /**
   * Demo files create dates.
   */
  @Test
  public void DemoFilesCreateDates() {
    try {
      context.setContextUri("/demo/file");
      BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_dates.ben.xml");
      Assert.assertEquals("/demo/file", benCtx.getContextUri());
    } catch (Exception e) {
      logger.info("Error executing Demo", e);
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
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_csv.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files xls demo.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoFilesXLSDemo() throws IOException {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/xls-demo.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo mass test.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoMassTest() throws IOException {
    context.setContextUri("/demo/db");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/hsqlmem.masstest.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
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
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/h2.multischema.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
  }

  /**
   * Demo db composite key.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoDbCompositeKey() throws IOException {
    context.setContextUri("/demo/db");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/compositekey.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
  }

  /**
   * Demo script code.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoScriptCode() throws IOException {
    context.setContextUri("/demo/script");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/script/scriptcode.ben.xml");
    Assert.assertEquals("/demo/script", benCtx.getContextUri());

  }

  /**
   * Demo script file.
   *
   * @throws IOException the io exception
   */
  @Test
  public void DemoScriptFile() throws IOException {
    context.setContextUri("/demo/script");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/script/scriptfile.ben.xml");
    Assert.assertEquals("/demo/script", benCtx.getContextUri());
  }


  /**
   * Shop script hsql mem.
   *
   * @throws IOException the io exception
   */
  @Test
  public void ShopScriptHSQLMem() throws IOException {
    context.setContextUri("/demo/shop");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-hsqlmem.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * Shop script h 2 mem.
   *
   * @throws IOException the io exception
   */
  @Test
  public void ShopScriptH2Mem() throws IOException {
    context.setContextUri("/demo/shop");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-h2.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }


}
