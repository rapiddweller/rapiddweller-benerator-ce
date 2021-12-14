/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.xml;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.parser.xml.AbstractComponentParser;
import com.rapiddweller.model.data.TypeDescriptor;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import static com.rapiddweller.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

/**
 * TODO JavaDoc.<br/><br/>
 * Created: 14.12.2021 06:35:12
 *
 * @author Volker Bergmann
 * @since TODO
 */
public class AbstractXMLComponentParser extends AbstractComponentParser {

  public AbstractXMLComponentParser(BeneratorContext context) {
    super(context);
  }

  protected  <T extends TypeDescriptor> T mapTypeDetails(Element element, T descriptor) {
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      Attr attr = (Attr) attributes.item(i);
      String detailValue = parseStringAttribute(attr, context);
      descriptor.setDetailValue(attr.getName(), detailValue);
    }
    return descriptor;
  }


}
