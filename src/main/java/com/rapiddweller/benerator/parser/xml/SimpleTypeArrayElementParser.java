/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.model.data.ArrayElementDescriptor;
import com.rapiddweller.model.data.ArrayTypeDescriptor;
import org.w3c.dom.Element;

/**
 * Parses an array element of simple type.<br/><br/>
 * Created: 14.12.2021 06:31:36
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SimpleTypeArrayElementParser extends AbstractComponentParser {

  public static final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_VAR_NAME, true, false);

  public SimpleTypeArrayElementParser(BeneratorContext context) {
    super(context);
  }

  public ArrayElementDescriptor parse(Element element, ArrayTypeDescriptor owner, int index) {
    ArrayElementDescriptor descriptor = new ArrayElementDescriptor(index, descriptorProvider, element.getAttribute("name"));
    mapInstanceDetails(element, false, descriptor);
    owner.addElement(descriptor);
    return descriptor;
  }

}
