/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.ConfigUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.ui.BufferedTextPrinter;
import com.rapiddweller.common.ui.TextPrinter;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link BeneratorUtil}.<br/><br/>
 * Created: 28.09.2021 12:59:23
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorUtilTest {

  public static final String TEST_ENV_FOLDER = "src/test/resources/com/rapiddweller/benerator/testenv";

  @Test
  public void testIsDescriptorFilePath_benerator_xml() {
    assertTrue(BeneratorUtil.isDescriptorFilePath("benerator.xml"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("BENERATOR.XML"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("myproject/benerator.xml"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("MYPROJECT/BENERATOR.XML"));
  }

  @Test
  public void testIsDescriptorFilePath_ben_xml() {
    assertTrue(BeneratorUtil.isDescriptorFilePath("generate.ben.xml"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("GENERATE.BEN.XML"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("myproject/generate.ben.xml"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("MYPROJECT/GENERATE.BEN.XML"));
  }

  @Test
  public void testIsDescriptorFilePath_illegal() {
    assertFalse(BeneratorUtil.isDescriptorFilePath(null));
    assertFalse(BeneratorUtil.isDescriptorFilePath(""));
    assertFalse(BeneratorUtil.isDescriptorFilePath("test.csv"));
    assertFalse(BeneratorUtil.isDescriptorFilePath("benerator/list.txt"));
  }

  @Test
  public void testisEEAvailable() {
    assertFalse(BeneratorUtil.isEEAvailable());
  }

  @Test
  public void testCheckSystem() {
    BufferedTextPrinter p = new BufferedTextPrinter();
    BeneratorUtil.checkSystem(p);
    assertTrue(p.toString().startsWith("Benerator "));
  }

  @Test
  public void testGetJVMInfo() {
    assertTrue(BeneratorUtil.getJVMInfo().startsWith(VMInfo.getJavaVmName()));
  }

  @Test
  public void getCpuAndMemInfo() {
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    assertTrue(BeneratorUtil.getCpuAndMemInfo().startsWith(availableProcessors + " cores"));
  }

  @Test
  public void testGetMemGB() {
    assertTrue(BeneratorUtil.getMemGB() > 0);
  }

  @Test
  public void testGetOsInfo() {
    assertTrue(BeneratorUtil.getOsInfo().startsWith(SystemInfo.getOsName()));
  }

  @Test
  public void testPrintEnvironments() {
    BeneratorUtil.printEnvironments();
    assertTrue(true);
  }

  @Test
  public void testFormatEnvironments() {
    String list = BeneratorUtil.formatEnvironmentList(TEST_ENV_FOLDER);
    assertFalse(list.isEmpty());
    assertTrue(list.contains("test_h2_mem"));
  }

  @Test
  public void testPrintEnvDbs() {
    TextPrinter printer = new BufferedTextPrinter();
    BeneratorUtil.printEnvDbs(printer);
    assertTrue(printer.toString().length() > 0);
  }

  @Test
  public void testPrintEnvKafkas() {
    TextPrinter printer = new BufferedTextPrinter();
    BeneratorUtil.printEnvKafkas(printer);
    assertTrue(printer.toString().length() > 0);
  }

  @Test
  public void testClearCaches() {
    BeneratorUtil.clearCaches();
    File cacheFolder = ConfigUtil.commonCacheFolder();
    assertTrue(cacheFolder.isDirectory());
    assertTrue(ArrayUtil.isEmpty(cacheFolder.list()));
  }

}
