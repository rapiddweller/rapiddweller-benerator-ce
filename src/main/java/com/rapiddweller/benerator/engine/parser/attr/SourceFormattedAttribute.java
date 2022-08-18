/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.ValuesParser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_FORMAT;

/**
 * {@link AttrInfo} for a source-related 'format' attribute.<br/><br/>
 * Created: 19.12.2021 22:32:41
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SourceFormattedAttribute extends AttrInfo<String> {
  public SourceFormattedAttribute(String errorId) {
    super(ATT_FORMAT, false, errorId, new ValuesParser("format", "formatted", "raw"));
  }
}
