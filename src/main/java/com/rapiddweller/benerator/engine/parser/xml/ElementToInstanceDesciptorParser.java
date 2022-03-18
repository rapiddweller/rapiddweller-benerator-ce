package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.factory.DescriptorUtil;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.model.data.*;
import com.rapiddweller.script.PrimitiveType;
import org.w3c.dom.Element;

import java.util.Map;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

public class ElementToInstanceDesciptorParser {

    private static final AttrInfo<String> nameAttr = new NameAttribute(null, false, false);
    private static final AttrInfo<String> typeAttr = new AttrInfo<>(ATT_TYPE, false, null, new IdParser(), null);

    public InstanceDescriptor parse(Element element, BeneratorContext context) {
        InstanceDescriptor instance = assembleInstanceDescriptor(context, element);
        mapElementAttributes(element, instance);
        DescriptorUtil.parseComponentConfig(element, instance.getLocalType(), context);
        return instance;
    }

    private InstanceDescriptor assembleInstanceDescriptor(BeneratorContext context, Element element) {
        // evaluate type
        String type = typeAttr.parse(element);
        TypeDescriptor localType;
        DescriptorProvider localDescriptorProvider = context.getLocalDescriptorProvider();
        if (PrimitiveType.ARRAY.getName().equals(type)
                || XMLUtil.getChildElements(element, false, EL_VALUE).length > 0) {
            localType = new ArrayTypeDescriptor(nameAttr.parse(element), localDescriptorProvider);
        } else {
            TypeDescriptor parentType = context.getDataModel().getTypeDescriptor(type);
            if (parentType != null) {
                type = parentType.getName(); // take over capitalization of the parent
                localType = new ComplexTypeDescriptor(parentType.getName(), localDescriptorProvider, (ComplexTypeDescriptor) parentType);
            } else {
                localType = new ComplexTypeDescriptor(type, localDescriptorProvider, "entity");
            }
        }

        // assemble instance descriptor
        InstanceDescriptor instance = new InstanceDescriptor(type, localDescriptorProvider, type);
        instance.setLocalType(localType);
        return instance;
    }

    private void mapElementAttributes(Element element, InstanceDescriptor instance) {
        for (Map.Entry<String, String> attribute : XMLUtil.getAttributes(element).entrySet()) {
            String attributeName = attribute.getKey();
            if (!CREATE_ENTITIES_EXT_SETUP.contains(attributeName)) {
                Object attributeValue = attribute.getValue();
                if (!instance.supportsDetail(attributeName)) {
                    instance.getLocalType().setDetailValue(attributeName, attributeValue);
                }
                if (instance.supportsDetail(attributeName)) {
                    instance.setDetailValue(attributeName, attributeValue);
                }
            }
        }
    }

}
