/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.cli.CommandLineConfig;

/**
 * Holds the configuration for a Benerator run.<br/><br/>
 * Created: 21.10.2021 14:38:58
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class BeneratorConfig extends CommandLineConfig {

  private boolean clearCaches;
  private BeneratorMode mode;
  private String list;
  private String file;
  private boolean exception;

  public BeneratorConfig() {
    this.clearCaches = false;
    this.mode = BeneratorMode.LENIENT;
    this.list = null;
    this.file = "benerator.xml";
    this.exception = false;
  }

  public String getList() {
    return list;
  }

  public void setList(String list) {
    if (!"db".equals(list) && !"kafka".equals(list)) {
      throw BeneratorExceptionFactory.getInstance().illegalCommandLineOptionValue("--list", list);
    } else {
      this.list = list;
    }
  }


  public boolean isClearCaches() {
    return clearCaches;
  }

  public void setClearCaches(boolean clearCaches) {
    this.clearCaches = clearCaches;
  }

  public BeneratorMode getMode() {
    return mode;
  }

  public void setMode(BeneratorMode mode) {
    this.mode = mode;
  }

  public boolean isException() {
    return exception;
  }

  public void setException(boolean exception) {
    this.exception = exception;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

}
