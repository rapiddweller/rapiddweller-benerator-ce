/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

/**
 * Holds the result of one sensor in one benchmark run.<br/><br/>
 * Created: 02.11.2021 07:30:29
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SensorResult {

  private final String sensor;
  private final long count;
  private final ExecutionMode executionMode;
  private final int duration;

  public SensorResult(String sensor, long count, ExecutionMode executionMode, int duration) {
    this.sensor = sensor;
    this.count = count;
    this.executionMode = executionMode;
    this.duration = duration;
  }

  public String getSensor() {
    return sensor;
  }

  public long getCount() {
    return count;
  }

  public ExecutionMode getExecutionMode() {
    return executionMode;
  }

  public int getDuration() {
    return duration;
  }

  public double entitiesPerSecond() {
    return (double) count / duration * 1000.;
  }

  public double entitiesPerHour() {
    return 3600. * entitiesPerSecond() / 1000000.;
  }
}
