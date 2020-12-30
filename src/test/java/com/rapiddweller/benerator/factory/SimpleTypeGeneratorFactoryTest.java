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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.script.BeneratorScriptFactory;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.collection.ObjectCounter;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link SimpleTypeGeneratorFactory}.<br/>
 * <br/>
 * Created at 29.04.2008 20:13:40
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class SimpleTypeGeneratorFactoryTest extends GeneratorTest {

	private static final String NAME_CSV = "com/rapiddweller/benerator/factory/name.csv";
	private static final String NAMES_TAB_CSV = "com/rapiddweller/benerator/factory/names_tab.csv";
	private static final String SCRIPTED_NAMES_CSV = "com/rapiddweller/benerator/factory/scripted_names.csv";
	private static final String SCRIPTED_NAMES_WGT_CSV = "com/rapiddweller/benerator/factory/scripted_names.wgt.csv";

	@BeforeClass
	public static void setUpBeneneratorScript() {
    	ScriptUtil.addFactory("ben", new BeneratorScriptFactory());
    	ScriptUtil.setDefaultScriptEngine("ben");
	}
	
	@Before
	public void setUpConverterManager() {
    	ConverterManager.getInstance().reset();
    	ConverterManager.getInstance().setContext(context);
	}
	
	// 'value' attribute tests -----------------------------------------------------------------------------------------

	@Test
	public void testValues() {
		SimpleTypeDescriptor type = createSimpleType("string");
		type.setValues("'A','B','C'");
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE);
		expectGeneratedSet(generator, 100, "A", "B", "C").withContinuedAvailability();
	}
	
	@Test
	public void testUniqueValues() {
		SimpleTypeDescriptor type = createSimpleType("string");
		type.setValues("'A','B','C'");
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.ORDERED);
		expectUniquelyGeneratedSet(generator, "A", "B", "C").withCeasedAvailability();
	}
	
	@Test
	public void testCreateSampleGeneratorWithoutValues() {
		Generator<?> generator = SimpleTypeGeneratorFactory.createValuesGenerator(createSimpleType("test"), Uniqueness.NONE, null);
		assertNull(generator);
	}
	
	@Test
	public void testCreateSampleGeneratorUnweighted() {
		BeneratorContext context = new DefaultBeneratorContext();
		Generator<?> generator = SimpleTypeGeneratorFactory.createValuesGenerator(createSimpleType("test").withValues("'a','b'"), Uniqueness.NONE, context);
		generator.init(context);
		expectRelativeWeights(generator, 1000, "a", 1, "b", 1);
		SimpleTypeDescriptor descriptor = (SimpleTypeDescriptor) createSimpleType("test").withValues("'a','b,c'").withSeparator("|");
		generator = SimpleTypeGeneratorFactory.createValuesGenerator(descriptor, Uniqueness.NONE, context);
		generator.init(context);
		expectRelativeWeights(generator, 1000, "a", 1, "b,c", 1);
	}

	@Test
	public void testCreateSampleGeneratorWeighted() {
		BeneratorContext context = new DefaultBeneratorContext();
		Generator<?> generator = SimpleTypeGeneratorFactory.createValuesGenerator(createSimpleType("test").withValues("'a'^2,'b'"), Uniqueness.NONE, context);
		generator.init(context);
		expectRelativeWeights(generator, 3000, "a", 2, "b", 1);
	}
	
	// CSV tests -------------------------------------------------------------------------------------------------------

	@Test
	public void testSimpleCSVImport() {
		SimpleTypeDescriptor type = createSimpleType("givenName");
		type.setSource(NAME_CSV);
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE);
		expectGeneratedSequence(generator, "Alice", "Otto").withCeasedAvailability();
	}

	@Test
	public void testSimpleCSVImportWithOffset() {
		SimpleTypeDescriptor type = createSimpleType("givenNameWithOffset");
		type.setSource(NAME_CSV);
		type.setOffset(1);
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE);
		expectGeneratedSequence(generator, "Otto").withCeasedAvailability();
	}

	@Test
	public void testTabSeparatedCSVImport() {
		SimpleTypeDescriptor type = createSimpleType("name");
		type.setSource(NAMES_TAB_CSV);
		type.setSeparator("\t");
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE);
		expectGeneratedSequence(generator, "Alice", "Bob", "Charly").withCeasedAvailability();
	}

	@Test
	public void testScriptedCSVImport() {
		SimpleTypeDescriptor type = createSimpleType("name");
		type.setSource(SCRIPTED_NAMES_CSV);
		context.set("some_user", "the_user");
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE, context);
		expectGeneratedSequence(generator, "Alice", "the_user", "Otto").withCeasedAvailability();
	}

	@Test
	public void testScriptedWgtCSVImport() {
		SimpleTypeDescriptor type = createSimpleType("name");
		type.setSource(SCRIPTED_NAMES_WGT_CSV); // contains an entry with {some_user},1 - all others have weight 0
		context.set("some_user", "the_user"); // {some_user} is assigned the value 'the_user'
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE, context);
		expectGeneratedSet(generator, 100, "the_user").withContinuedAvailability(); // {some_user} has been replaced with 'the_user', all other weight are 0
		Map<String, AtomicInteger> counts = countProducts(generator, 300);
		assertEquals(1, counts.size());
		assertEquals(300, counts.get("the_user").intValue());
	}

	@Test
	public void testWeightedCSVImport() {
		SimpleTypeDescriptor type = createSimpleType("givenName");
		type.setSource(NAME_CSV);
		type.setDetailValue("distribution", "weighted");
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE);
		expectGeneratedSet(generator, 100, "Alice", "Otto").withContinuedAvailability();
		ObjectCounter<String> counter = new ObjectCounter<String>(2);
		int n = 1000;
		for (int i = 0; i < n; i++)
			counter.count(GeneratorUtil.generateNonNull(generator));
		assertEquals(n * 24. / (24. + 89.), counter.getCount("Alice"), n / 20);
	}

	@Test
	public void testSequencedCSVImport() {
		SimpleTypeDescriptor type = createSimpleType("givenName");
		type.setSource(NAME_CSV);
		type.setDistribution("new StepSequence(-1)");
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE);
		expectGeneratedSequence(generator, "Otto", "Alice").withCeasedAvailability();
	}

	@Test
	public void testUniqueCSVImport() {
		SimpleTypeDescriptor type = createSimpleType("givenName");
		type.setSource(NAME_CSV);
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.SIMPLE);
		ObjectCounter<String> counter = new ObjectCounter<String>(2);
		for (int i = 0; i < 2; i++)
			counter.count(GeneratorUtil.generateNonNull(generator));
		assertEquals(counter.toString(), 1, counter.getCount("Alice"));
		assertEquals(counter.toString(), 1, counter.getCount("Otto"));
		assertUnavailable(generator);
	}
	
	@Test
	public void testCyclicCSVImport() {
		SimpleTypeDescriptor type = createSimpleType("givenName");
		type.setSource(NAME_CSV);
		type.setCyclic(true);
		Generator<String> generator = createAndInitGenerator(type, Uniqueness.NONE);
		expectGeneratedSequence(generator, "Alice", "Otto", "Alice").withContinuedAvailability();
	}

	// private helpers -------------------------------------------------------------------------------------------------
	
    private Generator<String> createAndInitGenerator(SimpleTypeDescriptor type, Uniqueness uniqueness) {
		return createAndInitGenerator(type, uniqueness, context);
	}

	@SuppressWarnings("unchecked")
    private static Generator<String> createAndInitGenerator(
    		SimpleTypeDescriptor type, Uniqueness uniqueness, BeneratorContext context) {
		Generator<String> generator = (Generator<String>) new SimpleTypeGeneratorFactory().createGenerator(
				type, null, false, uniqueness, context);
		generator.init(context);
		return generator;
	}

}
