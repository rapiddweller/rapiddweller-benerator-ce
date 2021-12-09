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
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.statement.EchoStatement;
import com.rapiddweller.benerator.engine.statement.EchoType;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.parser.EnumParser;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.format.xml.AttributeInfo;
import com.rapiddweller.script.Expression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableElementText;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableStringAttribute;

/**
 * Parses an &lt;echo&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:30:29
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class EchoParser extends AbstractBeneratorDescriptorParser {

  private static final AttributeInfo<String> MESSAGE = new AttributeInfo<>(
      ATT_MESSAGE, false, BeneratorErrorIds.SYN_ECHO_MESSAGE, null, null);

  private static final AttributeInfo<Expression<EchoType>> TYPE = new AttributeInfo<>(
      ATT_TYPE, false, BeneratorErrorIds.SYN_ECHO_TYPE,
      "console", new ScriptableParser<>(new EnumParser<>(EchoType.class)));

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(
      BeneratorErrorIds.SYN_ECHO, new EchoValidator(), MESSAGE, TYPE);

  public EchoParser() {
    super(EL_ECHO, ATTR_INFO);
  }

  @Override
  public EchoStatement doParse(
      Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    attrSupport.validate(element);
    Expression<String> messageEx = parseMessage(element);
    Expression<EchoType> typeEx = TYPE.parse(element);
    return new EchoStatement(messageEx, typeEx);
  }

  private Expression<String> parseMessage(Element element) {
    Expression<String> messageEx;
    if (!StringUtil.isEmpty(element.getAttribute(ATT_MESSAGE))) {
      messageEx = parseScriptableStringAttribute(ATT_MESSAGE, element);
    } else {
      messageEx = parseScriptableElementText(element, true);
    }
    return messageEx;
  }

  static class EchoValidator implements Validator<Element> {
    @Override
    public boolean valid(Element element) {
      boolean hasMsgText = !StringUtil.isEmpty(element.getTextContent());
      boolean hasMsgAttr = MESSAGE.isDefinedIn(element);
      if (hasMsgText && hasMsgAttr) {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "<echo> must contain either a message attribute or a text content, not both",
            null, BeneratorErrorIds.SYN_ECHO_MESSAGE, element);
      } else if (!hasMsgText && !hasMsgAttr) {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "<echo> must contain either a message attribute or a text content, but none of them is specified",
            null, BeneratorErrorIds.SYN_ECHO, element);
      }
      return true;
    }
  }

}
