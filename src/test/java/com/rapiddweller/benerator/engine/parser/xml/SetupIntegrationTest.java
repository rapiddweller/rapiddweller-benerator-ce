/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for the {@link SetupParser}, {@link BeneratorContext}
 * and {@link BeneratorRootStatement}.<br/><br/>
 * Created: 17.12.2021 11:12:00
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SetupIntegrationTest extends AbstractBeneratorIntegrationTest {

  @Test
  public void testGetGenerator_simple() throws Exception {
    check("com/rapiddweller/benerator/engine/statement/simple.ben.xml");
  }

  @Test
  public void testGetGenerator_include() throws Exception {
    check("com/rapiddweller/benerator/engine/statement/including.ben.xml");
  }

  @Test
  public void testDefaultImports_default() {
    // given the default settings
    String xml = "<setup/>";
    // when executing the RootStatement
    BeneratorContext context = parseAndExecuteRoot(xml);
    // then the default imports should have been applied,
    // thus com.rapiddweller.benerator.consumer.ConsoleExporter can be found
    context.forName("ConsoleExporter");
  }

  @Test
  public void testDefaultImports_true() {
    // given that defaults import is requested explicitly
    String xml = "<setup defaultImports='true'/>";
    // when executing the RootStatement
    BeneratorContext context = parseAndExecuteRoot(xml);
    // then com.rapiddweller.benerator.consumer.ConsoleExporter can be found
    context.forName("ConsoleExporter");
  }

  @Test(expected = ConfigurationError.class)
  public void testDefaultImports_false() {
    // given that defaults import is disabled
    String xml = "<setup defaultImports='false'/>";
    // when executing the RootStatement
    BeneratorContext context = parseAndExecuteRoot(xml);
    // then the default imports have not been applied,
    // and com.rapiddweller.benerator.consumer.ConsoleExporter cannot be found
    context.forName("ConsoleExporter");
  }

  @Test
  public void testDefaultScripted_default() {
    // given an empty <setup> element
    String xml = "<setup/>";
    // when executing the RootStatement
    BeneratorContext context = parseAndExecuteRoot(xml);
    // then context has defaultScripted=false
    assertFalse(context.isDefaultSourceScripted());
  }

  @Test
  public void testDefaultScripted_false() {
    // given a <setup> element which sets defaultSourceScripted to false
    String xml = "<setup defaultSourceScripted='false'/>";
    // when executing the RootStatement
    BeneratorContext context = parseAndExecuteRoot(xml);
    // then context has defaultScripted=false
    assertFalse(context.isDefaultSourceScripted());
  }

  @Test
  public void testDefaultScripted_true() {
    // given a <setup> element which sets defaultSourceScripted to true
    String xml = "<setup defaultSourceScripted='true'/>";
    // when executing the RootStatement
    BeneratorContext context = parseAndExecuteRoot(xml);
    // then context has defaultScripted=true
    assertTrue(context.isDefaultSourceScripted());
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  private void check(String uri) throws IOException {
    DescriptorRunner runner = new DescriptorRunner(uri, context);
    try {
      BeneratorRootStatement statement = runner.parseDescriptorFile();
      Generator<?> generator = statement.getGenerator("Person", runner.getContext());
      assertEquals(Object.class, generator.getGeneratedType());
      assertNotNull(generator);
      generator.init(context);
      for (int i = 0; i < 3; i++) {
        checkGeneration(generator);
      }
      assertUnavailable(generator);
      generator.close();
    } finally {
      IOUtil.close(runner);
    }
  }

  private static void checkGeneration(Generator<?> generator) {
    Entity entity = (Entity) GeneratorUtil.generateNonNull(generator);
    assertNotNull("generator unavailable: " + generator, entity);
    assertEquals("Person", entity.type());
    assertEquals("Alice", entity.get("name"));
  }

}
