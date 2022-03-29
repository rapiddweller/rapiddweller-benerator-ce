/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.engine.DescriptorConstants;
import com.rapiddweller.format.xml.AttrInfoSupport;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_ASSERT;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_ENCODING;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_ID;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_ILLEGAL_ATTR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_INVALIDATE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_ON_ERROR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_OPTIMIZE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_SEPARATOR;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_SHELL;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_TARGET;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_TEXT;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_TYPE;
import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_EVALUATE_URI;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an &lt;evaluate&gt; directive.<br/><br/>
 * Created: 29.11.2021 13:45:17
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class EvaluateParser extends AbstractEvaluateOrExecuteParser {

  public EvaluateParser() {
    super(EL_EVALUATE, null);
    this.id.setErrorId(SYN_EVALUATE_ID);
    this.uri.setErrorId(SYN_EVALUATE_URI);
    this.type.setErrorId(SYN_EVALUATE_TYPE);
    this.shell.setErrorId(SYN_EVALUATE_SHELL);
    this.target.setErrorId(SYN_EVALUATE_TARGET);
    this.separator.setErrorId(SYN_EVALUATE_SEPARATOR);
    this.onError.setErrorId(SYN_EVALUATE_ON_ERROR);
    this.encoding.setErrorId(SYN_EVALUATE_ENCODING);
    this.optimize.setErrorId(SYN_EVALUATE_OPTIMIZE);
    this.invalidate.setErrorId(SYN_EVALUATE_INVALIDATE);
    this.assertAttr.setErrorId(SYN_EVALUATE_ASSERT);
    this.attrSupport = new AttrInfoSupport(SYN_EVALUATE_ILLEGAL_ATTR, new ElementValidator(SYN_EVALUATE_TEXT),
        id, uri, type, shell, target, separator, onError, encoding, optimize, invalidate, assertAttr);
  }

  @Override
  public boolean supportsElementName(String elementName) {
    return DescriptorConstants.EL_EVALUATE.equals(elementName);
  }

}
