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
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.TranscodingTaskStatement;
import com.rapiddweller.benerator.engine.statement.WhileStatement;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.common.Expression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableStringAttribute;

/**
 * Parses Benerator's &lt;transcode&gt; XML descriptor element.<br/><br/>
 * Created: 10.09.2010 18:14:53
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class TranscodingTaskParser extends AbstractTranscodeParser {

  private static final AttrInfoSupport ATTR_INFO;
  static {
    ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_TRANSCODING_TASK_ILLEGAL_ATTR);
    ATTR_INFO.add(ATT_TARGET, true, BeneratorErrorIds.SYN_TRANSCODING_TASK_TARGET);
    ATTR_INFO.add(ATT_IDENTITY, false, BeneratorErrorIds.SYN_TRANSCODING_TASK_IDENTITY);
    ATTR_INFO.add(ATT_DEFAULT_SOURCE, false, BeneratorErrorIds.SYN_TRANSCODING_TASK_DEFAULT_SOURCE);
    ATTR_INFO.add(ATT_PAGESIZE, false, BeneratorErrorIds.SYN_TRANSCODING_TASK_PAGE_SIZE);
    ATTR_INFO.add(ATT_ON_ERROR, false, BeneratorErrorIds.SYN_TRANSCODING_TASK_ON_ERROR);
  }

  public TranscodingTaskParser() {
    super(EL_TRANSCODING_TASK, ATTR_INFO, BeneratorRootStatement.class, IfStatement.class, WhileStatement.class);
  }

  @Override
  public Statement doParse(Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext parsingContext) {
    Expression<ErrorHandler> errorHandlerExpression = parseOnErrorAttribute(element, "transcodingTask");
    TranscodingTaskStatement statement = new TranscodingTaskStatement(
        parseDefaultSource(element),
        parseTarget(element),
        parseIdentity(element),
        parsePageSize(element),
        errorHandlerExpression);
    Element[] subXmlPath = ArrayUtil.append(element, parentXmlPath);
    Statement[] subComponentPath = parsingContext.createSubPath(parentComponentPath, statement);
    statement.setSubStatements(parsingContext.parseChildElementsOf(element, subXmlPath, subComponentPath));
    return statement;
  }

  private static Expression<String> parseIdentity(Element element) {
    return parseScriptableStringAttribute("identity", element);
  }

  @SuppressWarnings("unchecked")
  protected Expression<AbstractDBSystem> parseDefaultSource(Element element) {
    return (Expression<AbstractDBSystem>) parseScriptAttribute("defaultSource", element);
  }

}
