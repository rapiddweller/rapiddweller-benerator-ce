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

import com.rapiddweller.benerator.BeneratorError;
import com.rapiddweller.benerator.engine.statement.ErrorStatement;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * Tests the {@link ErrorParser} and the {@link ErrorStatement}.<br/><br/>
 * Created: 12.01.2011 08:58:34
 * @author Volker Bergmann
 * @since o.6.4
 */
public class ErrorParserAndStatementTest extends AbstractBeneratorIntegrationTest {

  @Test(expected = BeneratorError.class)
  public void testNoInfo() {
    ErrorStatement statement = (ErrorStatement) parse("<error/>");
    assertNull(statement.messageEx.evaluate(context));
    assertNull(statement.codeEx.evaluate(context));
    statement.execute(context);
  }

  @Test(expected = BeneratorError.class)
  public void testExecute() {
    ErrorStatement statement = (ErrorStatement) parse("<error>Something bad happened</error>");
    statement.execute(context);
  }

}
