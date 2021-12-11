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

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.engine.statement.WaitStatement;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.common.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link WaitParser} and {@link WaitStatement}.<br/><br/>
 * Created: 21.02.2010 08:04:48
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class WaitParserAndStatementTest extends AbstractBeneratorIntegrationTest {

  @Test
  public void testConstantDuration() {
    Element element = XMLUtil.parseStringAsElement("<wait duration='12'/>");
    BeneratorContext context = new DefaultBeneratorContext();
    WaitStatement statement = (WaitStatement) new WaitParser().parse(element, null, null, null);
    assertEquals(12, statement.generateDuration(context));
    statement.execute(context);
  }

  @Test
  public void testDistributedDuration() {
    Element element = XMLUtil.parseStringAsElement(
        "<wait min='11' max='25' granularity='2' distribution='step'/>");
    BeneratorContext context = new DefaultBeneratorContext();
    WaitStatement statement = (WaitStatement) new WaitParser().parse(element, null, null, null);
    for (int i = 0; i < 5; i++) {
      assertEquals(11 + i * 2, statement.generateDuration(context));
    }
    statement.execute(context);
  }

  @Test(expected = SyntaxError.class)
  public void testMutualExclusionFull() {
    parse("<wait duration='1000' min='11' max='25' granularity='2' distribution='step'/>");
  }

  @Test(expected = SyntaxError.class)
  public void testMutualExclusionMin() {
    parse("<wait duration='1000' min='11'/>");
  }

  @Test(expected = SyntaxError.class)
  public void testMutualExclusionMax() {
    parse("<wait duration='1000' max='11'/>");
  }

  @Test(expected = SyntaxError.class)
  public void testMutualExclusionDistribution() {
    parse("<wait duration='1000' distribution='random'/>");
  }

  @Test(expected = SyntaxError.class)
  public void testMutualExclusionGranularity() {
    parse("<wait duration='1000' granularity='1000'/>");
  }

}
