/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import org.w3c.dom.Element;

/**
 * Parses &lt;id&gt; elements.<br/><br/>
 * Created: 14.12.2021 05:47:38
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class IdParser extends AbstractComponentParser {

  public static final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_ID_NAME, true, false);

  public IdParser(BeneratorContext context) {
    super(context);
  }

  public IdDescriptor parse(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    XMLAssert.assertElementName("id", element, BeneratorErrorIds.SYN_ID_NAME);
    String nameAttr = NAME.parse(element);
    XMLAssert.assertNoTextContent(element, BeneratorErrorIds.SYN_ID);
    DescriptorParserUtil.validateGeneratorAttribute(element, BeneratorErrorIds.SYN_ATTR_GENERATOR);
    IdDescriptor result;
    IdDescriptor resultTmp;
    if (descriptor instanceof IdDescriptor) {
      resultTmp = (IdDescriptor) descriptor;
    } else if (descriptor != null) {
      resultTmp = new IdDescriptor(descriptor.getName(), descriptorProvider, descriptor.getType());
    } else {
      resultTmp = new IdDescriptor(nameAttr, descriptorProvider, element.getAttribute("type"));
    }
    result = mapInstanceDetails(element, false, resultTmp);
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
