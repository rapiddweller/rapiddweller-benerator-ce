package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.parser.ModelParser;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.*;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_PART;

public class ItemListParser extends AbstractComponentParser {

    private ModelParser modelParser;

    public ItemListParser(ModelParser modelParser) {
        super(modelParser.getContext());
        this.modelParser = modelParser;
    }

    public ItemListDescriptor parse(Element element, ComplexTypeDescriptor owner) {
        ItemListDescriptor result;
        String listName = element.getAttribute("name");
        String localTypeName = owner.getName() + "." + listName;
        result = new ItemListDescriptor(listName, descriptorProvider,
                new ComplexTypeDescriptor(localTypeName, descriptorProvider, (ComplexTypeDescriptor) null));

        if (owner != null) {
            ComponentDescriptor parentComponent = owner.getComponent(result.getName());
            if (parentComponent != null) {
                TypeDescriptor parentType = parentComponent.getTypeDescriptor();
                result.getLocalType(true).setParent(parentType);
            }
            owner.addComponent(result);
        }

        var resultDescriptor = (ComplexTypeDescriptor) result.getLocalType();
        int count = 0;
        for (Element childElement : XMLUtil.getChildElements(element)) {
            var childElementNodeName = childElement.getNodeName();
            if (!childElementNodeName.equals(EL_ITEM)) {
                throw BeneratorExceptionFactory.getInstance().configurationError("Expected element name: " + EL_ITEM + ". Found: " + childElementNodeName);
            }
            var itemName = childElement.getLocalName() + count++;
            var itemElementDescriptor = new ItemElementDescriptor(itemName, descriptorProvider, null, new ComplexTypeDescriptor(localTypeName + "." + itemName, descriptorProvider, (ComplexTypeDescriptor) null));
            resultDescriptor.addComponent(itemElementDescriptor);
            parseItem(childElement, (ComplexTypeDescriptor) itemElementDescriptor.getLocalType());
        }
        return result;
    }

    private void parseItem(Element element, ComplexTypeDescriptor owner) {
        for (Element childElement : XMLUtil.getChildElements(element)) {
            String childElementName = XMLUtil.localName(childElement);
            if (EL_PART.equals(childElementName)) {
                modelParser.getPartParser().parse(childElement, owner, null);
            } else if (ModelParser.isSimpleTypeComponent(childElementName)) {
                modelParser.parseSimpleTypeComponent(childElement, owner, null);
            } else if (EL_LIST.equals(childElementName)) {
                modelParser.getItemListParser().parse(childElement, owner);
            } else {
                throw BeneratorExceptionFactory.getInstance().configurationError("Expected one of these element names: " +
                        EL_ATTRIBUTE + ", " + EL_ID + ", " + EL_REFERENCE + ", " + EL_LIST + ", or " + EL_PART + ". Found: " + childElementName);
            }
        }
    }
}
