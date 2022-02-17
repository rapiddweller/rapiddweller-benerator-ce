/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.ReferenceDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import org.w3c.dom.Element;

/**
 * Parses &lt;reference&gt; elements.<br/><br/>
 * Created: 14.12.2021 05:50:43
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ReferenceParser extends AbstractComponentParser {

  public static final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_REF_NAME, true, false);

  public ReferenceParser(BeneratorContext context) {
    super(context);
  }

  public ReferenceDescriptor parse(Element element, ComplexTypeDescriptor owner, ComponentDescriptor component) {
    XMLAssert.assertElementName("reference", element, BeneratorErrorIds.SYN_REF_NAME);
    String nameAttr = NAME.parse(element);
    XMLAssert.assertNoTextContent(element, BeneratorErrorIds.SYN_REF);
    DescriptorParserUtil.validateGeneratorAttribute(element, BeneratorErrorIds.SYN_ATTR_GENERATOR);
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

}
