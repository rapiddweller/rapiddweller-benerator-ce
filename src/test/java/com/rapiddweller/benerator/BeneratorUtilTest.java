/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.ui.BufferedInfoPrinter;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link BeneratorUtil}.<br/><br/>
 * Created: 28.09.2021 12:59:23
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorUtilTest {

  @Test
  public void testIsDescriptorFilePath() {
    assertFalse(BeneratorUtil.isDescriptorFilePath(null));
    assertFalse(BeneratorUtil.isDescriptorFilePath(""));
    assertTrue(BeneratorUtil.isDescriptorFilePath("benerator.xml"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("myproject/benerator.xml"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("generate.ben.xml"));
    assertTrue(BeneratorUtil.isDescriptorFilePath("myproject/generate.ben.xml"));
  }

  @Test
  public void testCheckSystem() {
    BufferedInfoPrinter p = new BufferedInfoPrinter();
    BeneratorUtil.checkSystem(p);
    assertTrue(p.toString().startsWith("Benerator "));
  }

  @Test
  public void testPrintVersionInfo() {
    BufferedInfoPrinter p = new BufferedInfoPrinter();
    BeneratorUtil.printVersionInfo(p);
    assertTrue(p.toString().startsWith("Benerator "));
  }

  @Test
  public void testGetJVMInfo() {
    assertTrue(BeneratorUtil.getJVMInfo().startsWith(VMInfo.getJavaVmName()));
  }

  @Test
  public void testLogConfig() {
    BeneratorUtil.logConfig("Test");
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

}
