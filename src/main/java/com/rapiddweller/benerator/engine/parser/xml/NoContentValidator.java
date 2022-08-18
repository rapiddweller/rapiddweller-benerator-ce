/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.common.Validator;
import com.rapiddweller.common.xml.XMLAssert;
import org.w3c.dom.Element;

/**
 * Asserts that an XML has no text content.<br/><br/>
 * Created: 13.12.2021 06:49:42
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class NoContentValidator implements Validator<Element> {

  private final String errorId;

  public NoContentValidator(String errorId) {
    this.errorId = errorId;
  }

  @Override
  public boolean valid(Element element) {
    XMLAssert.assertNoTextContent(element, errorId);
    return true;
  }

}
