/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.common.parser.FullyQualifiedNameParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttributeInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NAME;

/**
 * {@link AttributeInfo} for a 'name' attribute.<br/><br/>
 * Created: 11.12.2021 07:02:57
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class NameAttribute extends AttributeInfo<String> {

  public NameAttribute(String errorId, boolean required, boolean fqnAllowed) {
    super(ATT_NAME, required, errorId, createParser(fqnAllowed));
  }

  private static Parser<String> createParser(boolean fqnAllowed) {
    return (fqnAllowed ? new FullyQualifiedNameParser() : new IdParser());
  }

}
