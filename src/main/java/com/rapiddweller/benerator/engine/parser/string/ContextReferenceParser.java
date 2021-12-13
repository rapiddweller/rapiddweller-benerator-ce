/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.engine.expression.context.ContextReference;
import com.rapiddweller.common.parser.AbstractParser;

/**
 * Parses a String which refers to an object in the context.<br/><br/>
 * Created: 13.12.2021 13:06:08
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class ContextReferenceParser extends AbstractParser<ContextReference> {

  public ContextReferenceParser() {
    super("context reference");
  }

  @Override
  protected ContextReference parseImpl(String spec) {
    return new ContextReference(spec);
  }

}