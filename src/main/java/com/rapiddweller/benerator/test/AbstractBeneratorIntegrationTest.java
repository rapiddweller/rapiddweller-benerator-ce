/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.benerator.test;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorMonitor;
import com.rapiddweller.benerator.engine.ResourceManagerSupport;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.xml.BeneratorParseContext;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.ConfigUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.xml.XMLUtil;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.w3c.dom.Element;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Parent class for Benerator integration tests.<br/><br/>
 * Created: 10.08.2010 07:07:42
 * @author Volker Bergmann
 * @since 0.6.4
 */
public abstract class AbstractBeneratorIntegrationTest extends GeneratorTest {

  protected ResourceManagerSupport resourceManager;

  @Before
  public void setUpEnvironment() {
    System.setProperty(GeneratorContext.CELL_SEPARATOR_SYSPROP, "" + GeneratorContext.DEFAULT_CELL_SEPARATOR);
    this.resourceManager = new ResourceManagerSupport();
  }

  @After
  public void tearDown() {
    this.resourceManager.close();
    System.setProperty(GeneratorContext.CELL_SEPARATOR_SYSPROP, "" + GeneratorContext.DEFAULT_CELL_SEPARATOR);
  }

  protected void assumePostgresEnabled() {
    Assume.assumeTrue("Postgres testing is deactivated", ConfigUtil.isTestActive("postgres"));
  }

  protected BeneratorContext parseAndExecuteFile(String filename) {
    try {
      Assert.notNull(filename, "file name");
      String xml = IOUtil.getContentOfURI(filename);
      String contextUri = IOUtil.getParentUri(filename);
      if (contextUri.length() > 1 && contextUri.endsWith("/")) {
        contextUri = contextUri.substring(0, contextUri.length() - 1);
      }
      context.setContextUri(contextUri);
      return parseAndExecute(xml);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected BeneratorContext parseAndExecuteRoot(String xml) {
    context = BeneratorFactory.getInstance().createRootContext(".");
    Statement statement = parse(xml);
    statement.execute(context);
    return context;
  }

  public BeneratorContext parseAndExecute(String xml) {
    Statement statement = parse(xml);
    statement.execute(context);
    return context;
  }

  public Statement parse(String xml) {
    Element element = XMLUtil.parseStringAsElement(xml);
    BeneratorParseContext parsingContext = BeneratorFactory.getInstance().createParseContext(resourceManager);
    return parsingContext.parseElement(element, null);
  }

  public void assertMinGenerations(int expectedGenerations, Runnable task) {
    long c0 = BeneratorMonitor.INSTANCE.getTotalGenerationCount();
    task.run();
    long actualGenerations = BeneratorMonitor.INSTANCE.getTotalGenerationCount() - c0;
    assertTrue("Expected a minimum of " + expectedGenerations + " generations, " +
        "but had only " + actualGenerations, actualGenerations >= expectedGenerations);
  }

}
