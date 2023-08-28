/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.benerator.engine.BeneratorResult;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.common.exception.ExitCodes;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static com.rapiddweller.common.SystemInfo.isLinux;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link BeneratorErrorIds} returned from Benerator runs in different scenarios.<br/><br/>
 * Created: 18.11.2021 17:25:52
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class BeneratorErrorIdIntegrationTest {

  @BeforeClass
  public static void prepare() {
    BeneratorExceptionFactory.getInstance();
  }

  // Test for successful execution -----------------------------------------------------------------------------------

  @Test
  public void test_0000_ok() {
    BeneratorResult result = runFile("test_0000_ok.ben.xml");
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

  // Benerator file syntax errors ------------------------------------------------------------------------------------

  @Test
  public void test_0100_syn_empty_ben_file() {
    BeneratorResult result = runFile("test_0100_syn_empty_ben_file.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EMPTY_BEN_FILE, "Empty Benerator file. " +
        "File test_0100_syn_empty_ben_file.ben.xml", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0101_syn_ben_file_no_xml() {
    BeneratorResult result = runFile("test_0101_syn_ben_file_no_xml.ben.xml");
    assertResult(BeneratorErrorIds.SYN_BEN_FILE_NO_XML,
        "Benerator file does not start with <?xml...?> or <setup>. " +
            "File test_0101_syn_ben_file_no_xml.ben.xml, line 1",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0102_syn_benerator_file_illegal_root() {
    BeneratorResult result = runFile("test_0102_syn_benerator_file_illegal_root.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ILLEGAL_ROOT,
        "Illegal root element: blabla. File test_0102_syn_benerator_file_illegal_root.ben.xml, line 1",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0103_syn_illegal_element() {
    BeneratorResult result = runFile("test_0103_syn_illegal_element.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ILLEGAL_ELEMENT,
        "Illegal element: <xxx>. File test_0103_syn_illegal_element.ben.xml, line 2",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0104_syn_misplaced_element() {
    BeneratorResult result = runFile("test_0104_syn_misplaced_element.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MISPLACED_ELEMENT,
        "Illegal child element of <setup>: <setup>. File test_0104_syn_misplaced_element.ben.xml, line 2",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0111_syn_attr_ill_segment() {
    Assume.assumeTrue(isLinux());
    BeneratorResult result = runFile("test_0111_syn_attr_ill_segment.ben.xml");
    assertResult(BeneratorErrorIds.SEGMENT_NOT_FOUND,
        "Segment not found: 'none' in 'data.xls'",
        ExitCodes.FILE_NOT_FOUND, result);
  }

  // file not found --------------------------------------------------------------------------------------------------

  @Test
  public void test_0130_file_not_found_csv() {
    Assume.assumeTrue(isLinux());
    BeneratorResult result = runFile("test_0130_file_not_found_csv.ben.xml");
    assertResult(BeneratorErrorIds.FILE_REF_NOT_FOUND, "File not found: 'nonexistent.csv'.",
        ExitCodes.FILE_NOT_FOUND, result);
  }

  @Test
  public void test_0130_file_not_found_xls() {
    Assume.assumeTrue(isLinux());
    BeneratorResult result = runFile("test_0130_file_not_found_xls.ben.xml");
    assertResult(BeneratorErrorIds.FILE_REF_NOT_FOUND, "File not found: 'nonexistent.xls'.",
        ExitCodes.FILE_NOT_FOUND, result);
  }

  @Test
  public void test_0153_syn_missing_setup_end() {
    BeneratorResult result = runFile("test_0153_syn_missing_setup_end.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ILLEGAL_XML_END_TAG,
        "XML document structures must start and end within the same entity. " +
            "File test_0153_syn_missing_setup_end.ben.xml, line 2",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0154_syn_illegal_end_tag() {
    BeneratorResult result = runFile("test_0154_syn_illegal_end_tag.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ILLEGAL_XML_END_TAG,
        "The element type \"echo\" must be terminated by the matching end-tag \"</echo>\". " +
            "File test_0154_syn_illegal_end_tag.ben.xml, line 2",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0154_syn_setup_end_mismatch() {
    BeneratorResult result = runFile("test_0154_syn_setup_end_mismatch.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ILLEGAL_XML_END_TAG,
        "The element type \"setup\" must be terminated by the matching end-tag \"</setup>\". " +
            "File test_0154_syn_setup_end_mismatch.ben.xml, line 2",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <setup> tests ---------------------------------------------------------------------------------------------------

  @Test
  public void test_0200_syn_setup_illegal_attr() {
    BeneratorResult result = runFile("test_0200_syn_setup_illegal_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_ILLEGAL_ATTRIBUTE,
        "Illegal XML attribute: setup.noattr. File test_0200_syn_setup_illegal_attr.ben.xml, line 1",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0201_syn_setup_maxCount_alpha() {
    BeneratorResult result = runFile("test_0201_syn_setup_maxCount_alpha.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_MAX_COUNT,
        "Illegal attribute value for setup.maxCount: 'few'. " +
            "File test_0201_syn_setup_maxCount_alpha.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0201_syn_setup_maxCount_negative() {
    BeneratorResult result = runFile("test_0201_syn_setup_maxCount_negative.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_MAX_COUNT,
        "Illegal attribute value for setup.maxCount: '-1'. " +
            "File test_0201_syn_setup_maxCount_negative.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0202_syn_setup_defaultScript() {
    BeneratorResult result = runFile("test_0202_syn_setup_defaultScript_none.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_SCRIPT,
        "Illegal attribute value for setup.defaultScript: 'none'. " +
            "File test_0202_syn_setup_defaultScript_none.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0203_syn_setup_defaultNull() {
    BeneratorResult result = runFile("test_0203_syn_setup_defaultNull_none.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_NULL,
        "Illegal attribute value for setup.defaultNull: 'none'. " +
            "Use 'true' or 'false. File test_0203_syn_setup_defaultNull_none.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0204_syn_setup_defaultEncoding() {
    BeneratorResult result = runFile("test_0204_syn_setup_defaultEncoding_none.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_ENCODING,
        "Illegal attribute value for setup.defaultEncoding: 'none'. " +
            "File test_0204_syn_setup_defaultEncoding_none.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0206_syn_setup_defaultLocale() {
    BeneratorResult result = runFile("test_0206_syn_setup_defLocale.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_LOCALE,
        "Illegal attribute value for setup.defaultLocale: '$%/+-.'. " +
            "File test_0206_syn_setup_defLocale.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0207_syn_setup_defaultDataset() {
    BeneratorResult result = runFile("test_0207_syn_setup_defDataset.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_DATASET,
        "Illegal attribute value for setup.defaultDataset: '$%/+-.'. " +
            "File test_0207_syn_setup_defDataset.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0208_syn_setup_defaultPageSize() {
    BeneratorResult result = runFile("test_0208_syn_setup_defPageSize.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_PAGE_SIZE,
        "Illegal attribute value for setup.defaultPageSize: '-5'. " +
            "File test_0208_syn_setup_defPageSize.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0209_syn_setup_defaultSeparator() {
    BeneratorResult result = runFile("test_0209_syn_setup_defSeparator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_SEPARATOR,
        "Illegal attribute value for setup.defaultSeparator: 'none'. " +
            "File test_0209_syn_setup_defSeparator.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0210_syn_setup_defaultOneToOne() {
    BeneratorResult result = runFile("test_0210_syn_setup_defOneToOne.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_ONE_TO_ONE,
        "Illegal attribute value for setup.defaultOneToOne: 'none'. " +
            "Use 'true' or 'false. File test_0210_syn_setup_defOneToOne.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0211_syn_setup_defaultErrorHandler() {
    BeneratorResult result = runFile("test_0211_syn_setup_defErrorHandler.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_ERR_HANDLER,
        "Illegal attribute value for setup.defaultErrorHandler: 'none'. " +
            "File test_0211_syn_setup_defErrorHandler.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0212_syn_setup_defaultImports() {
    BeneratorResult result = runFile("test_0212_syn_setup_defImports.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_IMPORTS,
        "Illegal attribute value for setup.defaultImports: 'none'. " +
            "Use 'true' or 'false. File test_0212_syn_setup_defImports.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0213_syn_setup_defaultSourceScripted() {
    BeneratorResult result = runFile("test_0213_syn_setup_defSourceScripted.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_SOURCE_SCRIPTED,
        "Illegal attribute value for setup.defaultSourceScripted: 'none'. " +
            "Use 'true' or 'false. File test_0213_syn_setup_defSourceScripted.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0213_syn_setup_defaultAcceptUnkTypes() {
    BeneratorResult result = runFile("test_0214_syn_setup_acceptUnkSimpleTypes.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_ACCEPT_UNK_SIMPLE_TYPES,
        "Illegal attribute value for setup.acceptUnknownSimpleTypes: 'none'. " +
            "Use 'true' or 'false. File test_0214_syn_setup_acceptUnkSimpleTypes.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0215_syn_setup_defaultGenFact() {
    BeneratorResult result = runFile("test_0215_syn_setup_genFact.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_GENERATOR_FACTORY,
        "Illegal attribute value for setup.generatorFactory: 'none'. " +
            "File test_0215_syn_setup_genFact.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0216_syn_setup_defaultTimeZone() {
    BeneratorResult result = runFile("test_0216_syn_setup_defTimeZone.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETUP_DEF_TIMEZONE,
        "Illegal attribute value for setup.defaultTimeZone: 'nowhere'. " +
            "File test_0216_syn_setup_defTimeZone.ben.xml, line 1", ExitCodes.SYNTAX_ERROR, result);
  }

  // <echo> tests ----------------------------------------------------------------------------------------------------

  @Test
  public void test_0234_syn_echo_ill_attr() {
    BeneratorResult result = runFile("test_0234_syn_echo_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ECHO_ILL_ATTR,
        "Illegal XML attribute: echo.ill_attr. File test_0234_syn_echo_ill_attr.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0235_syn_echo_tyoe() {
    BeneratorResult result = runFile("test_0235_syn_echo_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ECHO_TYPE,
        "Illegal attribute value for echo.type: 'none'. File test_0235_syn_echo_type.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0236_syn_echo_message() {
    BeneratorResult result = runFile("test_0236_syn_echo_message.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ECHO_MESSAGE,
        "<echo> must contain either a message attribute or a text content, not both. File test_0236_syn_echo_message.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0237_syn_echo_lang() {
    BeneratorResult result = runFile("test_0237_syn_echo_lang.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ECHO_LANG,
        "Illegal attribute value for echo.lang: 'none'. File test_0237_syn_echo_lang.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0238_beep_with_content() {
    BeneratorResult result = runFile("test_0238_syn_beep_with_content.ben.xml");
    assertResult(BeneratorErrorIds.SYN_BEEP,
        "Element <beep> has illegal text content: 'text'. " +
            "File test_0238_syn_beep_with_content.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <import> tests --------------------------------------------------------------------------------------------------

  @Test
  public void test_0240_syn_import_with_content() {
    BeneratorResult result = runFile("test_0240_syn_import_with_content.ben.xml");
    assertResult(BeneratorErrorIds.SYN_IMPORT,
        "Element <import> has illegal text content: 'text'. File test_0240_syn_import_with_content.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0241_syn_import_ill_attr() {
    BeneratorResult result = runFile("test_0241_syn_import_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_IMPORT_ILLEGAL_ATTR,
        "Illegal XML attribute: import.ill_attr. File test_0241_syn_import_ill_attr.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0242_syn_import_class() {
    BeneratorResult result = runFile("test_0242_syn_import_class.ben.xml");
    assertResult(BeneratorErrorIds.SYN_IMPORT_CLASS,
        "Illegal attribute value for import.class: '-928'. File test_0242_syn_import_class.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0243_syn_import_domains() {
    BeneratorResult result = runFile("test_0243_syn_import_domains.ben.xml");
    assertResult(BeneratorErrorIds.SYN_IMPORT_DOMAINS,
        "Illegal attribute value for import.domains: '%/+-.'. File test_0243_syn_import_domains.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0244_syn_import_platforms() {
    BeneratorResult result = runFile("test_0244_syn_import_platforms.ben.xml");
    assertResult(BeneratorErrorIds.SYN_IMPORT_PLATFORMS,
        "Illegal attribute value for import.platforms: '%/+-.'. File test_0244_syn_import_platforms.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0245_syn_import_defaults() {
    BeneratorResult result = runFile("test_0245_syn_import_defaults.ben.xml");
    assertResult(BeneratorErrorIds.SYN_IMPORT_DEFAULTS,
        "Illegal attribute value for import.defaults: 'none'. " +
            "Use 'true' or 'false. File test_0245_syn_import_defaults.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <iterate> tests -------------------------------------------------------------------------------------------------


  // <setting> tests -------------------------------------------------------------------------------------------------

  @Test
  public void test_0251_syn_setting_ill_attr() {
    BeneratorResult result = runFile("test_0251_syn_setting_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETTING_ILLEGAL_ATTR,
        "Illegal XML attribute: setting.ill_attr. File test_0251_syn_setting_ill_attr.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0252_syn_setting_wo_name() {
    BeneratorResult result = runFile("test_0252_syn_setting_wo_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETTING_NAME,
        "Attribute 'name' is missing in <setting>. File test_0252_syn_setting_wo_name.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0253_syn_setting_value_exp() {
    BeneratorResult result = runFile("test_0253_syn_setting_value_exp.ben.xml");
    assertResult(BeneratorErrorIds.SYN_SETTING_VALUE,
        "Error resolving expression 'not_existent'. File test_0253_syn_setting_value_exp.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <evaluate> tests ------------------------------------------------------------------------------------------------

  @Test
  public void test_0300_syn_execute_no_text() {
    BeneratorResult result = runFile("test_0300_syn_execute_no_text.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_TEXT, "The script to execute must either be specified " +
            "with a 'url' attribute or as XML element content. File test_0300_syn_execute_no_text.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0301_syn_execute_ill_attr() {
    BeneratorResult result = runFile("test_0301_syn_execute_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_ILLEGAL_ATTR,
        "Illegal XML attribute: execute.illegal. File test_0301_syn_execute_ill_attr.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0302_syn_execute_uri() {
    BeneratorResult result = runFile("test_0302_syn_execute_uri.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_URI,
        "Illegal attribute value for execute.uri: ''. File test_0302_syn_execute_uri.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0303_syn_execute_enc() {
    BeneratorResult result = runFile("test_0303_syn_execute_enc.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_ENCODING,
        "Illegal attribute value for execute.encoding: 'none'. File test_0303_syn_execute_enc.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0304_syn_execute_target() {
    BeneratorResult result = runFile("test_0304_syn_execute_target.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_TARGET, "Illegal attribute value for execute.target: '$$$'. " +
        "File test_0304_syn_execute_target.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0305_syn_execute_separator() {
    BeneratorResult result = runFile("test_0305_syn_execute_separator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_SEPARATOR,
        "Illegal attribute value for execute.separator: '::'. File test_0305_syn_execute_separator.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0306_syn_execute_type() {
    BeneratorResult result = runFile("test_0306_syn_execute_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_TYPE, "Illegal attribute value for execute.type: 'SQL'. " +
            "Choose one of these: shell, js, py, ben, sql, ftl. File test_0306_syn_execute_type.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0307_syn_execute_shell() {
    BeneratorResult result = runFile("test_0307_syn_execute_shell.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_SHELL, "Illegal attribute value for execute.shell: '.'. " +
            "File test_0307_syn_execute_shell.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0308_syn_execute_on_error() {
    BeneratorResult result = runFile("test_0308_syn_execute_on_error.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_ON_ERROR, "Illegal attribute value for execute.onError: 'none'. " +
            "File test_0308_syn_execute_on_error.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0309_syn_execute_optimize() {
    BeneratorResult result = runFile("test_0309_syn_execute_optimize.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_OPTIMIZE, "Illegal attribute value for execute.optimize: 'none'. " +
            "Use 'true' or 'false. File test_0309_syn_execute_optimize.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0310_syn_execute_invalidate() {
    BeneratorResult result = runFile("test_0310_syn_execute_invalidate.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EXECUTE_INVALIDATE, "Illegal attribute value for execute.invalidate: 'none'. " +
            "Use 'true' or 'false. File test_0310_syn_execute_invalidate.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <evaluate> tests ------------------------------------------------------------------------------------------------

  @Test
  public void test_0320_syn_evaluate_no_text() {
    BeneratorResult result = runFile("test_0320_syn_evaluate_no_text.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_TEXT, "The script to execute must either be specified " +
            "with a 'url' attribute or as XML element content. File test_0320_syn_evaluate_no_text.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0321_syn_evaluate_ill_attr() {
    BeneratorResult result = runFile("test_0321_syn_evaluate_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_ILLEGAL_ATTR,
        "Illegal XML attribute: evaluate.illegal. File test_0321_syn_evaluate_ill_attr.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0322_syn_evaluate_uri() {
    BeneratorResult result = runFile("test_0322_syn_evaluate_uri.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_URI, "Illegal attribute value for evaluate.uri: ''. " +
            "File test_0322_syn_evaluate_uri.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0323_syn_evaluate_enc() {
    BeneratorResult result = runFile("test_0323_syn_evaluate_enc.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_ENCODING, "Illegal attribute value for evaluate.encoding: " +
            "'none'. File test_0323_syn_evaluate_enc.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0324_syn_evaluate_target() {
    BeneratorResult result = runFile("test_0324_syn_evaluate_target.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_TARGET, "Illegal attribute value for evaluate.target: '$$$'. " +
        "File test_0324_syn_evaluate_target.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0325_syn_evaluate_separator() {
    BeneratorResult result = runFile("test_0325_syn_evaluate_separator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_SEPARATOR, "Illegal attribute value for evaluate.separator: " +
            "'::'. File test_0325_syn_evaluate_separator.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0326_syn_evaluate_type() {
    BeneratorResult result = runFile("test_0326_syn_evaluate_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_TYPE, "Illegal attribute value for evaluate.type: 'SQL'. " +
            "Choose one of these: shell, js, py, ben, sql, ftl. File test_0326_syn_evaluate_type.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0327_syn_evaluate_shell() {
    BeneratorResult result = runFile("test_0327_syn_evaluate_shell.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_SHELL, "Illegal attribute value for evaluate.shell: '.'. " +
        "File test_0327_syn_evaluate_shell.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0328_syn_evaluate_on_error() {
    BeneratorResult result = runFile("test_0328_syn_evaluate_on_error.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_ON_ERROR, "Illegal attribute value for evaluate.onError: 'none'. " +
        "File test_0328_syn_evaluate_on_error.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0329_syn_evaluate_optimize() {
    BeneratorResult result = runFile("test_0329_syn_evaluate_optimize.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_OPTIMIZE, "Illegal attribute value for evaluate.optimize: 'none'. " +
            "Use 'true' or 'false. File test_0329_syn_evaluate_optimize.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0330_syn_evaluate_invalidate() {
    BeneratorResult result = runFile("test_0330_syn_evaluate_invalidate.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_INVALIDATE, "Illegal attribute value for evaluate.invalidate: 'none'. " +
            "Use 'true' or 'false. File test_0330_syn_evaluate_invalidate.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0331_syn_evaluate_id() {
    BeneratorResult result = runFile("test_0331_syn_evaluate_id.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_ID, "Illegal attribute value for evaluate.id: '::'. " +
            "File test_0331_syn_evaluate_id.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0332_syn_evaluate_assert() {
    BeneratorResult result = runFile("test_0332_syn_evaluate_assert.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_ASSERT, "Illegal attribute value for evaluate.assert: ''. " +
            "File test_0332_syn_evaluate_assert.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0333_syn_evaluate_result() {
    BeneratorResult result = runFile("test_0333_syn_evaluate_result.ben.xml");
    assertResult(BeneratorErrorIds.SYN_EVALUATE_RESULT, "At least one of these attributes must be set: id, assert. " +
            "File test_0333_syn_evaluate_result.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <iterate> tests -------------------------------------------------------------------------------------------------

  @Test
  public void test_0469_syn_iter_ill_selector() {
    BeneratorResult result = runFile("test_0469_syn_iter_ill_selector.ben.xml");
    assertResult(BeneratorErrorIds.SCRIPT_FAILED, // TODO should be SYN_ITERATE_SELECTOR
        "Unable to resolve 'none'. Script text: 'none'.",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <variable> tests ------------------------------------------------------------------------------------------------

  @Test
  public void test_0500_syn_var_with_content() {
    BeneratorResult result = runFile("test_0500_syn_var_with_content.ben.xml");
    assertResult(BeneratorErrorIds.SYN_VAR,
        "Element <variable> has illegal text content: 'text'. File test_0500_syn_var_with_content.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  /* TODO implement error mapping
  @Test
  public void test_0501_syn_var_ill_attr() {
    BeneratorResult result = runFile("test_0501_syn_var_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_VAR_ILLEGAL_ATTR, "Illegal attribute for <variable>: ill_attr",
        ExitCodes.SYNTAX_ERROR, result);
  }
  */

  @Test
  public void test_0502_syn_var_wo_name() {
    BeneratorResult result = runFile("test_0502_syn_var_wo_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_VAR_NAME,
        "Attribute 'name' is missing in <variable>. File test_0502_syn_var_wo_name.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0502_syn_var_no_count() {
    BeneratorResult result = runFile("test_0502_syn_var_wo_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_VAR_NAME,
        "Attribute 'name' is missing in <variable>. File test_0502_syn_var_wo_name.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <attribute> tests -----------------------------------------------------------------------------------------------

  @Test
  public void test_0550_syn_attr_with_content() {
    BeneratorResult result = runFile("test_0550_syn_attr_with_content.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR,
        "Element <attribute> has illegal text content: 'text'. File test_0550_syn_attr_with_content.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0551_attr_ill_attr() {
    BeneratorResult result = runFile("test_0551_syn_attr_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ILLEGAL_ATTR,
        "Illegal XML attribute: attribute.ill_attr. File test_0551_syn_attr_ill_attr.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0552_attr_wo_name() {
    BeneratorResult result = runFile("test_0552_syn_attr_wo_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_NAME,
        "Attribute 'name' is missing in <attribute>. File test_0552_syn_attr_wo_name.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0553_attr_ill_type() {
    BeneratorResult result = runFile("test_0553_syn_attr_ill_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_TYPE,
        "Illegal attribute value for attribute.type: 'none'. " +
            "File test_0553_syn_attr_ill_type.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  /* TODO
  @Test
  public void test_0554_syn_no_base_info() {
    BeneratorResult result = runFile("test_0554_syn_no_root_info.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "At least one of these attributes must be set: type, constant, values, pattern, script, generator, nullQuota",
        ExitCodes.SYNTAX_ERROR, result);
  }
  */

  @Test
  public void test_0554_syn_attr_constant_generator() {
    BeneratorResult result = runFile("test_0554_syn_attr_constant_generator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'constant' and 'generator' mutually exclude each other. " +
            "File test_0554_syn_attr_constant_generator.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_constant_maxLength() {
    BeneratorResult result = runFile("test_0554_syn_constant_maxLength.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'constant' and 'maxLength' mutually exclude each other. " +
            "File test_0554_syn_constant_maxLength.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_constant_minLength() {
    BeneratorResult result = runFile("test_0554_syn_constant_minLength.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'constant' and 'minLength' mutually exclude each other. " +
            "File test_0554_syn_constant_minLength.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_constant_pattern() {
    BeneratorResult result = runFile("test_0554_syn_constant_pattern.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'constant' and 'pattern' mutually exclude each other. " +
            "File test_0554_syn_constant_pattern.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_constant_script() {
    BeneratorResult result = runFile("test_0554_syn_constant_script.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'constant' and 'script' mutually exclude each other. " +
            "File test_0554_syn_constant_script.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_constant_values() {
    BeneratorResult result = runFile("test_0554_syn_constant_values.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'constant' and 'values' mutually exclude each other. " +
            "File test_0554_syn_constant_values.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_pattern_generator() {
    BeneratorResult result = runFile("test_0554_syn_pattern_generator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'pattern' and 'generator' mutually exclude each other. " +
            "File test_0554_syn_pattern_generator.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_pattern_script() {
    BeneratorResult result = runFile("test_0554_syn_pattern_script.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'pattern' and 'script' mutually exclude each other. " +
            "File test_0554_syn_pattern_script.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_script_generator() {
    BeneratorResult result = runFile("test_0554_syn_script_generator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'script' and 'generator' mutually exclude each other. " +
            "File test_0554_syn_script_generator.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_values_generator() {
    BeneratorResult result = runFile("test_0554_syn_values_generator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'values' and 'generator' mutually exclude each other. " +
            "File test_0554_syn_values_generator.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_values_pattern() {
    BeneratorResult result = runFile("test_0554_syn_values_pattern.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'values' and 'pattern' mutually exclude each other. " +
            "File test_0554_syn_values_pattern.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0554_syn_values_script() {
    BeneratorResult result = runFile("test_0554_syn_values_script.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROOT_INFO,
        "The attributes 'values' and 'script' mutually exclude each other. " +
            "File test_0554_syn_values_script.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0555_syn_attr_ill_mode() {
    BeneratorResult result = runFile("test_0555_syn_attr_ill_mode.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MODE,
        "Illegal attribute value for attribute.mode: 'none'. " +
            "File test_0555_syn_attr_ill_mode.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0557_syn_attr_ill_offset() {
    BeneratorResult result = runFile("test_0557_syn_attr_ill_offset.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_OFFSET,
        "Illegal attribute value for attribute.offset: '-1'. " +
            "File test_0557_syn_attr_ill_offset.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0558_syn_attr_ill_condition() {
    BeneratorResult result = runFile("test_0558_syn_attr_ill_condition.ben.xml");
    assertResult(BeneratorErrorIds.SCRIPT_FAILED, // TODO should be SYN_ATTR_CONDITION
        "Unable to resolve 'none'. Script text: 'none'.", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0559_syn_attr_ill_filter() {
    BeneratorResult result = runFile("test_0559_syn_attr_ill_filter.ben.xml");
    assertResult(BeneratorErrorIds.SCRIPT_FAILED, // TODO should be SYN_ATTR_FILTER
        "Unable to resolve 'none'. Script text: 'none'.",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0560_syn_attr_ill_unique() {
    BeneratorResult result = runFile("test_0560_syn_attr_ill_unique.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_UNIQUE,
        "Illegal attribute value for attribute.unique: 'none'. " +
            "Use 'true' or 'false. File test_0560_syn_attr_ill_unique.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0563_syn_attr_ill_values() {
    BeneratorResult result = runFile("test_0563_syn_attr_ill_values.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_VALUES,
        "Illegal attribute value for attribute.values: 'Alice,Bob,Charly'. " +
            "File test_0563_syn_attr_ill_values.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_attr_generator_equals_parent_type() {
    BeneratorResult result = runFile("test_0566_syn_attr_generator_equals_parent_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of attribute.generator must not be the same as the type 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_attr_generator_equals_parent_type.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_attr_generator_equals_parent_name() {
    BeneratorResult result = runFile("test_0566_syn_attr_generator_equals_parent_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of attribute.generator must not be the same as the name 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_attr_generator_equals_parent_name.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_attr_generator_equals_grandparent_type() {
    BeneratorResult result = runFile("test_0566_syn_attr_generator_equals_grandparent_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of attribute.generator must not be the same as the type 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_attr_generator_equals_grandparent_type.ben.xml, " +
            "line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_id_generator_equals_parent_type() {
    BeneratorResult result = runFile("test_0566_syn_id_generator_equals_parent_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of id.generator must not be the same as the type 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_id_generator_equals_parent_type.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_id_generator_equals_parent_name() {
    BeneratorResult result = runFile("test_0566_syn_id_generator_equals_parent_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of id.generator must not be the same as the name 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_id_generator_equals_parent_name.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_part_generator_equals_parent_type() {
    BeneratorResult result = runFile("test_0566_syn_part_generator_equals_parent_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of part.generator must not be the same as the type 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_part_generator_equals_parent_type.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_part_generator_equals_parent_name() {
    BeneratorResult result = runFile("test_0566_syn_part_generator_equals_parent_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of part.generator must not be the same as the name 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_part_generator_equals_parent_name.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_var_generator_equals_parent_type() {
    BeneratorResult result = runFile("test_0566_syn_var_generator_equals_parent_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of variable.generator must not be the same as the type 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_var_generator_equals_parent_type.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0566_syn_var_generator_equals_parent_name() {
    BeneratorResult result = runFile("test_0566_syn_var_generator_equals_parent_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_GENERATOR,
        "The value of variable.generator must not be the same as the name 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0566_syn_var_generator_equals_parent_name.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0567_syn_attr_ill_minLength() {
    BeneratorResult result = runFile("test_0567_syn_attr_ill_minLength.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MIN_LENGTH,
        "Illegal attribute value for attribute.minLength: '-3'. " +
            "File test_0567_syn_attr_ill_minLength.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0568_syn_attr_ill_maxLength() {
    BeneratorResult result = runFile("test_0568_syn_attr_ill_maxLength.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MAX_LENGTH,
        "Illegal attribute value for attribute.maxLength: '-3'. " +
            "File test_0568_syn_attr_ill_maxLength.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0568_syn_attr_maxLength_vs_minLength() {
    BeneratorResult result = runFile("test_0568_syn_attr_maxLength_vs_minLength.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MAX_LENGTH,
        "minLength (5) is greater than maxLength (3). " +
            "File test_0568_syn_attr_maxLength_vs_minLength.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0569_syn_attr_ill_nullQuota() {
    BeneratorResult result = runFile("test_0569_syn_attr_ill_nullQuota.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_NULL_QUOTA,
        "Illegal attribute value for attribute.nullQuota: '2'. " +
            "File test_0569_syn_attr_ill_nullQuota.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }


  @Test
  public void test_0571_syn_attr_encoding_wo_source() {
    BeneratorResult result = runFile("test_0571_syn_attr_encoding_wo_source.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ENCODING,
        "Element <attribute>'s attribute 'encoding' is only permitted in combination with a 'source' attribute. " +
            "File test_0571_syn_attr_encoding_wo_source.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0571_syn_attr_ill_encoding() {
    BeneratorResult result = runFile("test_0571_syn_attr_ill_encoding.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ENCODING,
        "Illegal attribute value for attribute.encoding: 'none'. " +
            "File test_0571_syn_attr_ill_encoding.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0572_syn_attr_segment_wo_source() {
    BeneratorResult result = runFile("test_0572_syn_attr_segment_wo_source.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_SEGMENT,
        "Element <attribute>'s attribute 'segment' is only permitted in combination with a 'source' attribute. " +
            "File test_0572_syn_attr_segment_wo_source.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0573_syn_attr_ill_separator() {
    BeneratorResult result = runFile("test_0573_syn_attr_ill_separator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_SEPARATOR,
        "Illegal attribute value for attribute.separator: 'none'. " +
            "File test_0573_syn_attr_ill_separator.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0573_syn_attr_separator_wo_source() {
    BeneratorResult result = runFile("test_0573_syn_attr_separator_wo_source.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_SEPARATOR,
        "Element <attribute>'s attribute 'separator' is only permitted in combination with a 'source' attribute. " +
            "File test_0573_syn_attr_separator_wo_source.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0574_syn_attr_ill_selector() {
    BeneratorResult result = runFile("test_0574_syn_attr_ill_selector.ben.xml");
    assertResult(BeneratorErrorIds.DB_QUERY_FAILED, // TODO should be SYN_ILL_SELECTOR
        "Error in query: 'none': unexpected token: NONE",
        ExitCodes.INTERNAL_SOFTWARE_ERROR, result);
  }

  @Test
  public void test_0574_syn_attr_selector_wo_source() {
    BeneratorResult result = runFile("test_0574_syn_attr_selector_wo_source.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_SELECTOR,
        "Element <attribute>'s attribute 'selector' is only permitted in combination with a 'source' attribute. " +
            "File test_0574_syn_attr_selector_wo_source.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0575_syn_attr_ill_sub_selector() {
    BeneratorResult result = runFile("test_0575_syn_attr_ill_sub_selector.ben.xml");
    assertResult(BeneratorErrorIds.DB_QUERY_FAILED, // TODO should be SYN_ATTR_SUB_SELECTOR
        "Error in query: 'none': unexpected token: NONE",
        ExitCodes.INTERNAL_SOFTWARE_ERROR, result);
  }

  @Test
  public void test_0575_syn_attr_sub_selector_wo_source() {
    BeneratorResult result = runFile("test_0575_syn_attr_sub_selector_wo_source.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_SUB_SELECTOR,
        "Element <attribute>'s attribute 'subSelector' is only permitted in combination with a 'source' attribute. " +
            "File test_0575_syn_attr_sub_selector_wo_source.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0576_syn_attr_ill_row_based() {
    BeneratorResult result = runFile("test_0576_syn_attr_ill_row_based.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROW_BASED,
        "Illegal attribute value for attribute.rowBased: 'none'. " +
            "Use 'true' or 'false. File test_0576_syn_attr_ill_row_based.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0576_syn_attr_row_based_wo_source() {
    BeneratorResult result = runFile("test_0576_syn_attr_row_based_wo_source.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_ROW_BASED,
        "Element <attribute>'s attribute 'rowBased' is only permitted in combination with a 'source' attribute. " +
            "File test_0576_syn_attr_row_based_wo_source.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0577_syn_attr_format_wo_source() {
    BeneratorResult result = runFile("test_0577_syn_attr_format_wo_source.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_FORMAT,
        "Element <attribute>'s attribute 'format' is only permitted in combination with a 'source' attribute. " +
            "File test_0577_syn_attr_format_wo_source.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0577_syn_attr_ill_format() {
    BeneratorResult result = runFile("test_0577_syn_attr_ill_format.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_FORMAT,
        "Illegal attribute value for attribute.format: 'none'. File test_0577_syn_attr_ill_format.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0578_syn_attr_empty_marker_wo_source() {
    BeneratorResult result = runFile("test_0578_syn_attr_empty_marker_wo_source.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_EMPTY_MARKER,
        "Element <attribute>'s attribute 'emptyMarker' is only permitted in combination with a 'source' attribute. " +
            "File test_0578_syn_attr_empty_marker_wo_source.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0581_syn_attr_ill_min() {
    BeneratorResult result = runFile("test_0581_syn_attr_ill_min.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MIN,
        "Illegal attribute value for attribute.min: 'none'. " +
            "File test_0581_syn_attr_ill_min.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0582_syn_attr_ill_minInclusive() {
    BeneratorResult result = runFile("test_0582_syn_attr_ill_minInclusive.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MIN_INCLUSIVE,
        "Illegal attribute value for attribute.minInclusive: 'none'. " +
            "Use 'true' or 'false. File test_0582_syn_attr_ill_minInclusive.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0582_syn_attr_minInclusive_wo_min() {
    BeneratorResult result = runFile("test_0582_syn_attr_minInclusive_wo_min.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MIN_INCLUSIVE,
        "Element <attribute>'s attribute 'minInclusive' is only permitted in combination with a 'min' attribute. " +
            "File test_0582_syn_attr_minInclusive_wo_min.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0583_syn_attr_ill_max() {
    BeneratorResult result = runFile("test_0583_syn_attr_ill_max.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MAX,
        "Illegal attribute value for attribute.max: 'none'. " +
            "File test_0583_syn_attr_ill_max.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0583_syn_attr_max_vs_min() {
    BeneratorResult result = runFile("test_0583_syn_attr_max_vs_min.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MAX,
        "min (5) is greater than max (3). File test_0583_syn_attr_max_vs_min.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0583_syn_attr_max_vs_minInclusive() {
    BeneratorResult result = runFile("test_0583_syn_attr_max_vs_minInclusive.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MIN_INCLUSIVE,
        "min equals max (3), but min is not inclusive. " +
            "File test_0583_syn_attr_max_vs_minInclusive.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0584_syn_attr_ill_maxInclusive() {
    BeneratorResult result = runFile("test_0584_syn_attr_ill_maxInclusive.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MAX_INCLUSIVE,
        "Illegal attribute value for attribute.maxInclusive: 'none'. " +
            "Use 'true' or 'false. File test_0584_syn_attr_ill_maxInclusive.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0584_syn_attr_maxInclusive_vs_min() {
    BeneratorResult result = runFile("test_0584_syn_attr_maxInclusive_vs_min.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MAX_INCLUSIVE,
        "min equals max (3), but max is not inclusive. " +
            "File test_0584_syn_attr_maxInclusive_vs_min.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0584_syn_attr_maxInclusive_vs_minInclusive() {
    BeneratorResult result = runFile("test_0584_syn_attr_maxInclusive_vs_minInclusive.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MAX_INCLUSIVE,
        "min equals max (3), but max is not inclusive. " +
            "File test_0584_syn_attr_maxInclusive_vs_minInclusive.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0584_syn_attr_maxInclusive_wo_max() {
    BeneratorResult result = runFile("test_0584_syn_attr_maxInclusive_wo_max.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ATTR_MAX_INCLUSIVE,
        "Element <attribute>'s attribute 'maxInclusive' is only permitted in combination with a 'max' attribute. " +
            "File test_0584_syn_attr_maxInclusive_wo_max.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <id> tests ------------------------------------------------------------------------------------------------------

  @Test
  public void test_0600_syn_id_with_content() {
    BeneratorResult result = runFile("test_0600_syn_id_with_content.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ID,
        "Element <id> has illegal text content: 'text'. File test_0600_syn_id_with_content.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  /* TODO implement error mapping
  @Test
  public void test_0601_id_ill_attr() {
    BeneratorResult result = runFile("test_0601_syn_id_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ID_ILLEGAL_ATTR, "Illegal attribute for <id>: ill_attr",
        ExitCodes.SYNTAX_ERROR, result);
  }
  */

  @Test
  public void test_0602_id_wo_name() {
    BeneratorResult result = runFile("test_0602_syn_id_wo_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_ID_NAME,
        "Attribute 'name' is missing in <id>. File test_0602_syn_id_wo_name.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // <reference> tests -----------------------------------------------------------------------------------------------

  @Test
  public void test_0650_syn_ref_with_content() {
    BeneratorResult result = runFile("test_0650_syn_ref_with_content.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF,
        "Element <reference> has illegal text content: 'text'. " +
            "File test_0650_syn_ref_with_content.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0651_ref_ill_attr() {
    BeneratorResult result = runFile("test_0651_syn_ref_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_ILLEGAL_ATTR, "Illegal XML attribute: " +
            "reference.ill_attr. File test_0651_syn_ref_ill_attr.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0652_syn_ref_wo_name() {
    BeneratorResult result = runFile("test_0652_syn_ref_wo_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_NAME,
        "Attribute 'name' is missing in <reference>. File test_0652_syn_ref_wo_name.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0652_syn_ref_ill_name() {
    BeneratorResult result = runFile("test_0652_syn_ref_ill_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_NAME,
        "Illegal attribute value for reference.name: '123'. File test_0652_syn_ref_ill_name.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0653_syn_ref_base_def() {
    BeneratorResult result = runFile("test_0653_syn_ref_base_def.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_BASE_DEF,
        "At least one of these attributes must be set: generator, script, source, constant, values. " +
            "File test_0653_syn_ref_base_def.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0654_syn_ref_ext_def() {
    BeneratorResult result = runFile("test_0654_syn_ref_ext_def.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_EXT_DEF,
        "At least one of these attributes must be set: targetType, selector, subSelector. " +
            "File test_0654_syn_ref_ext_def.ben.xml, line 15", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0654_syn_ref_subSel_and_sel() {
    BeneratorResult result = runFile("test_0654_syn_ref_subSel_and_sel.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_EXT_DEF,
        "The attributes 'selector' and 'subSelector' mutually exclude each other. " +
            "File test_0654_syn_ref_subSel_and_sel.ben.xml, line 5", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0656_syn_ref_ill_type() {
    BeneratorResult result = runFile("test_0656_syn_ref_ill_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_TYPE,
        "Illegal attribute value for reference.type: '-123'. " +
            "File test_0656_syn_ref_ill_type.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0657_syn_ref_ill_mode() {
    BeneratorResult result = runFile("test_0657_syn_ref_ill_mode.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_MODE,
        "Illegal attribute value for reference.mode: 'none'. " +
            "File test_0657_syn_ref_ill_mode.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0658_syn_ref_ill_nullQuota() {
    BeneratorResult result = runFile("test_0658_syn_ref_ill_nullQuota.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_NULL_QUOTA,
        "Illegal attribute value for reference.nullQuota: '2.'. " +
            "File test_0658_syn_ref_ill_nullQuota.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0659_syn_ref_ill_nullable() {
    BeneratorResult result = runFile("test_0659_syn_ref_ill_nullable.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_NULLABLE,
        "Illegal attribute value for reference.nullable: 'xyz'. " +
            "Use 'true' or 'false. File test_0659_syn_ref_ill_nullable.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0660_syn_ref_generator_equals_parent_type() {
    BeneratorResult result = runFile("test_0660_syn_ref_generator_equals_parent_type.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_GENERATOR,
        "The value of reference.generator must not be the same as the type 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0660_syn_ref_generator_equals_parent_type.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0660_syn_ref_ill_generator() {
    BeneratorResult result = runFile("test_0660_syn_ref_ill_generator.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_GENERATOR,
        "Illegal attribute value for reference.generator: '$$$'. " +
            "File test_0660_syn_ref_ill_generator.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0660_syn_ref_generator_equals_parent_name() {
    BeneratorResult result = runFile("test_0660_syn_ref_generator_equals_parent_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_GENERATOR,
        "The value of reference.generator must not be the same as the name 'UUIDGenerator' " +
            "of the surrounding <generate> element. File test_0660_syn_ref_generator_equals_parent_name.ben.xml, " +
            "line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0669_syn_ref_ill_offset() {
    BeneratorResult result = runFile("test_0669_syn_ref_ill_offset.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_OFFSET,
        "Illegal attribute value for reference.offset: '-5'. " +
            "File test_0669_syn_ref_ill_offset.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0670_syn_ref_ill_unique() {
    BeneratorResult result = runFile("test_0670_syn_ref_ill_unique.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_UNIQUE,
        "Illegal attribute value for reference.unique: '42'. " +
            "Use 'true' or 'false. File test_0670_syn_ref_ill_unique.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_0671_syn_ref_ill_cyclic() {
    BeneratorResult result = runFile("test_0671_syn_ref_ill_cyclic.ben.xml");
    assertResult(BeneratorErrorIds.SYN_REF_CYCLIC,
        "Illegal attribute value for reference.cyclic: '42'. " +
            "Use 'true' or 'false. File test_0671_syn_ref_ill_cyclic.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }



  // <part> tests ----------------------------------------------------------------------------------------------------

  /* TODO implement error mapping
  @Test
  public void test_0701_part_ill_attr() {
    BeneratorResult result = runFile("test_0701_syn_part_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_PART_ILLEGAL_ATTR, "Illegal attribute for <part>: ill_attr",
        ExitCodes.SYNTAX_ERROR, result);
  }
  */

  @Test
  public void test_0702_part_wo_name() {
    BeneratorResult result = runFile("test_0702_syn_part_wo_name.ben.xml");
    assertResult(BeneratorErrorIds.SYN_PART_NAME,
        "Attribute 'name' is missing in <part>. File test_0702_syn_part_wo_name.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  // database tests --------------------------------------------------------------------------------------------------

  @Test
  public void test_1000_syn_db_with_content() {
    BeneratorResult result = runFile("test_1000_syn_db_with_content.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB,
        "Element <database> has illegal text content: 'oracle'. File test_1000_syn_db_with_content.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1001_syn_db_ill_attr() {
    BeneratorResult result = runFile("test_1001_syn_db_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_ILLEGAL_ATTR,
        "Illegal XML attribute: database.ill_attr. File test_1001_syn_db_ill_attr.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1002_syn_db_no_id() {
    BeneratorResult result = runFile("test_1002_syn_db_wo_id.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_ID,
        "Attribute 'id' is missing in <database>. File test_1002_syn_db_wo_id.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1003_syn_db_ill_env() {
    BeneratorResult result = runFile("test_1003_syn_db_ill_env.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_ENVIRONMENT,
        "Illegal attribute value for database.environment: '!$%/'. " +
            "File test_1003_syn_db_ill_env.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1004_syn_db_ill_system() {
    BeneratorResult result = runFile("test_1004_syn_db_ill_system.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_SYSTEM,
        "Illegal attribute value for database.system: '!$%/'. " +
            "File test_1004_syn_db_ill_system.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1005_syn_db_ill_url() {
    BeneratorResult result = runFile("test_1005_syn_db_ill_url.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_URL,
        "Illegal attribute value for database.url: '%$/+-'. " +
            "File test_1005_syn_db_ill_url.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1006_syn_db_ill_driver() {
    BeneratorResult result = runFile("test_1006_syn_db_ill_driver.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_DRIVER,
        "Illegal attribute value for database.driver: '%$/+-'. " +
            "File test_1006_syn_db_ill_driver.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1015_syn_db_ill_batch() {
    BeneratorResult result = runFile("test_1015_syn_db_ill_batch.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_BATCH,
        "Illegal attribute value for database.batch: 'none'. " +
            "Use 'true' or 'false. File test_1015_syn_db_ill_batch.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1016_syn_db_ill_fetchSize() {
    BeneratorResult result = runFile("test_1016_syn_db_ill_fetchSize.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_FETCH_SIZE,
        "Illegal attribute value for database.fetchSize: '-1'. " +
            "File test_1016_syn_db_ill_fetchSize.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1017_syn_db_ill_readOnly() {
    BeneratorResult result = runFile("test_1017_syn_db_ill_readOnly.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_READ_ONLY,
        "Illegal attribute value for database.readOnly: 'none'. " +
            "Use 'true' or 'false. File test_1017_syn_db_ill_readOnly.ben.xml, line 4",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1018_syn_db_ill_lazy() {
    BeneratorResult result = runFile("test_1018_syn_db_ill_lazy.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_LAZY,
        "Illegal attribute value for database.lazy: 'none'. " +
            "Use 'true' or 'false. File test_1018_syn_db_ill_lazy.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1019_syn_db_ill_metaCache() {
    BeneratorResult result = runFile("test_1019_syn_db_ill_metaCache.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_META_CACHE,
        "Illegal attribute value for database.metaCache: 'none'. " +
            "Use 'true' or 'false. File test_1019_syn_db_ill_metaCache.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1020_syn_db_ill_acc_unk_col_types() {
    BeneratorResult result = runFile("test_1020_syn_db_ill_acc_unk_col_types.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_ACCEPT_UNK_COL_TYPES,
        "Illegal attribute value for database.acceptUnknownColumnTypes: 'none'. " +
            "Use 'true' or 'false. File test_1020_syn_db_ill_acc_unk_col_types.ben.xml, line 4", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1021_syn_db_url_group_incomplete() {
    BeneratorResult result = runFile("test_1021_syn_db_url_group_incomplete.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_URL_GROUP_INCOMPLETE,
        "if <database> has the attribute 'url' then it must have 'driver' too. " +
            "File test_1021_syn_db_url_group_incomplete.ben.xml, line 3", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test @Ignore("This is disabled for backwards compatibility. If that is dropped, then enable this test")
  public void test_1022_syn_db_env_wo_system() {
    BeneratorResult result = runFile("1022_syn_db_env_wo_system.ben.xml");
     assertResult(BeneratorErrorIds.SYN_DB_ENV_GROUP_INCOMPLETE,
     "if <database> has the attribute 'environment' then it must have 'system' too",
     ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_1022_db_url_and_env_group() {
    BeneratorResult result = runFile("test_1022_syn_db_url_and_env_group.ben.xml");
    assertResult(BeneratorErrorIds.SYN_DB_URL_AND_ENV_GROUP,
        "<database>'s attributes 'environment' and 'driver' mutually exclude each other. " +
            "File test_1022_syn_db_url_and_env_group.ben.xml, line 5", ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_2400_syn_mongo_ill_attr() {
    BeneratorResult result = runFile("test_2400_syn_mongo_ill_attr.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MONGO_ILL_ATTR,
        "Illegal XML attribute: mongodb.url. File test_2400_syn_mongo_ill_attr.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_2401_syn_mongo_missing_id() {
    BeneratorResult result = runFile("test_2401_syn_mongo_missing_id.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MONGO_ID,
        "Attribute 'id' is missing in <mongodb>. File test_2401_syn_mongo_missing_id.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_2401_syn_mongo_ill_id() {
    BeneratorResult result = runFile("test_2401_syn_mongo_ill_id.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MONGO_ID,
        "Illegal attribute value for mongodb.id: '23'. File test_2401_syn_mongo_ill_id.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_2402_syn_mongo_ill_port() {
    BeneratorResult result = runFile("test_2402_syn_mongo_ill_port.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MONGO_PORT,
        "Illegal attribute value for mongodb.port: 'none'. File test_2402_syn_mongo_ill_port.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_2407_syn_mongo_ill_clean() {
    BeneratorResult result = runFile("test_2407_syn_mongo_ill_clean.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MONGO_CLEAN,
        "Illegal attribute value for mongodb.clean: '42'. Use 'true' or 'false. File test_2407_syn_mongo_ill_clean.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_2408_syn_mongo_missing_env() {
    BeneratorResult result = runFile("test_2408_syn_mongo_missing_env.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MONGO_ENV,
        "system specified but no environment. File test_2408_syn_mongo_missing_env.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
  }

  @Test
  public void test_2409_syn_mongo_missing_sys() {
    BeneratorResult result = runFile("test_2409_syn_mongo_missing_sys.ben.xml");
    assertResult(BeneratorErrorIds.SYN_MONGO_SYS,
        "environment specified but no system. File test_2409_syn_mongo_missing_sys.ben.xml, line 3",
        ExitCodes.SYNTAX_ERROR, result);
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
    assertEquals("Wrong exit code", expectedExitCode, result.getExitCode());
    String errOut = result.getErrOut();
    if (errOut != null) {
      errOut = errOut.trim();
    }
    if (expectedErrOut != null) {
      assertNotNull("errout is empty", errOut);
      if (expectedErrOut.endsWith("*")) {
        String errMsg = "Expected: '" + expectedErrOut + "'\nActual:  '" + errOut + "'";
        assertTrue(errMsg, errOut.startsWith(expectedErrOut.substring(0, expectedErrOut.length() - 1)));
      } else {
        assertEquals(expectedErrOut, errOut);
      }
    } else {
      assertNull(errOut);
    }
  }

}
