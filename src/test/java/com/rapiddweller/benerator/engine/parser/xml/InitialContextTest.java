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
import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.benerator.factory.ConsumerMock;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.SysUtil;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests access to Benerator's InitialContext in descriptor file.<br/><br/>
 * Created: 21.10.2009 19:25:37
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class InitialContextTest extends GeneratorTest {

  private static final String DESCRIPTOR_XML = "string://<setup>" +
      "<bean id='ctx' class='com.rapiddweller.platform.jndi.InitialContext'>" +
      "  <property name='factory' value='com.rapiddweller.platform.jndi.InitialContextFactoryMock' />" +
      "  <property name='url' value='lru' />" +
      "  <property name='user' value='resu' />" +
      "  <property name='password' value='drowssap' />" +
      "</bean>" +
      "<generate name='Person' count='1' consumer=\"ctx.lookup('cons')\">" +
      "  <attribute name='name' constant='Alice'/>" +
      "</generate>" +
      "</setup>";

  /**
   * Test.
   */
  @Test
  public void test() {
    SysUtil.runWithSystemProperty("jndi.properties", "com/rapiddweller/benerator/engine/jndi.properties",
        () -> {
          ConsumerMock.lastInstance = null;
          DescriptorRunner runner = new DescriptorRunner(DESCRIPTOR_XML, context);
          try {
            BeneratorContext context = runner.getContext();
            context.setValidate(false);
            runner.run();
            assertNotNull("Consumer was not invoked", ConsumerMock.lastInstance.lastProduct);
            assertEquals("Alice", ((Entity) ConsumerMock.lastInstance.lastProduct).get("name"));
          } catch (IOException e) {
            throw new RuntimeException(e);
          } finally {
            IOUtil.close(runner);
          }
        });
  }

}
