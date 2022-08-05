/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.WeightedSample;

/**
 * Parses weighted literal lists.<br/><br/>
 * Created: 19.12.2021 19:23:53
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class WeightedLiteralListParser extends AbstractParser<WeightedSample<?>[]> {

  public WeightedLiteralListParser() {
    super("weighted literal list");
  }

  @Override
  protected WeightedSample<?>[] parseImpl(String spec) {
    return DatabeneScriptParser.parseWeightedLiteralList(spec);
  }
}
