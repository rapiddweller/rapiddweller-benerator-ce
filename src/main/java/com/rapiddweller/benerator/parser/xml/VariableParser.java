/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.VariableDescriptor;
import org.w3c.dom.Element;

/**
 * Parses &lt;variable&gt; elements.<br/><br/>
 * Created: 14.12.2021 05:42:53
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class VariableParser extends AbstractComponentParser {

  public static final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_VAR_NAME, true, false);

  public VariableParser(BeneratorContext context) {
    super(context);
  }

  public InstanceDescriptor parse(Element varElement) {
    // validate
    XMLAssert.assertElementName("variable", varElement, BeneratorErrorIds.SYN_VAR_NAME);
    NAME.parse(varElement);
    XMLAssert.assertNoTextContent(varElement, BeneratorErrorIds.SYN_VAR);
    DescriptorParserUtil.validateGeneratorAttribute(varElement, BeneratorErrorIds.SYN_ATTR_GENERATOR);
    String type = StringUtil.emptyToNull(varElement.getAttribute("type"));
    // create descriptor
    VariableDescriptor descriptor = new VariableDescriptor(varElement.getAttribute("name"), descriptorProvider, type);
    return mapInstanceDetails(varElement, false, descriptor);
  }


}
