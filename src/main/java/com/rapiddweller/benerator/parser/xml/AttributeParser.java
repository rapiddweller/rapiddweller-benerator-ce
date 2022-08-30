/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.ConstantAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ConverterAttribute;
import com.rapiddweller.benerator.engine.parser.attr.CyclicAttribute;
import com.rapiddweller.benerator.engine.parser.attr.DistributionAttribute;
import com.rapiddweller.benerator.engine.parser.attr.EncodingAttribute;
import com.rapiddweller.benerator.engine.parser.attr.FilterAttribute;
import com.rapiddweller.benerator.engine.parser.attr.GeneratorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ModeAttribute;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.attr.NullQuotaAttribute;
import com.rapiddweller.benerator.engine.parser.attr.NullableAttribute;
import com.rapiddweller.benerator.engine.parser.attr.OffsetAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ScopeAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ScriptAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SelectorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SeparatorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SimpleTypeAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceFormattedAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceScriptedAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SubSelectorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.UniqueAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ValidatorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ValuesAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.MinMaxParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.HF;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.DoubleParser;
import com.rapiddweller.common.parser.NonNegativeIntegerParser;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.WeightedSample;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.common.xml.XMLAssert.allowOnlyInContextOf;
import static com.rapiddweller.common.xml.XMLAssert.mutuallyExcludeAttributes;

/**
 * Parses &lt;attribute&gt; elements.<br/><br/>
 * Created: 14.12.2021 05:07:20
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class AttributeParser extends AbstractComponentParser {

  // name
  public final NameAttribute name = new NameAttribute(BeneratorErrorIds.SYN_ATTR_NAME, false, false);
  // root attributes
  public static final SimpleTypeAttribute TYPE = new SimpleTypeAttribute(SYN_ATTR_TYPE);

  public static final AttrInfo<String> MODE = new ModeAttribute(SYN_ATTR_MODE);
  public static final AttrInfo<String> SCOPE = new ScopeAttribute(SYN_ATTR_SCOPE);
  public static final AttrInfo<Expression<Long>> OFFSET = new OffsetAttribute(SYN_ATTR_OFFSET);
  public static final AttrInfo<Expression<Boolean>> FILTER = new FilterAttribute(SYN_ATTR_FILTER);
  public static final AttrInfo<Boolean> UNIQUE = new UniqueAttribute(SYN_ATTR_UNIQUE);
  public static final AttrInfo<String> UNIQUE_KEY = new AttrInfo<>(ATT_UNIQUE_KEY, false, SYN_ATTR_UNIQUE_KEY, null); // TODO what's this?

  public static final AttrInfo<String> CONSTANT = new ConstantAttribute(SYN_ATTR_CONSTANT);
  public static final AttrInfo<WeightedSample<?>[]> VALUES = new ValuesAttribute(SYN_ATTR_VALUES);
  public static final AttrInfo<String> SCRIPT = new ScriptAttribute(SYN_ATTR_SCRIPT);
  public static final AttrInfo<Expression<?>> GENERATOR = new GeneratorAttribute(SYN_ATTR_GENERATOR);

  public static final AttrInfo<String> PATTERN = new AttrInfo<>(ATT_PATTERN, false, SYN_ATTR_PATTERN, null);
  public static final AttrInfo<Integer> MIN_LENGTH = new AttrInfo<>(ATT_MIN_LENGTH, false, SYN_ATTR_MIN_LENGTH, new NonNegativeIntegerParser());
  public static final AttrInfo<Integer> MAX_LENGTH = new AttrInfo<>(ATT_MAX_LENGTH, false, SYN_ATTR_MAX_LENGTH, new NonNegativeIntegerParser());
  public static final AttrInfo<Expression<Double>> NULL_QUOTA = new NullQuotaAttribute(SYN_ATTR_NULL_QUOTA);

  public static final AttrInfo<String> SOURCE = new SourceAttribute(SYN_ATTR_SOURCE, false);
  public static final AttrInfo<Expression<Boolean>> SOURCE_SCRIPTED = new SourceScriptedAttribute(SYN_ATTR_SOURCE_SCRIPTED);
  public static final AttrInfo<Expression<String>> ENCODING = new EncodingAttribute(SYN_ATTR_ENCODING);
  public static final AttrInfo<String> SEGMENT = new AttrInfo<>(ATT_SEGMENT, false, SYN_ATTR_SEGMENT, null);
  public static final AttrInfo<Expression<Character>> SEPARATOR = new SeparatorAttribute(SYN_ATTR_SEPARATOR, null);
  public static final AttrInfo<Expression<Boolean>> CONDITION = new AttrInfo<>(ATT_CONDITION, false, SYN_ATTR_CONDITION, new ScriptParser<>(Boolean.class));
  public static final AttrInfo<String> SELECTOR = new SelectorAttribute(SYN_ATTR_SELECTOR);
  public static final AttrInfo<String> SUB_SELECTOR = new SubSelectorAttribute(SYN_ATTR_SUB_SELECTOR);
  public static final AttrInfo<Boolean> ROW_BASED = new AttrInfo<>(ATT_ROW_BASED, false, SYN_ATTR_ROW_BASED, new BooleanParser());
  public static final AttrInfo<String> FORMAT = new SourceFormattedAttribute(SYN_ATTR_FORMAT);
  public static final AttrInfo<String> EMPTY_MARKER = new AttrInfo<>(ATT_EMPTY_MARKER, false, SYN_ATTR_EMPTY_MARKER, null);

  public static final AttrInfo<Boolean> NULLABLE = new NullableAttribute(SYN_ATTR_NULLABLE);
  public static final AttrInfo<Double> TRUE_QUOTA = new AttrInfo<>(ATT_TRUE_QUOTA, false, SYN_ATTR_TRUE_QUOTA, new DoubleParser(0., 1.));

  public static final AttrInfo<Expression<Comparable>> MIN = new AttrInfo<>(ATT_MIN, false, SYN_ATTR_MIN, new ScriptableParser<>(new MinMaxParser("min"), Comparable.class));
  public static final AttrInfo<Boolean> MIN_INCLUSIVE = new AttrInfo<>(ATT_MIN_INCLUSIVE, false, SYN_ATTR_MIN_INCLUSIVE, new BooleanParser(), "true");
  public static final AttrInfo<Expression<Comparable>> MAX = new AttrInfo<>(ATT_MAX, false, SYN_ATTR_MAX,new ScriptableParser<>(new MinMaxParser("max"), Comparable.class));
  public static final AttrInfo<Boolean> MAX_INCLUSIVE = new AttrInfo<>(ATT_MAX_INCLUSIVE, false, SYN_ATTR_MAX_INCLUSIVE, new BooleanParser(), "true");
  public static final AttrInfo<Expression<Double>> GRANULARITY = new AttrInfo<>(ATT_GRANULARITY, false, SYN_ATTR_GRANULARITY, new ScriptableParser<>(new DoubleParser(0., null)));
  public static final AttrInfo<String> DISTRIBUTION = new DistributionAttribute(SYN_ATTR_DISTRIBUTION);

  public static final AttrInfo<Boolean> DATASET = new AttrInfo<>(ATT_DATASET, false, SYN_ATTR_DATASET, null);
  public static final AttrInfo<String> NESTING = new AttrInfo<>(ATT_NESTING, false, SYN_ATTR_NESTING, null);
  public static final AttrInfo<String> LOCALE = new AttrInfo<>(ATT_LOCALE, false, SYN_ATTR_LOCALE, new IdParser());

  // postprocessor attributes
  public static final AttrInfo<String> CONVERTER = new ConverterAttribute(SYN_ATTR_CONVERTER);
  public static final AttrInfo<String> VALIDATOR = new ValidatorAttribute(SYN_ATTR_VALIDATOR);
  public static final AttrInfo<Boolean> CYCLIC = new CyclicAttribute(SYN_ATTR_CYCLIC);
  public static final AttrInfo<String> MAP = new AttrInfo<>(ATT_MAP, false, SYN_ATTR_MAP, null);

  private final AttrInfoSupport attrSupport = new AttrInfoSupport(SYN_ATTR_ILLEGAL_ATTR,
      new AttributeValidator(),
      name, TYPE,
      MODE, SCOPE, OFFSET, CONDITION, FILTER, UNIQUE, UNIQUE_KEY,
      CONSTANT, VALUES, PATTERN, SCRIPT, GENERATOR, MIN_LENGTH, MAX_LENGTH, NULL_QUOTA,
      SOURCE, SOURCE_SCRIPTED, ENCODING, SEGMENT, SEPARATOR, SELECTOR, SUB_SELECTOR, ROW_BASED, FORMAT, EMPTY_MARKER,
      NULLABLE, TRUE_QUOTA,
      MIN, MIN_INCLUSIVE, MAX, MAX_INCLUSIVE, GRANULARITY, DISTRIBUTION,
      DATASET, NESTING, LOCALE,
      CONVERTER, VALIDATOR, CYCLIC, MAP
  );

  public AttributeParser(BeneratorContext context, boolean nameRequired) {
    super(context);
    name.setRequired(nameRequired);
  }

  public PartDescriptor parse(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    attrSupport.validate(element);
    XMLAssert.assertElementName("attribute", element, SYN_ATTR_NAME);
    PartDescriptor result;
    if (descriptor != null) {
      result = new PartDescriptor(descriptor.getName(), descriptorProvider, descriptor.getType());
    } else {
      String nameValue = this.name.parse(element);
      String typeName = TYPE.parse(element);
      XMLAssert.assertNoTextContent(element, SYN_ATTR);
      result = new PartDescriptor(nameValue, descriptorProvider, typeName);
    }
    mapInstanceDetails(element, false, result);
    applyDefaultCounts(result);
    if (owner != null) {
      ComponentDescriptor parentComponent = owner.getComponent(result.getName());
      if (parentComponent != null) {
        TypeDescriptor parentType = parentComponent.getTypeDescriptor();
        result.getLocalType(false).setParent(parentType);
      }
      owner.setComponent(result);
    }
    return result;
  }

  static class AttributeValidator implements Validator<Element> {

    @Override
    public boolean valid(Element element) {
      checkSourceRelatedElements(element);
      checkMinMax(element);
      checkLengths(element);
      DescriptorParserUtil.validateGeneratorAttribute(element, BeneratorErrorIds.SYN_ATTR_GENERATOR);
      return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void checkMinMax(Element element) {
      Expression<Comparable> minEx = MIN.parse(element);
      Expression<Comparable> maxEx = MAX.parse(element);
      if (minEx != null && maxEx != null && minEx.isConstant() && maxEx.isConstant()) {
        boolean minInclusive = MIN_INCLUSIVE.parse(element);
        boolean maxInclusive = MAX_INCLUSIVE.parse(element);
        Comparable min = minEx.evaluate(null);
        Comparable max = maxEx.evaluate(null);
        if (min instanceof Number && max instanceof Number) {
          min = ((Number) min).doubleValue();
          max = ((Number) max).doubleValue();
        }
        int comparison = min.compareTo(max);
        if (comparison > 0) {
          throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
              "min (" + format(min) + ") is greater than max (" + format(max) + ")",
              null, SYN_ATTR_MAX, element);
        } else if (comparison == 0) {
          if (!maxInclusive) {
            throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
                "min equals max (" + format(max) + "), but max is not inclusive",
                null, SYN_ATTR_MAX_INCLUSIVE, element);
          } else if (!minInclusive) {
            throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
                "min equals max (" + format(max) + "), but min is not inclusive",
                null, SYN_ATTR_MIN_INCLUSIVE, element);
          }
        }
      }
    }

    private String format(Object value) {
      if (value instanceof Number) {
        return HF.format(((Number) value).doubleValue());
      } else {
        return String.valueOf(value);
      }
    }

    private void checkLengths(Element element) {
      Integer minLength = MIN_LENGTH.parse(element);
      if (minLength != null) {
        Integer maxLength = MAX_LENGTH.parse(element);
        if (maxLength != null && minLength > maxLength) {
          throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
              "minLength (" + minLength + ") is greater than maxLength (" + maxLength + ")",
              null, SYN_ATTR_MAX_LENGTH, element);
        }
      }
    }

    private void checkSourceRelatedElements(Element element) {
      // mutually exclude the root generator attributes
      mutuallyExcludeAttributes(SYN_ATTR_ROOT_INFO, element,
          ATT_CONSTANT, ATT_VALUES, ATT_PATTERN, ATT_SCRIPT, ATT_GENERATOR);
      mutuallyExcludeAttributes(SYN_ATTR_ROOT_INFO, element, ATT_CONSTANT, ATT_MIN_LENGTH);
      mutuallyExcludeAttributes(SYN_ATTR_ROOT_INFO, element, ATT_CONSTANT, ATT_MAX_LENGTH);
      // min/maxInclude only make sense with their min/max reference value
      allowOnlyInContextOf(ATT_MIN, SYN_ATTR_MIN_INCLUSIVE, element, ATT_MIN_INCLUSIVE);
      allowOnlyInContextOf(ATT_MAX, SYN_ATTR_MAX_INCLUSIVE, element, ATT_MAX_INCLUSIVE);
      // check the attributes that only make sense when using a 'source'
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_ENCODING, element, ATT_ENCODING);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_SEGMENT, element, ATT_SEGMENT);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_SEPARATOR, element, ATT_SEPARATOR);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_SELECTOR, element, ATT_SELECTOR);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_SUB_SELECTOR, element, ATT_SUB_SELECTOR);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_ROW_BASED, element, ATT_ROW_BASED);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_FORMAT, element, ATT_FORMAT);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_EMPTY_MARKER, element, ATT_EMPTY_MARKER);
    }

  }

}
