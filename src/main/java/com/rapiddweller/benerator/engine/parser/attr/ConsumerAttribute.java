/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONSUMER;

/**
 * {@link AttrInfo} for a consumer attribute.<br/><br/>
 * Created: 19.12.2021 21:45:35
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ConsumerAttribute extends AttrInfo<String> { // TODO improve
  public ConsumerAttribute(String errorId) {
    super(ATT_CONSUMER, false, errorId, null);
  }
}
