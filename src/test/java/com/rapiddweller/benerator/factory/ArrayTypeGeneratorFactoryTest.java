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

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.test.PersonSource;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.commons.ArrayFormat;
import com.rapiddweller.commons.ConfigurationError;
import com.rapiddweller.commons.converter.ArrayElementExtractor;
import com.rapiddweller.commons.converter.ConverterManager;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.platform.db.DefaultDBSystem;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ArrayTypeGeneratorFactory}.<br/><br/>
 * Created: 01.05.2010 08:03:12
 * @since 0.6.1
 * @author Volker Bergmann
 */
@SuppressWarnings("unchecked")
public class ArrayTypeGeneratorFactoryTest extends GeneratorTest {
	
	static final Object[] ALICE = new Object[] { "Alice", 23 };
	static final Object[] BOB   = new Object[] { "Bob",   34 };
	static final Object[] OTTO  = new Object[] { "Otto",  89 };
	
	static final Object[] INT13 = new Object[] { 1, 3 };
	static final Object[] INT14 = new Object[] { 1, 4 };
	static final Object[] INT23 = new Object[] { 2, 3 };
	static final Object[] INT24 = new Object[] { 2, 4 };

	ArrayTypeGeneratorFactory arrayTypeGeneratorFactory = new ArrayTypeGeneratorFactory();
	
	@Before
	public void setup() {
		ConverterManager.getInstance().reset();
		testDescriptorProvider.addTypeDescriptor(createPersonDescriptor());
	}

	@Test
	public void testGenerator() {
		ArrayTypeDescriptor descriptor = createArrayType("testGenerator");
		descriptor.setGenerator(PersonAttrArrayGenerator.class.getName());
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				descriptor, "testGenerator", false, Uniqueness.NONE, context);
		generator.init(context);
		for (int i = 0; i < 10; i++)
			assertEqualArrays(PersonAttrArrayGenerator.ALICE, GeneratorUtil.generateNonNull(generator));
	}
	
	@Test
	public void testXlsSource() {
		ArrayTypeDescriptor parent = createPersonDescriptor();
		ArrayTypeDescriptor descriptor = createArrayType("testXlsSource", parent);
		descriptor.setSource("com/rapiddweller/benerator/factory/person.ent.xls");
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				descriptor, "testXlsSource", false, Uniqueness.NONE, context);
		context.set("otto_age", 89);
		generator.init(context);
		expectGeneratedSequence(generator, ALICE, OTTO).withCeasedAvailability();
	}
	
	@Test
	public void testXlsDataset() {
		ArrayTypeDescriptor parent = createPersonDescriptor();
		ArrayTypeDescriptor descriptor = createArrayType("testXlsDataset", parent);
		descriptor.setSource("com/rapiddweller/benerator/factory/dataset_{0}.xls");
		descriptor.setNesting("com/rapiddweller/benerator/factory/testnesting");
		descriptor.setDataset("DACH");
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				descriptor, "testXlsDataset", false, Uniqueness.SIMPLE, context);
		Generator<String> g = WrapperFactory.applyConverter(generator, 
				new ArrayElementExtractor<String>(String.class, 0));
		generator.init(context);
		expectUniquelyGeneratedSet(g, "de", "at", "ch");
		assertUnavailable(generator);
	}
	
	@Test
	public void testCsvSource() {
		ArrayTypeDescriptor parent = createPersonDescriptor();
		ArrayTypeDescriptor descriptor = createArrayType("testCsvSource", parent);
		descriptor.setSource("com/rapiddweller/benerator/factory/person.ent.csv");
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				descriptor, "testCsvSource", false, Uniqueness.NONE, context);
		context.set("ottos_age", 89);
		generator.init(context);
		assertEqualArrays(ALICE, GeneratorUtil.generateNonNull(generator));
		assertEqualArrays(OTTO, GeneratorUtil.generateNonNull(generator));
		assertUnavailable(generator);
	}
	
    @Test
	public void testCsvDataset() {
		ArrayTypeDescriptor parent = createPersonDescriptor();
		ArrayTypeDescriptor descriptor = createArrayType("testCsvDataset", parent);
		descriptor.setSource("com/rapiddweller/benerator/factory/dataset_{0}.csv");
		descriptor.setNesting("com/rapiddweller/benerator/factory/testnesting");
		descriptor.setDataset("DACH");
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				descriptor, "testCsvDataset", false, Uniqueness.SIMPLE, context);
		Generator<String> g = WrapperFactory.applyConverter(generator, new ArrayElementExtractor<String>(
				String.class, 0));
		generator.init(context);
		expectUniquelyGeneratedSet(g, "de", "at", "ch");
		assertUnavailable(generator);
	}
	
	@Test
	public void testDatabaseSource() throws Exception {
		// prepare DB
		DefaultDBSystem db = new DefaultDBSystem("db", HSQLUtil.getInMemoryURL("benerator"), HSQLUtil.DRIVER, "sa", null, context.getDataModel());
		context.set("db", db);
		try {
			db.execute(
					"create table agft_person (" +
					"  id   int         NOT NULL," +
					"  name varchar(30) NOT NULL," +
					"  age  int         NOT NULL" +
					")");
			db.execute("insert into agft_person (id, name, age) values (1, 'Alice', 23)");
			db.execute("insert into agft_person (id, name, age) values (2, 'Otto', 89)");
			
			// prepare descriptor
			ArrayTypeDescriptor parent = createPersonDescriptor();
			ArrayTypeDescriptor descriptor = createArrayType("testDatabaseSource", parent);
			descriptor.setSource("db");
			descriptor.setSelector("select name, age from agft_person");
			Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
					descriptor, "testDatabaseSource", false, Uniqueness.NONE, context);
			generator.init(context);
			
			// verify results
	        assertEqualArrays(ALICE, GeneratorUtil.generateNonNull(generator));
	        Object[] p2 = GeneratorUtil.generateNonNull(generator);
	        assertEqualArrays(OTTO,  p2);
	        assertUnavailable(generator);
			
		} finally {
			db.execute("drop table agft_person if exists");
			db.close();
		}
	}
	
	@Test
	public void testEntitySource() {
		ArrayTypeDescriptor descriptor = createArrayType("testEntitySourceType");
		descriptor.setSource(PersonSource.class.getName());
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				descriptor, "testEntitySource", false, Uniqueness.NONE, context);
		generator.init(context);
		for (int i = 0; i < 2; i++) {
	        Object[] product = GeneratorUtil.generateNonNull(generator);
	        assertTrue("Found: " + ArrayFormat.format(product), 
	        		Arrays.equals(ALICE, product) || Arrays.equals(BOB, product));
        }
		assertUnavailable(generator);
	}
	
	@Test
	public void testGeneratorSource() {
		context.set("myGen", new PersonAttrArrayGenerator());
		ArrayTypeDescriptor parent = createPersonDescriptor();
		ArrayTypeDescriptor descriptor = createArrayType("testGeneratorSource", parent);
		descriptor.setSource("myGen");
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				descriptor, "testGeneratorSource", false, Uniqueness.NONE, context);
		generator.init(context);
		for (int i = 0; i < 10; i++)
			assertEqualArrays(ALICE, GeneratorUtil.generateNonNull(generator));
	}
	
	@Test(expected = ConfigurationError.class)
	public void testIllegalSourceType() {
		ArrayTypeDescriptor descriptor = createArrayType("");
		descriptor.setSource("illegalSource");
		context.set("illegalSource", new File("txt.txt"));
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				descriptor, "testIllegalSourceType", false, Uniqueness.NONE, context);
		generator.init(context);
	}
	
	@Test
	public void testSyntheticGeneration() {
		// given an array descriptor
		ArrayTypeDescriptor arrayDescriptor = createArrayType("");
		ArrayElementDescriptor e1 = createArrayElement(0, "string");
		((SimpleTypeDescriptor) e1.getLocalType(false)).setValues("'Alice', 'Bob'");
		arrayDescriptor.addElement(e1);
		ArrayElementDescriptor e2 = createArrayElement(1, "int");
		((SimpleTypeDescriptor) e2.getLocalType(false)).setValues("23,34");
		arrayDescriptor.addElement(e2);
		// when creating a generator for the descriptor
		Generator<Object[]> generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				arrayDescriptor, "testSyntheticGeneration", false, Uniqueness.NONE, context);
		// it is expected to generate as specified
		generator.init(context);
		for (int i = 0; i < 10; i++) {
			Object[] product = GeneratorUtil.generateNonNull(generator);
			assertNotNull(product);
			assertTrue("Expected 'Alice' or 'Bob', but was: " + product[0], 
					"Alice".equals(product[0]) || "Bob".equals(product[0]));
			assertTrue((Integer) product[1] == 23 || (Integer) product[1] == 34);
		}
	}

	/** TODO v0.8 implement array mutation
	public void testMutatingGeneration() {
		Object[] MUTATED_ALICE = new Object[] { "Alice", 24 };
		
		// define descriptor
		ArrayTypeDescriptor descriptor = createPersonDescriptor();
		descriptor.setGenerator(PersonAttrArrayGenerator.class.getName());
		descriptor.getElement(1).getLocalType(false).setScript("p[1] + 1");
		
		// create generator
		Generator<Object[]> generator = ArrayGeneratorFactory.createArrayGenerator(
				"p", descriptor, Uniqueness.NONE, context);
		generator.init(context);
		
		// validate
		for (int i = 0; i < 10; i++)
			assertEqualArrays(MUTATED_ALICE, GeneratorUtil.generateNonNull(generator));
	}
	*/

	@Test
    public void testUniqueArrayGeneration() {
		ArrayTypeDescriptor arrayTypeDescriptor = createArrayType("MyArray");
		
		// create descriptor
		context.set("gen0", new SequenceTestGenerator<Integer>(1, 2));
		ArrayElementDescriptor e0 = createArrayElement(0, "int");
		((SimpleTypeDescriptor) e0.getLocalType(false)).setGenerator("gen0");
		arrayTypeDescriptor.addElement(e0);
		
		context.set("gen1", new SequenceTestGenerator<Integer>(3, 4));
		ArrayElementDescriptor e1 = createArrayElement(1, "int");
		((SimpleTypeDescriptor) e1.getLocalType(false)).setGenerator("gen1");
		arrayTypeDescriptor.addElement(e1);
		
		InstanceDescriptor arrayInstDescriptor = createInstance("array", arrayTypeDescriptor);
		arrayInstDescriptor.setUnique(true);
		
		// create generator
		Generator<Object[]> generator = (Generator<Object[]>) InstanceGeneratorFactory.createSingleInstanceGenerator(
				arrayInstDescriptor, Uniqueness.NONE, context);
		generator.init(context);
		
		// test generator
		assertArray(INT13, GeneratorUtil.generateNonNull(generator));
		assertArray(INT14, GeneratorUtil.generateNonNull(generator));
		assertArray(INT23, GeneratorUtil.generateNonNull(generator));
		assertArray(INT24, GeneratorUtil.generateNonNull(generator));
		assertUnavailable(generator);
	}

	@Test
    public void testUniqueValuesArrayGeneration() {
		ArrayElementDescriptor e0 = createArrayElement(0, "int");
		((SimpleTypeDescriptor) e0.getLocalType(false)).setValues("1,2");
		
		ArrayElementDescriptor e1 = createArrayElement(1, "int");
		((SimpleTypeDescriptor) e1.getLocalType(false)).setValues("3,4");
		
		ArrayTypeDescriptor arrayTypeDescriptor = createArrayType("MyArray");
		arrayTypeDescriptor.addElement(e0);
		arrayTypeDescriptor.addElement(e1);
		
		InstanceDescriptor arrayInstDescriptor = createInstance("array", arrayTypeDescriptor);
		arrayInstDescriptor.setUnique(true);
		
		Generator<Object[]> generator = (Generator<Object[]>) InstanceGeneratorFactory.createSingleInstanceGenerator(
				arrayInstDescriptor, Uniqueness.NONE, context);
		generator.init(context);
		for (int i = 0; i < 4; i++) {
	        Object[] product = GeneratorUtil.generateNonNull(generator);
	        assertTrue(
        		Arrays.equals(INT13, product)
        		|| Arrays.equals(INT14, product)
        		|| Arrays.equals(INT23, product)
        		|| Arrays.equals(INT24, product)
        	);
        }
		assertUnavailable(generator);
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------

	@SuppressWarnings("null")
    private static void assertArray(Object[] expected, Object[] actual) {
	    if (expected == null) {
	    	assertNull(actual);
	    	return;
	    }
        String failureMessage = failureMessage(expected, actual);
	    if (actual == null)
	        fail(failureMessage);
	    assertEquals(failureMessage, expected.length, actual.length);
	    for (int i = 0; i < expected.length; i++)
	    	assertTrue(failureMessage, expected[i].equals(actual[i]));
    }

	private static String failureMessage(Object[] expected, Object[] actual) {
		return "Expected " + Arrays.toString(expected) + ", found: " + Arrays.toString(actual);
    }

	private ArrayTypeDescriptor createPersonDescriptor() {
		ArrayTypeDescriptor arrayDescriptor = createArrayType("personType");
		ArrayElementDescriptor e1 = createArrayElement(0, "string");
		arrayDescriptor.addElement(e1);
		ArrayElementDescriptor e2 = createArrayElement(1, "int");
		arrayDescriptor.addElement(e2);
		return arrayDescriptor;
	}

	private static void assertEqualArrays(Object[] expected, Object[] actual) {
	    assertTrue(errMsg(expected, actual), Arrays.equals(expected, actual));
    }

	private static String errMsg(Object[] expected, Object[] actual) {
	    return "Expected {" + ArrayFormat.format(expected) + "} but found {" + ArrayFormat.format(actual) + "}";
    }

}
