/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
import com.rapiddweller.benerator.engine.DescriptorConstants;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.attr.EncodingAttribute;
import com.rapiddweller.benerator.engine.parser.attr.IdAttribute;
import com.rapiddweller.benerator.engine.parser.attr.OnErrorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.SeparatorAttribute;
import com.rapiddweller.benerator.engine.parser.attr.UriAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.statement.EvaluateStatement;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.parser.AbstractTypedParser;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.FilePathParser;
import com.rapiddweller.common.xml.XMLAssert;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.FeatureAccessExpression;
import com.rapiddweller.script.expression.StringExpression;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableElementText;

/**
 * Parses an &lt;evaluate&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 01:01:02
 * @author Volker Bergmann
 * @since 0.6.0
 */
public abstract class AbstractEvaluateOrExecuteParser extends AbstractBeneratorDescriptorParser {

  protected final AttrInfo<String> id = new IdAttribute(null, false);
  protected final AttrInfo<Expression<String>> uri = new UriAttribute(null, false);
  protected final AttrInfo<String> type = new AttrInfo<>(ATT_TYPE, false, null, new TypeParser(), null);
  protected final AttrInfo<Expression<String>> shell = new AttrInfo<>(ATT_SHELL, false, null, new ScriptableParser<>(new FilePathParser("shell", false)));
  protected final AttrInfo<String> target = new AttrInfo<>(ATT_TARGET, false, null, new IdParser());
  protected final AttrInfo<Expression<Character>> separator = new SeparatorAttribute(null, ';');
  protected final AttrInfo<Expression<String>> onError = new OnErrorAttribute(null);
  protected final AttrInfo<Expression<String>> encoding = new EncodingAttribute(null);
  protected final AttrInfo<Expression<Boolean>> optimize = new AttrInfo<>(ATT_OPTIMIZE, false, null, new ScriptableParser<>(new BooleanParser()));
  protected final AttrInfo<Expression<Boolean>> invalidate = new AttrInfo<>(ATT_INVALIDATE, false, null, new ScriptableParser<>(new BooleanParser()));
  protected final AttrInfo<Expression<Boolean>> assertAttr = new AttrInfo<>(ATT_ASSERT, false, null, new ScriptParser<>(Boolean.class));

  protected AbstractEvaluateOrExecuteParser(String elementName, AttrInfoSupport attrSupport, Class<?>... supportedParentTypes) {
    super(elementName, attrSupport, supportedParentTypes);
  }

  @Override
  public boolean supports(Element element, Element[] parentXmlPath, Statement[] parentComponentPath) {
    return supportsElementName(element.getNodeName());
  }

  @Override
  public EvaluateStatement doParse(Element element, Element[] parentXmlPath, Statement[] parentPath, BeneratorParseContext context) {
    boolean evaluate = DescriptorConstants.EL_EVALUATE.equals(element.getNodeName());
    if (evaluate) {
      XMLAssert.assertAtLeastOneAttributeIsSet(element, BeneratorErrorIds.SYN_EVALUATE_RESULT, ATT_ID, ATT_ASSERT);
    } else {
      XMLAssert.assertAttributeIsNotSet(ATT_ID, element, BeneratorErrorIds.SYN_EXECUTE_ILLEGAL_ATTR);
      XMLAssert.assertAttributeIsNotSet(ATT_ASSERT, element, BeneratorErrorIds.SYN_EXECUTE_ILLEGAL_ATTR);
    }
    String idVal = id.parse(element);
    Expression<String> uriEx = uri.parse(element);
    String typeVal = type.parse(element);
    String targetSpec = target.parse(element);
    Expression<?> targetEx = (targetSpec != null ? new FeatureAccessExpression<>(targetSpec) : null);
    Expression<String> shellEx = shell.parse(element);
    Expression<Character> separatorEx = separator.parse(element);
    Expression<String> onErrorEx = onError.parse(element);
    Expression<String> encodingEx = encoding.parse(element);
    Expression<Boolean> optimizeEx = optimize.parse(element);
    Expression<Boolean> invalidateEx = invalidate.parse(element);
    Expression<?> assertionEx = assertAttr.parse(element);
    Expression<String> textEx = new StringExpression(parseScriptableElementText(element, false));
    return new EvaluateStatement(evaluate, idVal, textEx, uriEx, typeVal, targetEx, shellEx, separatorEx, onErrorEx,
        encodingEx, optimizeEx, invalidateEx, assertionEx);
  }

  static class TypeParser extends AbstractTypedParser<String> {

    private final Set<String> supportedTypes;

    protected TypeParser() {
      super("type", String.class);
      this.supportedTypes = new HashSet<>();
      this.supportedTypes.add(EvaluateStatement.TYPE_SHELL);
      this.supportedTypes.add(EvaluateStatement.TYPE_SQL);
      this.supportedTypes.addAll(ScriptUtil.getFactoryIds());
    }

    @Override
    protected String parseImpl(String spec) {
      if (spec != null && !supportedTypes.contains(spec)) {
        String help = "Choose one of these: " + CollectionUtil.formatCommaSeparatedList(supportedTypes, null);
        throw ExceptionFactory.getInstance().illegalArgument(
            null,
            null, BeneratorErrorIds.SYN_EVALUATE_TYPE).withHelp(help);
      }
      return spec;
    }
  }

  class ElementValidator implements Validator<Element> {

    private final String errorId;

    public ElementValidator(String errorId) {
      this.errorId = errorId;
    }

    @Override
    public boolean valid(Element element) {
      if (StringUtil.isEmpty(element.getTextContent()) && uri.parse(element) == null) {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "The script to execute must either be specified with a 'url' attribute or as XML element content",
            null, errorId, element);
      }
      return true;
    }
  }

}
