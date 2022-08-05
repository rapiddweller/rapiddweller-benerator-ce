/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.environment;

import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.jdbacl.DatabaseDialect;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the {@link EnvironmentUtil}.<br/><br/>
 * Created: 04.11.2021 09:46:29
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class EnvironmentUtilTest {

  private static final String PROJECT_FOLDER = "target" + SystemInfo.getFileSeparator() + "test-classes" +
      SystemInfo.getFileSeparator() + BeanUtil.packageFolder(EnvironmentUtilTest.class);

  // tests -----------------------------------------------------------------------------------------------------------

  @Test
  public void testFindEnvironments_global_smoketest() {
    Map<String, Environment> environments = EnvironmentUtil.findEnvironments();
    assertNotNull(environments);
  }

  @Test @Ignore("Only for individual testing, since it requires a 'local.env.properties' in the user's home folder")
  public void testFindEnvironments_global() {
    Map<String, Environment> environments = EnvironmentUtil.findEnvironments();
    System.out.println(environments);
    Environment localEnv = environments.get("local");
    assertNotNull(localEnv);
    assertNotNull(localEnv.getSystem("h2"));
  }

  @Test
  public void testFindEnvironments_folder() {
    Map<String, Environment> environments = EnvironmentUtil.findEnvironments(PROJECT_FOLDER);
    System.out.println(environments);
    Environment env = environments.get("unittest");
    verifyUnittestEnvironment(env);
  }

  @Test
  public void testParse() {
    Environment environment = EnvironmentUtil.parse("unittest", PROJECT_FOLDER);
    assertEquals("unittest", environment.getName());
    verifyUnittestEnvironment(environment);
  }

  @Test
  public void testFindSystems() {
    SystemRef[] dbs = EnvironmentUtil.findSystems("db", PROJECT_FOLDER);
    for (SystemRef db : dbs) {
      if (db.isDb() && "xy".equals(db.getName())) {
        return;
      }
    }
    fail("Database 'xy' not found in any environment in " + PROJECT_FOLDER);
  }

  @Test
  public void testFileName() {
    assertEquals("unittest.env.properties", EnvironmentUtil.fileName("unittest"));
  }

  @Test
  public void testGetDbProductDescription() {
    SystemRef dbSystem = createDbTestSystem();
    try {
      String description = EnvironmentUtil.getDbProductDescription(dbSystem);
      assertTrue(description.startsWith("HSQL Database Engine "));
    } finally {
      HSQLUtil.shutdown(dbSystem.getProperty("url"), HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD);
    }
  }

  @Test
  public void testGetDbDialect() {
    SystemRef dbSystem = createDbTestSystem();
    try {
      DatabaseDialect dbDialect = EnvironmentUtil.getDbDialect(dbSystem);
      assertEquals("hsql", dbDialect.getDbType());
    } finally {
      HSQLUtil.shutdown(dbSystem.getProperty("url"), HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD);
    }
  }

  @Test
  public void testConnectDb() throws SQLException {
    SystemRef system = createDbTestSystem();
    Connection connection = EnvironmentUtil.connectDb(system);
    try {
      connection = EnvironmentUtil.connectDb(system);
      assertNotNull(connection);
    } finally {
      connection.close();
      HSQLUtil.shutdown(system.getProperty("url"), HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD);
    }
  }

  @Test(expected = ConfigurationError.class)
  public void testConnectDb_illegal_system_type() {
    Environment environment = new Environment("dbtest");
    Map<String, String> properties = new HashMap<>();
    SystemRef system = new SystemRef(environment, "testdb", "kafka", properties);
    EnvironmentUtil.connectDb(system);
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void verifyUnittestEnvironment(Environment env) {
    assertNotNull(env);
    SystemRef xy = env.getSystem("xy");
    assertNotNull(xy);
    assertEquals("db", xy.getType());
  }

  private SystemRef createDbTestSystem() {
    Environment environment = new Environment("dbtest");
    Map<String, String> properties = new HashMap<>();
    String url = HSQLUtil.getInMemoryURL("dbtest");
    properties.put("url", url);
    properties.put("driver", HSQLUtil.DRIVER);
    properties.put("user", HSQLUtil.DEFAULT_USER);
    properties.put("password", HSQLUtil.DEFAULT_PASSWORD);
    return new SystemRef(environment, "testdb", "db", properties);
  }

}
