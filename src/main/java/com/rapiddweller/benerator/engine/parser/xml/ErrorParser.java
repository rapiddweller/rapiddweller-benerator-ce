/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.statement.ErrorStatement;
import com.rapiddweller.common.parser.NonNegativeIntegerParser;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttributeInfo;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseIntAttribute;

/**
 * Parses Benerator's &lt;error&gt; descriptor XML element and maps it to an {@link ErrorStatement}.<br/><br/>
 * Created: 12.01.2011 09:03:58
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class ErrorParser extends AbstractBeneratorDescriptorParser {

  private static final AttributeInfo<String> ID = new AttributeInfo<>(
      ATT_ID, false, BeneratorErrorIds.SYN_ERROR_ID, null, null);

  private static final AttributeInfo<Integer> EXIT_CODE = new AttributeInfo<>(
      ATT_EXIT_CODE, false, BeneratorErrorIds.SYN_ERROR_EXIT_CODE, null, new NonNegativeIntegerParser());

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(
      BeneratorErrorIds.SYN_ERROR_ILLEGAL_ATTR, ID, EXIT_CODE);

  public ErrorParser() {
    super(EL_ERROR, ATTR_INFO);
  }

  @Override
  public ErrorStatement doParse(Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    attrSupport.validate(element);
    String errorId = ID.parse(element);
    Integer exitCode = EXIT_CODE.parse(element);
    String message = DescriptorParserUtil.getElementText(element);
    return new ErrorStatement(errorId, exitCode, message);
  }

}
