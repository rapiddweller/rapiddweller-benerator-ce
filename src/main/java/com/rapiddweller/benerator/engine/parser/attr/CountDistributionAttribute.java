/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.StringParser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_COUNT_DISTRIBUTION;

/**
 * {@link AttrInfo} for a count distribution.<br/><br/>
 * Created: 19.12.2021 21:27:13
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class CountDistributionAttribute extends AttrInfo<Expression<String>> {
  public CountDistributionAttribute(String errorId) {
    super(ATT_COUNT_DISTRIBUTION, false, errorId, new ScriptableParser<>(new StringParser("count distribution")));
  }
}
