/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.parser.RegexBasedStringParser;

/**
 * Parses an error id.<br/><br/>
 * Created: 09.12.2021 16:12:30
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ErrorIdParser extends RegexBasedStringParser {
  public ErrorIdParser() {
    super("error id", "[A-Z]{3}-[0-9]{4}");
  }
}
