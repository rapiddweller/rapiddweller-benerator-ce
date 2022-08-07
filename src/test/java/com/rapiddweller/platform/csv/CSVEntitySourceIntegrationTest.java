/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.csv;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Integration Test for the {@link CSVEntitySource}.<br/><br/>
 * Created: 17.12.2021 12:27:29
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CSVEntitySourceIntegrationTest extends AbstractBeneratorIntegrationTest {

  private static final String XML_DEFAULT = "<setup>" +
      "  <memstore id='mem'/>" +
      "  <iterate type='person' source='com/rapiddweller/platform/csv/scripted.csv' consumer='mem'/>" +
      "</setup>";

  private static final String XML_DEFAULT_SOURCE_SCRIPTED_TRUE = "<setup defaultSourceScripted='true'>" +
      "  <memstore id='mem'/>" +
      "  <iterate type='person' source='com/rapiddweller/platform/csv/scripted.csv' consumer='mem'/>" +
      "</setup>";

  private static final String XML_DEFAULT_SOURCE_SCRIPTED_FALSE = "<setup defaultSourceScripted='false'>" +
      "  <memstore id='mem'/>" +
      "  <iterate type='person' source='com/rapiddweller/platform/csv/scripted.csv' consumer='mem'/>" +
      "</setup>";

  private static final String XML_SOURCE_SCRIPTED_TRUE = "<setup>" +
      "  <memstore id='mem'/>" +
      "  <iterate type='person' source='com/rapiddweller/platform/csv/scripted.csv' sourceScripted='true' " +
      "     consumer='mem'/>" +
      "</setup>";

  private static final String XML_SOURCE_SCRIPTED_FALSE = "<setup defaultSourceScripted='true'>" +
      "  <memstore id='mem'/>" +
      "  <iterate type='person' source='com/rapiddweller/platform/csv/scripted.csv' sourceScripted='false' " +
      "     consumer='mem'/>" +
      "</setup>";

  private static final String XML_SOURCE_SCRIPTED_OLD = "<setup>" +
      "  <memstore id='mem'/>" +
      "  <iterate type='person' source='com/rapiddweller/platform/csv/scripted-old.csv' consumer='mem'/>" +
      "</setup>";

  @Test
  public void testDefault() {
    check(XML_DEFAULT, "{17*2}");
  }

  @Test
  public void test_defaultSourceScripted_false() {
    check(XML_DEFAULT_SOURCE_SCRIPTED_FALSE, "{17*2}");
  }

  @Test
  public void test_defaultSourceScripted_true() {
    check(XML_DEFAULT_SOURCE_SCRIPTED_TRUE, "34");
  }

  @Test
  public void test_ss_true() {
    check(XML_SOURCE_SCRIPTED_TRUE, "34");
  }

  @Test
  public void test_sourceScripted_false() {
    check(XML_SOURCE_SCRIPTED_FALSE, "{17*2}");
  }

  @Test
  public void test_sourceScripted_old() {
    check(XML_SOURCE_SCRIPTED_FALSE, "{17*2}");
  }

  private void check(String xml, String expectedAge) {
    parseAndExecuteXmlString(xml);
    MemStore mem = (MemStore) context.get("mem");
    List<Entity> persons = mem.getEntities("person");
    assertEquals(2, persons.size());
    assertEquals("Alice", persons.get(0).get("name"));
    assertEquals("23", persons.get(0).get("age"));
    assertEquals("Bob", persons.get(1).get("name"));
    assertEquals(expectedAge, persons.get(1).get("age"));
  }

}
