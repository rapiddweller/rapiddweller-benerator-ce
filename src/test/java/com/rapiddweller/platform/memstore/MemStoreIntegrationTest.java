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

package com.rapiddweller.platform.memstore;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for the {@link MemStore} class.<br/><br/>
 * Created: 08.03.2011 16:06:12
 * @author Volker Bergmann
 * @since 0.6.6
 */
public class MemStoreIntegrationTest extends AbstractBeneratorIntegrationTest {

  private MemStore dst;
  private ConsumerMock consumer;

  /** Sets up source, consumer and descriptor. */
  @Before
  public void setUpConsumerAndDescriptor() {
    consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);

    // create source store and prefill it
    MemStore src = new MemStore("src", context.getDataModel());
    context.setGlobal("src", src);
    ComplexTypeDescriptor descriptor = createComplexType("product");
    descriptor.setComponent(createId("id", "int"));
    for (int i = 3; i < 6; i++) {
      src.store(new Entity(descriptor, "id", i));
    }
    context.getDataModel().addDescriptorProvider(src);

    // create dest store
    dst = new MemStore("dst", context.getDataModel());
    context.setGlobal("dst", dst);
  }


  // test methods ----------------------------------------------------------------------------------------------------

  @Test
  public void testStore() {
    MemStore.ignoreClose = true;
    parseAndExecute(
        "<generate type='product' count='3' consumer='dst'>" +
            "	<id name='id' type='int' />" +
            "</generate>"
    );
    Collection<Entity> products = dst.getEntities("product");
    assertEquals(3, products.size());
    int index = 1;
    for (Entity product : products) {
      assertNotNull(product);
      assertEquals(index, product.get("id"));
      index++;
    }
  }

  /** Test iteration. */
  @SuppressWarnings("unchecked")
  @Test
  public void testIterate() {
    MemStore.ignoreClose = false;
    parseAndExecute("<iterate source='src' type='product' consumer='cons'/>");
    List<Entity> products = (List<Entity>) consumer.getProducts();
    assertEquals(3, products.size());
    int index = 3;
    for (Entity product : products) {
      assertNotNull(product);
      assertEquals(index, product.get("id"));
      index++;
    }
  }

  /** Test iteration with rapiddweller script selector. */
  @SuppressWarnings("unchecked")
  @Test
  public void testIterateWithSelector() {
    MemStore.ignoreClose = false;
    parseAndExecute("<iterate source='src' type='product' selector='_candidate.id == 4' consumer='cons'/>");
    List<Entity> products = (List<Entity>) consumer.getProducts();
    assertEquals(1, products.size());
    assertEquals(4, products.get(0).get("id"));
  }

  /** Test iterate with JavaScript selector. */
  @SuppressWarnings("unchecked")
  @Test
  public void testIterateWithJsSelector() {
    MemStore.ignoreClose = false;
    parseAndExecute("<execute type='js'>let test = 2 </execute>");
    parseAndExecute("<iterate source='src' type='product' selector='{js: _candidate.id == test + 1 + 1}' consumer='cons'/>");
    List<Entity> products = (List<Entity>) consumer.getProducts();
    assertEquals(1, products.size());
    assertEquals(4, products.get(0).get("id"));
  }

  /** Test variable evaluation. */
  @SuppressWarnings("unchecked")
  @Test
  public void testVariable() {
    MemStore.ignoreClose = false;
    context.setDefaultOneToOne(true);
    parseAndExecute(
        "<generate type='order' consumer='cons'>" +
            "	<variable name='p' source='src' type='product'/>" +
            "	<id name='id' type='int' />" +
            "	<attribute name='prod_id' type='int' script='p.id' />" +
            "</generate>"
    );
    List<Entity> orders = (List<Entity>) consumer.getProducts();
    assertEquals(3, orders.size());
    int index = 1;
    for (Entity order : orders) {
      assertNotNull(order);
      assertEquals(index, order.get("id"));
      assertEquals(index + 2, order.get("prod_id"));
      index++;
    }
  }

  /** Test sub part generation. */
  @SuppressWarnings("unchecked")
  @Test
  public void testPart() {
    MemStore.ignoreClose = false;
    parseAndExecute(
        "<generate type='order' consumer='cons'>" +
            "	<id name='id' type='int' />" +
            "	<part name='product' type='product' source='src' count='1'/>" +
            "</generate>"
    );
    Collection<Entity> orders = (List<Entity>) consumer.getProducts();
    assertEquals(3, orders.size());
    int index = 1;
    for (Entity order : orders) {
      assertNotNull(order);
      assertEquals(index, order.get("id"));
      Entity product = (Entity) order.get("product");
      assertEquals(index + 2, product.get("id"));
      index++;
    }
  }

  /** Test reference generation. */
  @SuppressWarnings("unchecked")
  @Test
  public void testReference() {
    MemStore.ignoreClose = false;
    parseAndExecute(
        "<generate type='order' consumer='cons'>" +
            "	<id name='id' type='int' />" +
            "	<reference name='product_id' type='int' source='src' targetType='product' selector='_candidate!=5' unique='true'/>" +
            "</generate>"
    );
    Collection<Entity> orders = (List<Entity>) consumer.getProducts();
    assertEquals(2, orders.size());
    int index = 1;
    for (Entity order : orders) {
      assertNotNull(order);
      assertEquals(index, order.get("id"));
      int product = (Integer) order.get("product_id");
      assertTrue(product >= 3 && product < 5);
      index++;
    }
  }

  @Test
  public void testInserter_with_type() {
    MemStore mem = new MemStore("mem", dataModel);
    context.set("mem", mem);
    parseAndExecute(
        "<generate type='user' count='5' consumer=\"mem.inserter('customer')\">" +
            "	<id name='id' type='int'/>" +
            "</generate>"
    );
    Collection<Entity> customers = mem.getEntities("customer");
    assertEquals(5, customers.size());
    int index = 1;
    for (Entity customer : customers) {
      assertNotNull(customer);
      assertEquals(index, customer.get("id"));
      index++;
    }
  }

  @Test
  public void testUpdater_with_type() {
    MemStore mem = new MemStore("mem", dataModel);
    context.set("mem", mem);
    parseAndExecute(
        "<setup>" +
            "  <generate type='customer' count='5' consumer='mem'>" +
            "    <id name='id' type='int'/>" +
            "    <attribute name='value' type='int' distribution='increment'/>" +
            "  </generate>" +
            "  <generate type='none' count='5' consumer=\"mem.updater('customer')\">" +
            "    <id name='id' type='int'/>" +
            "    <attribute name='value' type='int' min='2' distribution='increment'/>" +
            "  </generate>" +
            "  <iterate source='mem' type='customer' consumer='ConsoleExporter'/>" +
            "</setup>"
    );
    Collection<Entity> customers = mem.getEntities("customer");
    assertEquals(5, customers.size());
    int index = 1;
    for (Entity customer : customers) {
      assertNotNull(customer);
      assertEquals(index, customer.get("id"));
      index++;
    }
  }

  /** Test integration with XML-based setup. */
  @Test
  public void testIntegration() {
    MemStore.ignoreClose = true;
    parseAndExecute(
        "<setup>" +
            "	<import platforms='memstore'/>" +
            "	<memstore id='store'/>" +
            "	<generate type='product' count='100' consumer='store'>" +
            "		<id name='id' type='int' />" +
            "		<attribute name='name' pattern='[A-Z][a-z]{3,8}' />" +
            "	</generate>" +
            "</setup>"
    );
    MemStore store = (MemStore) context.get("store");
    Collection<Entity> products = store.getEntities("product");
    assertEquals(100, products.size());
    int index = 1;
    for (Entity order : products) {
      assertNotNull(order);
      assertEquals(index, order.get("id"));
      index++;
    }
  }

}
