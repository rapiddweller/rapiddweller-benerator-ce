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
import com.rapiddweller.benerator.engine.statement.IncludeStatement;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.SyntaxError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Integration test for {@link IncludeParser} and {@link IncludeStatement}.<br/><br/>
 * Created: 12.03.2010 10:37:04
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class IncludeParserAndStatementTest extends AbstractBeneratorIntegrationTest {

  private static final String DESC_URI = "com/rapiddweller/benerator/engine/parser/xml/included.ben.xml";
  private static final String PROP_URI = "com/rapiddweller/benerator/engine/parser/xml/included.properties";

  @Test
  public void testIncludePropertiesFile() {
    Statement statement = parse("<include uri='" + PROP_URI + "' />");
    statement.execute(context);
    assertEquals("done", context.get("incProp"));
  }

  @Test
  public void testIncludeDescriptorFile() {
    Statement statement = parse("<include uri='" + DESC_URI + "' />");
    statement.execute(context);
    assertEquals("done", context.get("incProp"));
  }

  @Test(expected = SyntaxError.class)
  public void testAttributeTypo() {
    parse("<include urr='bla.bla' />");
  }

}
