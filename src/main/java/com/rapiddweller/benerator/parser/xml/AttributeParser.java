/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SimpleTypeAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.CharacterParser;
import com.rapiddweller.common.parser.DoubleParser;
import com.rapiddweller.common.parser.EncodingParser;
import com.rapiddweller.common.parser.LongParser;
import com.rapiddweller.common.parser.NonNegativeIntegerParser;
import com.rapiddweller.common.parser.ValuesParser;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.script.Expression;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses &lt;attribute&gt; elements.<br/><br/>
 * Created: 14.12.2021 05:07:20
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class AttributeParser extends AbstractComponentParser {

  // name
  public final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_ATTR_NAME, false, false);
  // root attributes
  public static final SimpleTypeAttribute TYPE = new SimpleTypeAttribute(SYN_ATTR_TYPE);

  public static final AttrInfo<String> MODE = new AttrInfo<>(ATT_MODE, false, SYN_ATTR_MODE, null); // TODO
  public static final AttrInfo<String> SCOPE = new AttrInfo<>(ATT_SCOPE, false, SYN_ATTR_SCOPE, null);
  public static final AttrInfo<Expression<Long>> OFFSET = new AttrInfo<>(ATT_OFFSET, false, SYN_ATTR_OFFSET, new ScriptableParser<>(new LongParser()));
  public static final AttrInfo<String> CONDITION = new AttrInfo<>(ATT_CONDITION, false, SYN_ATTR_CONDITION, null);
  public static final AttrInfo<String> FILTER = new AttrInfo<>(ATT_FILTER, false, SYN_ATTR_FILTER, null);
  public static final AttrInfo<Boolean> UNIQUE = new AttrInfo<>(ATT_UNIQUE, false, SYN_ATTR_UNIQUE, new BooleanParser(), "false");
  public static final AttrInfo<String> UNIQUE_KEY = new AttrInfo<>(ATT_UNIQUE_KEY, false, SYN_ATTR_UNIQUE_KEY, null);

  public static final AttrInfo<String> CONSTANT = new AttrInfo<>(ATT_CONSTANT, false, SYN_ATTR_CONSTANT, null);
  public static final AttrInfo<String> VALUES = new AttrInfo<>(ATT_VALUES, false, SYN_ATTR_VALUES, null);
  public static final AttrInfo<String> PATTERN = new AttrInfo<>(ATT_PATTERN, false, SYN_ATTR_PATTERN, null);
  public static final AttrInfo<String> SCRIPT = new AttrInfo<>(ATT_SCRIPT, false, SYN_ATTR_SCRIPT, null);
  public static final AttrInfo<String> GENERATOR = new AttrInfo<>(ATT_GENERATOR, false, SYN_ATTR_GENERATOR, null);
  public static final AttrInfo<Integer> MIN_LENGTH = new AttrInfo<>(ATT_MIN_LENGTH, false, SYN_ATTR_MIN_LENGTH, new NonNegativeIntegerParser());
  public static final AttrInfo<Integer> MAX_LENGTH = new AttrInfo<>(ATT_MAX_LENGTH, false, SYN_ATTR_MAX_LENGTH, new NonNegativeIntegerParser());
  public static final AttrInfo<Expression<Double>> NULL_QUOTA = new AttrInfo<>(ATT_NULL_QUOTA, false, SYN_ATTR_NULL_QUOTA, new ScriptableParser<>(new DoubleParser(0., 1.)));

  public static final AttrInfo<String> SOURCE = new AttrInfo<>(ATT_SOURCE, false, SYN_ATTR_SOURCE, null);
  public static final AttrInfo<String> ENCODING = new AttrInfo<>(ATT_ENCODING, false, SYN_ATTR_ENCODING, new EncodingParser());
  public static final AttrInfo<String> SEGMENT = new AttrInfo<>(ATT_SEGMENT, false, SYN_ATTR_SEGMENT, null);
  public static final AttrInfo<Character> SEPARATOR = new AttrInfo<>(ATT_SEPARATOR, false, SYN_ATTR_SEPARATOR, new CharacterParser());
  public static final AttrInfo<String> SELECTOR = new AttrInfo<>(ATT_SELECTOR, false, SYN_ATTR_SELECTOR, null);
  public static final AttrInfo<String> SUB_SELECTOR = new AttrInfo<>(ATT_SUB_SELECTOR, false, SYN_ATTR_SUB_SELECTOR, null);
  public static final AttrInfo<Boolean> ROW_BASED = new AttrInfo<>(ATT_ROW_BASED, false, SYN_ATTR_ROW_BASED, new BooleanParser());
  public static final AttrInfo<String> FORMAT = new AttrInfo<>(ATT_FORMAT, false, SYN_ATTR_FORMAT, new ValuesParser("formatted", "raw"));
  public static final AttrInfo<String> EMPTY_MARKER = new AttrInfo<>(ATT_EMPTY_MARKER, false, SYN_ATTR_EMPTY_MARKER, null);

  public static final AttrInfo<Boolean> NULLABLE = new AttrInfo<>(ATT_NULLABLE, false, SYN_ATTR_NULLABLE, new BooleanParser(), "false");
  public static final AttrInfo<Double> TRUE_QUOTA = new AttrInfo<>(ATT_TRUE_QUOTA, false, SYN_ATTR_TRUE_QUOTA, new DoubleParser(0., 1.));

  public static final AttrInfo<String> MIN = new AttrInfo<>(ATT_MIN, false, SYN_ATTR_MIN, null);
  public static final AttrInfo<String> MIN_INCLUSIVE = new AttrInfo<>(ATT_MIN_INCLUSIVE, false, SYN_ATTR_MIN_INCLUSIVE, null);
  public static final AttrInfo<String> MAX = new AttrInfo<>(ATT_MAX, false, SYN_ATTR_MAX, null);
  public static final AttrInfo<String> MAX_INCLUSIVE = new AttrInfo<>(ATT_MAX_INCLUSIVE, false, SYN_ATTR_MAX_INCLUSIVE, null);
  public static final AttrInfo<String> GRANULARITY = new AttrInfo<>(ATT_GRANULARITY, false, SYN_ATTR_GRANULARITY, null);
  public static final AttrInfo<String> DISTRIBUTION = new AttrInfo<>(ATT_DISTRIBUTION, false, SYN_ATTR_DISTRIBUTION, null);

  public static final AttrInfo<Boolean> DATASET = new AttrInfo<>(ATT_DATASET, false, SYN_ATTR_DATASET, null);
  public static final AttrInfo<String> NESTING = new AttrInfo<>(ATT_NESTING, false, SYN_ATTR_NESTING, null);
  public static final AttrInfo<String> LOCALE = new AttrInfo<>(ATT_LOCALE, false, SYN_ATTR_LOCALE, new IdParser());

  // postprocessor attributes
  public static final AttrInfo<String> CONVERTER = new AttrInfo<>(ATT_CONVERTER, false, SYN_ATTR_CONVERTER, null);
  public static final AttrInfo<String> VALIDATOR = new AttrInfo<>(ATT_VALIDATOR, false, SYN_ATTR_VALIDATOR, null);
  public static final AttrInfo<Boolean> CYCLIC = new AttrInfo<>(ATT_CYCLIC, false, SYN_ATTR_CYCLIC, new BooleanParser());
  public static final AttrInfo<String> MAP = new AttrInfo<>(ATT_MAP, false, SYN_ATTR_MAP, null);

  private final AttrInfoSupport attrSupport = new AttrInfoSupport(SYN_ATTR_ILLEGAL_ATTR,
      new AttributeValidator(),
      NAME, TYPE,
      MODE, SCOPE, OFFSET, CONDITION, FILTER, UNIQUE, UNIQUE_KEY,
      CONSTANT, VALUES, PATTERN, SCRIPT, GENERATOR, MIN_LENGTH, MAX_LENGTH, NULL_QUOTA,
      SOURCE, ENCODING, SEGMENT, SEPARATOR, SELECTOR, SUB_SELECTOR, ROW_BASED, FORMAT, EMPTY_MARKER,
      NULLABLE, TRUE_QUOTA,
      MIN, MIN_INCLUSIVE, MAX, MAX_INCLUSIVE, GRANULARITY, DISTRIBUTION,
      DATASET, NESTING, LOCALE,
      CONVERTER, VALIDATOR, CYCLIC, MAP
  );

  public AttributeParser(BeneratorContext context, boolean nameRequired) {
    super(context);
    NAME.setRequired(nameRequired);
  }

  public PartDescriptor parse(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    attrSupport.validate(element);
    XMLAssert.assertElementName("attribute", element, SYN_ATTR_NAME);
    PartDescriptor result;
    if (descriptor != null) {
      result = new PartDescriptor(descriptor.getName(), descriptorProvider, descriptor.getType());
    } else {
      String name = NAME.parse(element);
      String typeName = TYPE.parse(element);
      XMLAssert.assertNoTextContent(element, SYN_ATTR);
      result = new PartDescriptor(name, descriptorProvider, typeName);
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
      /* XMLAssert.assertAtLeastOneAttributeIsSet(element, SYN_ATTR_ROOT_INFO,
          ATT_TYPE, ATT_CONSTANT, ATT_VALUES, ATT_PATTERN, ATT_SCRIPT, ATT_SOURCE, ATT_GENERATOR, ATT_NULL_QUOTA); */
      // check the attributes that only make sense when using a 'source'
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_ENCODING, element, ATT_ENCODING);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_SEGMENT, element, ATT_SEGMENT);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_SEPARATOR, element, ATT_SEPARATOR);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_SELECTOR, element, ATT_SELECTOR);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_SUB_SELECTOR, element, ATT_SUB_SELECTOR);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_ROW_BASED, element, ATT_ROW_BASED);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_FORMAT, element, ATT_FORMAT);
      allowOnlyInContextOf(ATT_SOURCE, SYN_ATTR_EMPTY_MARKER, element, ATT_EMPTY_MARKER);
      return true;
    }

    private void allowOnlyInContextOf(String base, String errorId, Element element, String... dependent) {
      if (!element.hasAttribute(base)) {
        for (String test : dependent) {
          Attr attr = element.getAttributeNode(test);
          if (attr != null) {
            throw ExceptionFactory.getInstance().illegalXmlAttributeName("Element <" + element.getNodeName() +
                ">'s attribute '" + test + "' is only permitted in combination with a 'source' attribute",
                null, errorId, attr, null);
          }
        }
      }
    }
  }

}
