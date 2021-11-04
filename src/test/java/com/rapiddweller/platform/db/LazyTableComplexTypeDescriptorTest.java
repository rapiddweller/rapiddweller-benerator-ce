package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.jdbacl.model.DBColumn;
import com.rapiddweller.jdbacl.model.DBDataType;
import com.rapiddweller.jdbacl.model.DBForeignKeyConstraint;
import com.rapiddweller.jdbacl.model.DBTable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Tests the {@link LazyTableComplexTypeDescriptor}. */
public class LazyTableComplexTypeDescriptorTest {

  @Test
  public void testGetParts() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    assertTrue(lazyTableComplexTypeDescriptor.getParts().isEmpty());
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getPKColumnNames().length);
    assertEquals(0, dbTable.getColumnNames().length);
  }

  @Test
  public void testGetParts2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    lazyTableComplexTypeDescriptor.getParts();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  @Test
  public void testGetComponent() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    assertNull(lazyTableComplexTypeDescriptor.getComponent("Name"));
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getPKColumnNames().length);
    assertEquals(0, dbTable.getColumnNames().length);
  }

  @Test
  public void testGetComponent2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    lazyTableComplexTypeDescriptor.getComponent("TestTable");
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  @Test
  public void testGetComponent3() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    lazyTableComplexTypeDescriptor.setParentName("Parent Name");
    assertNull(lazyTableComplexTypeDescriptor.getComponent("Name"));
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    String[] pKColumnNames = dbTable.getPKColumnNames();
    assertNull(lazyTableComplexTypeDescriptor.getParent());
    assertEquals(0, pKColumnNames.length);
    assertEquals(0, dbTable.getColumnNames().length);
  }

  @Test
  public void testGetComponents() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    assertTrue(lazyTableComplexTypeDescriptor.getComponents().isEmpty());
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getColumnNames().length);
    assertEquals(0, dbTable.getPKColumnNames().length);
  }

  @Test
  public void testGetComponents2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    lazyTableComplexTypeDescriptor.getComponents();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  @Test
  public void testGetDeclaredParts() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    assertTrue(lazyTableComplexTypeDescriptor.getDeclaredParts().isEmpty());
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getColumnNames().length);
    assertEquals(0, dbTable.getPKColumnNames().length);
  }

  @Test
  public void testGetDeclaredParts2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    lazyTableComplexTypeDescriptor.getDeclaredParts();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  @Test
  public void testIsDeclaredComponent() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    assertFalse(lazyTableComplexTypeDescriptor.isDeclaredComponent("Component Name"));
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getPKColumnNames().length);
    assertEquals(0, dbTable.getColumnNames().length);
  }

  @Test
  public void testIsDeclaredComponent2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    lazyTableComplexTypeDescriptor.isDeclaredComponent("Component Name");
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  @Test
  public void testGetIdComponentNames() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    assertEquals(0, lazyTableComplexTypeDescriptor.getIdComponentNames().length);
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getColumnNames().length);
    assertTrue(dbTable.isPKImported());
  }

  @Test
  public void testGetIdComponentNames2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    lazyTableComplexTypeDescriptor.getIdComponentNames();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  @Test
  public void testGetReferenceComponents() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    assertTrue(lazyTableComplexTypeDescriptor.getReferenceComponents().isEmpty());
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getColumnNames().length);
    assertEquals(0, dbTable.getPKColumnNames().length);
  }

  @Test
  public void testGetReferenceComponents2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", null, null, new DefaultBeneratorContext()));
    lazyTableComplexTypeDescriptor.getReferenceComponents();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

}

