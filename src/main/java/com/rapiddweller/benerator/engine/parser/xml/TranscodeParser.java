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
import com.rapiddweller.benerator.engine.statement.MutatingTypeExpression;
import com.rapiddweller.benerator.engine.statement.TranscodeStatement;
import com.rapiddweller.benerator.engine.statement.TranscodingTaskStatement;
import com.rapiddweller.common.ArrayUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.FallbackExpression;
import org.w3c.dom.Element;

import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.getAttributeAsString;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableStringAttribute;

/**
 * Parses a &lt;transcode&gt; element.<br/><br/>
 * Created: 08.09.2010 16:13:13
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class TranscodeParser extends AbstractTranscodeParser {

  private static final Set<String> MEMBER_ELEMENTS = CollectionUtil.toSet(EL_ID, EL_ATTRIBUTE, EL_REFERENCE);

  private static final AttrInfoSupport ATTR_INFO;
  static {
    ATTR_INFO = new AttrInfoSupport(BeneratorErrorIds.SYN_TRANSCODE_ILLEGAL_ATTR);
    ATTR_INFO.add(ATT_TABLE, true, BeneratorErrorIds.SYN_TRANSCODE_SOURCE);
    ATTR_INFO.add(ATT_SOURCE, false, BeneratorErrorIds.SYN_TRANSCODE_SOURCE);
    ATTR_INFO.add(ATT_SELECTOR, false, BeneratorErrorIds.SYN_TRANSCODE_SELECTOR);
    ATTR_INFO.add(ATT_TARGET, false, BeneratorErrorIds.SYN_TRANSCODE_TARGET);
    ATTR_INFO.add(ATT_PAGESIZE, false, BeneratorErrorIds.SYN_TRANSCODE_PAGE_SIZE);
    ATTR_INFO.add(ATT_ON_ERROR, false, BeneratorErrorIds.SYN_TRANSCODE_ON_ERROR);
  }

  public TranscodeParser() {
    super(EL_TRANSCODE, ATTR_INFO, TranscodingTaskStatement.class);
  }

  @Override
  public Statement doParse(
      Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    String table = DescriptorParserUtil.getAttributeAsString(ATT_TABLE, element);
    TranscodingTaskStatement parent = (TranscodingTaskStatement) ArrayUtil.lastElementOf(parentComponentPath);
    Expression<AbstractDBSystem> sourceEx = parseSource(element, parent);
    Expression<String> selectorEx = parseSelector(element, parent);
    Expression<AbstractDBSystem> targetEx = parseTarget(element, parent);
    Expression<Long> pageSizeEx = parsePageSize(element, parent);
    Expression<ErrorHandler> errorHandlerEx = parseOnErrorAttribute(element, table);
    TranscodeStatement result = new TranscodeStatement(new MutatingTypeExpression(element, getRequiredAttribute("table", element)),
        parent, sourceEx, selectorEx, targetEx, pageSizeEx, errorHandlerEx);
    Element[] currentXmlPath = ArrayUtil.append(element, parentXmlPath);
    Statement[] currentPath = context.createSubPath(parentComponentPath, result);
    for (Element child : XMLUtil.getChildElements(element)) {
      String childName = child.getNodeName();
      if (!MEMBER_ELEMENTS.contains(childName)) {
        result.addSubStatement(context.parseChildElement(child, currentXmlPath, currentPath));
      }
      // The 'component' child elements (id, attribute, reference) are handled by the MutatingTypeExpression
    }
    return result;
  }

  private static Expression<String> parseSelector(Element element, TranscodingTaskStatement parent) {
    return parseScriptableStringAttribute("selector", element);
  }

  private Expression<Long> parsePageSize(Element element, Statement parent) {
    Expression<Long> result = super.parsePageSize(element);
    if (parent instanceof TranscodingTaskStatement) {
      result = new FallbackExpression<>(result, ((TranscodingTaskStatement) parent).getPageSizeEx());
    }
    return result;
  }

  private Expression<AbstractDBSystem> parseSource(Element element, Statement parent) {
    Expression<AbstractDBSystem> result = super.parseSource(element);
    if (parent instanceof TranscodingTaskStatement) {
      result = new FallbackExpression<>(result, ((TranscodingTaskStatement) parent).getSourceEx());
    }
    return result;
  }

  private Expression<AbstractDBSystem> parseTarget(Element element, Statement parent) {
    Expression<AbstractDBSystem> result = super.parseTarget(element);
    if (parent instanceof TranscodingTaskStatement) {
      result = new FallbackExpression<>(result, ((TranscodingTaskStatement) parent).getTargetEx());
    }
    return result;
  }

}
