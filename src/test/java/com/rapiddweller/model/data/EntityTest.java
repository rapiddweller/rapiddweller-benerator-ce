/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.model.data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link Entity} class.<br/><br/>
 * Created: 28.09.2021 13:17:20
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class EntityTest {

  private static final String ALICE = "Alice";
  private static final Integer AGE = 23;
  private static final Integer ARRAY_ELEMENT_ID = 12;
  private static final String SUB_ID_VALUE = "sub1";
  private static final String PERSON_TYPE = "person";
  private static final String NAME_ATTRIB = "name";

  private DescriptorProvider dp;

  @Before
  public void setUp() {
    DataModel dataModel = new DataModel();
    this.dp = new DefaultDescriptorProvider("test", dataModel);
  }

  @Test
  public void testAccessors() {
    DataModel dataModel = new DataModel();
    DescriptorProvider dp = new DefaultDescriptorProvider("test", dataModel);
    Entity src = new Entity(PERSON_TYPE, dp);
    assertEquals(PERSON_TYPE, src.type());
    src.set(NAME_ATTRIB, ALICE);
    assertTrue(src.componentIsSet(NAME_ATTRIB));
    src.remove(NAME_ATTRIB);
    assertFalse(src.componentIsSet(NAME_ATTRIB));
  }

  @Test
  public void testEquals_self() {
    Entity alice = createAlice();
    assertEquals(alice, alice);
  }

  @Test
  public void testEquals_copy() {
    Entity alice = createAlice();
    Entity otherAlice = new Entity(alice);
    assertEquals(alice, otherAlice);
  }

  @Test
  public void testEquals_false() {
    Entity alice = createAlice();
    assertNotEquals(alice, null);
    assertNotEquals(alice, "Bob");
    assertNotEquals(alice, createBob());
    assertNotEquals(alice, new Entity("person", dp));
    Entity otherAlice = new Entity(alice);
    otherAlice.set("age", 56);
    assertNotEquals(alice, otherAlice);
  }

  @Test
  public void testEqualsIgnoringDescriptor_self() {
    Entity alice = createAlice();
    assertTrue(alice.equalsIgnoringDescriptor(alice));
  }

  @Test
  public void testEqualsIgnoringDescriptor_copy() {
    Entity alice = createAlice();
    Entity otherAlice = new Entity(alice);
    assertTrue(alice.equalsIgnoringDescriptor(otherAlice));
  }

  @Test
  public void testEqualsIgnoringDescriptor_false() {
    Entity alice = createAlice();
    assertFalse(alice.equalsIgnoringDescriptor(null));
    assertFalse(alice.equalsIgnoringDescriptor(new Entity("person", dp)));
    assertFalse(alice.equalsIgnoringDescriptor(createBob()));
    Entity otherAlice = new Entity(alice);
    otherAlice.set("age", 56);
    assertFalse(alice.equalsIgnoringDescriptor(otherAlice));
  }

  @Test
  public void testCopyConstructor() {
    // GIVEN an Entity with all types of data
    Entity src = createAlice();

    // WHEN creating a copy
    Entity dst = new Entity(src);

    // THEN the copied structure shall be the same, with mutable components copied and immutable components reused
    // immutable referenced
    assertReused(src.get(NAME_ATTRIB), dst.get(NAME_ATTRIB));
    assertReused(src.get("age"), dst.get("age"));
    // sub array copied
    Entity[] srcArray = (Entity[]) src.get("array");
    Entity[] dstArray = (Entity[]) dst.get("array");
    assertArrayEquals(srcArray, dstArray);
    assertEquals(1, dstArray.length);
    // array element copied
    assertCopied(srcArray[0], dstArray[0]);
    assertReused(srcArray[0].get("aeId"), dstArray[0].get("aeId"));
    // empty list copied
    assertCopied(src.get("list"), dst.get("list"));
    // sub entity copied
    assertCopied(src.get("sub"), dst.get("sub"));
    assertReused(((Entity) src.get("sub")).get("subId"), ((Entity) dst.get("sub")).get("subId"));
    // check Entity.equals()
  }


  // private helpers -------------------------------------------------------------------------------------------------

  private Entity createAlice() {
    Entity result = new Entity(PERSON_TYPE, dp);
    result.set(NAME_ATTRIB, ALICE);
    result.set("age", AGE);
    Entity srcArrayElem = new Entity("array", dp);
    srcArrayElem.set("aeId", ARRAY_ELEMENT_ID);
    Entity[] srcArray = { srcArrayElem };
    result.set("array", srcArray);
    List<Integer> srcList = new ArrayList<>();
    srcList.add(123);
    result.set("list", srcList);
    Entity subEntity = new Entity("other", dp);
    subEntity.set("subId", SUB_ID_VALUE);
    result.set("sub", subEntity);
    return result;
  }

  private Entity createBob() {
    Entity result = new Entity(PERSON_TYPE, dp);
    result.set(NAME_ATTRIB, "Bob");
    result.set("age", 34);
    return result;
  }

  private void assertCopied(Object expected, Object actual) {
    assertEquals(expected, actual);
    assertNotSame(expected, actual);
  }

  private void assertReused(Object expected, Object actual) {
    assertEquals(expected, actual);
    assertSame(expected, actual);
  }

}
