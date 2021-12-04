/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.model.data;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.util.List;

/**
 * Tests the {@link SimpleTypeDescriptor} with XML setup files.<br/><br/>
 * Created: 30.09.2021 20:55:04
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class SimpleTypeDescriptorIntegrationTest extends AbstractBeneratorIntegrationTest {

  @Test
  public void testAllDetails() {
    // GIVEN a generation setup
    String xml = "<setup>\n" +
        "  <import platforms='memstore'/>\n" +
        "  <memstore id='mem'/>\n" +
        "  <generate type='data' count='20000' consumer='mem'>\n" +
        "    <attribute name='bool' type='boolean' trueQuota='0.7'/>\n" +
        "    <attribute name='constant' type='string' constant='C'/>\n" +
        "    <attribute name='nullable' type='string' constant='X' nullQuota='0.5'/>\n" +
        "    <attribute name='value' type='int' values='1,2,3'/>\n" +
        "    <attribute name='digit' type='int' min='0' max='9' distribution='random'/>\n" +
        "    <attribute name='word' type='string' minLength='10' maxLength='15' distribution='random'/>\n" +
        "    <attribute name='exclusive' type='int' min='20' minInclusive='false' " +
        "        max='27' maxInclusive='false' distribution='random'/>\n" +
        "  </generate>\n" +
        "</setup>";
    // WHEN executing it
    BeneratorContext context = parseAndExecute(xml);
    // THEN the generated data shall have the configured properties
    MemStore mem = (MemStore) context.get("mem");
    List<Entity> entities = mem.getEntities("data");
    GeneratorTest.assertTrueQuota(entities, "bool", 0.7, 0.1);
    GeneratorTest.assertAllMatch(entities, "constant", 0.0, "C"::equals);
    GeneratorTest.assertNullQuota(entities, "nullable", 0.5, 0.26);
    GeneratorTest.assertEqualDistribution(entities, "value", 0.1, 1, 2, 3);
    GeneratorTest.assertEqualDistribution(entities, "digit", 0.1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    GeneratorTest.assertEqualLengthDistribution(entities, "word", 0.1, 10, 11, 12, 13, 14, 15);
    GeneratorTest.assertEqualDistribution(entities, "exclusive", 0.1, 21, 22, 23, 24, 25, 26);
  }

}
