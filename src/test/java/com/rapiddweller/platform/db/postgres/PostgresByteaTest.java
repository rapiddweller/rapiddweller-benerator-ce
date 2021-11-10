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
 * Tests support for the BYTEA type of Postgres.<br/><br/>
 * Created: 09.11.2021 10:09:49
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class PostgresByteaTest extends AbstractProstgresIntegrationTest {

  public static final byte[] PRE_1 = {0x12, 0x34, 0x56, 0x78};
  public static final byte[] PRE_2 = "Testdata".getBytes(StandardCharsets.UTF_8);
  public static final byte[] PRE_3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefhijklmnopqrstuvwxyz0123456789"
      .getBytes(StandardCharsets.UTF_8);
  public static final byte[] PRE_4 = "ÄÖÜäöüßÀéñçž$€¥£".getBytes(StandardCharsets.UTF_8);
  public static final byte[] DYN_BYTE_ARRAY = new byte[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

  // Tests -----------------------------------------------------------------------------------------------------------

  @Test //@Ignore("So far there is no agreed way to set this up uniformly on CI and a local system")
  public void testBytea() {
    parseAndExecuteFile(folder + "/postgres-bytea.ben.xml");
    checkPredefinedEntities((MemStore) context.get("mem_predef"));
    checkPredefinedEntities((MemStore) context.get("mem_written"));
    checkByteArrayGeneration((MemStore) context.get("mem_byte_array"));
  }

  // private helpers -------------------------------------------------------------------------------------------------

  private void checkPredefinedEntities(MemStore mem) {
    assertEquals(5, mem.totalEntityCount());
    List<Entity> entities = mem.getEntities("bytea_table");
    assertNull(entities.get(0).getComponent("c_bytea"));
    assertArrayEquals(PRE_1, (byte[]) entities.get(1).getComponent("c_bytea"));
    assertArrayEquals(PRE_2, (byte[]) entities.get(2).getComponent("c_bytea"));
    assertArrayEquals(PRE_3, (byte[]) entities.get(3).getComponent("c_bytea"));
    assertArrayEquals(PRE_4, (byte[]) entities.get(4).getComponent("c_bytea"));
  }

  private void checkByteArrayGeneration(MemStore mem) {
    assertEquals(100, mem.totalEntityCount());
    List<Entity> entities = mem.getEntities("bytea_table");
    for (Entity entity : entities) {
      assertArrayEquals(DYN_BYTE_ARRAY, (byte[]) entity.getComponent("c_bytea"));
    }
  }

}
