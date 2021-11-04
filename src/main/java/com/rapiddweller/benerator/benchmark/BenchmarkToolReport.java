/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.environment.SystemRef;
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
public class BenchmarkToolReport {

  private final BenchmarkToolConfig config;
  private final VersionInfo version;
  private final BeneratorMode mode;
  private final String osInfo;
  private final String cpuAndMemInfo;
  private final String javaVersion;
  private final String jvmInfo;
  private final List<SystemRef> systems;
  private final ZonedDateTime startDateTime;
  private final ExecutionMode[] executionModes;
  private final List<BenchmarkResult> results;
  private int durationSecs;

  public BenchmarkToolReport(BenchmarkToolConfig config) {
    this.config = config;
    this.version = VersionInfo.getInfo("benerator");
    this.osInfo = BeneratorUtil.getOsInfo();
    this.mode = Benerator.getMode();
    this.executionModes = config.getThreadings();
    this.cpuAndMemInfo = BeneratorUtil.getCpuAndMemInfo();
    this.javaVersion = VMInfo.getJavaVersion();
    this.jvmInfo = BeneratorUtil.getJVMInfo();
    this.startDateTime = ZonedDateTime.now();
    this.systems = new ArrayList<>();
    this.results = new ArrayList<>();
    this.durationSecs = 0;
  }

  public String getProjectFolder() {
    return config.getProjectFolder();
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

  public List<SystemRef> getSstems() {
    return systems;
  }

  public ZonedDateTime getStartDateTime() {
    return startDateTime;
  }

  public int getDurationSecs() {
    return durationSecs;
  }

  public BenchmarkToolReport stop() {
    this.durationSecs = (int) (ZonedDateTime.now().toEpochSecond() - startDateTime.toEpochSecond());
    return this;
  }

  public SystemRef[] getSystems() {
    return config.getSystems();
  }

  public SystemRef[] getSystems(String type) {
    ArrayBuilder<SystemRef> result = new ArrayBuilder<>(SystemRef.class);
    for (SystemRef candidate : config.getSystems()) {
      if (candidate.getType().equals(type)) {
        result.add(candidate);
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
