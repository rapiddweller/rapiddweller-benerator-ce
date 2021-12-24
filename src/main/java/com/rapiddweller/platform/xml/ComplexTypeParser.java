/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.parser.ModelParser;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import org.w3c.dom.Element;

/**
 * Parses &lt;entity&gt; and &lt;type&gt; elements.<br/><br/>
 * Created: 14.12.2021 06:17:44
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ComplexTypeParser extends AbstractXMLComponentParser {

  public static final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_XML_SIMPLE_NAME, true, false);

  private final ModelParser modelParser;

  public ComplexTypeParser(ModelParser modelParser) {
    super(modelParser.getContext());
    this.modelParser = modelParser;
  }

  public ComplexTypeDescriptor parse(Element ctElement, ComplexTypeDescriptor descriptor) {
    String elementName = ctElement.getLocalName();
    if (!"entity".equals(elementName) && !"type".equals(elementName)) {
      throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
          "Expected <entity> or <type>, but found <" + elementName + ">",
          null, BeneratorErrorIds.SYN_MISPLACED_ELEMENT, ctElement);
    }
    descriptor = new ComplexTypeDescriptor(descriptor.getName(), descriptorProvider, descriptor);
    mapTypeDetails(ctElement, descriptor);
    for (Element child : XMLUtil.getChildElements(ctElement)) {
      parseComplexTypeChild(child);
    }
    return descriptor;
  }

  public void parseComplexTypeChild(Element element) {
    String childName = XMLUtil.localName(element);
    if ("variable".equals(childName)) {
      modelParser.parseVariable(element);
    } else {
      throw BeneratorExceptionFactory.getInstance().programmerUnsupported("element type not supported here: " + childName);
    }
  }

}
