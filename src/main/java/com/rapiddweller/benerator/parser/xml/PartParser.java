/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.parser.ModelParser;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.ComponentDescriptor;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ATTRIBUTE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ID;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_LIST;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_PART;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_REFERENCE;

/**
 * Parses &lt;part&gt; elements.<br/><br/>
 * Created: 14.12.2021 05:36:16
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class PartParser extends AbstractComponentParser {

  private final ModelParser modelParser;
  private final NameAttribute nameAttr;

  public PartParser(ModelParser modelParser, boolean nameRequired) {
    super(modelParser.getContext());
    this.modelParser = modelParser;
    this.nameAttr = new NameAttribute(BeneratorErrorIds.SYN_PART_NAME, nameRequired, false);
  }

  public PartDescriptor parse(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
    XMLAssert.assertElementName("part", element, BeneratorErrorIds.SYN_PART_NAME);
    nameAttr.parse(element);
    DescriptorParserUtil.validateGeneratorAttribute(element, BeneratorErrorIds.SYN_ATTR_GENERATOR);
    PartDescriptor result;
    if (descriptor instanceof PartDescriptor) {
      result = (PartDescriptor) descriptor;
    } else if (descriptor != null) {
      result = new PartDescriptor(descriptor.getName(), descriptorProvider, descriptor.getType());
    } else {
      String typeName = StringUtil.emptyToNull(element.getAttribute("type"));
      String partName = element.getAttribute("name");
      String localTypeName = owner.getName() + "." + partName;
      if (typeName != null) {
        result = new PartDescriptor(partName, descriptorProvider, typeName);
      } else if (element.getNodeName().equals("part")) {
        result = new PartDescriptor(partName, descriptorProvider,
            new ComplexTypeDescriptor(localTypeName, descriptorProvider, (ComplexTypeDescriptor) null));
      } else {
        result = new PartDescriptor(partName, descriptorProvider,
            new SimpleTypeDescriptor(localTypeName, descriptorProvider, (SimpleTypeDescriptor) null));
      }
    }
    mapInstanceDetails(element, true, result);
    if (result.getLocalType().getSource() == null && (((ComplexTypeDescriptor) result.getLocalType()).getDynamicSource() == null)) {
      applyDefaultCounts(result);
    }
    if (owner != null) {
      ComponentDescriptor parentComponent = owner.getComponent(result.getName());
      if (parentComponent != null) {
        TypeDescriptor parentType = parentComponent.getTypeDescriptor();
        result.getLocalType(false).setParent(parentType);
      }
      owner.addComponent(result);
    }
    for (Element childElement : XMLUtil.getChildElements(element)) {
      parseComponentGeneration(childElement, (ComplexTypeDescriptor) result.getLocalType(true));
    }
    return result;
  }

  public ComponentDescriptor parseComponentGeneration(Element element, ComplexTypeDescriptor owner) {
    String elementName = XMLUtil.localName(element);
    if (EL_PART.equals(elementName)) {
      return parse(element, owner, null);
    } else if (ModelParser.isSimpleTypeComponent(elementName)) {
      return modelParser.parseSimpleTypeComponent(element, owner, null);
    } else if (EL_LIST.equals(elementName)) {
      return modelParser.getItemListParser().parse(element, owner);
    } else {
      throw BeneratorExceptionFactory.getInstance().configurationError("Expected one of these element names: " +
          EL_ATTRIBUTE + ", " + EL_ID + ", " + EL_REFERENCE + ", " + EL_LIST + ", or " + EL_PART + ". Found: " + elementName);
    }
  }

}
