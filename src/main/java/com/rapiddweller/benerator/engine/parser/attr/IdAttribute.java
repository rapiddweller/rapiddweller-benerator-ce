/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ID;

/**
 * {@link AttrInfo} implementation for 'id' attributes.<br/><br/>
 * Created: 12.12.2021 07:16:57
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class IdAttribute extends AttrInfo<String> {

  public IdAttribute(String errorId, boolean required) {
    super(ATT_ID, required, errorId, new IdParser());
  }

}
