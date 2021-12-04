/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests anonymization features.<br/><br/>
 * Created: 12.10.2021 15:31:50
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class AnonymizationIntegrationTest extends AbstractBeneratorIntegrationTest {

  @Before
  public void setUp() {
    DataModel model = new DataModel();
    DefaultDescriptorProvider provider = new DefaultDescriptorProvider("default", model);
    Entity alice = new Entity("person", provider);
    alice.setComponent("name", "Alice");
    MemStore mem = new MemStore("mem", model);
    mem.store(alice);
    context.set("mem", mem);
  }

  @Test
  public void testCopy() {
    String xml =
        "<setup>" +
            "  <import platforms='memstore'/>\n" +
            "  <memstore id='out'/>" +
            "  <iterate source='mem' type='person' consumer='out'/>" +
            "</setup>";
    BeneratorContext context = parseAndExecute(xml);
    MemStore out = (MemStore) context.get("out");
    for (Entity person : out.getEntities("person")) {
      assertEquals("Alice", person.get("name"));
    }
  }

  @Test
  public void testMask() {
    String xml =
        "<setup>" +
            "  <import platforms='memstore'/>\n" +
            "  <memstore id='out'/>" +
            "  <iterate source='mem' type='person' consumer='out'>" +
            "    <attribute name='name' converter='new MiddleMask(0,2)'/>" +
            "  </iterate>" +
            "</setup>";
    BeneratorContext context = parseAndExecute(xml);
    MemStore out = (MemStore) context.get("out");
    for (Entity person : out.getEntities("person")) {
      assertEquals("***ce", person.get("name"));
    }
  }

  @Test
  public void testConstant() {
    String xml =
        "<setup>" +
            "  <import platforms='memstore'/>\n" +
            "  <memstore id='out'/>" +
            "  <iterate source='mem' type='person' consumer='out'>" +
            "    <attribute name='name' constant='xxx'/>" +
            "  </iterate>" +
            "</setup>";
    BeneratorContext context = parseAndExecute(xml);
    MemStore out = (MemStore) context.get("out");
    for (Entity person : out.getEntities("person")) {
      assertEquals("xxx", person.get("name"));
    }
  }

  // TODO test anonymization of list- or array-type attributes

}
