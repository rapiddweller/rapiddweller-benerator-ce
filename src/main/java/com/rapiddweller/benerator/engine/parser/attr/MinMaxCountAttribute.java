/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.format.xml.AttrInfo;

/**
 * {@link AttrInfo} for the upper or lower bound of a count value range.<br/><br/>
 * Created: 19.12.2021 21:22:20
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class MinMaxCountAttribute extends AttrInfo<Expression<Long>> {
  public MinMaxCountAttribute(String name, String errorId) {
    super(name, false, errorId, new ScriptableParser<>(new NonNegativeLongParser()));
  }
}
