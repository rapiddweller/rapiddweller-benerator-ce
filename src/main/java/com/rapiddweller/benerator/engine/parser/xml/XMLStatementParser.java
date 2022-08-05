/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.format.xml.AbstractXMLElementParser;
import com.rapiddweller.format.xml.AttrInfoSupport;

/**
 * Parent class for all XML statement parsers of Benerator.<br/><br/>
 * Created: 01.12.2021 16:39:28
 * @author Volker Bergmann
 * @since 3.0.0
 */
public abstract class XMLStatementParser extends AbstractXMLElementParser<Statement> {
  protected XMLStatementParser(
      String elementName, AttrInfoSupport attrSupport, Class<?>... supportedParentTypes) {
    super(elementName, attrSupport, supportedParentTypes);
  }
}
