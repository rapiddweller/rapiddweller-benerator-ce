/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.format.xml.AttrInfoSupport;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ENCODING;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_INVALIDATE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ON_ERROR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_OPTIMIZE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SEPARATOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SHELL;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TARGET;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TYPE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_URI;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_EXECUTE;

/**
 * Parses an &lt;execute&gt; directive.<br/><br/>
 * Created: 29.11.2021 13:59:43
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ExecuteParser extends AbstractEvaluateOrExecuteParser {

  private static final AttrInfoSupport ATTR_INFO;
  static {
    ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_EXECUTE_ILLEGAL_ATTR);
    ATTR_INFO.add(ATT_URI, false, BeneratorErrorIds.SYN_EXECUTE_URI);
    ATTR_INFO.add(ATT_TYPE, false, BeneratorErrorIds.SYN_EXECUTE_TYPE);
    ATTR_INFO.add(ATT_SHELL, false, BeneratorErrorIds.SYN_EXECUTE_SHELL);
    ATTR_INFO.add(ATT_TARGET, false, BeneratorErrorIds.SYN_EXECUTE_TARGET);
    ATTR_INFO.add(ATT_SEPARATOR, false, BeneratorErrorIds.SYN_EXECUTE_SEPARATOR);
    ATTR_INFO.add(ATT_ON_ERROR, false, BeneratorErrorIds.SYN_EXECUTE_ON_ERROR);
    ATTR_INFO.add(ATT_ENCODING, false, BeneratorErrorIds.SYN_EXECUTE_ENCODING);
    ATTR_INFO.add(ATT_OPTIMIZE, false, BeneratorErrorIds.SYN_EXECUTE_OPTIMIZE);
    ATTR_INFO.add(ATT_INVALIDATE, false, BeneratorErrorIds.SYN_EXECUTE_INVALIDATE);
  }

  public ExecuteParser() {
    super(EL_EXECUTE, ATTR_INFO);
  }

}

