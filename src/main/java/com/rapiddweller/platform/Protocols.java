package com.rapiddweller.platform;

import com.rapiddweller.common.ConfigurationError;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Protocols {

  private static final Map<String, Protocol> INSTANCES = new HashMap<String, Protocol>();

  public static void register(Protocol instance) {
    INSTANCES.put(instance.getName(), instance);
  }

  public static Protocol get(String protocolName) {
    Protocol protocol = INSTANCES.get(protocolName);
    if (protocol == null)
      throw new ConfigurationError("No protocol defined for '" + protocolName + "'");
    return protocol;
  }

  public static Collection<Protocol> all() {
    return INSTANCES.values();
  }

}
