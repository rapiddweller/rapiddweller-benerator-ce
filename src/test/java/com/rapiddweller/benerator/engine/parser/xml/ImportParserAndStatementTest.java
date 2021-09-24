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
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.ImportStatement;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.SyntaxError;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * Tests {@link ImportParser} and {@link ImportStatement}.<br/><br/>
 * Created: 01.05.2010 07:24:25
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class ImportParserAndStatementTest extends AbstractBeneratorIntegrationTest {

  @Test(expected = ConfigurationError.class)
  public void testNoImport() {
    BeneratorContext context = new DefaultBeneratorContext();
    try {
      context.forName("IncrementGenerator");
    } finally {
      IOUtil.close(context);
    }
  }

  @Test
  public void testDefaults() {
    Statement statement = parse("<import defaults='true' />");
    BeneratorContext context = new DefaultBeneratorContext();
    statement.execute(context);
    context.forName("IncrementGenerator");
  }

  @Test
  public void testPlatforms() {
    Statement statement = parse("<import platforms='db, xml' />");
    BeneratorContext context = new DefaultBeneratorContext();
    statement.execute(context);
    context.forName("DBSystem");
    context.forName("XMLEntityExporter");
  }

  @Test
  public void testDomains() {
    Statement statement = parse("<import domains='person, address' />");
    BeneratorContext context = new DefaultBeneratorContext();
    statement.execute(context);
    context.forName("PersonGenerator");
    context.forName("AddressGenerator");
  }

  @Test(expected = SyntaxError.class)
  public void testImportAttributeTypo() {
    parse("<import platmof='typo' />");
  }

}
