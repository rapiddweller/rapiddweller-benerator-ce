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

package com.rapiddweller.benerator.parser.xml;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.script.BeneratorScriptFactory;
import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.format.script.ScriptUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link XmlDescriptorParser}.<br/>
 * <br/>
 * Created at 02.01.2009 17:58:45
 *
 * @author Volker Bergmann
 * @since 0.5.7
 */
public class XmlDescriptorParserTest {

  @BeforeClass
  public static void setUpBeneratorScript() {
    ScriptUtil.addFactory("ben", new BeneratorScriptFactory());
    ScriptUtil.setDefaultScriptEngine("ben");
  }

  /**
   * Test unresolved escaping.
   */
  @Test
  public void testUnresolvedEscaping() {
    Element element = XMLUtil.parseStringAsElement("<bla name='a\\tb\\\\c'/>");
    assertEquals("a\\tb\\\\c", XmlDescriptorParser.parseStringAttribute(element, "name", null));
  }

  /** Missing attributes fall back to the supplied default values. */
  @Test
  public void testDefaultsForMissingAttributes() {
    BeneratorContext context = new DefaultBeneratorContext();
    Element element = XMLUtil.parseStringAsElement("<x/>");
    assertEquals(42, XmlDescriptorParser.parseIntAttribute(element, "missing", context, 42));
    assertEquals(7L, XmlDescriptorParser.parseLongAttribute(element, "missing", context, 7L));
    assertTrue(XmlDescriptorParser.parseBooleanAttribute(element, "missing", context, true));
  }

  /** Present attributes are evaluated and converted to the requested primitive type. */
  @Test
  public void testTypedAttributeValues() {
    BeneratorContext context = new DefaultBeneratorContext();
    Element element = XMLUtil.parseStringAsElement("<x n='5' flag='true'/>");
    assertEquals(5, XmlDescriptorParser.parseIntAttribute(element, "n", context, 0));
    assertEquals(5L, XmlDescriptorParser.parseLongAttribute(element, "n", context, 0L));
    assertEquals("5", XmlDescriptorParser.parseStringAttribute(element, "n", context));
    assertTrue(XmlDescriptorParser.parseBooleanAttribute(element, "flag", context, false));
  }

  /** An empty attribute resolves to null. */
  @Test
  public void testEmptyAttributeIsNull() {
    Element element = XMLUtil.parseStringAsElement("<x a=''/>");
    assertNull(XmlDescriptorParser.parseAttribute(element, "a", null));
  }

  /** 'script' and 'dynamicSource' attributes and null values are passed through unevaluated. */
  @Test
  public void testResolveScriptPassthrough() {
    assertEquals("raw", XmlDescriptorParser.resolveScript("script", "raw", null));
    assertEquals("raw", XmlDescriptorParser.resolveScript("dynamicSource", "raw", null));
    assertNull(XmlDescriptorParser.resolveScript("name", null, null));
  }

}
