/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.SimpleTypeParser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TYPE;

/**
 * {@link AttrInfo} for simple data types.<br/><br/>
 * Created: 14.12.2021 08:54:03
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SimpleTypeAttribute extends AttrInfo<String> {
  public SimpleTypeAttribute(String errorId) {
    super(ATT_TYPE, false, errorId, new SimpleTypeParser());
  }
}
