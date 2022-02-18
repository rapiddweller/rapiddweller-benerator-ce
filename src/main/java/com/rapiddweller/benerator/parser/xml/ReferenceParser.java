/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.ConstantAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ConverterAttribute;
import com.rapiddweller.benerator.engine.parser.attr.CyclicAttribute;
import com.rapiddweller.benerator.engine.parser.attr.DistributionAttribute;
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
import com.rapiddweller.benerator.engine.parser.attr.SimpleTypeAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SourceAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SubSelectorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.UniqueAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ValidatorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.ValuesAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.script.WeightedSample;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_CONSTANT;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_CONVERTER;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_CYCLIC;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_DISTRIBUTION;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_FILTER;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_MODE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_NULLABLE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_NULL_QUOTA;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_OFFSET;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_SCOPE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_SELECTOR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_SOURCE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_SUB_SELECTOR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_TARGET_TYPE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_UNIQUE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_VALIDATOR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_REF_VALUES;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONSTANT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_GENERATOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SCRIPT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SELECTOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SOURCE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SUB_SELECTOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TARGET_TYPE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_VALUES;

/**
 * Parses &lt;reference&gt; elements.<br/><br/>
 * Created: 14.12.2021 05:50:43
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ReferenceParser extends AbstractComponentParser {

  public static final AttrInfo<String> NAME = new NameAttribute(BeneratorErrorIds.SYN_REF_NAME, true, false);
  public static final AttrInfo<String> TYPE = new SimpleTypeAttribute(BeneratorErrorIds.SYN_REF_TYPE);
  public static final AttrInfo<String> MODE = new ModeAttribute(SYN_REF_MODE);
  public static final AttrInfo<Expression<Double>> NULL_QUOTA = new NullQuotaAttribute(SYN_REF_NULL_QUOTA);
  public static final AttrInfo<Boolean> NULLABLE = new NullableAttribute(SYN_REF_NULLABLE);

  public static final AttrInfo<Expression<?>> GENERATOR = new GeneratorAttribute(BeneratorErrorIds.SYN_REF_GENERATOR);
  public static final AttrInfo<String> SCRIPT = new ScriptAttribute(BeneratorErrorIds.SYN_REF_SCRIPT);
  public static final AttrInfo<String> CONSTANT = new ConstantAttribute(SYN_REF_CONSTANT);
  public static final AttrInfo<WeightedSample<?>[]> VALUES = new ValuesAttribute(SYN_REF_VALUES);

  public static final AttrInfo<String> SOURCE = new SourceAttribute(SYN_REF_SOURCE, false);
  public static final AttrInfo<String> TARGET_TYPE = new AttrInfo<>(ATT_TARGET_TYPE, false, SYN_REF_TARGET_TYPE, new IdParser());
  public static final AttrInfo<String> SELECTOR = new SelectorAttribute(SYN_REF_SELECTOR);
  public static final AttrInfo<String> SUB_SELECTOR = new SubSelectorAttribute(SYN_REF_SUB_SELECTOR);

  public static final AttrInfo<String> SCOPE = new ScopeAttribute(SYN_REF_SCOPE);
  public static final AttrInfo<Expression<Boolean>> FILTER = new FilterAttribute(SYN_REF_FILTER);
  public static final AttrInfo<Boolean> CYCLIC = new CyclicAttribute(SYN_REF_CYCLIC);
  public static final AttrInfo<String> DISTRIBUTION = new DistributionAttribute(SYN_REF_DISTRIBUTION);
  public static final AttrInfo<Boolean> UNIQUE = new UniqueAttribute(SYN_REF_UNIQUE);

  public static final AttrInfo<String> CONVERTER = new ConverterAttribute(SYN_REF_CONVERTER);
  public static final AttrInfo<Expression<Long>> OFFSET = new OffsetAttribute(SYN_REF_OFFSET);
  public static final AttrInfo<String> VALIDATOR = new ValidatorAttribute(SYN_REF_VALIDATOR);

  private final AttrInfoSupport refAttrInfo;

  public ReferenceParser(BeneratorContext context) {
    super(context);
    this.refAttrInfo = new AttrInfoSupport(
        BeneratorErrorIds.SYN_REF_ILLEGAL_ATTR, new ReferenceElementValidator(), NAME, TYPE, MODE,
        NULL_QUOTA, NULLABLE,
        GENERATOR, SCRIPT, CONSTANT, VALUES, SOURCE, TARGET_TYPE, SELECTOR, SUB_SELECTOR,
        SCOPE, FILTER, OFFSET,
        UNIQUE, CYCLIC, DISTRIBUTION,
        CONVERTER, OFFSET, VALIDATOR);
  }

  public ReferenceDescriptor parse(Element element, ComplexTypeDescriptor owner, ComponentDescriptor component) {
    validateSyntax(element);
    String nameAttr = NAME.parse(element);
    ReferenceDescriptor result;
    if (component instanceof ReferenceDescriptor) {
      result = (ReferenceDescriptor) component;
    } else if (component != null) {
      result = new ReferenceDescriptor(component.getName(), descriptorProvider, component.getType());
    } else {
      result = new ReferenceDescriptor(nameAttr, descriptorProvider, StringUtil.emptyToNull(element.getAttribute("type")));
    }
    if (owner != null) {
      ComponentDescriptor parentComponent = owner.getComponent(result.getName());
      if (parentComponent != null) {
        TypeDescriptor parentType = parentComponent.getTypeDescriptor();
        result.getLocalType(false).setParent(parentType);
      }
      owner.setComponent(result);
    }
    return mapInstanceDetails(element, false, result);
  }

  private void validateSyntax(Element element) {
    // check element name and content
    XMLAssert.assertElementName("reference", element, BeneratorErrorIds.SYN_REF_NAME);
    XMLAssert.assertNoTextContent(element, BeneratorErrorIds.SYN_REF);
    // check attributes
    refAttrInfo.validate(element);
  }

  class ReferenceElementValidator implements Validator<Element> {

    @Override
    public boolean valid(Element element) {
      Expression<Double> nullQuota = NULL_QUOTA.parse(element);
      String source = SOURCE.parse(element);
      // check outer XML elements
      DescriptorParserUtil.validateGeneratorAttribute(element, BeneratorErrorIds.SYN_REF_GENERATOR);
      // check data definition
      String mode = MODE.parse(element);
      if (ModeAttribute.IGNORED.equals(mode) || (nullQuota != null && nullQuota.isConstant() && nullQuota.evaluate(context) < 1.)) {
        // if nullQuota == 1, no further check is necessary
        return true;
      }
      XMLAssert.mutuallyExcludeAttributes(BeneratorErrorIds.SYN_REF_BASE_DEF, element,
          ATT_GENERATOR, ATT_SCRIPT, ATT_SOURCE, ATT_CONSTANT, ATT_VALUES);
      XMLAssert.assertAtLeastOneAttributeIsSet(element, BeneratorErrorIds.SYN_REF_BASE_DEF,
          ATT_GENERATOR, ATT_SCRIPT, ATT_SOURCE, ATT_CONSTANT, ATT_VALUES);
      if (source != null) {
        XMLAssert.assertAtLeastOneAttributeIsSet(element, BeneratorErrorIds.SYN_REF_EXT_DEF,
            ATT_TARGET_TYPE, ATT_SELECTOR, ATT_SUB_SELECTOR);
      }
      XMLAssert.allowOnlyInContextOf(ATT_SOURCE, SYN_REF_TARGET_TYPE, element,
          ATT_TARGET_TYPE, ATT_SELECTOR, ATT_SUB_SELECTOR);
      XMLAssert.mutuallyExcludeAttributes(BeneratorErrorIds.SYN_REF_EXT_DEF, element,
          ATT_SELECTOR, ATT_SUB_SELECTOR);
      return true;
    }
  }

}
