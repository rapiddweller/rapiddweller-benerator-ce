/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.integration;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.ui.ConsolePrinter;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.util.Collection;

/**
 * Tests different &lt;part&gt; configuration options.<br/><br/>
 * Created: 21.09.2021 21:44:46
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class PartIntegrationTest extends AbstractBeneratorIntegrationTest {

  /** Tests an issue that occurred in XML generation */
  @Test
  public void testPartCount_3_to_5() {
    // GIVEN a <part> with minCount='3' and  maxCount='5'
    String xml =
              "<setup>\n"
            + "    <memstore id='mem'/>\n"
            + "    <generate type='person' count='5000' consumer='mem'>\n"
            + "        <part name='children' minCount='3' maxCount='5'>\n"
            + "        </part>\n"
            + "    </generate>\n"
            + "</setup>\n";
    // WHEN generating data
    BeneratorContext context = parseAndExecuteXmlString(xml);
    // THEN all counts (3, 4, 5) must have roughly the same frequency
    MemStore mem = (MemStore) context.get("mem");
    Collection<Entity> persons = mem.getEntities("person");
    assertEqualCardinalityDistribution(persons, "children", 0.1, 3, 4, 5);
  }

  /** Tests part count generation for cases in which only maxCount in specified */
  @Test
  public void testPartCount_max_2() {
    // GIVEN a <part> with only maxCount='2' specified
    String xml =
              "<setup>\n"
            + "    <memstore id='mem'/>\n"
            + "    <generate type='person' count='5000' consumer='mem'>\n"
            + "        <part name='children' maxCount='2'>\n"
            + "        </part>\n"
            + "    </generate>\n"
            + "</setup>\n";
    // WHEN generating data
    BeneratorContext context = parseAndExecuteXmlString(xml);
    // THEN all counts (0, 1, 2) must have roughly the same frequency
    MemStore mem = (MemStore) context.get("mem");
    mem.printContent(new ConsolePrinter());
    Collection<Entity> persons = mem.getEntities("person");
    assertEqualCardinalityDistribution(persons, "children", 0.1, 0, 1, 2);
  }

}
