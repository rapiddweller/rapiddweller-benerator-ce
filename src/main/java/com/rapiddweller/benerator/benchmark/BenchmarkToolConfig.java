/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.benerator.environment.Environment;
import com.rapiddweller.benerator.environment.SystemRef;
import com.rapiddweller.benerator.environment.EnvironmentUtil;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.cli.CommandLineConfig;

import java.util.TreeSet;

import static com.rapiddweller.benerator.BeneratorUtil.isEEAvailable;

/**
 * Holds the configuration of a Benchmark run as specified via commend line arguments.<br/><br/>
 * Created: 21.10.2021 16:55:29
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class BenchmarkToolConfig extends CommandLineConfig {

  private boolean ce;
  private boolean ee;
  private boolean list;
  private boolean uiResult;
  private BeneratorMode mode;
  private int minSecs;
  private int maxThreads;
  private SystemRef[] systems;
  private String csv;
  private char csvSep;
  private String xls;
  private String txt;
  private String name;
  private Benchmark[] benchmarks;
  private ExecutionMode[] executionModes;

  public BenchmarkToolConfig() {
    if (isEEAvailable()) {
      this.ce = false;
      this.ee = true;
    } else {
      this.ce = true;
      this.ee = false;
    }
    this.list = false;
    this.uiResult = false;
    this.mode = BeneratorMode.STRICT;
    this.minSecs = 10;
    this.maxThreads = 0;
    this.systems = new SystemRef[0];
    this.csv = null;
    this.csvSep = ',';
    this.xls = null;
    this.txt = null;
    this.benchmarks = Benchmark.getInstances();
  }

  public boolean isList() {
    return list;
  }

  public void setList(boolean list) {
    this.list = list;
  }

  public boolean isUiResult() {
    return uiResult;
  }

  public void setUiResult(boolean uiResult) {
    this.uiResult = uiResult;
  }

  public boolean isCe() {
    return ce;
  }

  public void setCe(boolean ce) {
    this.ce = ce;
  }

  public boolean isEe() {
    return ee;
  }

  public void setEe(boolean ee) {
    this.ee = ee;
  }

  public void setSystemsSpec(String systemsSpec) {
    String[] tokens = systemsSpec.split(",");
    ArrayBuilder<SystemRef> sysBuilder = new ArrayBuilder<>(SystemRef.class);
    for (String token : tokens) {
      String[] parts = StringUtil.splitOnFirstSeparator(token, '#');
      String envName = parts[0];
      if ("builtin".equals(envName)) {
        String envFileName = EnvironmentUtil.fileName(envName);
        IOUtil.copyFile(BenchmarkRunner.RESOURCE_FOLDER + "/" + envFileName, envFileName);
      }
      Environment environment = EnvironmentUtil.parse(envName, ".");
      String sysSpec = parts[1];
      if (sysSpec != null) {
        sysBuilder.add(environment.getSystem(sysSpec));
      } else {
        for (SystemRef system : environment.getSystems()) {
          sysBuilder.add(system);
        }
      }
    }
    this.systems = sysBuilder.toArray();
  }

  public SystemRef[] getSystems() {
    return this.systems;
  }

  public String getCsv() {
    return csv;
  }

  public void setCsv(String csv) {
    this.csv = csv;
  }

  public char getCsvSep() {
    return csvSep;
  }

  public void setCsvSep(char csvSep) {
    this.csvSep = csvSep;
  }

  public String getXls() {
    return xls;
  }

  public void setXls(String xls) {
    this.xls = xls;
  }

  public String getTxt() {
    return txt;
  }

  public void setTxt(String txt) {
    this.txt = txt;
  }

  public BeneratorMode getMode() {
    return mode;
  }

  public void setMode(BeneratorMode mode) {
    this.mode = mode;
  }

  public int getMinSecs() {
    return minSecs;
  }

  public void setMinSecs(int minSecs) {
    this.minSecs = minSecs;
  }

  public int getMaxThreads() {
    return maxThreads;
  }

  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    this.benchmarks = new Benchmark[] { Benchmark.getInstance(name) };
  }

  public Benchmark[] getBenchmarks() {
    return benchmarks;
  }

  public ExecutionMode[] getExecutionModes() {
    return executionModes;
  }

  public void prepareExecutionModes() {
    if (!ee || (!ce && maxThreads == 1)) {
      this.executionModes = new ExecutionMode[] { new ExecutionMode(ee, 1) };
    } else {
      // determine thread counts for EE
      TreeSet<Integer> set = new TreeSet<>();
      for (int i = 1; i < maxThreads; i *= 2) {
        set.add(i);
      }
      set.add(maxThreads);
      // create result object
      // add an element if a CE run is requested additionally to the EE runs
      int ceRun = (ce ? 1 : 0);
      ExecutionMode[] result = new ExecutionMode[set.size() + ceRun];
      // insert the CE run first...
      if (ceRun == 1) {
        result[0] = new ExecutionMode(false, 1);
      }
      // ...and then the EE runs with creasing thread count
      int i = ceRun;
      for (int n : set) {
        result[i++] = new ExecutionMode(true, n);
      }
      this.executionModes = result;
    }
  }

}
