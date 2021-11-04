/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.environment;

import com.rapiddweller.benerator.util.DeprecationLogger;
import com.rapiddweller.common.ArrayBuilder;
import com.rapiddweller.common.ConfigUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.ConnectFailedException;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.version.VersionNumber;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.jdbacl.DatabaseDialect;
import com.rapiddweller.jdbacl.DatabaseDialectManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides utilities related to database environment definition files.<br/><br/>
 * Created: 20.10.2021 07:50:59
 * @author Volker Bergmann
 * @since 1.1.12
 */
public class EnvironmentUtil {

  public static final String ENV_FILE_SUFFIX = ".env.properties";

  private EnvironmentUtil() {
    // private constructor to prevent instantiation of this utility class
  }

  public static Map<String, Environment> findEnvironments() {
    return findEnvironments(".");
  }

  public static Map<String, Environment> findEnvironments(String projectFolder) {
    Map<String, Environment> result = new HashMap<>();
    for (String location : ConfigUtil.defaultConfigLocations(projectFolder)) {
      File folder = new File(location);
      if (folder.exists()) {
        File[] envFiles = folder.listFiles((dir, name) -> name.endsWith(ENV_FILE_SUFFIX));
        if (envFiles != null) {
          for (File envFile : envFiles) {
            String envFileName = envFile.getName();
            String envName = envFileName.substring(0, envFileName.indexOf(ENV_FILE_SUFFIX));
            result.computeIfAbsent(envName, k -> parseFile(envName, envFile.getAbsolutePath()));
          }
        }
      }
    }
    return result;
  }

  public static SystemRef[] findSystems(String type) {
    ArrayBuilder<SystemRef> result = new ArrayBuilder<>(SystemRef.class);
    Collection<Environment> environments = EnvironmentUtil.findEnvironments().values();
    for (Environment environment : environments) {
      for (SystemRef system : environment.getSystems()) {
        if (type.equals(system.getType())) {
          result.add(system);
        }
      }
    }
    return result.toArray();
  }

  public static String getDbProductDescription(SystemRef system) {
    try (Connection connection = connectDb(system)) {
      DatabaseMetaData metaData = connection.getMetaData();
      return metaData.getDatabaseProductName() + " "
          + VersionNumber.valueOf(metaData.getDatabaseProductVersion());
    } catch (ConnectFailedException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static DatabaseDialect getDbDialect(SystemRef system) {
    try (Connection connection = connectDb(system)) {
      DatabaseMetaData metaData = connection.getMetaData();
      String databaseProductName = metaData.getDatabaseProductName();
      VersionNumber databaseProductVersion = VersionNumber.valueOf(metaData.getDatabaseProductVersion());
      return DatabaseDialectManager.getDialectForProduct(databaseProductName, databaseProductVersion);
    } catch (ConnectFailedException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static Connection connectDb(SystemRef system) throws ConnectFailedException {
    if (!system.isDb()) {
      throw new ConfigurationError("Not a database: " + system.getName() +
          " in environment " + system.getEnvironment().getName());
    }
    Map<String, String> pp = system.getProperties();
    String readOnlySpec = pp.get("readOnly");
    boolean readOnly = (!StringUtil.isEmpty(readOnlySpec) && Boolean.parseBoolean(readOnlySpec));
    return DBUtil.connect(pp.get("url"), pp.get("driver"), pp.get("user"), pp.get("password"), readOnly);
  }

  public static Environment parse(String envName, String projectFolder) {
    String filename = fileName(envName);
    try {
      String filePath = ConfigUtil.configFilePathDefaultLocations(filename, projectFolder);
      return parseFile(envName, filePath);
    } catch (IOException e) {
      throw new ConfigurationError("Error parsing environment file " + filename, e);
    }
  }

  public static Environment parseFile(String envName, String filePath) {
    try {
      Map<String, String> properties = IOUtil.readProperties(filePath);
      if (properties.containsKey("db_url") && properties.containsKey("db_driver")) {
        // old style db environment definition
        return parseOldStyleEnvironment(filePath, envName, properties);
      } else {
        // new style general environment definition
        return Environment.parse(envName, properties);
      }
    } catch (IOException e) {
      throw new ConfigurationError("Error parsing environment file " + filePath, e);
    }
  }

  private static Environment parseOldStyleEnvironment(String filePath, String envName, Map<String, String> srcProps) {
    DeprecationLogger.warn("Environment file " + filePath + " is using a deprecated environment file format. " +
        "Please update this to the new environment definition file format introduced in Benerator 2.1.0. " +
        "The old format is supported for backwards compatibility but will dropped in a future release");
    Map<String, String> resultProps = new HashMap<>();
    for (Map.Entry<String, String> entry : srcProps.entrySet()) {
      String name = entry.getKey();
      if (name.startsWith("db_")) {
        resultProps.put(name.substring(3), entry.getValue());
      }
    }
    Environment result = new Environment(envName);
    result.addSystem("db", "db", resultProps);
    return result;
  }

  public static String fileName(String envName) {
    return envName + ENV_FILE_SUFFIX;
  }

}
