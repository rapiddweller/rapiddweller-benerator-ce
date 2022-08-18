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
import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.benerator.engine.expression.ScriptableExpression;
import com.rapiddweller.benerator.engine.expression.TypedScriptExpression;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.script.ScriptUtil;
import com.rapiddweller.format.text.SplitStringConverter;
import com.rapiddweller.common.Expression;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.ConvertingExpression;
import com.rapiddweller.script.expression.ExpressionUtil;
import com.rapiddweller.script.expression.StringExpression;
import com.rapiddweller.script.expression.TypeConvertingExpression;
import com.rapiddweller.script.expression.UnescapeExpression;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.beans.PropertyDescriptor;

import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_GENERATE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.EL_ITERATE;

/**
 * Provides utility methods for XML descriptor parsing.<br/><br/>
 * Created: 19.02.2010 09:32:33
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DescriptorParserUtil {

  private DescriptorParserUtil() {
    // private constructor to prevent instantiation
  }

  public static void validateGeneratorAttribute(Element part, String errorId) {
    checkGeneratorVsOuterAttrs(part, "type", errorId);
    checkGeneratorVsOuterAttrs(part, "name", errorId);
  }

  static void checkGeneratorVsOuterAttrs(Element part, String parentAttr, String errorId) {
    Attr generatorAttr = part.getAttributeNode("generator");
    if (generatorAttr != null) {
      Element outer = part;
      while (outer.getParentNode() instanceof Element) {
        outer = (Element) outer.getParentNode();
        String nodeName = outer.getNodeName();
        if (!EL_GENERATE.equals(nodeName) && !EL_ITERATE.equals(nodeName)) {
          break;
        }
        checkGeneratorVsOuterAttr(part, outer, parentAttr, errorId);
      }
    }
  }

  private static void checkGeneratorVsOuterAttr(Element part, Element outer, String parentAttr, String errorId) {
    Attr generatorAttr = part.getAttributeNode("generator");
    if (generatorAttr != null) {
      String generatorSpec = generatorAttr.getValue();
      if (generatorSpec.equals(outer.getAttribute(parentAttr))) {
        throw BeneratorExceptionFactory.getInstance().illegalXmlAttributeValue(
            "The value of " + part.getNodeName() + ".generator must not be the same as the " +
                parentAttr + " '" + generatorSpec + "' " + "of the surrounding <" + outer.getNodeName() +
                "> element", null, errorId, generatorAttr);
      }
    }
  }

  // mapping attributes to bean properties ---------------------------------------------------------------------------

  public static <T> T mapXmlAttrsToBeanProperties(Element element, T bean) {
    for (String attrName : XMLUtil.getAttributes(element).keySet()) {
      try {
        Expression<String> expression = parseScriptableStringAttribute(attrName, element);
        String propertyName = normalizeAttributeName(attrName);
        PropertyDescriptor descriptor = BeanUtil.getPropertyDescriptor(bean.getClass(), propertyName);
        Object value = expression;
        if (!Expression.class.equals(descriptor.getPropertyType())) {
          value = ExpressionUtil.evaluate(expression, null);
        }
        BeanUtil.setPropertyValue(bean, propertyName, value, false);
      } catch (Exception e) {
        throw BeneratorExceptionFactory.getInstance().illegalXmlAttributeValue(
            "Error processing '" + attrName + "' attribute of " + XMLUtil.format(element),
            e, BeneratorErrorIds.UNSPECIFIC, element.getAttributeNode(attrName));
      }
    }
    return bean;
  }

  public static String normalizeAttributeName(String attrName) {
    String[] tokens = attrName.split("\\.");
    StringBuilder builder = new StringBuilder(tokens[0]);
    for (int i = 1; i < tokens.length; i++)
      builder.append(StringUtil.capitalize(tokens[i]));
    return builder.toString();
  }

  // creating expressions for data retrieval -------------------------------------------------------------------------

  public static Expression<String> parseScriptableElementText(Element element, boolean unescape) {
    String text = XMLUtil.getText(element);
    if (text != null) {
      String trimmedText = text.trim();
      if (ScriptUtil.isScript(trimmedText)) {
        text = trimmedText;
      }
    }
    Expression<String> result = new StringExpression(new ScriptableExpression(text, null));
    if (unescape) {
      result = new UnescapeExpression(result);
    }
    return result;
  }

  public static Expression<String[]> parseScriptableStringArrayAttribute(String name, Element element) {
    String attribute = getAttributeAsString(name, element);
    if (attribute == null) {
      return null;
    }
    Expression<String> rawEx = new TypeConvertingExpression<>(
        new ScriptableExpression(attribute, null), String.class);
    return new ConvertingExpression<>(rawEx, new SplitStringConverter(','));
  }

  public static Expression<Integer> parseIntAttribute(String name, Element element) {
    return new TypedScriptExpression<>(getAttributeAsString(name, element), Integer.class);
  }

  public static Expression<Integer> parseIntAttribute(String name, Element element, int defaultValue) {
    return parseIntAttribute(name, element, new ConstantExpression<>(defaultValue));
  }

  public static Expression<Integer> parseIntAttribute(String name, Element element, Expression<Integer> defaultValue) {
    String attribute = getAttributeAsString(name, element);
    if (StringUtil.isEmpty(attribute)) {
      return defaultValue;
    } else {
      return new TypedScriptExpression<>(attribute, Integer.class);
    }
  }

  public static Expression<Long> parseLongAttribute(String name, Element element, long defaultValue) {
    return parseLongAttribute(name, element, new ConstantExpression<>(defaultValue));
  }

  public static Expression<Long> parseLongAttribute(String name, Element element, Expression<Long> defaultValue) {
    String attribute = getAttributeAsString(name, element);
    if (StringUtil.isEmpty(attribute)) {
      return defaultValue;
    } else {
      return new TypedScriptExpression<>(attribute, Long.class);
    }
  }

  public static Expression<Boolean> parseBooleanExpressionAttribute(String name, Element element) {
    return parseBooleanExpressionAttribute(name, element, null);
  }

  public static Expression<Boolean> parseBooleanExpressionAttribute(String name, Element element, Boolean defaultValue) {
    String attribute = getAttributeAsString(name, element);
    if (StringUtil.isEmpty(attribute)) {
      return new ConstantExpression<>(defaultValue);
    } else {
      return new TypedScriptExpression<>(attribute, Boolean.class);
    }
  }

  public static Expression<?> parseScriptAttribute(String name, Element element) {
    String rawAttribute = getAttributeAsString(name, element);
    if (StringUtil.isEmpty(rawAttribute)) {
      return null;
    } else {
      return new ScriptExpression<>(rawAttribute);
    }
  }

  // scriptable strings ----------------------------------------------------------------------------------------------

  public static Expression<String> parseScriptableString(Element element, String attrName, String errorId) {
    try {
      return parseScriptableStringAttribute(attrName, element);
    } catch (ConversionException e) {
      throw BeneratorExceptionFactory.getInstance().illegalXmlAttributeValue(null, null,
          errorId, element.getAttributeNode(attrName));
    }
  }

  public static Expression<String> parseScriptableStringAttribute(String name, Element element) {
    return parseScriptableStringAttribute(name, element, true);
  }

  public static Expression<String> parseScriptableStringAttribute(String name, Element element, boolean unescape) {
    String attribute = getAttributeAsString(name, element);
    if (attribute == null) {
      return null;
    }
    Expression<String> result = new StringExpression(new ScriptableExpression(attribute, null));
    if (unescape) {
      result = new UnescapeExpression(result);
    }
    return result;
  }

  // Attribute value as Expression<String> ---------------------------------------------------------------------------

  public static ConstantExpression<String> getConstantStringAttributeAsExpression(String name, Element element) {
    return getConstantStringAttributeAsExpression(name, element, false, null);
  }

  public static ConstantExpression<String> getConstantStringAttributeAsExpression(
      String name, Element element, boolean required, String errorId) {
    return new ConstantExpression<>(getAttributeAsString(name, element, required, errorId));
  }

  // direct data retrieval -------------------------------------------------------------------------------------------

  public static String getAttributeAsString(String name, Element element) {
    return getAttributeAsString(name, element, false, null);
  }

  public static String getAttributeAsString(String name, Element element, boolean required, String errorId) {
    String attribute = element.getAttribute(name);
    if (StringUtil.isEmpty(attribute)) {
      if (required) {
        throw BeneratorExceptionFactory.getInstance().missingXmlAttribute(null, errorId, name, element);
      } else {
        return null;
      }
    }
    return attribute;
  }

  public static String getElementText(Element element) {
    return XMLUtil.getText(element);
  }

}
