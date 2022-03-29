/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.engine.DescriptorConstants;
import com.rapiddweller.format.xml.AttrInfoSupport;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_ENCODING;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_ILLEGAL_ATTR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_INVALIDATE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_ON_ERROR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_OPTIMIZE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_SEPARATOR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_SHELL;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_TARGET;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_TEXT;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_TYPE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EXECUTE_URI;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_EXECUTE;

/**
 * Parses an &lt;execute&gt; directive.<br/><br/>
 * Created: 29.11.2021 13:59:43
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class ExecuteParser extends AbstractEvaluateOrExecuteParser {

  public ExecuteParser() {
    super(EL_EXECUTE, null);
    this.uri.setErrorId(SYN_EXECUTE_URI);
    this.type.setErrorId(SYN_EXECUTE_TYPE);
    this.shell.setErrorId(SYN_EXECUTE_SHELL);
    this.target.setErrorId(SYN_EXECUTE_TARGET);
    this.separator.setErrorId(SYN_EXECUTE_SEPARATOR);
    this.onError.setErrorId(SYN_EXECUTE_ON_ERROR);
    this.encoding.setErrorId(SYN_EXECUTE_ENCODING);
    this.optimize.setErrorId(SYN_EXECUTE_OPTIMIZE);
    this.invalidate.setErrorId(SYN_EXECUTE_INVALIDATE);
    this.attrSupport = new AttrInfoSupport(SYN_EXECUTE_ILLEGAL_ATTR, new ElementValidator(SYN_EXECUTE_TEXT),
        uri, type, shell, target, separator, onError, encoding, optimize, invalidate);
  }

  @Override
  public boolean supportsElementName(String elementName) {
    return DescriptorConstants.EL_EXECUTE.equals(elementName);
  }

}

