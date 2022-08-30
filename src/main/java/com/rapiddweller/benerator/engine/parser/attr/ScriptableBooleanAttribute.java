/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

/**
 * {@link AttrInfo} for a scriptable boolean attribute.<br/><br/>
 * Created: 19.12.2021 21:38:03
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ScriptableBooleanAttribute extends AttrInfo<Expression<Boolean>>{

  public ScriptableBooleanAttribute(String name, boolean required, String errorId, Boolean defaultValue) {
    super(name, required, errorId, createParser(), toString(defaultValue));
  }

  private static Parser<Expression<Boolean>> createParser() {
    return new ScriptableParser<>(new BooleanParser());
  }

  private static String toString(Boolean value) {
    if (value == null) {
      return null;
    } else {
      return value.toString();
    }
  }

}
