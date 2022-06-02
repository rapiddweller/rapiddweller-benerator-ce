package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.xml.ElementToInstanceDesciptorParser;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.TypeDescriptor;
import com.rapiddweller.platform.mongodb.CustomStorageSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONSUMER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TYPE;
import static com.rapiddweller.model.data.TypeDescriptor.META_MODEL;

public class MetaModelStatement extends AbstractStatement {

    protected Logger logger = LoggerFactory.getLogger(MetaModelStatement.class);

    private final ElementToInstanceDesciptorParser elementToInstanceDescriptorParser = new ElementToInstanceDesciptorParser();

    private final Element rootElement;

    // constructor -------------------------------------------------------------------------------------------------------

    public MetaModelStatement(Element element) {
        this.rootElement = element;
    }

    @Override
    public boolean execute(BeneratorContext context) {
        for (Element element : getMetaModelElements(rootElement)) {
            String source = element.getAttribute(ATT_CONSUMER);
            DescriptorProvider descriptorProvider = context.getDataModel().getDescriptorProvider(source);
            Class<?> sourceClass = context.getDataModel().getDescriptorProvider(source).getClass();

            if (Objects.isNull(descriptorProvider)) {
                logger.error("Cannot create meta model for {} because there is no descriptorProvider.", source);
                return false;
            }

            if (!CustomStorageSystem.class.isAssignableFrom(sourceClass)) {
                logger.error("Could not add {} to {} because the providing object is not a CustomStorageSystem.",
                        element.getAttribute(ATT_TYPE), source);
                return false;
            }

            TypeDescriptor typeDescriptor = elementToInstanceDescriptorParser.parse(element, context).getTypeDescriptor();
            typeDescriptor.setDetailValue(META_MODEL, true);

            CustomStorageSystem customStorageSystem = CustomStorageSystem.class.cast(descriptorProvider);
            customStorageSystem.addTypeDescriptor(typeDescriptor);
            logger.info("Created meta model for {} and added it to {}.", typeDescriptor.getName(), source);
        }

        return true;
    }

    private List<Element> getMetaModelElements(Element element) {
        List<Element> metaModelElements = new ArrayList<>(List.of(element));
        NodeList metaModelNodeList = element.getElementsByTagName("meta-model");
        IntStream.range(0, metaModelNodeList.getLength())
                .mapToObj(metaModelNodeList::item)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(node -> (Element) node)
                .map(this::getMetaModelElements)
                .forEach(metaModelElements::addAll);
        return metaModelElements;
    }

}
