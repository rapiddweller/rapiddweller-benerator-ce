/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.PositiveIntegerParser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_THREADS;

/**
 * {@link AttrInfo} for the number of threads to execute a task.<br/><br/>
 * Created: 19.12.2021 21:17:18
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ThreadsAttribute extends AttrInfo<Expression<Integer>> {
  public ThreadsAttribute(String errorId) {
    super(ATT_THREADS, false, errorId, new ScriptableParser<>(new PositiveIntegerParser()), "1");
  }
}
