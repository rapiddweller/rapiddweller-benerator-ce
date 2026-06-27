/* (c) Copyright 2024 by rapiddweller GmbH & Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.test.GeneratorTest;
import org.bson.types.ObjectId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/** Tests {@link MongoDBObjectIdGenerator}. */
public class MongoDBObjectIdGeneratorTest extends GeneratorTest {

  @Test
  public void testGeneratesDistinctObjectIds() {
    MongoDBObjectIdGenerator generator = new MongoDBObjectIdGenerator();
    assertEquals(ObjectId.class, generator.getGeneratedType());
    generator.init(context);
    ObjectId first = generator.generate();
    ObjectId second = generator.generate();
    assertNotNull(first);
    assertNotNull(second);
    assertNotEquals(first, second);
  }
}
