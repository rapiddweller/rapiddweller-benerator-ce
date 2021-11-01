/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.main;

import com.rapiddweller.benerator.BeneratorMode;
import com.rapiddweller.common.cli.CommandLineConfig;

import static com.rapiddweller.benerator.BeneratorUtil.isEEAvailable;

/**
 * Holds the configuration of a Benchmark run as specified via commend line arguments.<br/><br/>
 * Created: 21.10.2021 16:55:29
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BenchmarkConfig extends CommandLineConfig {

  private boolean ce;
  private boolean ee;
  private boolean list;
  private BeneratorMode mode;
  private int minSecs;
  private int maxThreads;
  private String[] dbs;
  private String[] kafkas;
  private String name;
  private Benchmark.Setup[] setups;

  public BenchmarkConfig() {
    if (isEEAvailable()) {
      this.ce = false;
      this.ee = true;
    } else {
      this.ce = true;
      this.ee = false;
    }
    this.mode = BeneratorMode.STRICT;
    this.minSecs = 10;
    this.maxThreads = 0;
    this.dbs = new String[0];
    this.kafkas = new String[0];
    this.setups = Benchmark.SETUPS;
  }

  public boolean isList() {
    return list;
  }

  public void setList(boolean list) {
    this.list = list;
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

  public void setDbsSpec(String dbsSpec) {
    this.dbs = (dbsSpec != null ? dbsSpec.split(",") : new String[0]);
  }

  public String[] getDbs() {
    return dbs;
  }

  public void setKafkasSpec(String kafkaSpec) {
    this.kafkas = (kafkaSpec != null ? kafkaSpec.split(",") : new String[0]);
  }

  public String[] getKafkas() {
    return kafkas;
  }

  public Benchmark.Environment[] getEnvironments() {
    Benchmark.Environment[] result = new Benchmark.Environment[dbs.length + kafkas.length];
    for (int i = 0; i < dbs.length; i++) {
      result[i] = new Benchmark.Environment(Benchmark.EnvironmentType.DB, dbs[i]);
    }
    for (int i = 0; i < kafkas.length; i++) {
      result[dbs.length + i] = new Benchmark.Environment(Benchmark.EnvironmentType.KAFKA, kafkas[i]);
    }
    return result;
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
    this.setups = new Benchmark.Setup[] { Benchmark.getSetup(name) };
  }

  public Benchmark.Setup[] getSetups() {
    return setups;
  }

}
