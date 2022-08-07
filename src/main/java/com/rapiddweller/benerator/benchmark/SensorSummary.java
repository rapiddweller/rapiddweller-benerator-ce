/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import java.util.TreeMap;

/**
 * Holds the measurements of a sensor over tests with different ExecutionModes.<br/><br/>
 * Created: 02.11.2021 16:07:34
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SensorSummary {

  private TreeMap<ExecutionMode, SensorResult> sensorResults;

  public SensorSummary() {
    this.sensorResults = new TreeMap<>();
  }

  public void addResult(SensorResult result) {
    sensorResults.put(result.getExecutionMode(), result);
  }

  public SensorResult getResult(ExecutionMode executionMode) {
    return sensorResults.get(executionMode);
  }

}
