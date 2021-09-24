/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.test.PersonSource;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.collection.ObjectCounter;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the ComplexTypeGeneratorFactory.<br/><br/>
 * Created at 27.04.2008 18:29:59
 *
 * @author Volker Bergmann
 * @since 0.5.2
 */
public class ComplexTypeGeneratorFactoryTest extends GeneratorTest {

  private static final String PERSON_TAB_CSV = "com/rapiddweller/benerator/factory/person_tab.csv";
  private static final String PERSON_CSV = "com/rapiddweller/benerator/factory/person.ent.csv";

  private Entity alice;
  private Entity otto;

  /**
   * Sets up persons.
   */
  @Before
  public void setUpPersons() {
    alice = createEntity("person", "name", "Alice", "age", "23");
    otto = createEntity("person", "name", "Otto", "age", "89");
  }

  // testing generator feature ---------------------------------------------------------------------------------------

  /**
   * Test generator bean.
   */
  @Test
  public void testGeneratorBean() {
    ComplexTypeDescriptor type = createComplexType("LocaleGenerator");
    type.setDetailValue("generator", MyGenerator.class.getName());
    type.setDetailValue("locale", "de");
    Generator<Entity> generator = createGenerator(type);
    generator.init(context);
    Entity product = GeneratorUtil.generateNonNull(generator);
    assertNotNull(product);
    assertEquals(Locale.GERMAN, product.get("locale"));
  }

  // testing CSV file import -----------------------------------------------------------------------------------------

  /**
   * Test simple csv import.
   */
  @Test
  public void testSimpleCSVImport() {
    ComplexTypeDescriptor type = createComplexType("person");
    type.setSource(PERSON_CSV);
    Generator<Entity> generator = createGenerator(type);
    context.set("ottos_age", 89);
    generator.init(context);
    expectGeneratedSequence(generator, alice, otto).withCeasedAvailability();
  }

  /**
   * Test simple csv import scripted source.
   */
  @Test
  public void testSimpleCSVImport_scriptedSource() {
    context.set("filepath", PERSON_CSV);
    ComplexTypeDescriptor type = createComplexType("person");
    type.setSource("{filepath}");
    Generator<Entity> generator = createGenerator(type);
    context.set("ottos_age", 89);
    generator.init(context);
    expectGeneratedSequence(generator, alice, otto).withCeasedAvailability();
  }

  /**
   * Test tabbed csv import.
   */
  @Test
  public void testTabbedCSVImport() {
    ComplexTypeDescriptor type = createComplexType("person");
    type.setSource(PERSON_TAB_CSV);
    type.setSeparator("\t");
    Generator<Entity> generator = createGenerator(type);
    generator.init(context);
    expectGeneratedSequence(generator, alice, otto).withCeasedAvailability();
  }

  /**
   * Test cyclic csv import.
   */
  @Test
  public void testCyclicCSVImport() {
    ComplexTypeDescriptor type = createComplexType("person");
    type.setSource(PERSON_CSV);
    type.setCyclic(true);
    Generator<Entity> generator = createGenerator(type);
    context.set("ottos_age", 89);
    generator.init(context);
    expectGeneratedSequence(generator, alice, otto, alice).withContinuedAvailability();
  }

  /**
   * Test weighted csv import.
   */
  @Test
  public void testWeightedCSVImport() {
    ComplexTypeDescriptor type = createComplexType("person");
    type.setSource(PERSON_CSV);
    type.setDetailValue("distribution", "weighted[age]");
    Generator<Entity> generator = createGenerator(type);
    context.set("ottos_age", "89");
    generator.init(context);
    expectGeneratedSet(generator, 20, alice, otto).withContinuedAvailability();
    ObjectCounter<Entity> counter = new ObjectCounter<>(2);
    int n = 1000;
    for (int i = 0; i < n; i++) {
      counter.count(GeneratorUtil.generateNonNull(generator));
    }
    assertEquals(n * 24. / (24. + 89.), counter.getCount(alice), n / 20.);
  }

  /**
   * Test sequenced csv import.
   */
  @Test
  public void testSequencedCSVImport() {
    ComplexTypeDescriptor type = createComplexType("person");
    type.setSource(PERSON_CSV);
    type.setDistribution("new StepSequence(-1)");
    Generator<Entity> generator = createGenerator(type);
    context.set("ottos_age", 89);
    generator.init(context);
    expectGeneratedSequence(generator, otto, alice).withCeasedAvailability();
  }

  /**
   * Test unique csv import.
   */
  @Test
  public void testUniqueCSVImport() {
    ComplexTypeDescriptor type = createComplexType("person");
    type.setSource(PERSON_CSV);
    InstanceDescriptor instance = createInstance("person", type);
    instance.setUnique(true);
    Generator<Entity> generator = createGenerator(instance);
    context.set("ottos_age", 89);
    generator.init(context);
    Entity person1 = GeneratorUtil.generateNonNull(generator);
    Entity person2 = GeneratorUtil.generateNonNull(generator);
    assertTrue(alice.equals(person1) && otto.equals(person2) || otto.equals(person1) && alice.equals(person2));
    assertUnavailable(generator);
  }

  // other tests -----------------------------------------------------------------------------------------------------

  // private helpers -------------------------------------------------------------------------------------------------

  @SuppressWarnings("unchecked")
  private Generator<Entity> createGenerator(InstanceDescriptor instance) {
    ComplexTypeDescriptor type = (ComplexTypeDescriptor) instance.getTypeDescriptor();
    Generator<?> generator = BeneratorFactory.getInstance().getComplexTypeGeneratorFactory().createGenerator(
        type, type.getName(), false, instance.getUniqueness(), context);
    assertEquals(Entity.class, generator.getGeneratedType());
    return (Generator<Entity>) generator;
  }

  @SuppressWarnings("unchecked")
  private Generator<Entity> createGenerator(ComplexTypeDescriptor type) {
    Generator<?> generator = BeneratorFactory.getInstance().getComplexTypeGeneratorFactory().createGenerator(
        type, type.getName(), false, Uniqueness.NONE, context);
    assertEquals(Entity.class, generator.getGeneratedType());
    return (Generator<Entity>) generator;
  }

}
