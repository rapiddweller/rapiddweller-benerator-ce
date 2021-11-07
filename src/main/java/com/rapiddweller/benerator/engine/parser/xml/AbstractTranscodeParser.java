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

import com.rapiddweller.format.text.SplitStringConverter;
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ConvertingExpression;
import org.w3c.dom.Element;

import java.util.Set;

import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseAttribute;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptAttribute;

/**
 * Parent class for parsers that read parsing related XML elements.
 * It provides functionality for parsing <code>source</code>,
 * <code>target</code> and <code>irrelevantColumns</code>,
 * <code>pageSize</code> and <code>onError</code>.<br/><br/>
 * Created: 11.09.2010 07:12:55
 * @author Volker Bergmann
 * @since 0.6.4
 */
public abstract class AbstractTranscodeParser extends AbstractBeneratorDescriptorParser {

  protected AbstractTranscodeParser(String elementName,
                                 Set<String> requiredAttributes, Set<String> optionalAttributes, Class<?>... supportedParentTypes) {
    super(elementName, requiredAttributes, optionalAttributes, supportedParentTypes);
  }

  @SuppressWarnings("unchecked")
  protected Expression<AbstractDBSystem> parseTarget(Element element) {
    return (Expression<AbstractDBSystem>) parseScriptAttribute("target", element);
  }

  @SuppressWarnings("unchecked")
  protected Expression<AbstractDBSystem> parseSource(Element element) {
    return (Expression<AbstractDBSystem>) parseScriptAttribute("source", element);
  }

  protected Expression<String[]> parseIrrelevantColumns(Element element) {
    return new ConvertingExpression<>(
        parseAttribute("irrelevantColumns", element), new SplitStringConverter(','));
  }

}
