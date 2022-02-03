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
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.accessor.GraphAccessor;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.common.converter.ArrayElementExtractor;
import com.rapiddweller.common.converter.ConditionalConverter;
import com.rapiddweller.common.converter.ConverterChain;
import com.rapiddweller.common.converter.DateString2DurationConverter;
import com.rapiddweller.common.converter.LiteralParserConverter;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.common.exception.ParseException;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.format.util.DataFileUtil;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.UnionSimpleTypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.PrimitiveType;

import javax.annotation.Nullable;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.rapiddweller.model.data.SimpleTypeDescriptor.GRANULARITY;
import static com.rapiddweller.model.data.SimpleTypeDescriptor.MAX;
import static com.rapiddweller.model.data.SimpleTypeDescriptor.MIN;
import static com.rapiddweller.model.data.SimpleTypeDescriptor.PATTERN;

/**
 * Creates generators of simple types.<br/><br/>
 * @author Volker Bergmann
 */
public class SimpleTypeGeneratorFactory extends TypeGeneratorFactory<SimpleTypeDescriptor> {

  private static final SimpleTypeGeneratorFactory INSTANCE = new SimpleTypeGeneratorFactory();

  public static SimpleTypeGeneratorFactory getInstance() {
    return INSTANCE;
  }

  protected SimpleTypeGeneratorFactory() {
  }

  @Override
  protected Generator<?> createExplicitGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness,
                                                 BeneratorContext context) {
    Generator<?> generator = super.createExplicitGenerator(descriptor, uniqueness, context);
    if (generator == null) {
      generator = createConstantGenerator(descriptor, context);
    }
    if (generator == null) {
      generator = createValuesGenerator(descriptor, uniqueness, context);
    }
    if (generator == null) {
      generator = createPatternGenerator(descriptor, uniqueness, context);
    }
    return generator;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  protected Generator<?> createSpecificGenerator(SimpleTypeDescriptor descriptor, String instanceName,
                                                 boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
    Generator<?> generator = InstanceGeneratorFactory.createConfiguredDefaultGenerator(
        instanceName, uniqueness, context);
    if (generator == null && nullable && shouldNullifyEachNullable(context)) {
      generator = new ConstantGenerator(null, getGeneratedType(descriptor));
    }
    return generator;
  }

  @Nullable
  protected static Generator<?> createValuesGenerator(
      SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    PrimitiveType primitiveType = descriptor.getPrimitiveType();
    Class<?> targetType = (primitiveType != null ? primitiveType.getJavaType() : String.class);
    String valueSpec = descriptor.getValues();
    if (valueSpec == null) {
      return null;
    }
    if ("".equals(valueSpec)) {
      return new ConstantGenerator<>("");
    }
    try {
      Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
      return context.getGeneratorFactory().createFromWeightedLiteralList(valueSpec, targetType, distribution, uniqueness.isUnique());
    } catch (ParseException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Error parsing samples: " + valueSpec, e);
    }
  }

  protected static Generator<String> createPatternGenerator(SimpleTypeDescriptor type, Uniqueness uniqueness,
                                                            BeneratorContext context) {
    String pattern = type.getPattern();
    if (pattern != null) {
      return createStringGenerator(type, uniqueness, context);
    } else {
      return null;
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Nullable
  protected static Generator<?> createConstantGenerator(SimpleTypeDescriptor descriptor, BeneratorContext context) {
    Generator<?> generator = null;
    // check for constant
    String constant = descriptor.getConstant();
    if ("".equals(constant)) {
      generator = new ConstantGenerator<>("");
    } else if (constant != null) {
      Object value = LiteralParserConverter.parse(constant);
      PrimitiveType primitiveType = descriptor.getPrimitiveType();
      if (primitiveType != null) {
        value = AnyConverter.convert(value, primitiveType.getJavaType());
      }
      generator = new ConstantGenerator(value);
    }
    return generator;
  }

  @Override
  protected Generator<?> createHeuristicGenerator(
      SimpleTypeDescriptor descriptor, String instanceName, Uniqueness uniqueness, BeneratorContext context) {
    Generator<?> generator = createTypeGenerator(descriptor, uniqueness, context);
    if (generator == null) {
      generator = createStringGenerator(descriptor, uniqueness, context);
    }
    return generator;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected Generator<?> createSourceGenerator(
      SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    String source = descriptor.getSource();
    if (source == null) {
      return null;
    }
    String selector = descriptor.getSelector();
    String subSelector = descriptor.getSubSelector();
    Generator<?> generator;
    if (context.get(source) != null) {
      Object sourceObject = context.get(source);
      if (sourceObject instanceof StorageSystem) {
        if (!StringUtil.isEmpty(subSelector)) {
          generator = new DataSourceGenerator(((StorageSystem) sourceObject).query(subSelector, true, context));
          generator = WrapperFactory.applyHeadCycler(generator);
        } else {
          generator = new DataSourceGenerator(((StorageSystem) sourceObject).query(selector, true, context));
        }
      } else if (sourceObject instanceof Generator) {
        generator = (Generator<?>) sourceObject;
      } else if (sourceObject instanceof DataSource) {
        DataSource dataSource = (DataSource) sourceObject;
        generator = new DataSourceGenerator(dataSource);
      } else {
        throw BeneratorExceptionFactory.getInstance().illegalArgument("Not a supported source: " + sourceObject);
      }
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
    if (distribution != null) {
      generator = distribution.applyTo(generator, uniqueness.isUnique());
    }

    return generator;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
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
      } else {
        generator = new DataSourceGenerator(storage.queryEntities(descriptor.getName(), selector, context));
      }
    } else if (sourceObject instanceof Generator) {
      generator = (Generator<?>) sourceObject;
    } else {
      throw BeneratorExceptionFactory.getInstance().illegalArgument(
          "Source type not supported: " + sourceObject.getClass());
    }
    if (sourceSpec.isReference()) {
      generator = WrapperFactory.preventClosing(generator);
    }
    return generator;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Generator<?> createSimpleTypeCSVSourceGenerator(
      SimpleTypeDescriptor descriptor, String sourceName, Uniqueness uniqueness, BeneratorContext context) {
    String sourceUri = context.resolveRelativeUri(sourceName);
    Generator<?> generator;
    char separator = DescriptorUtil.getSeparator(descriptor, context);
    boolean rowBased = (descriptor.isRowBased() == null || descriptor.isRowBased());
    String encoding = descriptor.getEncoding();
    if (encoding == null) {
      encoding = context.getDefaultEncoding();
    }
    Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);

    String dataset = descriptor.getDataset();
    String nesting = descriptor.getNesting();
    Converter<String, Object> scriptProcessor = createScriptProcessor(descriptor, context);
    if (dataset != null && nesting != null) {
      if (uniqueness.isUnique()) {
        generator = new SequencedDatasetCSVGenerator(sourceUri, separator, dataset, nesting,
            distribution, encoding, scriptProcessor);
      } else {
        generator = new WeightedDatasetCSVGenerator(Object.class, sourceUri, separator, dataset, nesting, false,
            encoding, scriptProcessor);
      }
    } else if (sourceName.toLowerCase().endsWith(".wgt.csv") || distribution instanceof IndividualWeight) {
      generator = new WeightedCSVSampleGenerator(
          Object.class, sourceUri, encoding, separator, scriptProcessor);
    } else {
      Generator<String[]> src = SourceFactory.createCSVGenerator(sourceUri, separator, encoding, true, rowBased);
      Converter<String[], Object> converterChain = new ConverterChain<>(
          new ArrayElementExtractor<>(String.class, 0),
          scriptProcessor);
      generator = WrapperFactory.applyConverter(src, converterChain);
      if (distribution != null) {
        generator = distribution.applyTo(generator, uniqueness.isUnique());
      }
    }
    return generator;
  }

  private static Generator<?> createSimpleTypeXLSSourceGenerator(
      SimpleTypeDescriptor descriptor, String sourceName, Uniqueness uniqueness, BeneratorContext context) {
    // TODO define common mechanism for file sources CSV, XLS, ... and entity, array, simple type
    Generator<?> generator;
    Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
    String sourceUri = context.resolveRelativeUri(sourceName);
    boolean formatted = isFormatted(descriptor);
    Generator<Object[]> src = SourceFactory.createXLSLineGenerator(sourceUri, descriptor.getSegment(), formatted);
    Converter<Object[], Object> converterChain = new ConverterChain<>(
        new ArrayElementExtractor<>(Object.class, 0),
        new ConditionalConverter(String.class::isInstance,
            createScriptProcessor(descriptor, context)));
    generator = WrapperFactory.applyConverter(src, converterChain);
    if (distribution != null) {
      generator = distribution.applyTo(generator, uniqueness.isUnique());
    }
    return generator;
  }

  private static Converter<String, Object> createScriptProcessor(SimpleTypeDescriptor descriptor,
                                                                 BeneratorContext context) {
    boolean sourceScripted;
    if (descriptor.isSourceScripted() != null) {
      sourceScripted = descriptor.isSourceScripted();
    } else {
      sourceScripted = context.isDefaultSourceScripted();
    }
    return new SourceScriptConverter(sourceScripted, context);
  }

  static class SourceScriptConverter extends ThreadSafeConverter<String, Object> {

    private final boolean sourceScripted;
    private final BeneratorContext context;

    public SourceScriptConverter(boolean sourceScripted, BeneratorContext context) {
      super(String.class, Object.class);
      this.sourceScripted = sourceScripted;
      this.context = context;
    }

    @Override
    public Object convert(String sourceValue) throws ConversionException {
      if (sourceScripted) {
        return ScriptConverterForStrings.convert(sourceValue, context);
      } else {
        return sourceValue;
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Generator<?> createTypeGenerator(
      SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    if (descriptor instanceof UnionSimpleTypeDescriptor) {
      return createUnionTypeGenerator((UnionSimpleTypeDescriptor) descriptor, context);
    }
    PrimitiveType primitiveType = descriptor.getPrimitiveType();
    if (primitiveType == null) {
      return null;
    }
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
    } else if (Time.class == targetType) {
      return createTimeGenerator(descriptor, uniqueness, context);
    } else if (Timestamp.class == targetType) {
      return createTimestampGenerator(descriptor, uniqueness, context);
    } else if (byte[].class == targetType) {
      return createByteArrayGenerator(descriptor, context);
    } else {
      return null;
    }
  }

  @Override
  protected Class<?> getGeneratedType(SimpleTypeDescriptor descriptor) {
    PrimitiveType primitiveType = descriptor.getPrimitiveType();
    if (primitiveType == null) {
      throw BeneratorExceptionFactory.getInstance().configurationError("No type configured for " + descriptor.getName());
    }
    return primitiveType.getJavaType();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
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
    Generator<Byte> byteGenerator = new AsByteGeneratorWrapper<>(new RandomIntegerGenerator(-128, 127, 1));
    return new ByteArrayGenerator(byteGenerator,
        DescriptorUtil.getMinLength(descriptor), DescriptorUtil.getMaxLength(descriptor, context.getDefaultsProvider().defaultMaxLength()));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Generator<Timestamp> createTimestampGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    Generator<Date> source = createDateGenerator(descriptor, uniqueness, context);
    Converter<Date, Timestamp> converter = (Converter) new AnyConverter<>(Timestamp.class);
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

  private Generator<Date> createTimeGenerator(SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    Time min = parseTime(descriptor, MIN, null);
    Time max = parseTime(descriptor, MAX, null);
    long granularity = parseDateGranularity(descriptor);
    Distribution distribution = FactoryUtil.getDistribution(
        descriptor.getDistribution(), uniqueness, true, context);
    return context.getGeneratorFactory().createDateGenerator(min, max, granularity, distribution);
  }

  private static Generator<Character> createCharacterGenerator(
      SimpleTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    String pattern = descriptor.getPattern();
    if (pattern == null) {
      pattern = ".";
    }
    Locale locale = descriptor.getLocale();
    GeneratorFactory generatorFactory = context.getGeneratorFactory();
    return generatorFactory.createCharacterGenerator(pattern, locale, uniqueness.isUnique());
  }

  private Time parseTime(SimpleTypeDescriptor descriptor, String detailName, Time defaultTime) {
    String detail = (String) descriptor.getDeclaredDetailValue(detailName);
    try {
      if (detail != null) {
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        return new Time(timeFormat.parse(detail).getTime());
      } else {
        return defaultTime;
      }
    } catch (java.text.ParseException e) {
      logger.error("Error parsing date " + detail, e);
      return defaultTime;
    }
  }

  private Date parseDate(SimpleTypeDescriptor descriptor, String detailName, Date defaultDate) {
    String detail = (String) descriptor.getDeclaredDetailValue(detailName);
    try {
      if (detail != null) {
        DateFormat dateFormat = DescriptorUtil.getPatternAsDateFormat(descriptor);
        return dateFormat.parse(detail);
      } else {
        return defaultDate;
      }
    } catch (java.text.ParseException e) {
      logger.error("Error parsing date " + detail, e);
      return defaultDate;
    }
  }

  private static long parseDateGranularity(SimpleTypeDescriptor descriptor) {
    String detail = (String) descriptor.getDeclaredDetailValue(DescriptorConstants.ATT_GRANULARITY);
    if (detail != null) {
      return DateString2DurationConverter.defaultInstance().convert(detail);
    } else {
      return 24 * 3600 * 1000L;
    }
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
    Integer maxLength = DescriptorUtil.getMaxLength(descriptor, null);

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

}