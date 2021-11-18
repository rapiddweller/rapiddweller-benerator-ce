/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.cli.IllegaCommandLineArgumentException;
import com.rapiddweller.common.cli.IllegalCommandLineOptionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests the {@link Benerator} main class.<br/><br/>
 * Created: 21.10.2021 14:16:08
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorTest {

  private static final String DEFAULT_FILE = "benerator.xml";

  @Test
  public void testEmpty() {
    checkExecution(BeneratorMode.LENIENT, DEFAULT_FILE);
  }

  @Test
  public void testHelp() {
    checkHelpOrVersion(true, false, "--help");
    checkHelpOrVersion(true, false, "-h");
    checkHelpOrVersion(true, false, "-help");
  }

  @Test
  public void testVersion() {
    checkHelpOrVersion(false, true, "--version");
    checkHelpOrVersion(false, true, "-v");
    checkHelpOrVersion(false, true, "-version");
  }

  @Test
  public void testFileOnly() {
    checkExecution(BeneratorMode.LENIENT, "my.ben.xml", "my.ben.xml");
  }

  @Test
  public void testModes() {
    checkExecution(BeneratorMode.STRICT, "benerator.xml", "--mode", "strict");
    checkExecution(BeneratorMode.LENIENT, "benerator.xml", "--mode", "lenient");
    checkExecution(BeneratorMode.TURBO, "benerator.xml", "--mode", "turbo");
  }

  @Test
  public void testAll() {
    checkExecution(BeneratorMode.TURBO, "my.ben.xml", "--mode", "turbo", "my.ben.xml");
  }

  @Test(expected = IllegaCommandLineArgumentException.class)
  public void testModeFlagTypo() {
    checkExecution(BeneratorMode.STRICT, "test.ben.xml", "mode", "strict", "test.ben.xml");
  }

  @Test(expected = IllegalCommandLineOptionException.class)
  public void testIllegalMode() {
    checkExecution(BeneratorMode.STRICT, "test.ben.xml",  "--mode", "superduper", "test.ben.xml");
  }

  // test helpers ----------------------------------------------------------------------------------------------------

  private void checkHelpOrVersion(boolean expHelp, boolean expVersion, String... args) {
    BeneratorConfig config = Benerator.parseCommandLine(args);
    assertEquals(expHelp, config.isHelp());
    assertEquals(expVersion, config.isVersion());
  }

  private void checkExecution(BeneratorMode expMode, String expFile, String... args) {
    BeneratorConfig config = Benerator.parseCommandLine(args);
    assertFalse(config.isHelp());
    assertFalse(config.isVersion());
    assertEquals(expMode, config.getMode());
    assertEquals(expFile, config.getFile());
  }

}
