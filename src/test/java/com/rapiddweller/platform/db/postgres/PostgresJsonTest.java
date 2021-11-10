/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db.postgres;

import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Testing the JSON column type support of Postgres.<br/><br/>
 * Created: 09.11.2021 15:08:23
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PostgresJsonTest extends AbstractProstgresIntegrationTest {

  public static final String COLUMN_NAME = "c_json";

  public static final String PRE_0 = null;
  public static final String PRE_1 = "{\"foo\": {\"bar\": \"baz\"}}";
  public static final String PRE_2 = "{\"sam\": \"ple\"}";
  public static final String PRE_3 = "[1, 2, 3]";

  // Tests -----------------------------------------------------------------------------------------------------------

  @Test //@Ignore("So far there is no agreed way to set this up uniformly on CI and a local system")
  public void testJson() {
    parseAndExecuteFile(folder + "/postgres-json.ben.xml");
    checkPredefinedEntities("mem_predef");
    checkPredefinedEntities("mem_written");
    checkDynamicGeneration("mem_const");
    checkDynamicGeneration("mem_script");
    checkDynamicGeneration("mem_gen");
    checkPredefinedEntities("mem_csv_old"); // TODO make #{}# unnecessary
    checkPredefinedEntities("mem_csv_new"); // TODO make #{}# unnecessary
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void checkPredefinedEntities(String memstoreId) {
    MemStore mem = (MemStore) context.get(memstoreId);
    int expectedCount = (isOldCsv(memstoreId) ? 3 : 4);
    assertEquals(expectedCount, mem.totalEntityCount());
    List<Entity> entities = mem.getEntities("json_table");
    assertEquals(PRE_0, entities.get(0).getComponent(COLUMN_NAME));
    assertEquals(PRE_1, entities.get(1).getComponent(COLUMN_NAME));
    assertEquals(PRE_2, entities.get(2).getComponent(COLUMN_NAME));
    if (expectedCount == 4) {
      assertEquals(PRE_3, entities.get(3).getComponent(COLUMN_NAME));
    }
  }

  private boolean isOldCsv(String memstoreId) {
    return "mem_csv_old".equals(memstoreId);
  }

  private void checkDynamicGeneration(String memstoreId) {
    MemStore mem = (MemStore) context.get(memstoreId);
    assertEquals(10, mem.totalEntityCount());
    List<Entity> entities = mem.getEntities("json_table");
    for (Entity entity : entities) {
      assertEquals(JSONGenerator.SAMPLE, entity.getComponent(COLUMN_NAME));
    }
  }

}
