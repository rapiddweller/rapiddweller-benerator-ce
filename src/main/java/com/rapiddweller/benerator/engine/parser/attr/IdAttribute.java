/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.format.xml.AttributeInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ID;

/**
 * {@link AttributeInfo} implementation for 'id' attributes.<br/><br/>
 * Created: 12.12.2021 07:16:57
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class IdAttribute extends AttributeInfo<String> {

  public IdAttribute(String errorId, boolean required) {
    super(ATT_ID, required, errorId, new IdParser());
  }

}
