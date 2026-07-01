/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Migrates a Benerator environment properties file to DATAMIMIC's {@code conf/environment.env.properties}
 * format. Benerator stores a JDBC URL ({@code <id>.db.url=jdbc:postgresql://host:port/db}); DATAMIMIC needs
 * the parts split out ({@code <id>.db.host / .port / .database / .dbms} plus {@code user / password / schema}).
 * This is where a DB-backed migration actually connects — without it the converted descriptor has no
 * coordinates for its {@code <database>}.
 */
public final class EnvironmentMigrator {

  private EnvironmentMigrator() {
  }

  /** Parsed coordinates of a JDBC URL; a field is null when the URL does not carry it. */
  public static final class Coordinates {
    public final String dbms;
    public final String host;
    public final String port;
    public final String database;

    Coordinates(String dbms, String host, String port, String database) {
      this.dbms = dbms;
      this.host = host;
      this.port = port;
      this.database = database;
    }
  }

  // jdbc:<vendor>://host[:port]/database   (postgresql, mysql, mariadb)
  private static final Pattern HOST_URL =
      Pattern.compile("jdbc:(\\w+)://([^:/?;]+)(?::(\\d+))?/([^?;]+).*");
  // jdbc:sqlserver://host[:port];databaseName=db
  private static final Pattern SQLSERVER =
      Pattern.compile("jdbc:sqlserver://([^:;]+)(?::(\\d+))?;.*databaseName=([^;]+).*", Pattern.CASE_INSENSITIVE);
  // jdbc:oracle:thin:@host:port:sid  or  jdbc:oracle:thin:@//host:port/service
  private static final Pattern ORACLE =
      Pattern.compile("jdbc:oracle:\\w+:@(?://)?([^:/]+):(\\d+)[:/](.+)");
  // jdbc:sqlite:/path/to.db  or  jdbc:sqlite:file.db
  private static final Pattern SQLITE = Pattern.compile("jdbc:sqlite:(.+)");
  // jdbc:h2:mem:name / jdbc:hsqldb:mem:name / jdbc:derby:memory:name  (in-process Java DB) -> migrate to
  // SQLite: DATAMIMIC has no Java-embedded-DB driver, but SQLite is an equivalent file/in-process store.
  private static final Pattern JAVA_MEM =
      Pattern.compile("jdbc:(?:h2|hsqldb|derby):(?:mem|memory):([^;,?]+).*", Pattern.CASE_INSENSITIVE);

  private static final Map<String, String> DEFAULT_PORT = Map.of(
      "postgresql", "5432", "mysql", "3306", "mariadb", "3306", "oracle", "1521", "mssql", "1433");

  /** Parse a JDBC URL into DATAMIMIC coordinates, or return {@code null} if it is not translatable. */
  public static Coordinates parseJdbcUrl(String url) {
    if (url == null) {
      return null;
    }
    Matcher m;
    if ((m = JAVA_MEM.matcher(url)).matches()) {
      return new Coordinates("sqlite", null, null, m.group(1)); // h2/hsqldb/derby mem -> SQLite store
    }
    if ((m = SQLITE.matcher(url)).matches()) {
      return new Coordinates("sqlite", null, null, m.group(1));
    }
    if ((m = SQLSERVER.matcher(url)).matches()) {
      return new Coordinates("mssql", m.group(1), portOr(m.group(2), "mssql"), m.group(3));
    }
    if ((m = ORACLE.matcher(url)).matches()) {
      return new Coordinates("oracle", m.group(1), m.group(2), m.group(3));
    }
    if ((m = HOST_URL.matcher(url)).matches()) {
      String dbms = VocabularyMap.DBMS.getOrDefault(m.group(1), m.group(1));
      return new Coordinates(dbms, m.group(2), portOr(m.group(3), dbms), m.group(4));
    }
    return null;
  }

  private static String portOr(String port, String dbms) {
    return port != null ? port : DEFAULT_PORT.getOrDefault(dbms, null);
  }

  /**
   * Migrate Benerator env properties to DATAMIMIC form. For every {@code <id>.db.url} the URL is split into
   * {@code host/port/database/dbms}; {@code .user/.password/.schema} pass through; {@code .driver} is dropped
   * (DATAMIMIC derives everything from dbms). Non-{@code .db.} keys are kept verbatim. Returns the migrated
   * properties (insertion-ordered); untranslatable URLs (h2/hsqldb) are recorded in {@code report}.
   */
  public static Map<String, String> migrate(Map<String, String> benProps, MigrationReport report, String path) {
    Map<String, String> out = new LinkedHashMap<>();
    for (Map.Entry<String, String> e : benProps.entrySet()) {
      String key = e.getKey();
      String val = e.getValue();
      if (key.endsWith(".db.url")) {
        String id = key.substring(0, key.length() - ".db.url".length());
        Coordinates c = parseJdbcUrl(val);
        if (c == null) {
          out.put(key, val); // keep so nothing is lost; flag for manual attention
          report.add(path, "database", "env '" + key + "=" + val + "' -> no DATAMIMIC equivalent (Java-embedded DB?)");
          continue;
        }
        if (c.host != null) {
          out.put(id + ".db.host", c.host);
        }
        if (c.port != null) {
          out.put(id + ".db.port", c.port);
        }
        if (c.database != null) {
          out.put(id + ".db.database", c.database);
        }
        out.put(id + ".db.dbms", c.dbms);
      } else if (key.endsWith(".db.driver")) {
        // dropped: DATAMIMIC identifies the DB by dbms, not a JDBC driver class
      } else {
        out.put(key, val); // .db.user/.password/.schema and non-db keys pass through
      }
    }
    return out;
  }
}
