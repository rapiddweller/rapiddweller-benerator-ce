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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Benerator integration test for array generation.<br/><br/>
 * Created: 08.08.2011 16:57:47
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class ArrayGenerationIntegrationTest extends AbstractBeneratorIntegrationTest {

  private ConsumerMock consumer;

  /**
   * Sets up consumer.
   */
  @Before
  public void setUpConsumer() {
    consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
  }

  /**
   * Test simple case.
   */
  @SuppressWarnings("checkstyle:FileTabCharacter")
  @Test
  public void testSimpleCase() {
    parseAndExecuteXmlString(
        "<generate count='2' consumer='cons'>" +
            "	<value type='int' constant='3'/>" +
            "	<value type='string' constant='x'/>" +
            "</generate>");
    List<Object[]> products = getConsumedEntities();
    assertEquals(2, products.size());
    for (Object[] product : products) {
      assertEquals(2, product.length);
      assertEquals(3, product[0]);
      assertEquals("x", product[1]);
    }
  }


  // helpers ---------------------------------------------------------------------------------------------------------

  /**
   * Gets consumed entities.
   *
   * @return the consumed entities
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected List<Object[]> getConsumedEntities() {
    return (List) consumer.getProducts();
  }
}
