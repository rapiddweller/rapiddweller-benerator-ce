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
import com.rapiddweller.benerator.engine.parser.string.ScriptParser;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.SequentialStatement;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttributeInfo;
import com.rapiddweller.script.Expression;
import org.w3c.dom.Element;

import java.util.List;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses an &lt;if&gt; element.<br/><br/>
 * Created: 19.02.2010 09:07:51
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class IfParser extends AbstractBeneratorDescriptorParser {

  private static final AttributeInfo<Expression<Boolean>> TEST = new AttributeInfo<>(
      ATT_TEST, true, BeneratorErrorIds.SYN_IF_TEST, null, new ScriptParser<>(Boolean.class));

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_IF_ILLEGAL_ATTR, TEST);

  public IfParser() {
    super(EL_IF, ATTR_INFO);
  }

  @Override
  public Statement doParse(Element ifElement, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    // validate XML
    attrSupport.validate(ifElement);
    assertOnlyTheseChildNames(ifElement, EL_THEN, EL_ELSE, EL_COMMENT);
    Element thenElement = getUniqueChild(ifElement, "then", true);
    Element elseElement = getUniqueChild(ifElement, "else", false);
    if (elseElement != null && thenElement == null) {
      throw ExceptionFactory.getInstance().syntaxErrorForXmlElement("'else' without 'then'", elseElement);
    }
    // parse content
    Expression<Boolean> condition = TEST.parse(ifElement);
    IfStatement ifStatement = new IfStatement(condition);
    // parse children
    parseChildren(ifElement, parentXmlPath, parentComponentPath, context, thenElement, elseElement, ifStatement);
    return ifStatement;
  }

  private static void parseChildren(Element ifElement, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context,
                         Element thenElement, Element elseElement, IfStatement ifStatement) {
    Statement[] ifPath = context.createSubPath(parentComponentPath, ifStatement);
    if (thenElement != null) {
      // parse <if><then>...commands...</then></if>
      List<Statement> thenStatements = context.parseChildElementsOf(thenElement, parentXmlPath, ifPath);
      ifStatement.setThenStatement(new SequentialStatement(thenStatements));
    } else {
      // parse <if>...commands...</if>
      List<Statement> thenStatements = context.parseChildElementsOf(ifElement, parentXmlPath, ifPath);
      ifStatement.setThenStatement(new SequentialStatement(thenStatements));
    }
    if (elseElement != null) {
      // parse <if><then></then><else>...commands...</else></if>
      List<Statement> elseStatements = context.parseChildElementsOf(elseElement, parentXmlPath, ifPath);
      ifStatement.setElseStatement(new SequentialStatement(elseStatements));
    }
  }

}
