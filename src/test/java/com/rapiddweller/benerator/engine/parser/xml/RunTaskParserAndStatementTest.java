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

import com.rapiddweller.benerator.engine.statement.RunTaskStatement;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.task.PageListenerMock;
import com.rapiddweller.task.TaskMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Parses an XML &lt;run-task&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 26.10.2009 07:07:40
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class RunTaskParserAndStatementTest extends AbstractBeneratorIntegrationTest {

  @Before
  public void setUpTaskMock() {
    TaskMock.count.set(0);
  }

  @Test
  public void testSingleThreaded() {
    String xml =
        "<run-task id='myId' class='com.rapiddweller.task.TaskMock' count='5' pageSize='2' stats='true' " +
            "      pager='new com.rapiddweller.task.PageListenerMock(1)'>" +
            "  <property name='intProp' value='42' />" +
            "</run-task>";
    RunTaskStatement statement = (RunTaskStatement) parse(xml);
    assertEquals(5L, statement.getCount().evaluate(context).longValue());
    assertEquals(2L, statement.getPageSize().evaluate(context).longValue());
    assertEquals(new PageListenerMock(1), statement.getPager().evaluate(context));
    statement.execute(context);
    assertEquals(5, TaskMock.count.get());
  }

  @Test
  public void testMultiThreaded() {
    String xml =
        "<run-task id='myId' class='com.rapiddweller.task.TaskMock' count='5' pageSize='2' threads='2' stats='true' " +
            "      pager='new com.rapiddweller.task.PageListenerMock(1)'>" +
            "  <property name='intProp' value='42' />" +
            "</run-task>";
    RunTaskStatement statement = (RunTaskStatement) parse(xml);
    assertEquals(5L, statement.getCount().evaluate(context).longValue());
    assertEquals(2L, statement.getPageSize().evaluate(context).longValue());
    assertEquals(new PageListenerMock(1), statement.getPager().evaluate(context));
    statement.execute(context);
    assertEquals(5, TaskMock.count.get());
  }

}
