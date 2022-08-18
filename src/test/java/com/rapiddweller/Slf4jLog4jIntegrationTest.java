/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.junit.Test;
import org.slf4j.impl.StaticLoggerBinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Verifies that Slf4j is properly configured to call Log4j 2.<br/><br/>
 * Created: 07.10.2021 19:02:05
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class Slf4jLog4jIntegrationTest {

  public static final String SLF4J_LOG4J_LOGGER_FACTORY = "org.apache.logging.slf4j.Log4jLoggerFactory";

  @Test
  public void testSl4fjStaticLoggerBinder() {
    StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();
    assertEquals(SLF4J_LOG4J_LOGGER_FACTORY, binder.getLoggerFactoryClassStr());
  }

  @Test
  public void testLog4j2() {
    LoggerContext context = (LoggerContext) LogManager.getContext();
    Configuration configuration = context.getConfiguration();
    ConfigurationSource configSource = configuration.getConfigurationSource();
    assertNotNull(configSource);
    String configSourceLocation = configSource.getLocation();
    assertNotNull(configSourceLocation);
    assertTrue(configSourceLocation.endsWith("log4j2.xml"));
  }

}
