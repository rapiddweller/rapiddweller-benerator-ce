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
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.DescriptorConstants;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.expression.ScriptableExpression;
import com.rapiddweller.benerator.engine.expression.context.ContextReference;
import com.rapiddweller.benerator.engine.parser.attr.NameAttribute;
import com.rapiddweller.benerator.engine.parser.string.ContextReferenceParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableObjectParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.engine.statement.SetSettingStatement;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.common.parser.StringParser;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.CompositeExpression;
import com.rapiddweller.script.expression.DynamicExpression;
import com.rapiddweller.script.expression.ExpressionUtil;
import com.rapiddweller.script.expression.IsNullExpression;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;

/**
 * Parses a &lt;Property&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 00:58:53
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class SettingParser extends AbstractBeneratorDescriptorParser {

  public static final NameAttribute NAME = new NameAttribute(BeneratorErrorIds.SYN_SETTING_NAME, true, true);

  public static final AttrInfo<Expression<String>> DEFAULT = new AttrInfo<>(
      ATT_DEFAULT,false, BeneratorErrorIds.SYN_SETTING_DEFAULT,
      new ScriptableParser<>(new StringParser("setting value")));

  public static final AttrInfo<Expression<Object>> VALUE = new AttrInfo<>(
      ATT_VALUE, false, BeneratorErrorIds.SYN_SETTING_VALUE,
      new ScriptableObjectParser("legal setting value"));

  public static final AttrInfo<ContextReference> REF = new AttrInfo<>(
      ATT_REF, false, BeneratorErrorIds.SYN_SETTING_REF, new ContextReferenceParser());

  public static final AttrInfo<Expression<Object>> SOURCE = new AttrInfo<>(
      ATT_SOURCE, false, BeneratorErrorIds.SYN_SETTING_SOURCE, new SourceParser());

  private static final AttrInfoSupport ATTR_INFO = new AttrInfoSupport(
      BeneratorErrorIds.SYN_SETTING_ILLEGAL_ATTR, NAME, VALUE, DEFAULT, REF, SOURCE);

  public SettingParser() {
    super(DescriptorConstants.EL_SETTING, ATTR_INFO);
  }

  @Override
  public Statement doParse(
      Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    String propertyName = NAME.parse(element);
    if (element.hasAttribute(ATT_DEFAULT)) {
      return parseDefault(propertyName, element.getAttribute(ATT_DEFAULT));
    } else {
      Expression<?> valueEx = parseValueRefSourceOrChildren(element);
      return new SetSettingStatement(propertyName, valueEx);
    }
  }

  public static Expression<Object> parseValueRefSourceOrChildren(Element element) {
    if (element.hasAttribute(ATT_VALUE)) {
      return VALUE.parse(element);
    } else if (element.hasAttribute(ATT_REF)) {
      return REF.parse(element);
    } else if (element.hasAttribute(ATT_SOURCE)) {
      return SOURCE.parse(element);
    } else {
      return parseChildElements(element);
    }
  }

  /** Maps child elements to a collection or array */
  @SuppressWarnings("unchecked")
  private static Expression<Object> parseChildElements(Element element) {
    Element[] childElements = XMLUtil.getChildElements(element);
    Expression<Object>[] subExpressions = new Expression[childElements.length];
    for (int j = 0; j < childElements.length; j++) {
      subExpressions[j] = (Expression<Object>) BeanParser.parseBeanExpression(childElements[j], false);
    }
    switch (subExpressions.length) {
      case 0:
        throw ExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "Not a valid property spec", element);
      case 1:
        return subExpressions[0];
      default:
        return new CompositeExpression<>(subExpressions) {
          @Override
          public Object[] evaluate(Context context) {
            return ExpressionUtil.evaluateAll(terms, context);
          }
        };
    }
  }

  private static Statement parseDefault(String propertyName, String defaultValue) {
    try {
      ScriptableExpression valueExpression = new ScriptableExpression(defaultValue, null);
      SetSettingStatement setterStatement = new SetSettingStatement(propertyName, valueExpression);
      Expression<Boolean> condition = new IsNullExpression(new ContextReference(propertyName));
      return new IfStatement(condition, setterStatement);
    } catch (Exception e) {
      throw ExceptionFactory.getInstance().configurationError(
          "Error parsing property default value expression: " + defaultValue, e);
    }
  }

  private static class SourceParser extends AbstractParser<Expression<Object>> {

    protected SourceParser() {
      super("source");
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Expression<Object> parseImpl(String spec) {
      return new SourceExpression(DatabeneScriptParser.parseBeanSpec(spec));
    }
  }

  /**
   * Evaluates a 'source' expression to a Generator.<br/><br/>
   * Created: 26.10.2009 08:38:44
   * @author Volker Bergmann
   * @since 0.6.0
   */
  private static class SourceExpression<E> extends DynamicExpression<E> {

    final Expression<Generator<E>> source;

    public SourceExpression(Expression<Generator<E>> source) {
      this.source = source;
    }

    @Override
    public E evaluate(Context context) {
      Generator<E> generator = source.evaluate(context);
      ProductWrapper<E> wrapper = generator.generate(new ProductWrapper<>());
      if (wrapper == null) {
        throw ExceptionFactory.getInstance().configurationError("Generator not available: " + generator);
      }
      return wrapper.unwrap();
    }
  }

}
