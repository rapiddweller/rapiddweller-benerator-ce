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
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import com.rapiddweller.platform.db.AbstractDBSystem;
import com.rapiddweller.platform.db.DefaultDBSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link AbstractEvaluateOrExecuteParser} with respect to the features used
 * in the &lt;execute&gt; element.<br/><br/>
 * Created: 30.10.2009 08:11:56
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class ExecuteParserAndStatementTest extends AbstractBeneratorIntegrationTest {

  @Test
  public void testBeanInvocation() {
    Statement statement = parseXmlString("<execute>bean.invoke(2)</execute>");
    BeanMock bean = new BeanMock();
    context.set("bean", bean);
    statement.execute(context);
    assertEquals(1, bean.invocationCount);
    assertEquals(2, bean.lastValue);
  }

  @Test
  public void testSimpleTypeVariableDefinition() {
    Statement statement = parseXmlString("<execute>x = 3</execute>");
    statement.execute(context);
    assertEquals(3, context.get("x"));
  }

  @Test
  public void testSimpleTypeVariableAccess() {
    context.setGlobal("x", 3);
    Statement statement = parseXmlString("<execute>x = x + 2</execute>");
    statement.execute(context);
    assertEquals(5, context.get("x"));
  }

  @Test(expected = ConfigurationError.class)
  public void testSqlExecutionWithoutTarget() {
    Statement statement = parseXmlString("<execute type='sql'>create sequence seq</execute>");
    statement.execute(context);
  }

  @Test
  public void testEmptyResultSet() {
    String url = HSQLUtil.getInMemoryURL("benerator");
    AbstractDBSystem db = new DefaultDBSystem("db", url, HSQLUtil.DRIVER, "sa", null, context.getDataModel());
    BeneratorContext context = new DefaultBeneratorContext();
    context.setGlobal("db", db);
    try {
      db.execute("create table epast_test (id int)");
      Statement statement = parseXmlString("<execute target='db'>select * from epast_test where 1 = 0</execute>");
      statement.execute(context);
    } finally {
      db.execute("drop table epast_test");
      db.close();
    }
  }

  @Test
  public void testDbInvalidationDefault() {
    String url = HSQLUtil.getInMemoryURL("benerator");
    AbstractDBSystem db = new DefaultDBSystem("db", url, HSQLUtil.DRIVER, "sa", null, context.getDataModel());
    BeneratorContext context = new DefaultBeneratorContext();
    context.setGlobal("db", db);
    assertEquals(0, db.invalidationCount());
    try {
      db.execute("create table epast_test (id int)");
      Statement statement = parseXmlString("<execute target='db'>select * from epast_test where 1 = 0</execute>");
      statement.execute(context);
      assertEquals(1, db.invalidationCount());
      Statement statement2 = parseXmlString("<execute target='db'>create table BBB (id int)</execute>");
      statement2.execute(context);
      assertEquals(2, db.invalidationCount());
    } finally {
      db.execute("drop table epast_test");
      db.close();
    }
  }

  @Test
  public void testDbInvalidationOverride() {
    String url = HSQLUtil.getInMemoryURL("benerator");
    AbstractDBSystem db = new DefaultDBSystem("db", url, HSQLUtil.DRIVER, "sa", null, context.getDataModel());
    BeneratorContext context = new DefaultBeneratorContext();
    context.setGlobal("db", db);
    assertEquals(0, db.invalidationCount());
    try {
      db.execute("create table epast_test (id int)");
      Statement statement = parseXmlString("<execute target='db' invalidate='true'>select * from epast_test where 1 = 0</execute>");
      statement.execute(context);
      assertEquals(2, db.invalidationCount());
      Statement statement2 = parseXmlString("<execute target='db' invalidate='false'>create table AAA (id int)</execute>");
      statement2.execute(context);
      assertEquals(2, db.invalidationCount());
    } finally {
      db.execute("drop table epast_test");
      db.close();
    }
  }

}
