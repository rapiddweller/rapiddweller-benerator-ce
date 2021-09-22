/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.integration;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.util.Collection;

/**
 * Tests &lt;part&gt; setup.<br/><br/>
 * Created: 21.09.2021 21:44:46
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class PartIntegrationTest extends AbstractBeneratorIntegrationTest {

  @Test
  public void testPartCount_3_to_5() {
    String xml =
              "<setup>\n"
            + "    <memstore id='mem'/>\n"
            + "    <generate type='person' count='1000' consumer='mem'>\n"
            + "        <part name='children' minCount='3' maxCount='5'>\n"
            + "        </part>\n"
            + "    </generate>\n"
            + "</setup>\n";
    BeneratorContext context = parseAndExecute(xml);
    MemStore mem = (MemStore) context.get("mem");
    Collection<Entity> persons = mem.getEntities("person");
    assertEqualCardinalityDistribution(persons, "children", 0.1, 3, 4, 5);
  }

  @Test
  public void testPartCount_max_2() {
    String xml =
              "<setup>\n"
            + "    <memstore id='mem'/>\n"
            + "    <generate type='person' count='1000' consumer='mem'>\n"
            + "        <part name='children' maxCount='2'>\n"
            + "        </part>\n"
            + "    </generate>\n"
            + "</setup>\n";
    BeneratorContext context = parseAndExecute(xml);
    MemStore mem = (MemStore) context.get("mem");
    mem.printContent();
    Collection<Entity> persons = mem.getEntities("person");
    assertEqualCardinalityDistribution(persons, "children", 0.1, 0, 1, 2);
  }

}
