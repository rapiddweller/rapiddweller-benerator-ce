package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.DescriptorConstants;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.statement.PreParseGenerateStatement;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

public class PreParseGenerateParser extends AbstractBeneratorDescriptorParser {

    private static final AttrInfoSupport ATTR_INFO_SUPPORT = new AttrInfoSupport(BeneratorErrorIds.SYN_PRE_PARSE_GENERATE_ATTR,
            new AttrInfo<>(ATT_TARGET, true, BeneratorErrorIds.SYN_PRE_PARSE_GENERATE_TARGET, new IdParser(), null));

    protected PreParseGenerateParser() {
        super(DescriptorConstants.EL_PRE_PARSE_GENERATE, ATTR_INFO_SUPPORT);
    }

    @Override
    public Statement doParse(Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
        return new PreParseGenerateStatement(element);
    }
}
