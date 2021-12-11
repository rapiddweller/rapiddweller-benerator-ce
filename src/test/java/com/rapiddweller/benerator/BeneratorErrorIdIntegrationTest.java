/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.benerator.engine.BeneratorResult;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.common.exception.ExitCodes;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link BeneratorErrorIds} returned from Benerator runs in different scenarios.<br/><br/>
 * Created: 18.11.2021 17:25:52
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorErrorIdIntegrationTest {

  @BeforeClass
  public static void prepare() {
    BeneratorExceptionFactory.getInstance();
  }

  // Test for successful execution -----------------------------------------------------------------------------------

  @Test
  public void test_0000_ok() {
    BeneratorResult result = runFile("0000_ok.ben.xml");
    assertResult(null, ExitCodes.OK, result);
  }

  // command line error tests ----------------------------------------------------------------------------------------

  @Test
  public void test_cli_1_flag_typo_without_file() {
    BeneratorResult result = Benerator.runWithArgs("--excep");
    assertResult(BeneratorErrorIds.CLI_ILLEGAL_OPTION, "Illegal command line option: --excep",
        ExitCodes.COMMAND_LINE_USAGE_ERROR, result);
  }

  @Test
  public void test_cli_2_illegal_option_mode() {
    BeneratorResult result = Benerator.runWithArgs("--mode", "nomode");
    assertResult(BeneratorErrorIds.CLI_ILLEGAL_MODE,
        "Illegal mode value: nomode. Allowed values are lenient, strict and turbo",
        ExitCodes.COMMAND_LINE_USAGE_ERROR, result);
  }

  @Test
  public void test_cli_3_illegal_option_list() {
    BeneratorResult result = Benerator.runWithArgs("--list", "nolist");
    assertResult(BeneratorErrorIds.CLI_ILLEGAL_LIST,
        "Illegal value for list option: nolist. Allowed values are db and kafka",
        ExitCodes.COMMAND_LINE_USAGE_ERROR, result);
  }

  @Test
  public void test_cli_4_flag_typo_with_file() {
    BeneratorResult result = Benerator.runWithArgs("--except", "benerator.xml");
    assertResult(BeneratorErrorIds.CLI_ILLEGAL_OPTION, "Illegal command line option: --except",
        ExitCodes.COMMAND_LINE_USAGE_ERROR, result);
  }

  @Test
  public void test_cli_5_benerator_file_not_found() {
    BeneratorResult result = Benerator.runWithArgs("not_a_file.ben.xml");
    assertResult(BeneratorErrorIds.BEN_FILE_NOT_FOUND, "Benerator file not found: not_a_file.ben.xml",
        ExitCodes.FILE_NOT_FOUND, result);
  }

  // Initialization errors -------------------------------------------------------------------------------------------

  @Test
  public void test_init_1_ConverterManager_failed() {
    ConverterManager.removeInstance();
    File file = new File("converters.txt");
    try {
      FileUtil.writeTextFileContent("not.a.Converter", file, Encodings.UTF_8);
      BeneratorResult result = Benerator.runWithArgs();
      assertResult(BeneratorErrorIds.COMP_INIT_FAILED_CONVERTER,
          "Component initialization failed for ConverterManager",
          ExitCodes.INTERNAL_SOFTWARE_ERROR, result);
    } finally {
      FileUtil.deleteIfExists(file);
      ConverterManager.getInstance().reset();
    }
  }

  /*
  @Test
  public void test_init_2_Delocalizing_failed() {
    // TODO implement test
  }

  @Test
  public void test_init_3_ScriptUtil_failed() {
    // TODO implement test
  }

  @Test
  public void test_init_4_DatabaseManager_failed() {
    // TODO implement test
  }

  @Test
  public void test_init_5_Country_failed() {
    // TODO implement test
  }

  @Test
  public void test_init_6_BeneratorMonitor_failed() {
    // TODO implement test
  }
*/

  // Benerator file syntax errors ------------------------------------------------------------------------------------

  @Test
  public void test_0100_syn_empty_ben_file() {
    BeneratorResult result = runFile("0100_syn_empty_ben_file.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EMPTY_BEN_FILE, "Empty Benerator file", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0101_syn_ben_file_no_xml() {
    BeneratorResult result = runFile("0101_syn_ben_file_no_xml.ben.xml");
    assertResult(BeneratorErrorIds.SYN_NO_XML_FILE,
        "File does not start with <?xml...?> or a tag", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0102_syn_benerator_file_illegal_root() {
    BeneratorResult result = runFile("0102_syn_benerator_file_illegal_root.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ILLEGAL_ROOT, "Illegal root element: blabla",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0103_syn_illegal_element() {
    BeneratorResult result = runFile("0103_syn_illegal_element.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ILLEGAL_ELEMENT,
        "Illegal element: <xxx>", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0104_syn_misplaced_element() {
    BeneratorResult result = runFile("0104_syn_misplaced_element.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MISPLACED_ELEMENT,
        "Illegal child element of <setup>: <setup>", ExitCodes.SYNTAX_ERROR, result);
  }

  // <setup> tests ---------------------------------------------------------------------------------------------------

  @Test
  public void test_0200_syn_setup_illegal_attr() {
    BeneratorResult result = runFile("0200_syn_setup_illegal_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_ILLEGAL_ATTRIBUTE,
        "Illegal XML attribute: setup.noattr", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0201_syn_setup_maxCount_alpha() {
    BeneratorResult result = runFile("0201_syn_setup_maxCount_alpha.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_MAX_COUNT,
        "Illegal attribute value for setup.maxCount: few", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0201_syn_setup_maxCount_negative() {
    BeneratorResult result = runFile("0201_syn_setup_maxCount_negative.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_MAX_COUNT,
        "Illegal attribute value for setup.maxCount: -1",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0202_syn_setup_defaultScript() {
    BeneratorResult result = runFile("0202_syn_setup_defaultScript_none.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_SCRIPT,
        "Illegal attribute value for setup.defaultScript: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0203_syn_setup_defaultNull() {
    BeneratorResult result = runFile("0203_syn_setup_defaultNull_none.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_NULL,
        "Illegal attribute value for setup.defaultNull: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0204_syn_setup_defaultEncoding() {
    BeneratorResult result = runFile("0204_syn_setup_defaultEncoding_none.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_ENCODING,
        "Illegal attribute value for setup.defaultEncoding: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0205_syn_setup_defaultLineSeparator() {
    BeneratorResult result = runFile("0205_syn_setup_defLineSep_none.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_LINE_SEPARATOR,
        "Illegal attribute value for setup.defaultLineSeparator: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0206_syn_setup_defaultLocale() {
    BeneratorResult result = runFile("0206_syn_setup_defLocale.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_LOCALE,
        "Illegal attribute value for setup.defaultLocale: $%/+-.",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0207_syn_setup_defaultDataset() {
    BeneratorResult result = runFile("0207_syn_setup_defDataset.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_DATASET,
        "Illegal attribute value for setup.defaultDataset: $%/+-.",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0208_syn_setup_defaultPageSize() {
    BeneratorResult result = runFile("0208_syn_setup_defPageSize.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_PAGE_SIZE,
        "Illegal attribute value for setup.defaultPageSize: -5",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0209_syn_setup_defaultSeparator() {
    BeneratorResult result = runFile("0209_syn_setup_defSeparator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_SEPARATOR,
        "Illegal attribute value for setup.defaultSeparator: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0210_syn_setup_defaultOneToOne() {
    BeneratorResult result = runFile("0210_syn_setup_defOneToOne.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_ONE_TO_ONE,
        "Illegal attribute value for setup.defaultOneToOne: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0211_syn_setup_defaultErrorHandler() {
    BeneratorResult result = runFile("0211_syn_setup_defErrorHandler.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_ERR_HANDLER,
        "Illegal attribute value for setup.defaultErrorHandler: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0212_syn_setup_defaultImports() {
    BeneratorResult result = runFile("0212_syn_setup_defImports.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_IMPORTS,
        "Illegal attribute value for setup.defaultImports: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0213_syn_setup_defaultSourceScripted() {
    BeneratorResult result = runFile("0213_syn_setup_defSourceScripted.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_SOURCE_SCRIPTED,
        "Illegal attribute value for setup.defaultSourceScripted: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0213_syn_setup_defaultAcceptUnkTypes() {
    BeneratorResult result = runFile("0214_syn_setup_acceptUnkSimpleTypes.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_ACCEPT_UNK_SIMPLE_TYPES,
        "Illegal attribute value for setup.acceptUnknownSimpleTypes: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0213_syn_setup_defaultGenFact() {
    BeneratorResult result = runFile("0215_syn_setup_genFact.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_GENERATOR_FACTORY,
        "Illegal attribute value for setup.generatorFactory: none",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // database tests --------------------------------------------------------------------------------------------------

  @Test
  public void test_0520_syn_db_not_available() {
    BeneratorResult result = runFile("0520_syn_db_not_available.ben.xml");
    assertResult(BeneratorErrorIds.DB_CONNECT_FAILED,
        "Connecting the database failed. URL: jdbc:postgresql://localhost:54321/postgres",
        ExitCodes.MISCELLANEOUS_ERROR, result);
  }

  // helper methods --------------------------------------------------------------------------------------------------

  private BeneratorResult runFile(String name) {
    return Benerator.runWithArgs("com/rapiddweller/benerator/main/" + name);
  }

  private void assertResult(String expectedErrorId, String expectedMessage, int expectedExitCode, BeneratorResult result) {
    String expectedErrOut = "Error " + expectedErrorId + ": " + expectedMessage;
    assertResult(expectedErrOut, expectedExitCode, result);
  }

  private void assertResult(String expectedErrOut, int expectedExitCode, BeneratorResult result) {
    String errOut = result.getErrOut();
    if (errOut != null) {
      errOut = errOut.trim();
    }
    if (expectedErrOut != null) {
      if (expectedErrOut.endsWith("*")) {
        String errMsg = "Expected: '" + expectedErrOut + "'\nActual:  '" + errOut + "'";
        assertTrue(errMsg, errOut.startsWith(expectedErrOut.substring(0, expectedErrOut.length() - 1)));
      } else {
        assertEquals(expectedErrOut, errOut);
      }
    } else {
      assertNull(errOut);
    }
    assertEquals(expectedExitCode, result.getExitCode());
  }

}
