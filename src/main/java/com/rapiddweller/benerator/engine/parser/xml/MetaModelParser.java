package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.DescriptorConstants;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.statement.MetaModelStatement;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONSUMER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TYPE;

public class MetaModelParser extends AbstractBeneratorDescriptorParser {

    private static final AttrInfoSupport ATTR_INFO_SUPPORT = new AttrInfoSupport(BeneratorErrorIds.SYN_META_MODEL_ATTR,
            new AttrInfo<>(ATT_TYPE, true, BeneratorErrorIds.SYN_META_MODEL_ID, new IdParser(), null),
            new AttrInfo<>(ATT_CONSUMER, true, BeneratorErrorIds.SYN_META_MODEL_ATTR, new IdParser(), null));

    protected MetaModelParser() {
        super(DescriptorConstants.EL_META_MODEL, ATTR_INFO_SUPPORT);
    }

    @Override
    public Statement doParse(Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
        return new MetaModelStatement(element);
    }
}
