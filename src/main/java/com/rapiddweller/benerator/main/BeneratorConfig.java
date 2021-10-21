/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.common.cli.CommandLineConfig;

/**
 * Holds the configuration for a Benerator run.<br/><br/>
 * Created: 21.10.2021 14:38:58
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorConfig extends CommandLineConfig {

  private BeneratorMode mode;
  private String file;

  public BeneratorConfig() {
    this.file = "benerator.xml";
    this.mode = BeneratorMode.LENIENT;
  }

  public BeneratorMode getMode() {
    return mode;
  }

  public void setMode(BeneratorMode mode) {
    this.mode = mode;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

}
