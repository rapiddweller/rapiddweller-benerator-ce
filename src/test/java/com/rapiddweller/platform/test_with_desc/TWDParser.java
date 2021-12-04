/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.test_with_desc;

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.xml.XMLStatementParser;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.ParseContext;
import org.w3c.dom.Element;

/**
 * Simple parser for testing.<br/><br/>
 * Created: 01.12.2021 16:59:12
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class TWDParser extends XMLStatementParser {

  private static final AttrInfoSupport ATTR_SUPPORT;

  static {
    ATTR_SUPPORT = new AttrInfoSupport("TWD-0001");
    ATTR_SUPPORT.add("text", true, "TWD-0002");
  }

  protected TWDParser() {
    super("twd", ATTR_SUPPORT);
  }

  @Override
  public boolean supports(Element element, Element[] parentXmlPath, Statement[] parentComponentPath) {
    return "twd".equals(element.getNodeName());
  }

  @Override
  protected Statement doParse(Element element, Element[] parentXmlPath, Statement[] parentPath, ParseContext<Statement> context) {
    return (ctx) -> {
      ctx.set("twd_text", element.getAttribute("text"));
      return true;
    };
  }

}
