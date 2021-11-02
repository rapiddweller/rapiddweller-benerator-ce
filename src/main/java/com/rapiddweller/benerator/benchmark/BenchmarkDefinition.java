/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

import com.rapiddweller.common.ObjectNotFoundException;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.common.version.VersionNumberParser;

import java.util.Objects;

/**
 * Holds the core data of a benchmark definition.<br/><br/>
 * Created: 02.11.2021 07:32:01
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BenchmarkDefinition {

  private static final String V200 = "2.0.0";
  private static final String V210 = "2.1.0";

  static final BenchmarkDefinition[] INSTANCES = {
      new BenchmarkDefinition("gen-string", false, V200, 120000, "Generation of big entities with random strings"),
      new BenchmarkDefinition("gen-big-entity", false, V200, 100000, "Generation of big entities (323 attributes)"),
      new BenchmarkDefinition("gen-person-showcase", false, V200, 80000, "Generation of real-looking person data"),
      new BenchmarkDefinition("anon-person-showcase", false, V200, 100000, "Anonymization with real-looking person data"),
      new BenchmarkDefinition("anon-person-regex", false, V200, 1500000, "Anonymization with regular expressions"),
      new BenchmarkDefinition("anon-person-hash", false, V200, 1500000, "Anonymization with hashes of the original values"),
      new BenchmarkDefinition("anon-person-random", false, V200, 1500000, "Anonymization with random data"),
      new BenchmarkDefinition("anon-person-constant", false, V200, 8000000, "Anonymization with constant data"),
      new BenchmarkDefinition("file-csv", false, V210, 1000000, "Reading/writing CSV files"),
      new BenchmarkDefinition("file-dbunit", false, V210, 1000000, "Reading/writing DbUnit files"),
      new BenchmarkDefinition("file-json", true, V210, 800000, "Reading/writing JSON files"),
      new BenchmarkDefinition("file-fixedwidth", false, V210, 500000, "Reading/writing fixed-width-files"),
      new BenchmarkDefinition("file-out-xml", false, V210, 500000, "Writing XML files"),
      new BenchmarkDefinition("db-small-table", false, V200, 15000, "Reading/writing small database tables (10 columns)"),
      new BenchmarkDefinition("db-big-table", false, V200, 5000, "Reading/writing big database tables (323 columns)"),
      new BenchmarkDefinition("kafka-small-entity", true, V200, 10000, "Sending/receiving small entities to/from Kafka"),
      new BenchmarkDefinition("kafka-big-entity", true, V200, 10000, "Sending/receiving big entities to/from Kafka (652 attributes)")
  };

  public static BenchmarkDefinition[] getInstances() {
    return INSTANCES;
  }

  public static BenchmarkDefinition getInstance(String setupName) {
    for (BenchmarkDefinition setup : INSTANCES) {
      if (setup.name.equals(setupName)) {
        return setup;
      }
    }
    throw new ObjectNotFoundException("Found no setup of name '" + setupName + "'");
  }

  // instance members ------------------------------------------------------------------------------------------------

  private final String name;
  private final String fileName;
  private final boolean reqEE;
  private final VersionNumber requiredVersion;
  private final int initialCount;
  private final String description;

  public BenchmarkDefinition(String name, boolean reqEE, String requiredVersion, int initialCount, String description) {
    this.name = name;
    this.fileName = name + ".ben.xml";
    this.reqEE = reqEE;
    this.requiredVersion = new VersionNumberParser().parse(requiredVersion);
    this.initialCount = initialCount;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public boolean isDb() {
    return name.startsWith("db-");
  }

  public boolean isKafka() { return name.startsWith("kafka-"); }

  public String getFileName() {
    return fileName;
  }

  public VersionNumber getRequiredVersion() {
    return requiredVersion;
  }

  public boolean isReqEE() {
    return reqEE;
  }

  public int getInitialCount() {
    return initialCount;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BenchmarkDefinition that = (BenchmarkDefinition) o;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

}
