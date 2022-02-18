/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.DoubleParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NULL_QUOTA;

/**
 * {@link AttrInfo} for the nullQuota attribute.<br/><br/>
 * Created: 19.12.2021 21:59:36
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class NullQuotaAttribute extends AttrInfo<Expression<Double>> {
  public NullQuotaAttribute(String errorId) {
    super(ATT_NULL_QUOTA, false, errorId, createParser());
  }

  private static Parser<Expression<Double>> createParser() {
    return new ScriptableParser<>(new DoubleParser(0., 1.));
  }

}
