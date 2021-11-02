/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.benchmark;

/**
 * Specifies an environment.<br/><br/>
 * Created: 02.11.2021 07:34:02
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class Environment {

  private final EnvironmentType type;
  private final String name;

  public Environment(EnvironmentType type, String name) {
    this.type = type;
    this.name = name;
  }

  public static Environment ofDb(String name) {
    return new Environment(EnvironmentType.DB, name);
  }

  public static Environment ofKafka(String name) {
    return new Environment(EnvironmentType.KAFKA, name);
  }

  public EnvironmentType getType() {
    return type;
  }

  public boolean isDb() {
    return (type == EnvironmentType.DB);
  }

  public boolean isKafka() {
    return (type == EnvironmentType.KAFKA);
  }

  public String getName() {
    return name;
  }

}
