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

package com.rapiddweller.benerator.distribution;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the definition of custom weight functions.<br/><br/>
 * Created: 09.07.2010 07:23:54
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class CustomWeightFunctionIntegrationTest extends AbstractBeneratorIntegrationTest {

  final String xml =
      "<generate type='entity' count='1000' consumer='cons'>" +
          "	<attribute name='c' values=\"'a', 'b', 'c'\" " +
          "distribution='new " + StandardWeightingFunction.class.getName() + "(50,30,20)' />" +
          "</generate>";

  @SuppressWarnings("unchecked")
  @Test
  public void test() {
    ConsumerMock consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
    parseAndExecute(xml);
    List<Entity> products = (List<Entity>) consumer.getProducts();
    assertEquals(1000, products.size());
    int a = 0, b = 0, c = 0;
    for (Entity e : products) {
      String val = (String) e.get("c");
      switch (val.charAt(0)) {
        case 'a':
          a++;
          break;
        case 'b':
          b++;
          break;
        case 'c':
          c++;
          break;
        default:
          fail("expected 'a', 'b' or 'c', found: " + val.charAt(0));
      }
    }
    assertTrue(a > b);
    assertTrue(b > c);
  }

}

