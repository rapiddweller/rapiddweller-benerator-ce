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

import com.rapiddweller.common.context.DefaultContext;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.common.Expression;
import com.rapiddweller.platform.PersonBean;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.junit.Test;
import org.w3c.dom.Element;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link DescriptorParserUtil}.<br/><br/>
 * Created: 11.04.2011 13:10:30
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class DescriptorParserUtilTest {

  @Test
  public void testNormalizeAttributeName() {
    assertEquals("id", DescriptorParserUtil.normalizeAttributeName("id"));
    assertEquals("pageSize", DescriptorParserUtil.normalizeAttributeName("page.size"));
    assertEquals("enableAutoCommit", DescriptorParserUtil.normalizeAttributeName("enable.auto.commit"));
  }

  @Test
  public void testParseScriptableElementText() {
    Element element = XMLUtil.parseStringAsElement("<text>'\\'Test\\''</text>");

    Expression<String> asIsExpression = DescriptorParserUtil.parseScriptableElementText(element, false);
    System.out.println(asIsExpression);
    assertEquals("'\\'Test\\''", asIsExpression.evaluate(new DefaultContext()));

    Expression<String> unescapingExpression = DescriptorParserUtil.parseScriptableElementText(element, true);
    System.out.println(unescapingExpression);
    assertEquals("''Test''", unescapingExpression.evaluate(new DefaultContext()));
  }

  @Test
  public void testCheckGeneratorVsOuterAttrs_no_generator() {
    Element outer = XMLUtil.parseStringAsElement("<outer><part></part></outer>");
    Element part = XMLUtil.getChildElement(outer, false, true, "part");
    assertNotNull(part);
    DescriptorParserUtil.checkGeneratorVsOuterAttrs(part, "xyz", "err");
  }

  @Test
  public void testMapXmlAttrsToBeanProperties() {
    Element element = XMLUtil.parseStringAsElement("<person name='Alice' age='23'/>");
    PersonBean bean = new PersonBean();
    DescriptorParserUtil.mapXmlAttrsToBeanProperties(element, bean);
    assertEquals("Alice", bean.getName());
    assertEquals(23, bean.getAge());
  }

  @Test
  public void testParseIntAttribute() {
    Element element = XMLUtil.parseStringAsElement("<person age='23'/>");
    assertEquals(23, (DescriptorParserUtil.parseIntAttribute("age", element).evaluate(null)).intValue());
  }

  @Test
  public void testParseIntAttribute_fallback() {
    Element element = XMLUtil.parseStringAsElement("<person/>");
    assertEquals(18, (DescriptorParserUtil.parseIntAttribute("age", element, 18).evaluate(null)).intValue());
  }

  @Test
  public void testParseScriptableStringArrayAttribute_defined() {
    Element element = XMLUtil.parseStringAsElement("<x array='Alice,Bob,Charly'/>");
    Expression<String[]> actualEx = DescriptorParserUtil.parseScriptableStringArrayAttribute("array", element);
    String[] actual = ExpressionUtil.evaluate(actualEx, null);
    String[] expected = new String[] { "Alice", "Bob", "Charly" };
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testParseScriptableStringArrayAttribute_undefined() {
    Element element = XMLUtil.parseStringAsElement("<x/>");
    assertNull(DescriptorParserUtil.parseScriptableStringArrayAttribute("array", element));
  }

  @Test
  public void testParseLongAttribute_defined() {
    Element element = XMLUtil.parseStringAsElement("<person age='23'/>");
    assertEquals(23L, (DescriptorParserUtil.parseLongAttribute("age", element, 18).evaluate(null)).longValue());
  }

  @Test
  public void testParseLongAttribute_undefined() {
    Element element = XMLUtil.parseStringAsElement("<person/>");
    assertEquals(18L, (DescriptorParserUtil.parseIntAttribute("age", element, 18).evaluate(null)).longValue());
  }

  @Test
  public void testParseBooleanExpressionAttribute_defined() {
    Element element = XMLUtil.parseStringAsElement("<person member='true'/>");
    assertTrue((DescriptorParserUtil.parseBooleanExpressionAttribute("member", element, false).evaluate(null)));
  }

  @Test
  public void testParseBooleanExpressionAttribute_undefined() {
    Element element = XMLUtil.parseStringAsElement("<person />");
    assertFalse((DescriptorParserUtil.parseBooleanExpressionAttribute("age", element, false).evaluate(null)));
  }

  @Test
  public void testParseScriptableString() {
    Element element = XMLUtil.parseStringAsElement("<person name='Otto'/>");
    assertEquals("Otto", DescriptorParserUtil.parseScriptableString(element, "name", "err").evaluate(null));
  }

  @Test
  public void testParseScriptableStringAttribute_unescape() {
    Element element = XMLUtil.parseStringAsElement("<person name='Otto'/>");
    assertEquals("Otto", DescriptorParserUtil.parseScriptableStringAttribute("name", element, true).evaluate(null));
  }

  @Test
  public void testGetAttributeAsString() {
    Element element = XMLUtil.parseStringAsElement("<person name='Otto'/>");
    assertEquals("Otto", DescriptorParserUtil.getAttributeAsString("name", element));
  }

}
