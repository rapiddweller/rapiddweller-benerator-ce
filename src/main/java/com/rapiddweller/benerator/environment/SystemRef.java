/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.environment;

import com.rapiddweller.common.Named;

import java.util.Map;
import java.util.Objects;

/**
 * Environment entry for a system.<br/><br/>
 * Created: 03.11.2021 17:58:34
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class SystemRef implements Named {

  private final Environment environment;
  private final String name;
  private final String type;
  private final Map<String, String> properties;

  public SystemRef(Environment environment, String name, String type, Map<String, String> properties) {
    this.environment = environment;
    this.name = name;
    this.type = type;
    this.properties = properties;
  }

  public Environment getEnvironment() {
    return environment;
  }

  @Override
  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public String getProperty(String name) {
    return properties.get(name);
  }

  public boolean isDb() {
    return "db".equals(type);
  }

  public boolean isKafka() {
    return "kafka".equals(type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SystemRef that = (SystemRef) o;
    return Objects.equals(this.environment, that.environment)
        && Objects.equals(this.name, that.type)
        && Objects.equals(this.type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(environment, name, type);
  }

  @Override
  public String toString() {
    return environment.getName() + '#' + name;
  }

}
