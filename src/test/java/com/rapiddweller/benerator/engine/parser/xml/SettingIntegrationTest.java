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
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.common.exception.SyntaxError;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link SettingParser}.<br/><br/>
 * Created: 18.02.2010 22:46:46
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class SettingIntegrationTest extends AbstractBeneratorIntegrationTest {

  @Test
  public void testValue() {
    parseAndExecuteXmlString("<setting name='globalProp' value='XYZ' />");
    assertEquals("XYZ", context.get("globalProp"));
  }

  @Test
  public void testEscapedValue() {
    parseAndExecuteXmlString("<setting name='globalProp' value=\"\\'\\t\\'\" />");
    assertEquals("'\t'", context.get("globalProp"));
  }

  @Test
  public void testDateValue() {
    parseAndExecuteXmlString("<setting name='globalProp' value='{new java.util.Date()}' />");
    Object prop = context.get("globalProp");
    assertTrue(prop instanceof Date);
    long expected = TimeUtil.millisSinceOwnEpoch((Date) prop);
    long actual = TimeUtil.millisSinceOwnEpoch((Date) prop);
    assertEquals(expected, actual, 2000.);
  }

  @Test
  public void testDefault_undefined() {
    parseAndExecuteXmlString("<setting name='globalProp' default='XYZ' />");
    assertEquals("XYZ", context.get("globalProp"));
  }

  @Test
  public void testDefault_predefined() {
    Statement statement = parseXmlString("<setting name='globalProp' default='XYZ' />");
    context.setGlobal("globalProp", "ZZZ");
    statement.execute(context);
    assertEquals("ZZZ", context.get("globalProp"));
  }

  @Test
  public void testRef() {
    context.setGlobal("setting", "cfg");
    parseAndExecuteXmlString("<setting name='globalProp' ref='setting' />");
    assertEquals("cfg", context.get("globalProp"));
  }

  @Test
  public void testSource() {
    context.setGlobal("myGen", new ConstantGenerator<>("myProd"));
    parseAndExecuteXmlString("<setting name='globalProp' source='myGen' />");
    assertEquals("myProd", context.get("globalProp"));
  }

  @Test
  public void testNestedBean() {
    parseAndExecuteXmlString(
        "<setting name='globalProp'>" +
            "	<bean spec='new com.rapiddweller.benerator.engine.parser.xml.BeanMock(123)'/>" +
            "</setting>");
    assertEquals(123, ((BeanMock) context.get("globalProp")).lastValue);
  }

  @Test
  public void testNestedBeanArray() {
    parseAndExecuteXmlString(
        "<setting name='globalProp'>" +
            "	<bean spec='new com.rapiddweller.benerator.engine.parser.xml.BeanMock(1)'/>" +
            "	<bean spec='new com.rapiddweller.benerator.engine.parser.xml.BeanMock(2)'/>" +
            "</setting>");
    Object[] beans = (Object[]) context.get("globalProp");
    assertEquals(2, beans.length);
    assertEquals(1, ((BeanMock) beans[0]).lastValue);
    assertEquals(2, ((BeanMock) beans[1]).lastValue);
  }

  @Test(expected = SyntaxError.class)
  public void testInvalid() {
    parseAndExecuteXmlString("<setting name='globalProp' xyz='XYZ' />");
  }

  @Test
  public void testBeneratorProperty() {
    assertNotEquals(123, context.getDefaultPageSize());
    parseAndExecuteXmlString("<setting name='context.defaultPageSize' value='123' />");
    assertEquals(123, context.getDefaultPageSize());
  }

}
