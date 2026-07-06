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
    return migrate(benProps, report, path, java.util.Collections.emptyMap());
  }

  /**
   * As {@link #migrate(Map, MigrationReport, String)}, additionally informed by the descriptors that
   * reference this environment: {@code systems} maps each system prefix to {@code "mongo"} or {@code "db"}.
   * Mongo systems get their key segment rewritten ({@code mongodb.db.host} -&gt; {@code mongodb.mongo.host},
   * DATAMIMIC resolves {@code <system>.<systemType>.*}); Benerator's OLD flat format ({@code db_url=...}
   * without a system prefix) is prefixed with the single db system bound to this environment.
   */
  public static Map<String, String> migrate(Map<String, String> benProps, MigrationReport report, String path,
      Map<String, String> systems) {
    benProps = normalizeFlatKeys(benProps, report, path, systems);
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
      } else if (key.endsWith(".db.catalog") || key.endsWith(".db.readOnly") || key.endsWith(".db.batch")
          || key.endsWith(".db.quoteTableNames")) {
        // dropped: Benerator-only connection knobs; DATAMIMIC would pass unknown keys to the engine and fail
        report.info(path, "database", "env '" + key + "' dropped (Benerator-only connection option)");
      } else {
        out.put(key, val); // .db.user/.password/.schema and non-db keys pass through
      }
    }
    // SQLite has no schemas: an h2/hsqldb-mem env migrated to sqlite must not keep schema=PUBLIC
    // (DATAMIMIC would qualify every table as PUBLIC.<t> and fail on PUBLIC.sqlite_master).
    java.util.List<String> sqliteSchemaKeys = new java.util.ArrayList<>();
    for (Map.Entry<String, String> e : out.entrySet()) {
      if (e.getKey().endsWith(".db.dbms") && e.getValue().equals("sqlite")) {
        String id = e.getKey().substring(0, e.getKey().length() - ".db.dbms".length());
        sqliteSchemaKeys.add(id + ".db.schema");
      }
    }
    for (String schemaKey : sqliteSchemaKeys) {
      out.remove(schemaKey);
    }
    // DATAMIMIC resolves <system>.<systemType>.* - a mongo system's keys use segment 'mongo', not 'db'
    // (Benerator wrote mongodb.db.host; DATAMIMIC's <mongodb system="mongodb"> reads mongodb.mongo.host).
    Map<String, String> renamed = new LinkedHashMap<>();
    for (Map.Entry<String, String> e : out.entrySet()) {
      String key = e.getKey();
      int dbSeg = key.indexOf(".db.");
      if (dbSeg > 0 && "mongo".equals(systems.get(key.substring(0, dbSeg)))) {
        key = key.substring(0, dbSeg) + ".mongo." + key.substring(dbSeg + 4);
      }
      renamed.put(key, e.getValue());
    }
    return renamed;
  }

  /**
   * Benerator's OLD env format has no system prefix ({@code db_url=jdbc:...}). DATAMIMIC always resolves
   * {@code <system>.db.*} (system defaults to the database element's id), so flat keys are prefixed with
   * the single db system the descriptors bind to this environment. Ambiguous/unbound stays flat + flagged.
   */
  private static Map<String, String> normalizeFlatKeys(Map<String, String> benProps, MigrationReport report,
      String path, Map<String, String> systems) {
    boolean hasFlat = false;
    for (String key : benProps.keySet()) {
      if (key.startsWith("db_")) {
        hasFlat = true;
        break;
      }
    }
    if (!hasFlat) {
      return benProps;
    }
    java.util.List<String> dbSystems = new java.util.ArrayList<>();
    for (Map.Entry<String, String> s : systems.entrySet()) {
      if ("db".equals(s.getValue())) {
        dbSystems.add(s.getKey());
      }
    }
    if (dbSystems.size() != 1) {
      report.add(path, "database", "old flat env format (db_url=...) but " + dbSystems.size()
          + " db systems bound to this environment - prefix the keys with '<system>.db.' manually");
      return benProps;
    }
    String system = dbSystems.get(0);
    Map<String, String> out = new LinkedHashMap<>();
    for (Map.Entry<String, String> e : benProps.entrySet()) {
      String key = e.getKey();
      out.put(key.startsWith("db_") ? system + ".db." + key.substring(3) : key, e.getValue());
    }
    report.info(path, "database", "old flat env format normalized to system '" + system + "'");
    return out;
  }
}
