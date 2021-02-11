package com.rapiddweller.platform.db;

import com.rapiddweller.jdbacl.model.DBColumn;
import com.rapiddweller.jdbacl.model.DBDataType;
import com.rapiddweller.jdbacl.model.DBForeignKeyConstraint;
import com.rapiddweller.jdbacl.model.DBTable;
import com.rapiddweller.model.data.DataModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The type Lazy table complex type descriptor test.
 */
public class LazyTableComplexTypeDescriptorTest {
  /**
   * Test get parts.
   */
  @Test
  public void testGetParts() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", "", new DataModel()));
    assertTrue(lazyTableComplexTypeDescriptor.getParts().isEmpty());
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getPKColumnNames().length);
    assertEquals(0, dbTable.getColumnNames().length);
  }

  /**
   * Test get parts 2.
   */
  @Test
  public void testGetParts2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", "", new DataModel()));
    lazyTableComplexTypeDescriptor.getParts();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  /**
   * Test get component.
   */
  @Test
  public void testGetComponent() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", "", new DataModel()));
    assertNull(lazyTableComplexTypeDescriptor.getComponent("Name"));
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getPKColumnNames().length);
    assertEquals(0, dbTable.getColumnNames().length);
  }

  /**
   * Test get component 2.
   */
  @Test
  public void testGetComponent2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", "", new DataModel()));
    lazyTableComplexTypeDescriptor.getComponent("TestTable");
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  /**
   * Test get component 3.
   */
  @Test
  public void testGetComponent3() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", "", new DataModel()));
    lazyTableComplexTypeDescriptor.setParentName("Parent Name");
    assertNull(lazyTableComplexTypeDescriptor.getComponent("Name"));
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    String[] pKColumnNames = dbTable.getPKColumnNames();
    assertNull(lazyTableComplexTypeDescriptor.getParent());
    assertEquals(0, pKColumnNames.length);
    assertEquals(0, dbTable.getColumnNames().length);
  }

  /**
   * Test get components.
   */
  @Test
  public void testGetComponents() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", "", new DataModel()));
    assertTrue(lazyTableComplexTypeDescriptor.getComponents().isEmpty());
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getColumnNames().length);
    assertEquals(0, dbTable.getPKColumnNames().length);
  }

  /**
   * Test get components 2.
   */
  @Test
  public void testGetComponents2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", "", new DataModel()));
    lazyTableComplexTypeDescriptor.getComponents();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  /**
   * Test get declared parts.
   */
  @Test
  public void testGetDeclaredParts() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", "", new DataModel()));
    assertTrue(lazyTableComplexTypeDescriptor.getDeclaredParts().isEmpty());
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getColumnNames().length);
    assertEquals(0, dbTable.getPKColumnNames().length);
  }

  /**
   * Test get declared parts 2.
   */
  @Test
  public void testGetDeclaredParts2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", "", new DataModel()));
    lazyTableComplexTypeDescriptor.getDeclaredParts();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  /**
   * Test is declared component.
   */
  @Test
  public void testIsDeclaredComponent() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", "", new DataModel()));
    assertFalse(lazyTableComplexTypeDescriptor.isDeclaredComponent("Component Name"));
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getPKColumnNames().length);
    assertEquals(0, dbTable.getColumnNames().length);
  }

  /**
   * Test is declared component 2.
   */
  @Test
  public void testIsDeclaredComponent2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", "", new DataModel()));
    lazyTableComplexTypeDescriptor.isDeclaredComponent("Component Name");
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  /**
   * Test get id component names.
   */
  @Test
  public void testGetIdComponentNames() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", "", new DataModel()));
    assertEquals(0, lazyTableComplexTypeDescriptor.getIdComponentNames().length);
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getColumnNames().length);
    assertTrue(dbTable.isPKImported());
  }

  /**
   * Test get id component names 2.
   */
  @Test
  public void testGetIdComponentNames2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", "", new DataModel()));
    lazyTableComplexTypeDescriptor.getIdComponentNames();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }

  /**
   * Test get reference components.
   */
  @Test
  public void testGetReferenceComponents() {
    DBTable table = new DBTable("Name");
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(table,
        new DefaultDBSystem("42", "", new DataModel()));
    assertTrue(lazyTableComplexTypeDescriptor.getReferenceComponents().isEmpty());
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
    DBTable dbTable = lazyTableComplexTypeDescriptor.table;
    assertEquals(0, dbTable.getColumnNames().length);
    assertEquals(0, dbTable.getPKColumnNames().length);
  }

  /**
   * Test get reference components 2.
   */
  @Test
  public void testGetReferenceComponents2() {
    DBTable dbTable = new DBTable("TestTable");
    DBTable owner = new DBTable("TestOwner");
    DBColumn column = new DBColumn("TestColumn", dbTable, DBDataType.getInstance("int"));
    dbTable.addColumn(column);
    dbTable.addForeignKey(
        new DBForeignKeyConstraint("TestFK", true, owner, column.getName(), new DBTable("Name"), "Referee Column Name"));
    LazyTableComplexTypeDescriptor lazyTableComplexTypeDescriptor = new LazyTableComplexTypeDescriptor(dbTable,
        new DefaultDBSystem("42", "", new DataModel()));
    lazyTableComplexTypeDescriptor.getReferenceComponents();
    assertTrue(lazyTableComplexTypeDescriptor.loaded);
  }
}

