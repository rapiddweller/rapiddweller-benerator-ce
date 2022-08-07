/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs deprecation messages.<br/><br/>
 * Created: 03.11.2021 18:59:48
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class DeprecationLogger {

  private static final Logger logger = LoggerFactory.getLogger(DeprecationLogger.class);

  private DeprecationLogger() {
    // private constructor to prevent instantiation of this utility class
  }

  public static void warn(String message) {
    logger.warn(message);
  }

}
