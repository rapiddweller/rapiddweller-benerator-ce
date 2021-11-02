/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.main.Benerator;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.VMInfo;
import com.rapiddweller.common.version.VersionInfo;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the results of a Benchmark set execution.<br/><br/>
 * Created: 02.11.2021 09:13:06
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BenchmarkSummary {

  private final BenchmarkConfig config;
  private final VersionInfo version;
  private final BeneratorMode mode;
  private final String osInfo;
  private final String cpuAndMemInfo;
  private final String javaVersion;
  private final String jvmInfo;
  private final List<String> dbs;
  private final ZonedDateTime startDateTime;
  private int durationSecs;
  private final ExecutionMode[] executionModes;
  private final List<BenchmarkResult> results;

  public BenchmarkSummary(BenchmarkConfig config) {
    this.config = config;
    this.version = VersionInfo.getInfo("benerator");
    this.osInfo = BeneratorUtil.getOsInfo();
    this.mode = Benerator.getMode();
    this.executionModes = config.getThreadings();
    this.cpuAndMemInfo = BeneratorUtil.getCpuAndMemInfo();
    this.javaVersion = VMInfo.getJavaVersion();
    this.jvmInfo = BeneratorUtil.getJVMInfo();
    this.startDateTime = ZonedDateTime.now();
    this.dbs = new ArrayList<>();
    this.results = new ArrayList<>();
  }

  public VersionInfo getVersionInfo() {
    return version;
  }

  public ExecutionMode[] getExecutionModes() {
    return executionModes;
  }

  public BeneratorMode getMode() {
    return this.mode;
  }

  public String getOsInfo() {
    return this.osInfo;
  }

  public String getCpuAndMemInfo() {
    return cpuAndMemInfo;
  }

  public String getJavaVersion() {
    return javaVersion;
  }

  public String getJVMInfo() {
    return this.jvmInfo;
  }

  public List<String> getDbs() {
    return dbs;
  }

  public ZonedDateTime getStartDateTime() {
    return startDateTime;
  }

  public int getDurationSecs() {
    return durationSecs;
  }

  public BenchmarkSummary stop() {
    this.durationSecs = (int) (ZonedDateTime.now().toEpochSecond() - startDateTime.toEpochSecond());
    return this;
  }

  public Environment[] getEnvironments() {
    return config.getEnvironments();
  }

  public Environment[] getEnvironments(EnvironmentType type) {
    ArrayBuilder<Environment> result = new ArrayBuilder<>(Environment.class);
    for (Environment tmp : config.getEnvironments()) {
      if (tmp.getType().equals(type)) {
        result.add(tmp);
      }
    }
    return result.toArray();
  }

  public List<BenchmarkResult> getResults() {
    return results;
  }

  public void addResult(BenchmarkResult result) {
    this.results.add(result);
  }

  public long getMinSecs() {
    return config.getMinSecs();
  }

}
