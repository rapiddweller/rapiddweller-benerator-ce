/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import org.w3c.dom.Element;

/**
 * Parses &lt;attribute&gt; elements.<br/><br/>
 * Created: 14.12.2021 05:07:20
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class AttributeParser extends AbstractComponentParser {

  public static final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_ATTR_NAME, true, false);

  public AttributeParser(BeneratorContext context) {
    super(context);
  }

  public PartDescriptor parse(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    XMLAssert.assertElementName("attribute", element, BeneratorErrorIds.SYN_ATTR_NAME);
    PartDescriptor result;
    if (descriptor != null) {
      result = new PartDescriptor(descriptor.getName(), descriptorProvider, descriptor.getType());
    } else {
      String typeName = StringUtil.emptyToNull(element.getAttribute("type"));
      String name = NAME.parse(element);
      XMLAssert.assertNoTextContent(element, BeneratorErrorIds.SYN_ATTR);
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

}
