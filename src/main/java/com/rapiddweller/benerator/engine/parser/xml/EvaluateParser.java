/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.format.xml.AttrInfoSupport;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an &lt;evaluate&gt; directive.<br/><br/>
 * Created: 29.11.2021 13:45:17
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class EvaluateParser extends AbstractEvaluateOrExecuteParser {

  private static final AttrInfoSupport ATTR_INFO;
  static {
    ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_EVALUATE);
    ATTR_INFO.add(ATT_ID, false, BeneratorErrorIds.SYN_EVALUATE_ID);
    ATTR_INFO.add(ATT_URI, false, BeneratorErrorIds.SYN_EVALUATE_URI);
    ATTR_INFO.add(ATT_TYPE, false, BeneratorErrorIds.SYN_EVALUATE_TYPE);
    ATTR_INFO.add(ATT_SHELL, false, BeneratorErrorIds.SYN_EVALUATE_SHELL);
    ATTR_INFO.add(ATT_TARGET, false, BeneratorErrorIds.SYN_EVALUATE_TARGET);
    ATTR_INFO.add(ATT_SEPARATOR, false, BeneratorErrorIds.SYN_EVALUATE_SEPARATOR);
    ATTR_INFO.add(ATT_ON_ERROR, false, BeneratorErrorIds.SYN_EVALUATE_ON_ERROR);
    ATTR_INFO.add(ATT_ENCODING, false, BeneratorErrorIds.SYN_EVALUATE_ENCODING);
    ATTR_INFO.add(ATT_OPTIMIZE, false, BeneratorErrorIds.SYN_EVALUATE_OPTIMIZE);
    ATTR_INFO.add(ATT_INVALIDATE, false, BeneratorErrorIds.SYN_EVALUATE_INVALIDATE);
    ATTR_INFO.add(ATT_ASSERT, false, BeneratorErrorIds.SYN_EVALUATE_ASSERT);
  }

  public EvaluateParser() {
    super(EL_EVALUATE, ATTR_INFO);
  }

}
