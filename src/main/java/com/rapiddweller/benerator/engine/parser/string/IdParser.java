/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.parser.RegexBasedStringParser;

/**
 * Parses an id.<br/><br/>
 * Created: 08.12.2021 15:25:04
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class IdParser extends RegexBasedStringParser {

  public IdParser() {
    super("id", "[A-Za-z_][A-Za-z0-9_-]*");
  }

}
