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

import com.rapiddweller.benerator.engine.expression.ScriptExpression;
import com.rapiddweller.benerator.engine.expression.ScriptableExpression;
import com.rapiddweller.benerator.engine.expression.TypedScriptExpression;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.text.SplitStringConverter;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.ConvertingExpression;
import com.rapiddweller.script.expression.StringExpression;
import com.rapiddweller.script.expression.TypeConvertingExpression;
import com.rapiddweller.script.expression.UnescapeExpression;
import org.w3c.dom.Element;

/**
 * Provides utility methods for XML descriptor parsing.<br/><br/>
 * Created: 19.02.2010 09:32:33
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class DescriptorParserUtil {

  // direct data retrieval -------------------------------------------------------------------------------------------

  /**
   * Gets attribute.
   *
   * @param name    the name
   * @param element the element
   * @return the attribute
   */
  public static String getAttribute(String name, Element element) {
    return (element.hasAttribute(name) ? element.getAttribute(name) : null);
  }

  /**
   * Gets element text.
   *
   * @param element the element
   * @return the element text
   */
  public static String getElementText(Element element) {
    return XMLUtil.getText(element);
  }

  // creating expressions for data retrieval -------------------------------------------------------------------------

  /**
   * Parse scriptable element text expression.
   *
   * @param element  the element
   * @param unescape the unescape
   * @return the expression
   */
  public static Expression<String> parseScriptableElementText(Element element, boolean unescape) {
    Expression<String> result = new StringExpression(new ScriptableExpression(XMLUtil.getText(element), null));
    if (unescape) {
      result = new UnescapeExpression(result);
    }
    return result;
  }

  /**
   * Parse scriptable string attribute expression.
   *
   * @param name    the name
   * @param element the element
   * @return the expression
   */
  public static Expression<String> parseScriptableStringAttribute(String name, Element element) {
    return parseScriptableStringAttribute(name, element, true);
  }

  /**
   * Parse scriptable string attribute expression.
   *
   * @param name     the name
   * @param element  the element
   * @param unescape the unescape
   * @return the expression
   */
  public static Expression<String> parseScriptableStringAttribute(String name, Element element, boolean unescape) {
    String attribute = getAttribute(name, element);
    if (attribute == null) {
      return null;
    }
    Expression<String> result = new StringExpression(new ScriptableExpression(attribute, null));
    if (unescape) {
      result = new UnescapeExpression(result);
    }
    return result;
  }

  /**
   * Parse scriptable string array attribute expression.
   *
   * @param name    the name
   * @param element the element
   * @return the expression
   */
  public static Expression<String[]> parseScriptableStringArrayAttribute(String name, Element element) {
    String attribute = getAttribute(name, element);
    if (attribute == null) {
      return null;
    }
    Expression<String> rawEx = new TypeConvertingExpression<>(
        new ScriptableExpression(attribute, null), String.class);
    return new ConvertingExpression<>(rawEx, new SplitStringConverter(','));
  }

  /**
   * Parse int attribute expression.
   *
   * @param name    the name
   * @param element the element
   * @return the expression
   */
  public static Expression<Integer> parseIntAttribute(String name, Element element) {
    return new TypedScriptExpression<>(getAttribute(name, element), Integer.class);
  }

  /**
   * Parse int attribute expression.
   *
   * @param name         the name
   * @param element      the element
   * @param defaultValue the default value
   * @return the expression
   */
  public static Expression<Integer> parseIntAttribute(String name, Element element, int defaultValue) {
    return parseIntAttribute(name, element, new ConstantExpression<>(defaultValue));
  }

  /**
   * Parse int attribute expression.
   *
   * @param name         the name
   * @param element      the element
   * @param defaultValue the default value
   * @return the expression
   */
  public static Expression<Integer> parseIntAttribute(String name, Element element, Expression<Integer> defaultValue) {
    String attribute = getAttribute(name, element);
    if (StringUtil.isEmpty(attribute)) {
      return defaultValue;
    } else {
      return new TypedScriptExpression<>(attribute, Integer.class);
    }
  }

  /**
   * Parse long attribute expression.
   *
   * @param name         the name
   * @param element      the element
   * @param defaultValue the default value
   * @return the expression
   */
  public static Expression<Long> parseLongAttribute(String name, Element element, long defaultValue) {
    return parseLongAttribute(name, element, new ConstantExpression<>(defaultValue));
  }

  /**
   * Parse long attribute expression.
   *
   * @param name         the name
   * @param element      the element
   * @param defaultValue the default value
   * @return the expression
   */
  public static Expression<Long> parseLongAttribute(String name, Element element, Expression<Long> defaultValue) {
    String attribute = getAttribute(name, element);
    if (StringUtil.isEmpty(attribute)) {
      return defaultValue;
    } else {
      return new TypedScriptExpression<>(attribute, Long.class);
    }
  }

  /**
   * Parse boolean expression attribute expression.
   *
   * @param name    the name
   * @param element the element
   * @return the expression
   */
  public static Expression<Boolean> parseBooleanExpressionAttribute(String name, Element element) {
    return parseBooleanExpressionAttribute(name, element, null);
  }

  /**
   * Parse boolean expression attribute expression.
   *
   * @param name         the name
   * @param element      the element
   * @param defaultValue the default value
   * @return the expression
   */
  public static Expression<Boolean> parseBooleanExpressionAttribute(String name, Element element, Boolean defaultValue) {
    String attribute = getAttribute(name, element);
    if (StringUtil.isEmpty(attribute)) {
      return new ConstantExpression<>(defaultValue);
    } else {
      return new TypedScriptExpression<>(attribute, Boolean.class);
    }
  }

  /**
   * Parse attribute constant expression.
   *
   * @param name    the name
   * @param element the element
   * @return the constant expression
   */
  public static ConstantExpression<String> parseAttribute(String name, Element element) {
    String attribute = getAttribute(name, element);
    return (attribute != null ? new ConstantExpression<>(attribute) : null);
  }

  /**
   * Parse script attribute expression.
   *
   * @param name    the name
   * @param element the element
   * @return the expression
   */
  public static Expression<?> parseScriptAttribute(String name, Element element) {
    String rawAttribute = getAttribute(name, element);
    if (StringUtil.isEmpty(rawAttribute)) {
      return null;
    } else {
      return new ScriptExpression<>(rawAttribute);
    }
  }

}
