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

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.ErrorStatement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.StringExpression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TYPE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ERROR;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseIntAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableElementText;

/**
 * Parses Benerator's &lt;error&gt; descriptor XML element and maps it to an {@link ErrorStatement}.<br/><br/>
 * Created: 12.01.2011 09:03:58
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class ErrorParser extends AbstractBeneratorDescriptorParser {

  /**
   * Instantiates a new Error parser.
   */
  public ErrorParser() {
    super(EL_ERROR, null, CollectionUtil.toSet(ATT_TYPE));
  }

  @Override
  public ErrorStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
    Expression<String> messageEx = new StringExpression(parseScriptableElementText(element, true));
    Expression<Integer> codeEx = parseIntAttribute(ATT_TYPE, element);
    return new ErrorStatement(messageEx, codeEx);
  }

}
