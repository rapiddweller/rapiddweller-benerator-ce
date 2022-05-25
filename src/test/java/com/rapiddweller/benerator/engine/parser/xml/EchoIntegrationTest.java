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

import com.rapiddweller.benerator.engine.statement.EchoStatement;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link EchoParser}.<br/><br/>
 * Created: 11.02.2010 15:16:48
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class EchoIntegrationTest extends AbstractBeneratorIntegrationTest {

  @Test
  public void testSimpleMessageAttribute() {
    EchoStatement statement = (EchoStatement) parseXmlString("<echo message='Hello' />");
    assertEquals("Hello", statement.getExpression().evaluate(context));
  }

  @Test
  public void testSimpleElementText() {
    EchoStatement statement = (EchoStatement) parseXmlString("<echo>Hello</echo>");
    assertEquals("Hello", statement.getExpression().evaluate(context));
  }

  @Test
  public void testEscapedMessageAttribute() {
    EchoStatement statement = (EchoStatement) parseXmlString("<echo message=\"\\'Test\\'\" />");
    assertEquals("'Test'", statement.getExpression().evaluate(context));
  }

  @Test
  public void testEscapedElementText() {
    EchoStatement statement = (EchoStatement) parseXmlString("<echo>\\'Test\\'</echo>");
    assertEquals("'Test'", statement.getExpression().evaluate(context));
  }

  @Test
  public void testScriptedMessageAttribute() {
    checkNameResolution("<echo message='{ftl:Hello ${name}}'/>");
  }

  @Test
  public void testScriptedMessageAttributeWithLangAttribute() {
    checkNameResolution("<echo lang='ftl' message='Hello ${name}'/>");
  }

  @Test
  public void testScriptedMessageElementText() {
    checkNameResolution("<echo>{ftl:Hello ${name}}</echo>");
  }

  @Test
  public void testScriptedMessageElementTextWithLangAttribute() {
    checkNameResolution("<echo lang='ftl'>Hello ${name}</echo>");
  }

  @Test
  public void testScriptedMessageElementTextWithLineBreak() {
    checkNameResolution("<echo>\n  {ftl:Hello ${name}}\n</echo>");
  }

  @Test
  public void testLeafElement() {
    EchoStatement statement = (EchoStatement) parseXmlString("<echo/>");
    assertEquals("", statement.getExpression().evaluate(context));
  }

  @Test
  public void testEmptyElement() {
    EchoStatement statement = (EchoStatement) parseXmlString("<echo></echo>");
    assertEquals("", statement.getExpression().evaluate(context));
  }

  private void checkNameResolution(String xmlText) {
    EchoStatement statement = (EchoStatement) parseXmlString(xmlText);
    context.set("name", "Volker");
    assertEquals("Hello Volker", statement.getExpression().evaluate(context));
  }

}
