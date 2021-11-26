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
import com.rapiddweller.benerator.engine.statement.EchoStatement;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.script.Expression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_MESSAGE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_TYPE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ECHO;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableElementText;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableStringAttribute;

/**
 * Parses an &lt;echo&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:30:29
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class EchoParser extends AbstractBeneratorDescriptorParser {

  public EchoParser() {
    super(EL_ECHO, null, CollectionUtil.toSet(ATT_MESSAGE, ATT_TYPE));
  }

  @Override
  public EchoStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
    Expression<String> messageEx;
    if (!StringUtil.isEmpty(element.getAttribute(ATT_MESSAGE))) {
      messageEx = parseScriptableStringAttribute(ATT_MESSAGE, element);
    } else {
      messageEx = parseScriptableElementText(element, true);
    }
    Expression<String> typeEx = parseScriptableStringAttribute("type", element);
    return new EchoStatement(messageEx, typeEx);
  }

}
