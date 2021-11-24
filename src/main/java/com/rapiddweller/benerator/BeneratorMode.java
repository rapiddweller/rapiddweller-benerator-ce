/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates Benerator execution modes.<br/><br/>
 * Created: 18.10.2021 19:40:36
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorMode {

  private static final Map<String, BeneratorMode> INSTANCES = new HashMap<>();

  public static final BeneratorMode STRICT  = new BeneratorMode("strict");
  public static final BeneratorMode LENIENT = new BeneratorMode("lenient");
  public static final BeneratorMode TURBO   = new BeneratorMode("turbo");

  public static BeneratorMode getInstance(String code) {
    BeneratorMode result = INSTANCES.get(code);
    if (result == null) {
      throw BeneratorExceptionFactory.getInstance().objectNotFound("No BeneratorMode od code " + code);
    }
    return result;
  }

  private final String code;

  private BeneratorMode(@NotNull String code) {
    this.code = code;
    INSTANCES.put(this.getCode(), this);
  }

  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return code;
  }

}
