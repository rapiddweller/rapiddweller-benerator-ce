/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.common.OrderedMap;

import java.util.Collection;

/**
 * Holds the measurement of all sensors on all execution modes in one benchmark in one environment.<br/><br/>
 * Created: 02.11.2021 07:32:46
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BenchmarkResult {

  private final BenchmarkDefinition benchmark;
  private final Environment environment;
  private final OrderedMap<String, SensorSummary> sensorSummaries;

  public BenchmarkResult(BenchmarkDefinition benchmark, Environment environment) {
    this.benchmark = benchmark;
    this.environment = environment;
    this.sensorSummaries = new OrderedMap<>();
  }

  public BenchmarkDefinition getBenchmark() {
    return benchmark;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public Collection<String> getSensors() {
    return sensorSummaries.keySet();
  }

  public SensorSummary getSensorSummary(String sensor) {
    return sensorSummaries.get(sensor);
  }

  public void addResult(SensorResult result) {
    SensorSummary results = sensorSummaries.computeIfAbsent(result.getSensor(), k -> new SensorSummary());
    results.addResult(result);
  }

}
