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

import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.csv.SequencedDatasetCSVGenerator;
import com.rapiddweller.benerator.csv.WeightedDatasetCSVGenerator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.IndividualWeight;
import com.rapiddweller.benerator.distribution.sequence.RandomIntegerGenerator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DescriptorConstants;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.sample.WeightedCSVSampleGenerator;
import com.rapiddweller.benerator.wrapper.AccessingGenerator;
import com.rapiddweller.benerator.wrapper.AlternativeGenerator;
import com.rapiddweller.benerator.wrapper.AsByteGeneratorWrapper;
import com.rapiddweller.benerator.wrapper.ByteArrayGenerator;
import com.rapiddweller.benerator.wrapper.DataSourceGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.commons.Condition;
import com.rapiddweller.commons.ConfigurationError;
import com.rapiddweller.commons.Converter;
import com.rapiddweller.commons.StringUtil;
import com.rapiddweller.commons.Validator;
import com.rapiddweller.commons.accessor.GraphAccessor;
import com.rapiddweller.commons.converter.AnyConverter;
import com.rapiddweller.commons.converter.ArrayElementExtractor;
import com.rapiddweller.commons.converter.ConditionalConverter;
import com.rapiddweller.commons.converter.ConverterChain;
import com.rapiddweller.commons.converter.DateString2DurationConverter;
import com.rapiddweller.commons.converter.LiteralParser;
import com.rapiddweller.commons.converter.ToStringConverter;
import com.rapiddweller.commons.validator.StringLengthValidator;
import com.rapiddweller.formats.DataSource;
import com.rapiddweller.formats.script.ScriptConverterForStrings;
import com.rapiddweller.formats.util.DataFileUtil;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.UnionSimpleTypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.PrimitiveType;

import static com.rapiddweller.model.data.SimpleTypeDescriptor.*;

/**
 * Creates generators of simple types.<br/>
 * <br/>
 * @author Volker Bergmann
 */
public class SimpleTypeGeneratorFactory extends TypeGeneratorFactory<SimpleTypeDescriptor> {
	
	private static SimpleTypeGeneratorFactory INSTANCE = new SimpleTypeGeneratorFactory();
	
	public static SimpleTypeGeneratorFactory getInstance() {
		return INSTANCE;
	}
	
    protected SimpleTypeGeneratorFactory() { }
    
	@Override
	protected Generator<?> createExplicitGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness,
			BeneratorContext context) {
		Generator<?> generator = super.createExplicitGenerator(descriptor, uniqueness, context);
        if (generator == null)
        	generator = createConstantGenerator(descriptor, context);
        if (generator == null)
        	generator = createValuesGenerator(descriptor, uniqueness, context);
        if (generator == null)
        	generator = createPatternGenerator(descriptor, uniqueness, context);
        return generator;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Generator<?> createSpecificGenerator(SimpleTypeDescriptor descriptor, String instanceName, 
			boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
        Generator<?> generator = InstanceGeneratorFactory.createConfiguredDefaultGenerator(
        		instanceName, uniqueness, context);
		if (generator == null && nullable && shouldNullifyEachNullable(context))
			generator = new ConstantGenerator(null, getGeneratedType(descriptor));
		return generator;
	}

	protected static Generator<?> createValuesGenerator(
    		SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    	PrimitiveType primitiveType = descriptor.getPrimitiveType();
		Class<?> targetType = (primitiveType != null ? primitiveType.getJavaType() : String.class);
		String valueSpec = descriptor.getValues();
		if (valueSpec == null)
			return null;
		if ("".equals(valueSpec))
			return new ConstantGenerator<String>("");
        try {
			Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
			return context.getGeneratorFactory().createFromWeightedLiteralList(valueSpec, targetType, distribution, uniqueness.isUnique());
        } catch (com.rapiddweller.commons.ParseException e) {
	        throw new ConfigurationError("Error parsing samples: " + valueSpec, e);
        }
    }

	protected static Generator<String> createPatternGenerator(SimpleTypeDescriptor type, Uniqueness uniqueness,
			BeneratorContext context) {
		String pattern = type.getPattern();
		if (pattern != null)
	        return createStringGenerator(type, uniqueness, context);
	    else
	    	return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected static Generator<?> createConstantGenerator(
    		SimpleTypeDescriptor descriptor, BeneratorContext context) {
        Generator<?> generator = null;
        // check for constant
        String constant = descriptor.getConstant();
        if ("".equals(constant))
        	generator = new ConstantGenerator<String>("");
        else if (constant != null) {
        	Object value = LiteralParser.parse(constant);
            generator = new ConstantGenerator(value);
        }
        return generator;
    }

	@Override
	protected Generator<?> createHeuristicGenerator(
			SimpleTypeDescriptor descriptor, String instanceName, Uniqueness uniqueness, BeneratorContext context) {
		Generator<?> generator = createTypeGenerator(descriptor, uniqueness, context);
        if (generator == null)
            generator = createStringGenerator(descriptor, uniqueness, context);
		return generator;
	}

    @Override
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
    protected Generator<?> createSourceGenerator(
    		SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        String source = descriptor.getSource();
        if (source == null)
            return null;
        String selector = descriptor.getSelector();
        String subSelector = descriptor.getSubSelector();
        Generator<?> generator;
        if (context.get(source) != null) {
            Object sourceObject = context.get(source);
            if (sourceObject instanceof StorageSystem)
            	if (!StringUtil.isEmpty(subSelector)) {
            		generator = new DataSourceGenerator(((StorageSystem) sourceObject).query(subSelector, true, context));
                    generator = WrapperFactory.applyHeadCycler(generator);
            	} else
            		generator = new DataSourceGenerator(((StorageSystem) sourceObject).query(selector, true, context));
            else if (sourceObject instanceof Generator)
                generator = (Generator<?>) sourceObject;
            else if (sourceObject instanceof DataSource) {
				DataSource dataSource = (DataSource) sourceObject;
				generator = new DataSourceGenerator(dataSource);
			} else
                throw new UnsupportedOperationException("Not a supported source: " + sourceObject);
        } else if (DataFileUtil.isCsvDocument(source)) {
            return createSimpleTypeCSVSourceGenerator(descriptor, source, uniqueness, context);
        } else if (DataFileUtil.isExcelDocument(source)) {
            return createSimpleTypeXLSSourceGenerator(descriptor, source, uniqueness, context);
        } else if (DataFileUtil.isPlainTextDocument(source)) {
            generator = SourceFactory.createTextLineGenerator(source);
        } else {
        	try {
	        	BeanSpec sourceSpec = DatabeneScriptParser.resolveBeanSpec(source, context);
	        	generator = createSourceGeneratorFromObject(descriptor, context, sourceSpec);
        	} catch (Exception e) {
                generator = new AccessingGenerator(Object.class, new GraphAccessor(source), context);
        	}
        }

        Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
        if (distribution != null)
            generator = distribution.applyTo(generator, uniqueness.isUnique());
        
    	return generator;
    }

	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
    private static Generator<?> createSourceGeneratorFromObject(SimpleTypeDescriptor descriptor,
            BeneratorContext context, BeanSpec sourceSpec) {
		Object sourceObject = sourceSpec.getBean();
		Generator<?> generator;
	    if (sourceObject instanceof StorageSystem) {
	        StorageSystem storage = (StorageSystem) sourceObject;
	        String selector = descriptor.getSelector();
	        String subSelector = descriptor.getSubSelector();
	        if (!StringUtil.isEmpty(subSelector)) {
	        	generator = new DataSourceGenerator(storage.queryEntities(descriptor.getName(), subSelector, context));
		        generator = WrapperFactory.applyHeadCycler(generator);
	        } else
		        generator = new DataSourceGenerator(storage.queryEntities(descriptor.getName(), selector, context));
	    } else if (sourceObject instanceof Generator) {
	        generator = (Generator<?>) sourceObject;
	    } else
	        throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
	    if (sourceSpec.isReference())
	    	generator = WrapperFactory.preventClosing(generator);
	    return generator;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<?> createSimpleTypeCSVSourceGenerator(
			SimpleTypeDescriptor descriptor, String sourceName, Uniqueness uniqueness, BeneratorContext context) {
		String sourceUri = context.resolveRelativeUri(sourceName);
		Generator<?> generator;
		char separator = DescriptorUtil.getSeparator(descriptor, context);
		boolean rowBased = (descriptor.isRowBased() == null || descriptor.isRowBased());
		String encoding = descriptor.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
        Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);

		String dataset = descriptor.getDataset();
		String nesting = descriptor.getNesting();
		if (dataset != null && nesting != null) {
			if (uniqueness.isUnique()) {
			    generator = new SequencedDatasetCSVGenerator(sourceUri, separator, dataset, nesting, 
			    		distribution, encoding, new ScriptConverterForStrings(context));
			} else {
			    generator = new WeightedDatasetCSVGenerator(Object.class, sourceUri, separator, dataset, nesting, false, 
			    		encoding, new ScriptConverterForStrings(context));
			}
		} else if (sourceName.toLowerCase().endsWith(".wgt.csv") || distribution instanceof IndividualWeight) {
        	generator = new WeightedCSVSampleGenerator(
        			Object.class, sourceUri, encoding, new ScriptConverterForStrings(context));
        } else {
    		Generator<String[]> src = SourceFactory.createCSVGenerator(sourceUri, separator, encoding, true, rowBased);
    		Converter<String[], Object> converterChain = new ConverterChain<String[], Object>(
    				new ArrayElementExtractor<String>(String.class, 0), 
    				new ScriptConverterForStrings(context));
    		generator = WrapperFactory.applyConverter(src, converterChain);
            if (distribution != null)
            	generator = distribution.applyTo(generator, uniqueness.isUnique());
        }
		return generator;
	}

    private static Generator<?> createSimpleTypeXLSSourceGenerator(
			SimpleTypeDescriptor descriptor, String sourceName, Uniqueness uniqueness, BeneratorContext context) {
		// TODO v0.8 define common mechanism for file sources CSV, XLS, ... and entity, array, simple type
		Generator<?> generator;
        Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
		Generator<Object[]> src = SourceFactory.createXLSLineGenerator(sourceName);
		Converter<Object[], Object> converterChain = new ConverterChain<Object[], Object>(
				new ArrayElementExtractor<Object>(Object.class, 0),
				new ConditionalConverter(new Condition<Object>() {
					@Override
					public boolean evaluate(Object argument) {
						return (argument instanceof String);
					}
				},
				new ScriptConverterForStrings(context)));
		generator = WrapperFactory.applyConverter(src, converterChain);
        if (distribution != null)
        	generator = distribution.applyTo(generator, uniqueness.isUnique());
		return generator;
	}


    @SuppressWarnings("unchecked")
    private Generator<?> createTypeGenerator(
            SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        if (descriptor instanceof UnionSimpleTypeDescriptor)
            return createUnionTypeGenerator((UnionSimpleTypeDescriptor) descriptor, context);
        PrimitiveType primitiveType = descriptor.getPrimitiveType();
        if (primitiveType == null)
            return null;
        Class<?> targetType = primitiveType.getJavaType();
        if (Number.class.isAssignableFrom(targetType)) {
            return createNumberGenerator(descriptor, (Class<? extends Number>) targetType, uniqueness, context);
        } else if (String.class.isAssignableFrom(targetType)) {
            return createStringGenerator(descriptor, uniqueness, context);
        } else if (Boolean.class == targetType) {
            return createBooleanGenerator(descriptor, context);
        } else if (Character.class == targetType) {
            return createCharacterGenerator(descriptor, uniqueness, context);
        } else if (Date.class == targetType) {
            return createDateGenerator(descriptor, uniqueness, context);
        } else if (Timestamp.class == targetType) {
            return createTimestampGenerator(descriptor, uniqueness, context);
        } else if (byte[].class == targetType) {
            return createByteArrayGenerator(descriptor, context);
        } else
            return null;
    }

	@Override
	protected Class<?> getGeneratedType(SimpleTypeDescriptor descriptor) {
		PrimitiveType primitiveType = descriptor.getPrimitiveType();
		if (primitiveType == null)
			throw new ConfigurationError("No type configured for " + descriptor.getName());
		return primitiveType.getJavaType();
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Generator<?> createUnionTypeGenerator(
            UnionSimpleTypeDescriptor descriptor, BeneratorContext context) {
        int n = descriptor.getAlternatives().size();
        Generator<?>[] sources = new Generator[n];
        for (int i = 0; i < n; i++) {
            SimpleTypeDescriptor alternative = descriptor.getAlternatives().get(i);
            sources[i] = createGenerator(alternative, null, false, Uniqueness.NONE, context);
        }
        Class<?> javaType = descriptor.getPrimitiveType().getJavaType();
        return new AlternativeGenerator(javaType, sources);
    }
    
    private static Generator<?> createByteArrayGenerator(SimpleTypeDescriptor descriptor, BeneratorContext context) {
        Generator<Byte> byteGenerator = new AsByteGeneratorWrapper<Integer>(new RandomIntegerGenerator(-128, 127, 1));
        return new ByteArrayGenerator(byteGenerator, 
        		DescriptorUtil.getMinLength(descriptor), DescriptorUtil.getMaxLength(descriptor, context.getDefaultsProvider()));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Generator<Timestamp> createTimestampGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        Generator<Date> source = createDateGenerator(descriptor, uniqueness, context);
        Converter<Date, Timestamp> converter = (Converter) new AnyConverter<Timestamp>(Timestamp.class);
		return WrapperFactory.applyConverter(source, converter);
    }

    private Generator<Date> createDateGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        Date min = parseDate(descriptor, MIN, null);
        Date max = parseDate(descriptor, MAX, null);
        long granularity = parseDateGranularity(descriptor);
        Distribution distribution = FactoryUtil.getDistribution(
        		descriptor.getDistribution(), uniqueness, true, context);
	    return context.getGeneratorFactory().createDateGenerator(min, max, granularity, distribution);
    }

    private static Generator<Character> createCharacterGenerator(
    		SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        String pattern = descriptor.getPattern();
        if (pattern == null)
            pattern = ".";
        Locale locale = descriptor.getLocale();
        GeneratorFactory generatorFactory = context.getGeneratorFactory();
		return generatorFactory.createCharacterGenerator(pattern, locale, uniqueness.isUnique());
    }

	private Date parseDate(SimpleTypeDescriptor descriptor, String detailName, Date defaultDate) {
        String detail = (String) descriptor.getDeclaredDetailValue(detailName);
        try {
            if (detail != null) {
                DateFormat dateFormat = DescriptorUtil.getPatternAsDateFormat(descriptor);
                return dateFormat.parse(detail);
            } else
                return defaultDate;
        } catch (java.text.ParseException e) {
            logger.error("Error parsing date " + detail, e);
            return defaultDate;
        }
    }

    private static long parseDateGranularity(SimpleTypeDescriptor descriptor) {
        String detail = (String) descriptor.getDeclaredDetailValue(DescriptorConstants.ATT_GRANULARITY);
		if (detail != null)
        	return DateString2DurationConverter.defaultInstance().convert(detail);
        else
            return 24 * 3600 * 1000L;
    }

    private static Generator<Boolean> createBooleanGenerator(SimpleTypeDescriptor descriptor, BeneratorContext context) {
        return context.getGeneratorFactory().createBooleanGenerator(descriptor.getTrueQuota());
    }

    private static <T extends Number> Generator<T> createNumberGenerator(
            SimpleTypeDescriptor descriptor, Class<T> targetType, Uniqueness uniqueness, BeneratorContext context) {
        T min = DescriptorUtil.getNumberDetail(descriptor, MIN, targetType);
        Boolean minInclusive = descriptor.isMinInclusive();
        T max = DescriptorUtil.getNumberDetail(descriptor, MAX, targetType);
        Boolean maxInclusive = descriptor.isMaxInclusive();
        T granularity = DescriptorUtil.getNumberDetail(descriptor, GRANULARITY, targetType);
        Distribution distribution = FactoryUtil.getDistribution(
        		descriptor.getDistribution(), uniqueness, false, context);
        return context.getGeneratorFactory().createNumberGenerator(targetType, min, minInclusive, max, maxInclusive, 
                granularity, distribution, uniqueness);
    }

    private static Generator<String> createStringGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        // evaluate max length
        Integer maxLength = null;
        SimpleTypeDescriptor tmp = descriptor;
        while (maxLength == null && tmp != null) {
        	maxLength = tmp.getMaxLength();
        	tmp = tmp.getParent();
        }

        // check pattern against null
        String pattern = ToStringConverter.convert(descriptor.getDetailValue(PATTERN), null);

        Integer minLength = descriptor.getMinLength();
        int lengthGranularity = 1;
        Distribution lengthDistribution = FactoryUtil.getDistribution(
        		descriptor.getLengthDistribution(), Uniqueness.NONE, false, context);
        Locale locale = descriptor.getLocale();
        GeneratorFactory factory = context.getGeneratorFactory();
		return factory.createStringGenerator(pattern, locale, minLength, maxLength, lengthGranularity, 
				lengthDistribution, uniqueness);
    }
    
    @SuppressWarnings("unchecked")
    protected static <A extends Annotation, T> Validator<T> createRestrictionValidator(
            SimpleTypeDescriptor descriptor, boolean nullable, GeneratorFactory context) {
        if ((descriptor.getMinLength() != null || descriptor.getMaxLength() != null) && "string".equals(descriptor.getName())) {
            Integer minLength = DescriptorUtil.getMinLength(descriptor);
            Integer maxLength = DescriptorUtil.getMaxLength(descriptor, context.getDefaultsProvider());
            return (Validator<T>) new StringLengthValidator(minLength, maxLength, nullable);
        }
        return null;
    }

}