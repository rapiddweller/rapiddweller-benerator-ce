/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine;

/**
 * Assembles the information returned after a Benerator run.<br/><br/>
 * Created: 18.11.2021 16:41:46
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorResult {

  private final int exitCode;
  private final String errOut;

  public BeneratorResult(int exitCode, String errOut) {
    this.exitCode = exitCode;
    this.errOut = errOut;
  }

  public int getExitCode() {
    return exitCode;
  }

  public String getErrOut() {
    return errOut;
  }

}
