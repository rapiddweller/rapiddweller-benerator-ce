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

package com.rapiddweller.benerator.anno;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DescriptorBasedGenerator;
import com.rapiddweller.benerator.factory.ArrayTypeGeneratorFactory;
import com.rapiddweller.benerator.factory.CoverageGeneratorFactory;
import com.rapiddweller.benerator.factory.DescriptorUtil;
import com.rapiddweller.benerator.factory.EquivalenceGeneratorFactory;
import com.rapiddweller.benerator.factory.GeneratorFactory;
import com.rapiddweller.benerator.factory.GentleDefaultsProvider;
import com.rapiddweller.benerator.factory.InstanceGeneratorFactory;
import com.rapiddweller.benerator.factory.MeanDefaultsProvider;
import com.rapiddweller.benerator.factory.SerialGeneratorFactory;
import com.rapiddweller.benerator.factory.StochasticGeneratorFactory;
import com.rapiddweller.benerator.wrapper.LastFlagGenerator;
import com.rapiddweller.benerator.wrapper.NShotGeneratorProxy;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.ParseException;
import com.rapiddweller.common.ProgrammerError;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.context.ContextAware;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.util.DataFileUtil;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.Mode;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.platform.db.DBSystem;
import com.rapiddweller.platform.db.DefaultDBSystem;
import com.rapiddweller.platform.java.BeanDescriptorProvider;
import com.rapiddweller.platform.java.Entity2JavaConverter;
import com.rapiddweller.script.DatabeneScriptParser;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Maps Java annotations to descriptor objects.<br/><br/>
 * Created: 29.04.2010 06:59:02
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class AnnotationMapper extends DefaultDescriptorProvider {
	
	private static final Logger LOGGER = LogManager.getLogger(AnnotationMapper.class);
	
	private static final Set<String> STANDARD_METHODS;

	private static final Package BENERATOR_ANNO_PACKAGE = Unique.class.getPackage();
	private static final Package BEANVAL_ANNO_PACKAGE = Max.class.getPackage();
	
	private static final Set<Class<? extends Annotation>> EXPLICITLY_MAPPED_ANNOTATIONS = CollectionUtil.toSet(
			Bean.class, 
			Database.class, 
			Descriptor.class, 
			InvocationCount.class, 
			Factory.class, Equivalence.class, Coverage.class, Serial.class, 
			Defaults.class, Gentle.class, Mean.class,
			ThreadPoolSize.class);

	static {
		STANDARD_METHODS = new HashSet<>();
		for (Method method : Annotation.class.getMethods())
			STANDARD_METHODS.add(method.getName());
	}

	private final DataModel dataModel;
	private final PathResolver pathResolver;

	private final ArrayTypeGeneratorFactory arrayTypeGeneratorFactory;
	
	public AnnotationMapper(DataModel dataModel, PathResolver pathResolver) {
		super("anno", dataModel);
		this.dataModel = dataModel;
		this.dataModel.addDescriptorProvider(this);
		this.arrayTypeGeneratorFactory = new ArrayTypeGeneratorFactory();
		this.pathResolver = pathResolver;
	}
	
	// interface -------------------------------------------------------------------------------------------------------
	
	/** Parses @{@link Database} and @{@link Bean} annotations attached to a class, 
	 *  initializes the related objects and puts them into the {@link BeneratorContext} */
	public void parseClassAnnotations(Annotation[] annotations, BeneratorContext context) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Database)
				parseDatabase((Database) annotation, context);
			else if (annotation instanceof Bean)
				parseBean((Bean) annotation, context);
		}
	}
	
	/** scans test class attributes for attributes with @{@link Source} annotation 
	 * and initializes them with a value from the referred source object */
	public Generator<?> createAndInitAttributeGenerator(Field attribute, BeneratorContext context) {
		Source sourceAnno = attribute.getAnnotation(Source.class);
		if (sourceAnno != null)
			return createAndInitAttributeSourceGenerator(sourceAnno, attribute, context);
		else
			return null;
	}

	public Generator<Object[]> createAndInitMethodParamsGenerator(Method testMethod, BeneratorContext context) {
		return createAndInitMethodParamsGenerator(new MethodDescriptor(testMethod), context);
    }

	public Generator<Object[]> createAndInitMethodParamsGenerator(MethodDescriptor testMethod, BeneratorContext context) {
		applyMethodGeneratorFactory(testMethod, context);

		// Evaluate @Bean and @Database annotations attached to the test method
		if (testMethod.getAnnotation(Bean.class) != null)
			parseBean(testMethod.getAnnotation(Bean.class), context);
		if (testMethod.getAnnotation(Database.class) != null)
			parseDatabase(testMethod.getAnnotation(Database.class), context);
		
		// map the native Java bean informations to an array descriptor
		ArrayTypeDescriptor type = createMethodParamsType(testMethod);
	    InstanceDescriptor instance = createMethodParamsInstanceDescriptor(testMethod, type);
	    Uniqueness uniqueness = (instance.isUnique() != null && instance.isUnique() ? Uniqueness.ORDERED : Uniqueness.NONE);

		// create and return generator
	    Generator<Object[]> generator = createGenerator(type, testMethod, uniqueness, context);
		generator.init(context);
		return generator;
    }

    // helper methods --------------------------------------------------------------------------------------------------
	
	protected ArrayTypeDescriptor createMethodParamsType(MethodDescriptor testMethod) {
		ArrayTypeDescriptor nativeType = createNativeParamsDescriptor(testMethod);
		return createConfiguredParamsDescriptor(testMethod, nativeType);
	}

	protected InstanceDescriptor createMethodParamsInstanceDescriptor(
			MethodDescriptor testMethod, ArrayTypeDescriptor type) {
		InstanceDescriptor instance = new InstanceDescriptor(testMethod.getName(), this, type);
		for (Annotation annotation : testMethod.getAnnotations())
			mapParamAnnotation(annotation, instance, testMethod.getDeclaringClass());
		return instance;
	}

	protected ArrayTypeDescriptor createNativeParamsDescriptor(MethodDescriptor testMethod) {
		ArrayTypeDescriptor nativeDescriptor = new ArrayTypeDescriptor(testMethod.getName() + "_native", this);
		Class<?>[] paramTypes = testMethod.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			TypeDescriptor elementType = dataModel.getTypeDescriptor(paramTypes[i].getName());
			BeanDescriptorProvider beanDescriptorProvider = dataModel.getBeanDescriptorProvider();
			ArrayElementDescriptor elementDescriptor = new ArrayElementDescriptor(i, beanDescriptorProvider, elementType);
		    if (elementDescriptor.isNullable() == null) { // assure an explicit setting for nullability
		    	if (BeanUtil.isPrimitiveType(paramTypes[i].getName()))
		    		elementDescriptor.setNullable(false); // primitives can never be null
		    	else if (elementDescriptor.getNullQuota() != null && 
		    			((Double) elementDescriptor.getDeclaredDetailValue("nullQuota")) == 0.)
		    		elementDescriptor.setNullable(false); // if nullQuota == 0, then set nullable to false
		    	else
		    		elementDescriptor.setNullable(true);
		    }
			nativeDescriptor.addElement(elementDescriptor);
		}
		return nativeDescriptor;
	}

	private ArrayTypeDescriptor createConfiguredParamsDescriptor(
			MethodDescriptor testMethod, ArrayTypeDescriptor nativeDescriptor) {
		ArrayTypeDescriptor type = new ArrayTypeDescriptor(
				testMethod.getName() + "_configured", this, nativeDescriptor);
		Class<?>[] parameterTypes = testMethod.getParameterTypes();
		Annotation[][] paramAnnos = testMethod.getParameterAnnotations();
		for (int i = 0; i < parameterTypes.length; i++) {
			ArrayElementDescriptor parentElement = nativeDescriptor.getElement(i);
			if (containsConfig(paramAnnos[i])) {
				TypeDescriptor parentElementType = parentElement.getTypeDescriptor();
				TypeDescriptor elementType = DescriptorUtil.deriveType(
						parentElementType.getName(), parentElementType);
				ArrayElementDescriptor element = new ArrayElementDescriptor(i, this, elementType);
				element.setParent(parentElement);
			    for (Annotation annotation : paramAnnos[i])
		            mapParamAnnotation(annotation, element, testMethod.getDeclaringClass());
				type.addElement(element);
			}
		}
		return type;
	}

	private static boolean containsConfig(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			Package annoPkg = annotation.annotationType().getPackage();
			if ((annoPkg == BENERATOR_ANNO_PACKAGE || annoPkg == BEANVAL_ANNO_PACKAGE) && 
					!explicitlyMappedAnnotation(annotation))
				return true;
		}
		return false;
	}

	protected static boolean explicitlyMappedAnnotation(Annotation annotation) {
		return EXPLICITLY_MAPPED_ANNOTATIONS.contains(annotation.annotationType());
	}

	protected void applyMethodGeneratorFactory(MethodDescriptor testMethod, BeneratorContext context) {
		boolean configured = applyGeneratorFactory(testMethod.getAnnotations(), context);
		if (!configured)
			applyClassGeneratorFactory(testMethod.getDeclaringClass().getAnnotations(), context);
		applyMethodDefaultsProvider(testMethod, context);
	}

	private void applyClassGeneratorFactory(Annotation[] annotations, BeneratorContext context) {
		boolean configured = applyGeneratorFactory(annotations, context);
		if (!configured)
			context.setGeneratorFactory(context.getGeneratorFactory());
	}

	protected boolean applyGeneratorFactory(Annotation[] annotations, BeneratorContext context) {
		boolean configured = false;
		for (Annotation annotation : annotations) {
			if (annotation instanceof Factory) {
				GeneratorFactory factory = BeanUtil.newInstance(((Factory) annotation).value());
				factory.setDefaultsProvider(context.getDefaultsProvider());
				context.setGeneratorFactory(factory);
				return true;
			} else if (annotation instanceof Equivalence) {
				context.setGeneratorFactory(new EquivalenceGeneratorFactory());
				return true;
			} else if (annotation instanceof Coverage) {
				context.setGeneratorFactory(new CoverageGeneratorFactory());
				return true;
			} else if (annotation instanceof Stochastic) {
				context.setGeneratorFactory(new StochasticGeneratorFactory());
				return true;
			} else if (annotation instanceof Serial) {
				context.setGeneratorFactory(new SerialGeneratorFactory());
				return true;
			}
		}
		return configured;
	}

	protected void applyMethodDefaultsProvider(MethodDescriptor testMethod, BeneratorContext context) {
		// check if the method is annotated with an individual DefaultsProvider...
		boolean configured = applyDefaultsProvider(testMethod.getAnnotations(), context);
		// ... otherwise check for a class-wide DefaultsProvider annotation...
		if (!configured)
			applyDefaultsProvider(testMethod.getDeclaringClass().getAnnotations(), context);
		// ...otherwise the GeneratorFactory's DefaultProvider is used
	}

	private static boolean applyDefaultsProvider(Annotation[] annotations, BeneratorContext context) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Defaults) {
				context.setDefaultsProvider(BeanUtil.newInstance(((Defaults) annotation).value()));
				return true;
			} else if (annotation instanceof Gentle) {
				context.setDefaultsProvider(new GentleDefaultsProvider());
				return true;
			} else if (annotation instanceof Mean) {
				context.setDefaultsProvider(new MeanDefaultsProvider());
				return true;
			}
		}
		return false;
	}
/*
	@SuppressWarnings("unchecked")
	private Generator<Object[]> createMethodSourceGenerator(com.rapiddweller.benerator.anno.Source source,
			Method testMethod, ArrayTypeDescriptor type, BeneratorContext context) {
		Generator<Object[]> baseGenerator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				type, testMethod.getName(), false, Uniqueness.NONE, context);
		return baseGenerator;
	}
*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Generator<?> createAndInitAttributeSourceGenerator(
            Source source, Field attribute, BeneratorContext context) {
		String attName = attribute.getName();
		TypeDescriptor typeDescriptor = createTypeDescriptor(attribute.getType());
		InstanceDescriptor descriptor = new InstanceDescriptor(attName, this, typeDescriptor);
		Class<?> testClass = attribute.getDeclaringClass();
		mapParamAnnotation(source, descriptor, testClass);
		Offset offset = attribute.getAnnotation(Offset.class);
		if (offset != null)
			mapParamAnnotation(offset, descriptor, testClass);
		Generator generator = InstanceGeneratorFactory.createSingleInstanceGenerator(
				descriptor, Uniqueness.NONE, context);
		generator = WrapperFactory.applyConverter(generator, new Entity2JavaConverter());
		generator.init(context);
		return generator;
	}
/*
	@SuppressWarnings("unchecked")
	private Generator<Object[]> createGeneratorGenerator(com.rapiddweller.benerator.anno.Generator annotation,
			Method testMethod, ArrayTypeDescriptor type, BeneratorContext context) {
		return (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				type, testMethod.getName(), false, Uniqueness.NONE, context);
	}
*/

    @SuppressWarnings("unchecked")
	protected Generator<Object[]> createGenerator(ArrayTypeDescriptor type,
			MethodDescriptor testMethod, Uniqueness uniqueness, BeneratorContext context) {
		Generator<Object[]> generator;
		
		Descriptor descriptorBasedAnno = testMethod.getAnnotation(Descriptor.class);
		if (descriptorBasedAnno != null) {
			context.setGeneratorFactory(new StochasticGeneratorFactory());
			generator = createDescriptorBasedGenerator(descriptorBasedAnno, testMethod, context);
		} else {
			generator = (Generator<Object[]>) arrayTypeGeneratorFactory.createGenerator(
				type, testMethod.getName(), false, uniqueness, context);
		}
		Class<?> generatedType = generator.getGeneratedType();
		if (generatedType != Object[].class && generatedType != Object.class)
	    	throw new ProgrammerError("Expected Generator<Object[]> or Generator<Object>, but found Generator<" + 
	    			generatedType.getClass().getSimpleName() + '>');
		
		// evaluate @TestFeed annotation
		InvocationCount testCount = testMethod.getAnnotation(InvocationCount.class);
		if (testCount != null)
			generator = new NShotGeneratorProxy<>(generator, testCount.value());
		
		// apply LastInstanceDetector
		generator = WrapperFactory.applyLastProductDetector(generator);

		int indexOfLast = indexOfLast(testMethod);
		if (indexOfLast >= 0)
			generator = new LastFlagGenerator(generator, indexOfLast);
		return generator;
	}

	private static int indexOfLast(MethodDescriptor testMethod) {
		Annotation[][] paramsAnnotations = testMethod.getParameterAnnotations();
		for (int i = 0; i < paramsAnnotations.length; i++) {
			for (Annotation paramAnnotation : paramsAnnotations[i])
				if (paramAnnotation.annotationType() == Last.class)
					return i;
		}
		return -1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Generator<Object[]> createDescriptorBasedGenerator(Descriptor annotation, MethodDescriptor testMethod, BeneratorContext context) {
		String filename = null;
		try {
			if (annotation.file().length() > 0)
				filename = annotation.file();
			else
				filename = testMethod.getDeclaringClass().getName().replace('.', File.separatorChar) + ".ben.xml";
			String testName;
			if (annotation.name().length() > 0)
				testName = annotation.name();
			else
				testName = testMethod.getName();
			return (Generator) new DescriptorBasedGenerator(filename, testName, context);
		} catch (IOException e) {
			throw new RuntimeException("Error opening file " + filename, e);
		}
	}

    
    
	private static void parseDatabase(Database annotation, BeneratorContext context) {
		DBSystem db;
		if (!StringUtil.isEmpty(annotation.environment()))
			db = new DefaultDBSystem(annotation.id(), annotation.environment(), context.getDataModel());
		else 
			db = new DefaultDBSystem(annotation.id(), annotation.url(), annotation.driver(), 
					annotation.user(), annotation.password(), context.getDataModel());
		if (!StringUtil.isEmpty(annotation.catalog()))
			db.setCatalog(annotation.catalog());
		if (!StringUtil.isEmpty(annotation.schema()))
			db.setSchema(annotation.schema());
		db.setLazy(true);
		context.setGlobal(db.getId(), db);
	}
	
	private static void parseBean(Bean annotation, BeneratorContext context) {
        Object bean = instantiateBean(annotation, context);
        applyProperties(annotation.properties(), bean, context);
        context.setGlobal(annotation.id(), bean);
        if (bean instanceof ContextAware)
        	((ContextAware) bean).setContext(context);
	}

	private static Object instantiateBean(Bean beanAnno, BeneratorContext context) {
		String beanSpec = beanAnno.spec();
		Class<?> beanClass = beanAnno.type();
		if (!StringUtil.isEmpty(beanSpec)) {
			try {
				if (beanClass != Object.class)
					throw new ConfigurationError("'type' and 'spec' exclude each other in a @Bean");
		        return DatabeneScriptParser.parseBeanSpec(beanSpec).evaluate(context);
			} catch (ParseException e) {
				throw new ConfigurationError("Error parsing bean spec: " + beanSpec, e);
			}
		} else if (beanClass != Object.class) {
		    return BeanUtil.newInstance(beanClass);
		} else
			throw new ConfigurationError("@Bean is missing 'type' or 'spec' attribute");
	}
	
	private static void applyProperties(Property[] properties, Object bean, BeneratorContext context) {
		for (Property property : properties) {
			Object value = resolveProperty(property, bean, context);
			BeanUtil.setPropertyValue(bean, property.name(), value, true, true);
		}
    }

    private static Object resolveProperty(Property property, Object bean, BeneratorContext context) {
		if (!StringUtil.isEmpty(property.value())) {
			if (!StringUtil.isEmpty(property.ref()))
				throw new ConfigurationError("'value' and 'ref' exclude each other in a @Property");
			Object value = ScriptUtil.evaluate(property.value(), context);
			if (value instanceof String)
				value = StringUtil.unescape((String) value);
			return value;
		} else if (!StringUtil.isEmpty(property.ref())) {
			return context.get(property.ref());
		} else
			throw new ConfigurationError("@Property is missing 'value' or 'ref' attribute");
	}
/*
    private Generator<Object[]> createParamsGenerator(
    		Method testMethod, InstanceDescriptor descriptor, BeneratorContext context) {
        Uniqueness uniqueness = DescriptorUtil.getUniqueness(descriptor, context);
        Generator<Object[]> generator = arrayTypeGeneratorFactory.createSimpleArrayGenerator(descriptor.getName(),
				(ArrayTypeDescriptor) descriptor.getTypeDescriptor(), uniqueness, context);
		return generator;
    }
*/
	private <T> void mapParamAnnotation(Annotation annotation, InstanceDescriptor descriptor, Class<?> testClass) {
	    Package annoPackage = annotation.annotationType().getPackage();
	    if (BENERATOR_ANNO_PACKAGE.equals(annoPackage))
	    	mapBeneratorParamAnnotation(annotation, descriptor, testClass);
	    else if (BEANVAL_ANNO_PACKAGE.equals(annoPackage))
	    	mapBeanValidationParameter(annotation, descriptor);
    }

	private void mapBeneratorParamAnnotation(Annotation annotation, InstanceDescriptor instanceDescriptor, Class<?> testClass) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("mapDetails(" + annotation + ", " + instanceDescriptor + ")");
	    try {
			Class<?> annotationType = annotation.annotationType();
			if (annotationType == Unique.class)
				instanceDescriptor.setDetailValue("unique", true);
			else if (annotationType == Granularity.class)
				instanceDescriptor.getLocalType(false).setDetailValue("granularity", String.valueOf(DescriptorUtil.convertType(((Granularity) annotation).value(), (SimpleTypeDescriptor) instanceDescriptor.getLocalType(false))));
			else if (annotationType == DecimalGranularity.class)
				instanceDescriptor.getLocalType(false).setDetailValue("granularity", String.valueOf(DescriptorUtil.convertType(((DecimalGranularity) annotation).value(), (SimpleTypeDescriptor) instanceDescriptor.getLocalType(false))));
			else if (annotationType == SizeDistribution.class)
				instanceDescriptor.getLocalType(false).setDetailValue("lengthDistribution", ((SizeDistribution) annotation).value());
			else if (annotationType == Pattern.class)
				mapPatternAnnotation((Pattern) annotation, instanceDescriptor);
			else if (annotationType == Size.class)
				mapSizeAnnotation((Size) annotation, instanceDescriptor);
			else if (annotationType == Source.class)
				mapSourceAnnotation((Source) annotation, instanceDescriptor, testClass);
			else if (annotationType == Values.class)
				mapValuesAnnotation((Values) annotation, instanceDescriptor);
			else if (annotationType == Offset.class)
				mapOffsetAnnotation((Offset) annotation, instanceDescriptor);
			else if (annotationType == MinDate.class)
				mapMinDateAnnotation((MinDate) annotation, instanceDescriptor);
			else if (annotationType == MaxDate.class)
				mapMaxDateAnnotation((MaxDate) annotation, instanceDescriptor);
			else if (annotationType == Last.class)
				instanceDescriptor.setMode(Mode.ignored);
			else if (!explicitlyMappedAnnotation(annotation))
				mapAnyValueTypeAnnotation(annotation, instanceDescriptor);
		} catch (Exception e) {
			throw new ConfigurationError("Error mapping annotation settings", e);
		}
    }

	private static void mapSizeAnnotation(Size size, InstanceDescriptor instanceDescriptor) {
    	setDetail("minLength", size.min(), instanceDescriptor);
    	setDetail("maxLength", size.max(), instanceDescriptor);
    }

	private static void mapPatternAnnotation(Pattern pattern, InstanceDescriptor instanceDescriptor) {
	    if (!StringUtil.isEmpty(pattern.regexp()))
	    	setDetail("pattern", pattern.regexp(), instanceDescriptor);
    }

	private void mapSourceAnnotation(Source source, InstanceDescriptor instanceDescriptor, Class<?> testClass) {
		mapSourceUriOrValue(source.value(),  instanceDescriptor, testClass);
		mapSourceUriOrValue(source.uri(),    instanceDescriptor, testClass);
		mapSourceSetting(source.segment(),   "segment",   instanceDescriptor);
		mapSourceSetting(source.id(),        "source",    instanceDescriptor);
		mapSourceSetting(source.dataset(),   "dataset",   instanceDescriptor);
		mapSourceSetting(source.nesting(),   "nesting",   instanceDescriptor);
		mapSourceSetting(source.encoding(),  "encoding",  instanceDescriptor);
		mapSourceSetting(source.filter(),    "filter",    instanceDescriptor);
		mapSourceSetting(source.selector(),  "selector",  instanceDescriptor);
		mapSourceSetting(source.separator(), "separator", instanceDescriptor);
		mapSourceSetting(source.emptyMarker(), "emptyMarker", instanceDescriptor);
		mapSourceSetting(source.nullMarker(), "nullMarker", instanceDescriptor);
		mapFormatted(source, instanceDescriptor);
    	setDetail("rowBased", source.rowBased(), instanceDescriptor);
    }

	private void mapSourceUriOrValue(String value, InstanceDescriptor instanceDescriptor, Class<?> testClass) {
		if (DataFileUtil.isExcelOrCsvDocument(value))
			value = pathResolver.getPathFor(value, testClass);
		mapSourceSetting(value, "source", instanceDescriptor);
	}

	private static void mapSourceSetting(String value, String detailName, InstanceDescriptor instanceDescriptor) {
	    if (!StringUtil.isEmpty(value))
	    	setDetail(detailName, value, instanceDescriptor);
    }

	private static void mapFormatted(Source source, InstanceDescriptor instanceDescriptor) {
		switch (source.format()) {
			case formatted: case raw: setDetail("format", source.format().name(), instanceDescriptor); break;
			case globalDefault: break;
			default: throw new UnsupportedOperationException("Not a supported Format: " + source.format());
		}
	}

	private static void mapValuesAnnotation(Values annotation, InstanceDescriptor instanceDescriptor) throws Exception {
		Method method = annotation.annotationType().getMethod("value");
		String[] values = (String[]) method.invoke(annotation);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i > 0)
				builder.append(',');
			builder.append("'").append(values[i].replace("'", "\\'")).append("'");
		}
		((SimpleTypeDescriptor) instanceDescriptor.getLocalType(false)).setValues(builder.toString());
    }

	private static void mapOffsetAnnotation(Offset annotation, InstanceDescriptor instanceDescriptor) {
		if (annotation.value() != 0)
			instanceDescriptor.getLocalType().setOffset(annotation.value());
    }

	private static void mapMinDateAnnotation(MinDate annotation, InstanceDescriptor instanceDescriptor) {
		TypeDescriptor localType = instanceDescriptor.getLocalType();
		if (!(localType instanceof SimpleTypeDescriptor))
			throw new ConfigurationError("@MinDate can only be applied to Date types");
		((SimpleTypeDescriptor) localType).setMin(annotation.value());
    }

	private static void mapMaxDateAnnotation(MaxDate annotation, InstanceDescriptor instanceDescriptor) {
		TypeDescriptor localType = instanceDescriptor.getLocalType();
		if (!(localType instanceof SimpleTypeDescriptor))
			throw new ConfigurationError("@MaxDate can only be applied to Date types");
		((SimpleTypeDescriptor) localType).setMax(annotation.value());
    }

	private static void mapAnyValueTypeAnnotation(Annotation annotation, InstanceDescriptor instanceDescriptor) throws Exception {
		Method method = annotation.annotationType().getMethod("value");
		Object value = normalize(method.invoke(annotation));
		String detailName = StringUtil.uncapitalize(annotation.annotationType().getSimpleName());
		setDetail(detailName, value, instanceDescriptor);
    }

	private static void mapBeanValidationParameter(Annotation annotation, InstanceDescriptor element) {
    	SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) element.getLocalType(false);
		if (annotation instanceof AssertFalse)
    		typeDescriptor.setTrueQuota(0.);
    	else if (annotation instanceof AssertTrue)
    		typeDescriptor.setTrueQuota(1.);
    	else if (annotation instanceof DecimalMax)
    		typeDescriptor.setMax(String.valueOf(DescriptorUtil.convertType(((DecimalMax) annotation).value(), typeDescriptor)));
    	else if (annotation instanceof DecimalMin)
    		typeDescriptor.setMin(String.valueOf(DescriptorUtil.convertType(((DecimalMin) annotation).value(), typeDescriptor)));
    	else if (annotation instanceof Digits) {
    		Digits digits = (Digits) annotation;
			typeDescriptor.setGranularity(String.valueOf(Math.pow(10, - digits.fraction())));
    	} else if (annotation instanceof Future)
	        typeDescriptor.setMin(new SimpleDateFormat("yyyy-MM-dd").format(TimeUtil.tomorrow()));
        else if (annotation instanceof Max)
			typeDescriptor.setMax(String.valueOf(((Max) annotation).value()));
        else if (annotation instanceof Min)
    		typeDescriptor.setMin(String.valueOf(((Min) annotation).value()));
    	else if (annotation instanceof NotNull) {
    		element.setNullable(false);
    		element.setNullQuota(0.);
    	} else if (annotation instanceof Null) {
    		element.setNullable(true);
    		element.setNullQuota(1.);
    	} else if (annotation instanceof Past)
	        typeDescriptor.setMax(new SimpleDateFormat("yyyy-MM-dd").format(TimeUtil.yesterday()));
        else if (annotation instanceof Pattern)
    		typeDescriptor.setPattern(((Pattern) annotation).regexp());
    	else if (annotation instanceof Size) {
    		Size size = (Size) annotation;
    		typeDescriptor.setMinLength(size.min());
    		typeDescriptor.setMaxLength(size.max());
    	}
    }

	private static void setDetail(String detailName, Object detailValue, InstanceDescriptor instanceDescriptor) {
		if (instanceDescriptor.supportsDetail(detailName))
			instanceDescriptor.setDetailValue(detailName, detailValue);
		else
			instanceDescriptor.getLocalType().setDetailValue(detailName, detailValue);
    }

	private static Object normalize(Object value) {
		if (value == null)
			return null;
		if (value instanceof String && ((String) value).length() == 0)
			return null;
		if (value.getClass().isArray() && Array.getLength(value) == 0)
			return null;
		return value;
	}

	protected TypeDescriptor createTypeDescriptor(Class<?> type) {
		String abstractType = dataModel.getBeanDescriptorProvider().abstractType(type);
		TypeDescriptor baseTypeDescriptor = dataModel.getTypeDescriptor(abstractType);
		TypeDescriptor typeDescriptor;
		if (baseTypeDescriptor instanceof SimpleTypeDescriptor) {
			typeDescriptor = new SimpleTypeDescriptor(type.getName(), this, (SimpleTypeDescriptor) baseTypeDescriptor);
		} else if (baseTypeDescriptor instanceof ComplexTypeDescriptor) {
			typeDescriptor = new ComplexTypeDescriptor(type.getName(), this, (ComplexTypeDescriptor) baseTypeDescriptor);
		} else
			throw new ConfigurationError("Cannot handle descriptor: " + baseTypeDescriptor);
		return typeDescriptor;
	}

}