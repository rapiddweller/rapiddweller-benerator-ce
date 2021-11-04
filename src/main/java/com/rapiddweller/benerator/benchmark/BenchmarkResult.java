/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.common.OrderedMap;

import java.util.Collection;

/**
 * Holds the measurement of all sensors on all execution modes in one benchmark in one environment.<br/><br/>
 * Created: 02.11.2021 07:32:46
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BenchmarkResult {

  private final Benchmark benchmark;
  private final SystemRef system;
  private final OrderedMap<String, SensorSummary> sensorSummaries;

  public BenchmarkResult(Benchmark benchmark, SystemRef system) {
    this.benchmark = benchmark;
    this.system = system;
    this.sensorSummaries = new OrderedMap<>();
  }

  public Benchmark getBenchmark() {
    return benchmark;
  }

  public SystemRef getSystem() {
    return system;
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
