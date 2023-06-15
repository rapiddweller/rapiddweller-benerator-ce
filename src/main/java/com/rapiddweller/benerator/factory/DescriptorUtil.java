/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.expression.GlobalMaxCountExpression;
import com.rapiddweller.benerator.parser.ModelParser;
import com.rapiddweller.benerator.primitive.DynamicCountGenerator;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.util.ExpressionBasedGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.exception.ParseException;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.common.converter.ConverterChain;
import com.rapiddweller.common.converter.FormatFormatConverter;
import com.rapiddweller.common.converter.String2NumberConverter;
import com.rapiddweller.common.converter.ToStringConverter;
import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.common.validator.AndValidator;
import com.rapiddweller.common.validator.bean.BeanConstraintValidator;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import com.rapiddweller.script.BeanSpec;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.PrimitiveType;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.ExpressionUtil;
import com.rapiddweller.script.expression.MinExpression;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import javax.validation.ConstraintValidator;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;


import static com.rapiddweller.benerator.engine.DescriptorConstants.COMPONENT_TYPES;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_LIST;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_VALUE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_VARIABLE;
import static com.rapiddweller.model.data.SimpleTypeDescriptor.MAX_LENGTH;
import static com.rapiddweller.model.data.SimpleTypeDescriptor.PATTERN;

/**
 * Utility class for parsing and combining descriptor settings.<br/><br/>
 * Created at 31.12.2008 09:28:28
 * @author Volker Bergmann
 * @since 0.5.7
 */
public class DescriptorUtil {

  private DescriptorUtil() {
  }

  public static Object convertType(Object sourceValue, SimpleTypeDescriptor targetType) {
    if (sourceValue == null) {
      return null;
    }
    PrimitiveType primitive = targetType.getPrimitiveType();
    if (primitive == null) {
      primitive = PrimitiveType.STRING;
    }
    Class<?> javaType = primitive.getJavaType();
    return AnyConverter.convert(sourceValue, javaType);
  }

  public static boolean isWrappedSimpleType(ComplexTypeDescriptor complexType) {
    List<ComponentDescriptor> components = complexType.getComponents();
    return (components.size() == 1
        && ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(components.get(0).getName()));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Generator<?> createConvertingGenerator(TypeDescriptor descriptor,
                                                       Generator<?> generator, BeneratorContext context) {
    Converter<?, ?> converter = DescriptorUtil.getConverter(descriptor.getConverter(), context);
    if (converter != null) {
      if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
        BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
      }
      return WrapperFactory.applyConverter((Generator) generator, converter);
    }
    return generator;
  }

  public static Generator<?> getGeneratorByName(TypeDescriptor descriptor, BeneratorContext context) {
    String generatorSpec = descriptor.getGenerator();
    try {
      Generator<?> generator = null;
      if (generatorSpec != null) {
        if (generatorSpec.startsWith("{") && generatorSpec.endsWith("}")) {
          generatorSpec = generatorSpec.substring(1, generatorSpec.length() - 1);
        }
        BeanSpec generatorBeanSpec = DatabeneScriptParser.resolveBeanSpec(generatorSpec, context);
        generator = (Generator<?>) generatorBeanSpec.getBean();
        FactoryUtil.mapDetailsToBeanProperties(descriptor, generator, context);
        if (generatorBeanSpec.isReference()) {
          generator = WrapperFactory.preventClosing(generator);
        }
      }
      return generator;
    } catch (SyntaxError e) {
      throw BeneratorExceptionFactory.getInstance().syntaxErrorForText(
          "Error in generator spec", e, generatorSpec);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Validator getValidator(String validatorSpec, BeneratorContext context) {
    try {
      if (StringUtil.isEmpty(validatorSpec)) {
        return null;
      }

      Validator result = null;
      Expression[] beanExpressions = DatabeneScriptParser.parseBeanSpecList(validatorSpec);
      Object[] beans = ExpressionUtil.evaluateAll(beanExpressions, context);
      for (Object bean : beans) {
        // check validator type
        Validator validator;
        if (bean instanceof Validator) {
          validator = (Validator<?>) bean;
        } else if (bean instanceof ConstraintValidator) {
          validator = new BeanConstraintValidator((ConstraintValidator) bean);
        } else {
          throw BeneratorExceptionFactory.getInstance().configurationError("Unknown validator type: " + BeanUtil.simpleClassName(bean));
        }

        // compose one or more validators
        if (result == null) {
          // if it is the first or even only validator, simply use it
          result = validator;
        } else if (result instanceof AndValidator) {
          // else compose all validators to an AndValidator
          ((AndValidator) result).add(validator);
        } else {
          result = new AndValidator(result, validator);
        }
      }
      result = BeneratorFactory.getInstance().configureValidator(result, context);
      return result;
    } catch (ParseException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Invalid validator definition", e);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Converter getConverter(String converterSpec, BeneratorContext context) {
    try {
      if (StringUtil.isEmpty(converterSpec)) {
        return null;
      }

      Converter result = null;
      Expression[] beanExpressions = DatabeneScriptParser.parseBeanSpecList(converterSpec);
      Object[] beans = ExpressionUtil.evaluateAll(beanExpressions, context);
      for (Object bean : beans) {
        Converter converter;
        if (bean instanceof java.text.Format) {
          converter = new FormatFormatConverter(Object.class, (java.text.Format) bean, false);
        } else if (bean instanceof Converter) {
          converter = (Converter) bean;
        } else {
          throw BeneratorExceptionFactory.getInstance().configurationError(bean + " is not an instance of " + Converter.class);
        }
        converter = BeneratorFactory.getInstance().configureConverter(converter, context);

        if (result == null) {
          result = converter;
        } else if (result instanceof ConverterChain) {
          ((ConverterChain) result).addComponent(converter);
        } else {
          result = new ConverterChain(result, converter);
        }
      }
      return result;
    } catch (ParseException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Error parsing converter spec: " + converterSpec, e);
    }
  }

  public static DateFormat getPatternAsDateFormat(TypeDescriptor descriptor) {
    String pattern = descriptor.getPattern();
    if (pattern != null) {
      return new SimpleDateFormat(pattern);
    } else {
      return TimeUtil.createDefaultDateFormat();
    }
  }

  public static Uniqueness getUniqueness(InstanceDescriptor descriptor, BeneratorContext context) {
    if (descriptor instanceof IdDescriptor) {
      return Uniqueness.ORDERED;
    } else if (isUnique(descriptor, context)) {
      return Uniqueness.SIMPLE;
    } else {
      return Uniqueness.NONE;
    }
  }

  public static boolean isUnique(InstanceDescriptor descriptor, BeneratorContext context) {
    Boolean unique = descriptor.isUnique();
    if (unique == null) {
      unique = context.getGeneratorFactory().defaultUnique();
    }
    return unique;
  }

  public static String getEncoding(TypeDescriptor type, BeneratorContext context) {
    String encoding = type.getEncoding();
    if (encoding == null) {
      encoding = context.getDefaultEncoding();
    }
    return encoding;
  }

  public static boolean isSourceScripted(ComplexTypeDescriptor descriptor, BeneratorContext context) {
    Boolean result = descriptor.isSourceScripted();
    if (result == null) {
      result = context.isDefaultSourceScripted();
    }
    return result;
  }

  public static char getSeparator(TypeDescriptor descriptor, BeneratorContext context) {
    char separator = (context != null ? context.getDefaultSeparator() : ',');
    if (!StringUtil.isEmpty(descriptor.getSeparator())) {
      if (descriptor.getSeparator().length() > 1) {
        throw BeneratorExceptionFactory.getInstance().configurationError("A CSV separator must be one character, but was: " + descriptor.getSeparator());
      }
      separator = descriptor.getSeparator().charAt(0);
    }
    return separator;
  }

  /** Calculates the 'count' value.
   *  @param descriptor the descriptor
   *  @return the 'count' value. If a global 'maxCount' was set too, it returns the minimum of 'count' and 'maxCount'.
   *  If no 'count' value was specified, it returns null. */
  @SuppressWarnings("unchecked")
  public static Expression<Long> getCount(InstanceDescriptor descriptor) {
    Expression<Long> result = descriptor.getCount();
    if (result != null) {
      Expression<Long> globalMaxCount = getGlobalMaxCount();
      result = new MinExpression<>(result, globalMaxCount);
    }
    return result;
  }

  public static Expression<Long> getMinCount(InstanceDescriptor descriptor) {
    return getMinCount(descriptor, 1L);
  }

  /** Calculates the 'minCount' value.
   *  @param descriptor the descriptor
   *  @return the 'minCount' value. If a global 'maxCount' was set too, it returns the minimum of 'minCount' and 'maxCount'.
   *  If no 'minCount' value was specified, it returns the value of the 'defaultMin' parameter. */
  @SuppressWarnings("unchecked")
  public static Expression<Long> getMinCount(InstanceDescriptor descriptor, Long defaultMin) {
    Expression<Long> result = null;
    if (descriptor.getCount() != null) {
      result = descriptor.getCount();
    } else if (descriptor.getMinCount() != null) {
      result = descriptor.getMinCount();
    } else if (defaultMin != null) {
      result = new ConstantExpression<>(defaultMin);
    } else {
      return new ConstantExpression<>(null);
    }
    Expression<Long> globalMaxCount = getGlobalMaxCount();
    if (!ExpressionUtil.isNull(globalMaxCount)) {
      result = new MinExpression<>(result, globalMaxCount);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static Expression<Long> getMaxCount(InstanceDescriptor descriptor, Long defaultMax) {
    Expression<Long> result = null;
    if (descriptor.getCount() != null) {
      result = descriptor.getCount();
    } else if (descriptor.getMaxCount() != null) {
      result = descriptor.getMaxCount();
    } else if (descriptor instanceof ComponentDescriptor && defaultMax != null) {
      result = new ConstantExpression<>(defaultMax);
    } else {
      return getGlobalMaxCount();
    }
    Expression<Long> globalMaxCount = getGlobalMaxCount();
    if (!ExpressionUtil.isNull(globalMaxCount)) {
      result = new MinExpression<>(result, globalMaxCount);
    }
    return result;
  }

  private static Expression<Long> getGlobalMaxCount() {
    return new GlobalMaxCountExpression();
  }

  public static Expression<Long> getCountGranularity(InstanceDescriptor descriptor) {
    return (descriptor.getCountGranularity() != null ?
        descriptor.getCountGranularity() :
        new ConstantExpression<>(1L));
  }

  public static Converter<String, String> createStringScriptConverter(BeneratorContext context) {
    return new ConverterChain<>(
        new ScriptConverterForStrings(context),
        new ToStringConverter(null)
    );
  }

  @NotNull
  public static Generator<Long> createDynamicCountGenerator(
      final InstanceDescriptor descriptor, Long defaultMin, Long defaultMax, boolean resetToMin,
      BeneratorContext context) {
    Expression<Long> count = DescriptorUtil.getCount(descriptor);
    if (count != null) {
      if (count.isConstant()) {
        return new ConstantGenerator<>(count.evaluate(context));
      } else {
        return new ExpressionBasedGenerator<>(count, Long.class);
      }
    } else {
      final Expression<Long> minCount = DescriptorUtil.getMinCount(descriptor, defaultMin);
      final Expression<Long> maxCount = DescriptorUtil.getMaxCount(descriptor, defaultMax);
      final Expression<Long> countGranularity = DescriptorUtil.getCountGranularity(descriptor);
      if (minCount.isConstant() && maxCount.isConstant()) {
        // if minCount and maxCount are constants of the same value,
        // then create a generator for a constant value
        Long minCountValue = minCount.evaluate(context);
        Long maxCountValue = maxCount.evaluate(context);
        if (NullSafeComparator.equals(minCountValue, maxCountValue)) {
          return new ConstantGenerator<>(minCountValue);
        }
      }
      // if no simplification was found above, then create a fully featured distributed count generator
      final Expression<Distribution> countDistribution =
          FactoryUtil.getDistributionExpression(descriptor.getCountDistribution(), Uniqueness.NONE, true);
      return new DynamicCountGenerator(minCount, maxCount, countGranularity, countDistribution,
          ExpressionUtil.constant(false), resetToMin);
    }
  }

  @NotNull
  public static Generator<Long> createDynamicCountGenerator(
      Expression<Long> count, Expression<Long> minCount, Expression<Long> maxCount,
      Expression<Long> countGranularity, Expression<String> countDistributionEx,
      Long defaultMin, Long defaultMax, boolean isComponent, boolean resetToMin, BeneratorContext context) {
    if (count != null) {
      if (count.isConstant()) {
        return new ConstantGenerator<>(count.evaluate(context));
      } else {
        return new ExpressionBasedGenerator<>(count, Long.class);
      }
    } else {
      minCount = getMinCount(count, minCount, defaultMin);
      maxCount = getMaxCount(count, maxCount, defaultMax, isComponent);
      if (minCount.isConstant() && maxCount.isConstant()) {
        // if minCount and maxCount are constants of the same value,
        // then create a generator for a constant value
        Long minCountValue = minCount.evaluate(context);
        Long maxCountValue = maxCount.evaluate(context);
        if (NullSafeComparator.equals(minCountValue, maxCountValue)) {
          return new ConstantGenerator<>(minCountValue);
        }
      }
      if (countDistributionEx == null) {
        countDistributionEx = new ConstantExpression<>("random");
      }
      // if no simplification was found above, then create a fully featured distributed count generator
      final Expression<Distribution> countDistribution = FactoryUtil.getDistributionExpression(
          countDistributionEx.evaluate(context), Uniqueness.NONE, true);
      return new DynamicCountGenerator(minCount, maxCount, countGranularity, countDistribution,
          ExpressionUtil.constant(false), resetToMin);
    }
  }

  public static Expression<Long> getMinCount(Expression<Long> count, Expression<Long> minCount, Long defaultMin) {
    Expression<Long> result = null;
    if (count != null) {
      result = count;
    } else if (minCount != null) {
      result = minCount;
    } else if (defaultMin != null) {
      result = new ConstantExpression<>(defaultMin);
    } else {
      return new ConstantExpression<>(null);
    }
    Expression<Long> globalMaxCount = getGlobalMaxCount();
    if (!ExpressionUtil.isNull(globalMaxCount)) {
      result = new MinExpression<>(result, globalMaxCount);
    }
    return result;
  }

  public static Expression<Long> getMaxCount(Expression<Long> count, Expression<Long> maxCount,
                                             Long defaultMax, boolean isComponent) {
    Expression<Long> result;
    if (count != null) {
      result = count;
    } else if (maxCount != null) {
      result = maxCount;
    } else if (isComponent && defaultMax != null) {
      result = new ConstantExpression<>(defaultMax);
    } else {
      return getGlobalMaxCount();
    }
    Expression<Long> globalMaxCount = getGlobalMaxCount();
    if (!ExpressionUtil.isNull(globalMaxCount)) {
      result = new MinExpression<>(result, globalMaxCount);
    }
    return result;
  }

  @Nullable
  public static <T extends Number> T getNumberDetail(SimpleTypeDescriptor descriptor, String detailName, Class<T> targetType) {
    String detailValue = (String) descriptor.getDetailValue(detailName);
    try {
      return (detailValue != null ? new String2NumberConverter<>(targetType).convert(detailValue) : null);
    } catch (ConversionException e) {
      throw BeneratorExceptionFactory.getInstance().configurationError("Error converting '" + detailValue + "' to a number", e);
    }
  }

  public static void parseComponentConfig(Element element, TypeDescriptor type, BeneratorContext context) {
    ModelParser modelParser = new ModelParser(context, true);
    int valueCount = 0;
    for (Element child : XMLUtil.getChildElements(element)) {
      String childType = XMLUtil.localName(child);
      if (EL_VARIABLE.equals(childType)) {
        modelParser.parseVariable(child);
      } else if (COMPONENT_TYPES.contains(childType)) {
        modelParser.getPartParser().parseComponentGeneration(child, (ComplexTypeDescriptor) type);
      } else if (EL_VALUE.equals(childType)) {
        modelParser.parseSimpleTypeArrayElement(child, (ArrayTypeDescriptor) type, valueCount++);
      } else if (EL_LIST.equals(childType)) {
        modelParser.getItemListParser().parse(child, (ComplexTypeDescriptor) type);
      }
    }
  }

  public static boolean isNullable(InstanceDescriptor descriptor, BeneratorContext context) {
    Boolean nullable = descriptor.isNullable();
    if (nullable != null) {
      return nullable;
    }
    Double nullQuota = descriptor.getNullQuota();
    if (nullQuota != null && nullQuota > 0) {
      return true;
    }
    TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
    if (descriptor.getNullQuota() == null && typeDescriptor != null
          && (typeDescriptor.getSource() != null || typeDescriptor.getGenerator() != null)) {
      return false;
    }
    return context.getDefaultsProvider().defaultNullable();
  }

  public static boolean shouldNullifyEachNullable(
      InstanceDescriptor descriptor, BeneratorContext context) {
    // nullQuota == 1?
    Double nullQuota = descriptor.getNullQuota();
    if (nullQuota != null && nullQuota == 1.) {
      return true;
    }
    // nullable?
    Boolean nullable = descriptor.isNullable();
    if (nullable != null && !nullable) {
      // nullable defaults to true
      return false;
    }
    if (context.getDefaultsProvider().defaultNullQuota() < 1) {
      return false; // if the factory requires nullification, it overrides the context setting
    }
    return (!descriptor.overwritesParent() && context.isDefaultNull());
  }

  // helpers ---------------------------------------------------------------------------------------------------------

  protected static <T> Generator<T> wrapWithProxy(Generator<T> generator, TypeDescriptor descriptor) {
    generator = processOffset(generator, descriptor);
    generator = processCyclic(generator, descriptor);
    return generator;
  }

  public static <T> Generator<T> processCyclic(Generator<T> generator,
                                               TypeDescriptor descriptor) {
    boolean cyclic = descriptor.isCyclic() != null && descriptor.isCyclic();
    if (cyclic) {
      generator = WrapperFactory.applyCycler(generator);
    }
    return generator;
  }

  public static <T> Generator<T> processOffset(Generator<T> generator, TypeDescriptor descriptor) {
    int offset = getOffset(descriptor);
    if (offset > 0) {
      generator = WrapperFactory.applyOffset(generator, offset);
    }
    return generator;
  }

  protected static int getOffset(TypeDescriptor descriptor) {
    Integer offset = descriptor.getOffset();
    return (offset != null ? offset : 0);
  }

  protected static Integer getMinLength(SimpleTypeDescriptor descriptor) {
    Integer minLength = descriptor.getMinLength();
    if (minLength == null) {
      minLength = 0;
    }
    return minLength;
  }

  /** Scans the {@link SimpleTypeDescriptor} hierarchy from bottom to top (child -> parent)
   *  and returns the first maxLength setting it finds. If none is found, it returns the defaultValue. */
  public static Integer getMaxLength(SimpleTypeDescriptor descriptor, Integer defaultValue) {
    Integer maxLength = null;
    SimpleTypeDescriptor tmp = descriptor;
    while (maxLength == null && tmp != null) {
      maxLength = (Integer) tmp.getDetailValue(MAX_LENGTH);
      tmp = tmp.getParent();
    }
    if (maxLength == null) {
      maxLength = defaultValue;
    }
    return maxLength;
  }

  @Nullable
  public static Generator<?> createNullQuotaOneGenerator(InstanceDescriptor descriptor, BeneratorContext context) {
    // check if nullQuota is 1
    Double nullQuota = descriptor.getNullQuota();
    if (nullQuota != null && nullQuota == 1.) {
      return MetaGeneratorFactory.createNullGenerator(descriptor.getTypeDescriptor(), context);
    } else {
      return null;
    }
  }

  public static TypeDescriptor deriveType(String name, TypeDescriptor parentType) {
    if (parentType instanceof SimpleTypeDescriptor) {
      return new SimpleTypeDescriptor(name, parentType.getProvider(), (SimpleTypeDescriptor) parentType);
    } else if (parentType instanceof ComplexTypeDescriptor) {
      return new ComplexTypeDescriptor(name, parentType.getProvider(), (ComplexTypeDescriptor) parentType);
    } else if (parentType instanceof ArrayTypeDescriptor) {
      return new ArrayTypeDescriptor(name, parentType.getProvider(), (ArrayTypeDescriptor) parentType);
    } else {
      throw BeneratorExceptionFactory.getInstance().programmerUnsupported("Cannot derive child type from " + parentType.getClass());
    }
  }

  public static void applyValues(Set<String> values, SimpleTypeDescriptor descriptor) {
    if (!CollectionUtil.isEmpty(values)) {
      StringBuilder builder = new StringBuilder();
      for (String value : values) {
        if (builder.length() > 0) {
          builder.append(',');
        }
        builder.append(value);
      }
      descriptor.setValues(builder.toString());
    }
  }

}
