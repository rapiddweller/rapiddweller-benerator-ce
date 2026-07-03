/* (c) Copyright 2025 by rapiddweller GmbH. All rights reserved. */

package com.rapiddweller.benerator.main.datamimic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/** Splits the REAL Benerator shop dbunit dataset - the fixture is production data, not a toy. */
public class DbunitSplitterTest {

  private static final File SHOP = new File("src/demo/resources/demo/shop/shop.dbunit.xml");

  @Test
  public void splitsEachElementIntoItsTableInFirstAppearanceOrder() throws Exception {
    Map<String, List<Map<String, String>>> tables = DbunitSplitter.split(SHOP);

    // element name -> table; row counts from the real dataset
    assertEquals(28, tables.get("db_category").size());
    assertEquals(4, tables.get("db_user").size());
    assertEquals(3, tables.get("db_role").size());
    assertEquals(3, tables.get("db_product").size());
    assertEquals(1, tables.get("db_order").size());

    // FK/dependency order preserved: categories first (referenced), orders later (referencing)
    assertEquals("db_category", tables.keySet().iterator().next());
    // the <dataset> wrapper is not a table
    assertFalse(tables.containsKey("dataset"));
  }

  @Test
  public void rowAttributesBecomeColumnsAndRaggedRowsKeepTheirOwnKeys() throws Exception {
    Map<String, List<Map<String, String>>> tables = DbunitSplitter.split(SHOP);
    List<Map<String, String>> cats = tables.get("db_category");

    // first row: <db_category id="FOOD" name="Food"/> - no parent_id
    assertEquals("FOOD", cats.get(0).get("id"));
    assertEquals("Food", cats.get(0).get("name"));
    assertFalse("top-level category has no parent_id", cats.get(0).containsKey("parent_id"));

    // second row: <db_category id="FOOD/MEAT" name="Meat" parent_id="FOOD"/> - ragged extra column
    assertEquals("FOOD", cats.get(1).get("parent_id"));
  }

  @Test
  public void writesTableRowsAsAJsonArrayDatamimicCanRead() throws Exception {
    Map<String, List<Map<String, String>>> tables = DbunitSplitter.split(SHOP);
    File json = File.createTempFile("db_category", ".json");
    json.deleteOnExit();
    DbunitSplitter.writeTableJson(tables.get("db_category"), json);

    String written = new String(java.nio.file.Files.readAllBytes(json.toPath()), java.nio.charset.StandardCharsets.UTF_8);
    assertTrue("is a JSON array", written.trim().startsWith("[") && written.trim().endsWith("]"));
    assertTrue("keeps real values", written.contains("\"id\": \"FOOD\"") && written.contains("\"name\": \"Food\""));
    // one "id" column per row -> 28 rows
    assertEquals(28, written.split("\"id\":").length - 1);
    // ragged rows are unified: the first category (no parent_id) still emits parent_id as null, so an
    // RDBMS batch insert sees a uniform column set.
    assertTrue("top-level row pads the missing column with null",
        written.contains("{\"id\": \"FOOD\", \"name\": \"Food\", \"parent_id\": null}"));
  }
}
