/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.environment;

import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Named;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Specifies one or more systems and optional plain settings.<br/><br/>
 * Created: 03.11.2021 14:56:30
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class Environment implements Named {

  // factory method --------------------------------------------------------------------------------------------------

  public static Environment parse(String envName, Map<String, String> properties) {
    Environment result = new Environment(envName);
    Map<String, Map<String, String>> nameMap = CollectionUtil.stripOffPrefixes(properties);
    for (Map.Entry<String, Map<String, String>> nameEntry : nameMap.entrySet()) {
      String name = nameEntry.getKey();
      Map<String, Map<String, String>> typesMap = CollectionUtil.stripOffPrefixes(nameEntry.getValue());
      if (typesMap.size() > 1) {
        throw new ConfigurationError("Invalid environment definition: There are different system types " +
            typesMap.keySet() + "assigned to the same system name '" + name + "' in '" + envName + "' environment");
      }
      Map.Entry<String, Map<String, String>> typeEntry = typesMap.entrySet().iterator().next();
      String type = typeEntry.getKey();
      if (type.isEmpty()) {
        result.settings.put(name, properties.get(name));
      } else {
        Map<String, String> sysProps = typeEntry.getValue();
        result.systems.put(name, new SystemRef(result, name, type, sysProps));
      }
    }
    return result;
  }

  // instance members ------------------------------------------------------------------------------------------------

  private final String name;
  private final Map<String, String> settings;
  private final Map<String, SystemRef> systems;

  public Environment(String name) {
    this.name = name;
    this.settings = new HashMap<>();
    this.systems = new HashMap<>();
  }

  public String getName() {
    return this.name;
  }

  public String getSetting(String name) {
    return settings.get(name);
  }

  public Map<String, String> getSettings() {
    return settings;
  }

  public SystemRef getSystem(String name) {
    return systems.get(name);
  }

  public Collection<SystemRef> getSystems() {
    return systems.values();
  }

  public void addSystem(String name, String type, Map<String, String> properties) {
    this.systems.put(name, new SystemRef(this, name, type, properties));
  }

  @Override
  public String toString() {
    return name;
  }

}
