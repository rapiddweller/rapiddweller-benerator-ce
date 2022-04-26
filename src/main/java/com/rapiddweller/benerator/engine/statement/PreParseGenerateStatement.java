package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.xml.ElementToInstanceDesciptorParser;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.FeatureDescriptor;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.platform.nosql.CustomStorageSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.stream.Collectors;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TARGET;

public class PreParseGenerateStatement extends AbstractStatement {

    protected Logger logger = LoggerFactory.getLogger(PreParseGenerateStatement.class);

    private final ElementToInstanceDesciptorParser elementToInstanceDesciptorParser = new ElementToInstanceDesciptorParser();

    private final Element element;
    private final String target;

    // constructor -------------------------------------------------------------------------------------------------------

    public PreParseGenerateStatement(Element element) {
        this.element = element;
        this.target = element.getAttribute(ATT_TARGET);
    }

    @Override
    public boolean execute(BeneratorContext context) {
        DescriptorProvider descriptorProvider = context.getDataModel().getDescriptorProvider(target);
        if (Objects.isNull(descriptorProvider)) {
            logger.error("Cannot add type descriptors to {} because there is no descriptorProvider.", target);
            return false;
        }
        if (!CustomStorageSystem.class.isAssignableFrom(descriptorProvider.getClass())) {
            logger.error("Could not add type descriptors to {} because the provided storage system is not a custom storage system.", target);
            return false;
        }

        CustomStorageSystem customStorageSystem = (CustomStorageSystem) descriptorProvider;
        List<TypeDescriptor> typeDescriptors = parseGeneratesToTypeDescriptors(this.element.getParentNode(), context);
        if (!typeDescriptors.isEmpty()) {
            logger.info("Created type descriptors: {}",
                    typeDescriptors.stream().map(FeatureDescriptor::getName).collect(Collectors.joining(",")));
            typeDescriptors.forEach(customStorageSystem::addTypeDescriptor);
            logger.info("Added type descriptors to {}.", target);
        }
        return true;
    }

    private List<TypeDescriptor> parseGeneratesToTypeDescriptors(Node rootNode, BeneratorContext context) {
        List<TypeDescriptor> typeDescriptors = new ArrayList<>();
        NodeList setupChildNodes = rootNode.getChildNodes();
        for (int i = 0; i < setupChildNodes.getLength(); i++) {
            Node currentNode = setupChildNodes.item(i);
            if (currentNode.getNodeName().equals("generate")) {
                typeDescriptors.addAll(parseGeneratesToTypeDescriptors(currentNode, context));
                if (generatesForTarget(currentNode)) {
                    typeDescriptors.add(elementToInstanceDesciptorParser.parse((Element) currentNode, context).getTypeDescriptor());
                }
            }
        }
        return typeDescriptors;
    }

    private boolean generatesForTarget(Node generateNode) {
        return  XMLUtil.getAttribute((Element) generateNode, "consumer", true).equals(target);
    }
}
