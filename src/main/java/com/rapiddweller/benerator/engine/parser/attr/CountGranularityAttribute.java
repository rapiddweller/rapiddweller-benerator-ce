/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.PositiveLongParser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_COUNT_GRANULARITY;

/**
 * {@link AttrInfo} for count granularities.<br/><br/>
 * Created: 20.12.2021 00:07:31
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class CountGranularityAttribute extends AttrInfo<Expression<Long>> {
  public CountGranularityAttribute(String errorId) {
    super(ATT_COUNT_GRANULARITY, false, errorId, new ScriptableParser<>(new PositiveLongParser()), "1");
  }
}
