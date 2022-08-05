/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.parser.xml.AbstractComponentParser;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import static com.rapiddweller.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

/**
 * Parses &lt;type&gt; elements.<br/><br/>
 * Created: 14.12.2021 06:21:24
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SimpleTypeParser extends AbstractXMLComponentParser {

  public static final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_XML_SIMPLE_NAME, true, false);

  public SimpleTypeParser(BeneratorContext context) {
    super(context);
  }

  public SimpleTypeDescriptor parse(Element element, SimpleTypeDescriptor descriptor) {
    XMLAssert.assertElementName("type", element, BeneratorErrorIds.SYN_XML_SIMPLE_TYPE);
    return mapTypeDetails(element, descriptor);
  }

}
