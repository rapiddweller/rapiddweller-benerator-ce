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

import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.TranscodingTaskStatement;
import com.rapiddweller.benerator.engine.statement.WhileStatement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.platform.db.DBSystem;
import com.rapiddweller.script.Expression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DEFAULT_SOURCE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_IDENTITY;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ON_ERROR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_PAGESIZE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TARGET;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_TRANSCODING_TASK;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableStringAttribute;

/**
 * Parses Benerator's &lt;transcode&gt; XML descriptor element.<br/><br/>
 * Created: 10.09.2010 18:14:53
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class TranscodingTaskParser extends AbstractTranscodeParser {

  /**
   * Instantiates a new Transcoding task parser.
   */
  public TranscodingTaskParser() {
    super(EL_TRANSCODING_TASK,
        CollectionUtil.toSet(ATT_TARGET),
        CollectionUtil.toSet(ATT_IDENTITY, ATT_DEFAULT_SOURCE, ATT_PAGESIZE, ATT_ON_ERROR),
        BeneratorRootStatement.class, IfStatement.class, WhileStatement.class);
  }

  @Override
  public Statement doParse(Element element, Statement[] parentPath, BeneratorParseContext parsingContext) {
    Expression<ErrorHandler> errorHandlerExpression = parseOnErrorAttribute(element, "transcodingTask");
    TranscodingTaskStatement statement = new TranscodingTaskStatement(
        parseDefaultSource(element),
        parseTarget(element),
        parseIdentity(element),
        parsePageSize(element),
        errorHandlerExpression);
    Statement[] subPath = parsingContext.createSubPath(parentPath, statement);
    statement.setSubStatements(parsingContext.parseChildElementsOf(element, subPath));
    return statement;
  }

  private static Expression<String> parseIdentity(Element element) {
    return parseScriptableStringAttribute("identity", element);
  }

  /**
   * Parse default source expression.
   *
   * @param element the element
   * @return the expression
   */
  @SuppressWarnings("unchecked")
  protected Expression<DBSystem> parseDefaultSource(Element element) {
    return (Expression<DBSystem>) parseScriptAttribute("defaultSource", element);
  }

}
