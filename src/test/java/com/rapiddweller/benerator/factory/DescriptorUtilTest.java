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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.AttachedWeight;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.FeatureWeight;
import com.rapiddweller.benerator.distribution.WeightFunction;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.test.ConverterMock;
import com.rapiddweller.benerator.test.GeneratorMock;
import com.rapiddweller.benerator.test.JSR303ConstraintValidatorMock;
import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.benerator.test.ValidatorMock;
import com.rapiddweller.benerator.test.WeightFunctionMock;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link DescriptorUtil} class.<br/>
 * <br/>
 * Created at 31.12.2008 09:29:38
 * @since 0.5.7
 * @author Volker Bergmann
 */
public class DescriptorUtilTest extends ModelTest {
	
	private BeneratorContext context;
	
	@Before
	public void setUpContext() {
		context = new DefaultBeneratorContext();
	}
	
	@After
	public void tearDown() throws Exception {
		ConverterMock.latestInstance = null;
	}

	// instantiation tests ---------------------------------------------------------------------------------------------

	@Test
	public void testConvertType() {
		// test string parsing
		checkConversion("1", "long", 1L);
		checkConversion("1", "int", 1);
		checkConversion("1", "short", (short) 1);
		checkConversion("1", "byte", (byte) 1);
		checkConversion("1", "big_decimal", BigDecimal.ONE);
		checkConversion("1", "big_integer", BigInteger.ONE);
		checkConversion("1", "string", "1");
		checkConversion("true", "boolean", true);

		// test object to string conversion
		checkConversion(1L, "string", "1");
		checkConversion(1, "string", "1");
		checkConversion((short) 1, "string", "1");
		checkConversion((byte) 1, "string", "1");
		checkConversion(BigDecimal.ONE, "string", "1");
		checkConversion(BigInteger.ONE, "string", "1");
		checkConversion("1", "string", "1");
		checkConversion(true, "string", "true");

		// test number to number conversion
		checkConversion((byte) 1, "long", 1L);
		checkConversion(1L, "int", 1);
		checkConversion((short) 1, "short", (short) 1);
		checkConversion(1, "byte", (byte) 1);
		checkConversion(1, "big_integer", BigInteger.ONE);
		checkConversion(1, "big_decimal", BigDecimal.ONE);
	}
	
	private static void checkConversion(Object source, String targetType, Object expectedResult) {
		DataModel model = new DataModel();
		SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) model.getTypeDescriptor(targetType);
		Object result = DescriptorUtil.convertType(source, typeDescriptor);
		if (expectedResult instanceof BigDecimal && result instanceof BigDecimal)
			assertTrue(((BigDecimal) expectedResult).compareTo((BigDecimal) result) == 0);
		else
			assertEquals(expectedResult, result);
	}
	
	@Test
	public void testGetConverter() {
		
		// test bean reference
		checkGetConverter("c", new ConverterMock(2), "c", 3);
		
		// test class name specification
		checkGetConverter(null, null, ConverterMock.class.getName(), 2);
		
		// test constructor spec
		checkGetConverter(null, null, "new " + ConverterMock.class.getName() + "(2)", 3);
		
		// test property spec
		checkGetConverter(null, null, "new " + ConverterMock.class.getName() + "{increment=3}", 4);
		
		// test converter chaining
		checkGetConverter("c", new ConverterMock(3), "c, new " + ConverterMock.class.getName() + "(5)", 9);
	}

	@Test
	public void testGetValidator() {
		
		// test bean reference
		checkGetValidator("c", new ValidatorMock(2), "c", 2);
		
		// test class name specification
		checkGetValidator(null, null, ValidatorMock.class.getName(), 1);
		
		// test constructor spec
		checkGetValidator(null, null, "new " + ValidatorMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetValidator(null, null, "new " + ValidatorMock.class.getName() + "{value=3}", 3);
		
		// test converter chaining
		checkGetValidator("c", new ValidatorMock(3), "c, new " + ValidatorMock.class.getName() + "(5)", null);
		checkGetValidator("c", new ValidatorMock(3), "c, new " + ValidatorMock.class.getName() + "(3)", 3);
		
		// test JSR 303 constraint validator
		checkGetValidator(null, null, "new " + JSR303ConstraintValidatorMock.class.getName() + "(2)", 2);
	}
	
	@Test
	public void testGetGeneratorByName() {
		// test bean reference
		checkGetGeneratorByName("c", new GeneratorMock(2), "c", 2);
		
		// test class name specification
		checkGetGeneratorByName(null, null, GeneratorMock.class.getName(), 1);
		
		// test constructor spec
		checkGetGeneratorByName(null, null, "new " + GeneratorMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetGeneratorByName(null, null, "new " + GeneratorMock.class.getName() + "{value=3}", 3);
	}

	// distribution tests ----------------------------------------------------------------------------------------------
	
	@Test
	public void testGetDistributionForWeightFunction() {
		// test bean reference
		checkGetWeightFunction("c", new WeightFunctionMock(2), "c", 2);
		
		// test class name specification
		checkGetWeightFunction(null, null, WeightFunctionMock.class.getName(), 1);
		
		// test constructor spec
		checkGetWeightFunction(null, null, "new " + WeightFunctionMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetWeightFunction(null, null, "new " + WeightFunctionMock.class.getName() + "{value=3}", 3);
	}

	@Test
    public void testGetDistributionWeighted_simple() { // testing 'weighted'
		SimpleTypeDescriptor descriptor = createSimpleType("myType").withDistribution("weighted");
		Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), Uniqueness.NONE, true, context);
		assertTrue(distribution instanceof AttachedWeight);
	}

	@Test
    public void testGetDistributionWeighted_property() { // testing 'weighted[population]'
		SimpleTypeDescriptor descriptor2 = createSimpleType("myType").withDistribution("weighted[population]");
		Distribution distribution2 = FactoryUtil.getDistribution(descriptor2.getDistribution(), Uniqueness.NONE, true, context);
		assertTrue(distribution2 instanceof FeatureWeight);
		assertEquals("population", ((FeatureWeight) distribution2).getWeightFeature());
	}

	@Test
	public void testIsWrappedSimpleType() {
		// test wrapped simple type
		PartDescriptor bodyDescriptor = createPart(ComplexTypeDescriptor.__SIMPLE_CONTENT);
		ComplexTypeDescriptor wrappedSimpleType = createComplexType("Test").withComponent(bodyDescriptor);
		assertTrue(DescriptorUtil.isWrappedSimpleType(wrappedSimpleType));
		
		// test complex type
		PartDescriptor partDescriptor = createPart("name");
		ComplexTypeDescriptor complexType = createComplexType("Test").withComponent(partDescriptor);
		assertFalse(DescriptorUtil.isWrappedSimpleType(complexType));
	}

	
	// configuration tests ---------------------------------------------------------------------------------------------
	
	@Test
	public void testGetPatternAsDateFormat() {
		Date date = TimeUtil.date(2000, 0, 2);
		// test default format
		DateFormat format = DescriptorUtil.getPatternAsDateFormat(createSimpleType("test"));
		assertEquals("2000-01-02", format.format(date));
		// test custom format
		format = DescriptorUtil.getPatternAsDateFormat(createSimpleType("test").withPattern("yy-MM-dd"));
		assertEquals("00-01-02", format.format(date));
	}

	@Test
	public void testIsUnique() {
		assertEquals(false, DescriptorUtil.isUnique(createInstance("test"), context));
		assertEquals(false, DescriptorUtil.isUnique(createInstance("test").withUnique(false), context));
		assertEquals(true, DescriptorUtil.isUnique(createInstance("test").withUnique(true), context));
	}

	@Test
	public void testGetSeparator() {
		try {
			assertEquals(',', DescriptorUtil.getSeparator(createSimpleType("x"), context));
			context.setDefaultSeparator('|');
			assertEquals('|', DescriptorUtil.getSeparator(createSimpleType("x"), context));
			assertEquals(';', DescriptorUtil.getSeparator(createSimpleType("x").withSeparator(";"), context));
		} finally {
			context.setDefaultSeparator(',');
		}
	}
	
	@Test
	public void testGetMinCount() {
		// default
		assertEquals(1, DescriptorUtil.getMinCount(createInstance("x")).evaluate(context).intValue());
		// set explicitly
		assertEquals(2, DescriptorUtil.getMinCount(createInstance("x").withMinCount(2)).evaluate(context).intValue());
		// override by global maxCount
		context.setMaxCount(3L);
		assertEquals(3, DescriptorUtil.getMinCount(createInstance("x").withMinCount(4)).evaluate(context).intValue());
		// ignore global maxCount in default case
		context.setMaxCount(5L);
		assertEquals(1, DescriptorUtil.getMinCount(createInstance("x")).evaluate(context).intValue());
		// global maxCount overrides default
		context.setMaxCount(0L);
		assertEquals(0, DescriptorUtil.getMinCount(createInstance("x")).evaluate(context).intValue());
	}
	
	@Test
	public void testGetMaxCount() {
		// default
		assertNull(DescriptorUtil.getMaxCount(createInstance("x"), 1L).evaluate(context));
		// explicit setting
		assertEquals(2L, DescriptorUtil.getMaxCount(createInstance("x").withMaxCount(2), 1L).evaluate(context).longValue());
		// override by global maxCount
		context.setMaxCount(3L);
		assertEquals(3L, DescriptorUtil.getMaxCount(createInstance("x").withMaxCount(4), 1L).evaluate(context).longValue());
		// global maxCount overrides default
		context.setMaxCount(null);
		assertEquals(null, DescriptorUtil.getMaxCount(createInstance("x"), 1L).evaluate(context));
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private void checkGetConverter(String contextKey, Converter<Integer, ?> contextValue, String converterSpec, int expectedValue) {
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = createSimpleType("x");
		descriptor.setConverter(converterSpec);
		Converter<Integer, ?> converter = DescriptorUtil.getConverter(descriptor.getConverter(), context);
		assertNotNull(converter);
		assertEquals(expectedValue, converter.convert(1));
	}
	
	@SuppressWarnings("unchecked")
	private void checkGetValidator(String contextKey, Validator<Integer> contextValue, String validatorSpec, Integer validValue) {
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = createSimpleType("x");
		descriptor.setValidator(validatorSpec);
		Validator<Integer> validator = DescriptorUtil.getValidator(descriptor.getValidator(), context);
		assertNotNull(validator);
		if (validValue != null)
			assertEquals(true, validator.valid(validValue));
	}
	
	private void checkGetGeneratorByName(
			String contextKey, Generator<?> contextValue, String generatorSpec, int expectedValue) {
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = createSimpleType("x");
		descriptor.setGenerator(generatorSpec);
		Generator<?> generator = DescriptorUtil.getGeneratorByName(descriptor, context);
		assertNotNull(generator);
		generator.init(context);
		assertEquals(expectedValue, GeneratorUtil.generateNonNull(generator));
	}

	private void checkGetWeightFunction(
			String contextKey, Distribution contextValue, String distributionSpec, double expectedValue) {
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = createSimpleType("x");
		descriptor.setDistribution(distributionSpec);
		Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), Uniqueness.NONE, true, context);
		assertNotNull(distribution);
		assertTrue(distribution instanceof WeightFunction);
		assertEquals(expectedValue, ((WeightFunction) distribution).value(0), 0);
	}

}
