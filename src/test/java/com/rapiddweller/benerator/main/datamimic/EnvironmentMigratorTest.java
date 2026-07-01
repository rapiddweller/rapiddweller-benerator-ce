/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */
package com.rapiddweller.benerator.main.datamimic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

public class EnvironmentMigratorTest {
  @Test public void migratesPostgresEnv() {
    Map<String,String> in = new LinkedHashMap<>();
    in.put("postgres.db.url", "jdbc:postgresql://localhost:35432/benerator");
    in.put("postgres.db.driver", "org.postgresql.Driver");
    in.put("postgres.db.user", "benerator");
    in.put("postgres.db.password", "benerator");
    in.put("postgres.db.schema", "public");
    Map<String,String> out = EnvironmentMigrator.migrate(in, new MigrationReport(), "x");
    assertEquals("localhost", out.get("postgres.db.host"));
    assertEquals("35432", out.get("postgres.db.port"));
    assertEquals("benerator", out.get("postgres.db.database"));
    assertEquals("postgresql", out.get("postgres.db.dbms"));
    assertEquals("benerator", out.get("postgres.db.user"));
    assertEquals("public", out.get("postgres.db.schema"));
    assertNull("driver dropped", out.get("postgres.db.driver"));
  }
  @Test public void parsesVariousJdbcUrls() {
    assertEquals("mysql", EnvironmentMigrator.parseJdbcUrl("jdbc:mysql://db:3306/app").dbms);
    assertEquals("3306", EnvironmentMigrator.parseJdbcUrl("jdbc:mysql://db/app").port); // default port
    assertEquals("sqlite", EnvironmentMigrator.parseJdbcUrl("jdbc:sqlite:/tmp/x.db").dbms);
    assertEquals("oracle", EnvironmentMigrator.parseJdbcUrl("jdbc:oracle:thin:@ora:1521:xe").dbms);
    assertEquals("mssql", EnvironmentMigrator.parseJdbcUrl("jdbc:sqlserver://mssql:1433;databaseName=app").dbms);
    assertNull("h2 mem not translatable", EnvironmentMigrator.parseJdbcUrl("jdbc:h2:mem:testdb"));
    assertNull("hsqldb mem not translatable", EnvironmentMigrator.parseJdbcUrl("jdbc:hsqldb:mem:benerator"));
  }
}
