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

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.engine.parser.String2DistributionConverter;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.converter.ConverterManager;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static com.rapiddweller.common.SystemInfo.isLinux;

/**
 * Integration test for Benerator's Demo Files.<br/><br/>
 * Created at 30.12.2020
 * @author Alexander Kell, Volker Bergmann
 * @since 1.1.0
 */
public class DemoIntegrationNoExtDBTest extends AbstractBeneratorIntegrationTest {

  @BeforeClass
  public static void setUp() {
    BeneratorFactory.setInstance(new DefaultBeneratorFactory());
    ConverterManager.getInstance().reset();
    ConverterManager.getInstance().registerConverterClass(String2DistributionConverter.class);
  }

  /**
   * Demo files postprocess.
   */
  @Test
  public void demoFilesPostprocess() throws IOException {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/postprocess-import.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files import fixed width.
   */
  @Test
  public void demoFilesImportFixedWidth() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/import_fixed_width.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo memstore.
   */
  @Test
  public void demoMemstore() {
    context.setContextUri("/demo/memstore");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/memstore/memstore.ben.xml");
    Assert.assertEquals("/demo/memstore", benCtx.getContextUri());
  }

  /**
   * Demo files greeting csv.
   */
  @Test
  public void demoFilesGreetingCSV() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/greetings_csv.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files csvio.
   */
  @Test
  public void demoFilesCSVIO() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/csv_io.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files xml by script.
   */
  @Test
  public void demoFilesXMLByScript() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_xml_by_script.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files create xml.
   */
  @Test
  public void demoFilesCreateXML() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_xml.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files create xlsl.
   */
  @Test
  public void demoFilesCreateXLSL() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_xls.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }


  @Test
  public void demoFilesCreateDates() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_dates.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files create csv.
   */
  @Test
  public void demoFilesCreateCSV() {
    Assume.assumeTrue(isLinux());
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/create_csv.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files xls demo.
   */
  @Test
  public void demoFilesXLSDemo() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/xls-demo.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files demoSimpleNumbers demo.
   */
  // TODO add check eval methods to script
  @Test
  public void demoSimpleNumbers() {
    context.setContextUri("/demo/simple");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/simple/numbers.ben.xml");
    Assert.assertEquals("/demo/simple", benCtx.getContextUri());
  }

  /**
   * Demo mass test.
   */
  @Test
  public void demoMassTest() {
    context.setContextUri("/demo/db");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/hsqlmem.masstest.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
  }

  /**
   * Demo h 2 multi schema.
   */
  @Ignore
  @Test
  public void demoH2MultiSchema() {
    context.setContextUri("/demo/db");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/h2.multischema.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
  }

  /**
   * Demo env db
   */
  @Test
  public void demoDbEnvOld() throws IOException {
    context.setContextUri("/demo/db");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/dbenv-old.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
  }

  /** Tests the new environment file format introduced in Benerator 2.1 */
  @Test
  public void demoDbEnvNew() {
    context.setContextUri("/demo/db");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/dbenv-new.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
  }

  /**
   * Demo env db conf
   */
  @Test
  public void demoDbEnvConf() {
    context.setContextUri("/demo/db");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/dbenvconf.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
  }

  /**
   * Demo db composite key.
   */
  @Test
  public void demoDbCompositeKey() {
    context.setContextUri("/demo/db");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/db/compositekey.ben.xml");
    Assert.assertEquals("/demo/db", benCtx.getContextUri());
  }

  /**
   * Demo script db.
   */
  @Test
  public void demoScriptDb() {
    context.setContextUri("/demo/script");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/script/scriptdb.ben.xml");
    Assert.assertEquals("/demo/script", benCtx.getContextUri());

  }

  /**
   * Demo script code.
   */
  @Test
  public void demoScriptCode() {
    context.setContextUri("/demo/script");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/script/scriptcode.ben.xml");
    Assert.assertEquals("/demo/script", benCtx.getContextUri());

  }

  /**
   * Demo script file.
   */
  @Test
  public void demoScriptFile() {
    context.setContextUri("/demo/script");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/script/scriptfile.ben.xml");
    Assert.assertEquals("/demo/script", benCtx.getContextUri());
  }


  /**
   * Shop script hsql mem.
   */
  @Test
  public void shopScriptHSQLMem() {
    context.setContextUri("/demo/shop");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-hsqlmem.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }


  /**
   * Shop script hsql mem AdvancedSQLEntityExporter.
   */
  @Test
  public void shopScriptHSQLMemAdvSQLEntityExporter() {
    context.setContextUri("/demo/shop");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-hsqlmem-adv-sql-exporter.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
    Assert.assertNotNull(IOUtil.getContentOfURI("target/out.sql"));
    Assert.assertNotNull(IOUtil.getContentOfURI("target/out2.sql"));
  }

  /**
   * Shop script h 2 mem.
   */
  @Test
  public void shopScriptH2Mem() {
    context.setContextUri("/demo/shop");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/shop/shop-h2.ben.xml");
    Assert.assertEquals("/demo/shop", benCtx.getContextUri());
  }

  /**
   * wartermark
   */
  @Test
  public void watermark() {
    context.setContextUri("/demo/watermark");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/watermark/watermark.ben.xml");
    Assert.assertEquals("/demo/watermark", benCtx.getContextUri());
  }

  /**
   * Test files empty csv.
   */
  @Test
  public void testFilesEmptyCSV() {
    context.setContextUri("/demo/file");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/file/empty_csv.ben.xml");
    Assert.assertEquals("/demo/file", benCtx.getContextUri());
  }

  /**
   * Demo files simplecity demo.
   */
  @Test
  public void demoSimpleCities() {
    context.setContextUri("/demo/simple");
    BeneratorContext benCtx = parseAndExecuteFile("/demo/simple/cities.ben.xml");
    Assert.assertEquals("/demo/simple", benCtx.getContextUri());
  }
}
