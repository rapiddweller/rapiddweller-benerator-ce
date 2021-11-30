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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.engine.BeneratorMonitor;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.primitive.IncrementGenerator;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.benerator.test.PersonSource;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.exception.IllegalArgumentError;
import com.rapiddweller.common.converter.UnsafeConverter;
import com.rapiddweller.common.validator.AbstractValidator;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.util.DataIteratorTestCase;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.platform.db.DefaultDBSystem;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link AbstractGenIterParser}.<br/><br/>
 * Created: 10.11.2009 15:08:46
 * @author Volker Bergmann
 * @since 0.6.0
 */
@SuppressWarnings("CheckStyle")
public class GenerateOrIterateParserAndStatementTest extends AbstractBeneratorIntegrationTest {

  @Test(expected = IllegalArgumentError.class)
  public void testIllegalNullable() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    Statement statement = parse(
        "<generate type='dummy' count='1' consumer='NoConsumer'>" +
            "   <attribute name='x' nullable='xxx'/>" +
            "</generate>");
    statement.execute(context);
  }

  @Test
  public void testPaging() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    Statement statement = parse(
        "<generate type='dummy' count='{c}' pageSize='{ps}' consumer='cons'/>");
    ConsumerMock consumer = new ConsumerMock(false);
    context.setGlobal("cons", consumer);
    context.setGlobal("c", 100);
    context.setGlobal("ps", 20);
    statement.execute(context);
    assertEquals(100, consumer.startConsumingCount.get());
    assertEquals(100, consumer.finishConsumingCount.get());
    assertEquals(100L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  @Test
  public void testConverter() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    Statement statement = parse("<generate type='dummy' count='3' converter='conv' consumer='cons'/>");
    ConsumerMock consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
    context.setGlobal("conv", new UnsafeConverter<>(Entity.class, Entity.class) {
      @Override
      public Entity convert(Entity sourceValue) {
        ComplexTypeDescriptor descriptor = sourceValue.descriptor();
        descriptor.setName("CONV_DUMMY");
        return new Entity(descriptor);
      }
    });
    statement.execute(context);
    List<?> products = consumer.getProducts();
    assertEquals(3, products.size());
    for (int i = 0; i < 3; i++) {
      assertEquals("CONV_DUMMY", ((Entity) products.get(i)).type());
    }
    assertEquals(3L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  @Test
  public void testValidator() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    Statement statement = parse(
        "<generate type='dummy' count='3' validator='vali' consumer='cons'>" +
            "   <id name='id' type='int' />" +
            "</generate>");
    ConsumerMock consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
    context.setGlobal("vali", new AbstractValidator<Entity>() {
      @Override
      public boolean valid(Entity entity) {
        return ((Integer) entity.get("id")) % 2 == 0;
      }
    });
    statement.execute(context);
    List<?> products = consumer.getProducts();
    assertEquals(3, products.size());
    for (int i = 0; i < 3; i++) {
      assertEquals(2 + 2 * i, ((Entity) products.get(i)).get("id"));
    }
    assertEquals(3L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testArray() {
    Statement statement = parse(
        "<generate type='array' count='5' consumer='cons'>" +
            "  <value pattern='ABC' />" +
            "  <value type='int' constant='42' />" +
            "</generate>");
    ConsumerMock consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    List<Object[]> products = (List) consumer.getProducts();
    assertEquals(5, products.size());
    Object[] array1 = products.get(0);
    assertEquals(2, array1.length);
    assertEquals("ABC", array1[0]);
    assertEquals(42, array1[1]);
    Object[] array2 = products.get(1);
    assertEquals(2, array2.length);
    assertEquals("ABC", array2[0]);
    assertEquals(42, array2[1]);
  }

  @Test
  public void testGeneratePageSize2() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    ConsumerMock cons = new ConsumerMock(false);
    context.setGlobal("cons", cons);
    Statement statement = parse(
        "<generate type='top' count='4' pageSize='2' consumer='cons' />"
    );
    statement.execute(context);
    context.close();
    List<String> expectedInvocations = CollectionUtil.toList(
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH
    );
    assertEquals(expectedInvocations, cons.invocations);
    assertEquals(4L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  @Test
  public void testGeneratePageSize0() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    ConsumerMock cons = new ConsumerMock(false);
    context.setGlobal("cons", cons);
    Statement statement = parse(
        "<generate type='top' count='4' pageSize='0' consumer='cons' />"
    );
    statement.execute(context);
    List<String> expectedInvocations = CollectionUtil.toList(
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING
    );
    assertEquals(expectedInvocations, cons.invocations);
    assertEquals(4L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  @Test
  public void testSimpleSubGenerate() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    Statement statement = parse(
        "<generate type='top' count='3' consumer='cons1'>" +
            "    <generate type='sub' count='2' consumer='new " + ConsumerMock.class.getName() + "(false, 2)'/>" +
            "</generate>"
    );
    ConsumerMock outerConsumer = new ConsumerMock(false, 1);
    context.setGlobal("cons1", outerConsumer);
    statement.execute(context);
    assertEquals(3, outerConsumer.startConsumingCount.get());
    assertEquals(0, outerConsumer.closeCount.get());
    ConsumerMock innerConsumer = ConsumerMock.getInstance(2);
    assertEquals(6, innerConsumer.startConsumingCount.get());
    assertTrue(innerConsumer.flushCount.get() > 0);
    assertEquals(0, outerConsumer.closeCount.get());
    assertTrue(innerConsumer.closeCount.get() > 0);
    assertEquals(9L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  @Test
  public void testSubGenerateLifeCycle() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    Statement statement = parse(
        "<generate type='top' count='3'>" +
            "    <generate type='sub' count='2' consumer='new " + ConsumerMock.class.getName() + "(true)'>" +
            "      <attribute name='x' type='int' generator='" + IncrementGenerator.class.getName() + "' />" +
            "   </generate>" +
            "</generate>"
    );
    statement.execute(context);
    ConsumerMock innerConsumer = ConsumerMock.getInstance(0);
    assertEquals(6, innerConsumer.getProducts().size());
    assertEquals(1, ((Entity) innerConsumer.getProducts().get(0)).get("x"));
    assertEquals(2, ((Entity) innerConsumer.getProducts().get(1)).get("x"));
    assertEquals(1, ((Entity) innerConsumer.getProducts().get(2)).get("x"));
    assertEquals(2, ((Entity) innerConsumer.getProducts().get(3)).get("x"));
    assertEquals(1, ((Entity) innerConsumer.getProducts().get(4)).get("x"));
    assertEquals(2, ((Entity) innerConsumer.getProducts().get(5)).get("x"));
    assertTrue(innerConsumer.flushCount.get() > 0);
    assertTrue(innerConsumer.closeCount.get() > 0);
    assertEquals(9L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  @Test
  public void testSubGeneratePageSize2() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    ConsumerMock cons = new ConsumerMock(false);
    context.setGlobal("cons", cons);
    Statement statement = parse(
        "<generate type='top' count='2' pageSize='1' consumer='cons'>" +
            "    <generate type='sub' count='4' pageSize='2' consumer='cons'/>" +
            "</generate>"
    );
    statement.execute(context);
    List<String> expectedInvocations = CollectionUtil.toList(
        ConsumerMock.START_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH,
        ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH,

        ConsumerMock.START_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.START_CONSUMING, ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH,
        ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH
    );
    assertEquals(expectedInvocations, cons.invocations);
    assertEquals(10L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  @Test
  public void testSubGeneratePageSize0() {
    BeneratorMonitor.INSTANCE.setTotalGenerationCount(0);
    ConsumerMock cons = new ConsumerMock(false);
    context.setGlobal("cons", cons);
    Statement statement = parse(
        "<generate type='top' count='2' pageSize='1' consumer='cons'>" +
            "    <generate type='sub' count='1' pageSize='0' consumer='cons'/>" +
            "</generate>"
    );
    statement.execute(context);
    List<String> expectedInvocations = CollectionUtil.toList(
        ConsumerMock.START_CONSUMING,
        ConsumerMock.START_CONSUMING,
        ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH,
        ConsumerMock.START_CONSUMING,
        ConsumerMock.START_CONSUMING,
        ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FINISH_CONSUMING,
        ConsumerMock.FLUSH
    );
    assertEquals(expectedInvocations, cons.invocations);
    assertEquals(4L, BeneratorMonitor.INSTANCE.getTotalGenerationCount());
  }

  /** Tests a sub loop that derives its loop length from a parent attribute. */
  @Test
  public void testSubGenerateParentRef() {
    Statement statement = parse(
        "<generate name='pName' type='outer' count='3' consumer='cons'>" +
            "    <attribute name='n' type='int' distribution='step' />" +
            "    <generate type='inner' count='pName.n' consumer='cons'/>" +
            "</generate>");
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(9, consumer.startConsumingCount.get());
    assertOuter(1, consumer.getProducts().get(0));
    assertEquals(createEntity("inner"), consumer.getProducts().get(1));
    assertOuter(2, consumer.getProducts().get(2));
    assertEquals(createEntity("inner"), consumer.getProducts().get(3));
    assertEquals(createEntity("inner"), consumer.getProducts().get(4));
    assertOuter(3, consumer.getProducts().get(5));
    assertEquals(createEntity("inner"), consumer.getProducts().get(6));
    assertEquals(createEntity("inner"), consumer.getProducts().get(7));
    assertEquals(createEntity("inner"), consumer.getProducts().get(8));
  }

  /** Tests a combination of variable and attribute with the same name. */
  @Test
  public void testVariableOfSameNameAsAttribute() {
    Statement statement = parse(
        "<generate name='pName' type='outer' count='3' consumer='cons'>" +
            "    <variable name='n' type='int' distribution='step' />" +
            "    <attribute name='n' type='int' script='n + 1' />" +
            "</generate>");
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(3, consumer.startConsumingCount.get());
    assertOuter(2, consumer.getProducts().get(0));
    assertOuter(3, consumer.getProducts().get(1));
    assertOuter(4, consumer.getProducts().get(2));
  }

  private static void assertOuter(int n, Object object) {
    Entity entity = (Entity) object;
    assertNotNull(entity);
    assertEquals("outer", entity.type());
    assertEquals(n, ((Integer) entity.get("n")).intValue());
  }

  /** Tests the nesting of an &lt;execute&gt; element within a &lt;generate&gt; element */
  @Test
  public void testSubExecute() {
    Statement statement = parse(
        "<generate type='dummy' count='3'>" +
            "   <execute>bean.invoke(2)</execute>" +
            "</generate>");
    BeanMock bean = new BeanMock();
    bean.invocationCount = 0;
    context.setGlobal("bean", bean);
    statement.execute(context);
    assertEquals(3, bean.invocationCount);
    assertEquals(2, bean.lastValue);
  }

  /** Tests iterating an {@link EntitySource} */
  @Test
  public void testIterate() {
    Statement statement = parse("<iterate type='Person' source='personSource' consumer='cons' />");
    PersonSource source = new PersonSource();
    source.setContext(context);
    context.setGlobal("personSource", source);
    ConsumerMock consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(2, consumer.getProducts().size());
    assertEquals(source.createPersons(), consumer.getProducts());
  }

  /** Tests pure {@link Entity} generation */
  @Test
  public void testGenerate() {
    Statement statement = parse("<generate type='Person' count='2' consumer='cons' />");
    ConsumerMock consumer = new ConsumerMock(false);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(2, consumer.startConsumingCount.get());
    assertEquals(2, consumer.finishConsumingCount.get());
  }

  @Test
  public void testDBUpdate() {
    // create DB
    DefaultDBSystem db = new DefaultDBSystem("db", HSQLUtil.getInMemoryURL("benetest"),
        HSQLUtil.DRIVER, HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD, context.getDataModel());
    db.getDialect();
    try {
      // prepare DB
      db.execute(
          "create table GOIPAST (" +
              "   ID int," +
              "   N  int," +
              "   primary key (ID)" +
              ")");
      db.execute("insert into GOIPAST (id, n) values (1, 3)");
      db.execute("insert into GOIPAST (id, n) values (2, 4)");
      // parse and run statement
      Statement statement = parse(
          "<iterate type='GOIPAST' source='db' consumer='db.updater()'>" +
              "   <attribute name='n' constant='2' />" +
              "</iterate>"
      );
      context.setGlobal("db", db);
      statement.execute(context);
      DataSource<?> check = db.query("select N from GOIPAST", true, context);
      DataIterator<?> iterator = check.iterator();
      DataIteratorTestCase.expectNextElements(iterator, 2, 2).withNoNext();
      iterator.close();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      // clean up
      db.execute("drop table GOIPAST");
      db.close();
    }
  }

  @Test
  public void testGenerateWithOffset() {
    Statement statement = parse(
        "<generate name='array' count='3' consumer='cons'>" +
            "    <value type='int' distribution='step' offset='2'/>" +
            "</generate>");
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(3, consumer.startConsumingCount.get());
    assertArrayEquals(new Object[] {3}, (Object[]) consumer.getProducts().get(0));
    assertArrayEquals(new Object[] {4}, (Object[]) consumer.getProducts().get(1));
    assertArrayEquals(new Object[] {5}, (Object[]) consumer.getProducts().get(2));
  }

  @Test
  public void testIterateWithOffset() {
    Generator<Integer[]> source = new SequenceTestGenerator<>(
        new Integer[] {1},
        new Integer[] {2},
        new Integer[] {3},
        new Integer[] {4},
        new Integer[] {5});
    context.setGlobal("source", source);
    Statement statement = parse("<iterate source='source' offset='2' type='array' count='3' consumer='cons' />");
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(3, consumer.startConsumingCount.get());
    assertArrayEquals(new Object[] {3}, (Object[]) consumer.getProducts().get(0));
    assertArrayEquals(new Object[] {4}, (Object[]) consumer.getProducts().get(1));
    assertArrayEquals(new Object[] {5}, (Object[]) consumer.getProducts().get(2));
  }

  @Test
  public void testScopeWithAttributes() {
    Statement statement = parse(
        "<generate name='a' count='2' consumer='NoConsumer'>" +
            "   <generate name='b' count='2' consumer='NoConsumer'>" +
            "      <generate name='c' count='2' consumer='ConsoleExporter,cons'>" +
            "         <attribute name='slash' type='int' distribution='increment' scope='/'/>" +
            "         <attribute name='a' type='int' distribution='increment' scope='a'/>" +
            "         <attribute name='b' type='int' distribution='increment' scope='b'/>" +
            "         <attribute name='c' type='int' distribution='increment' scope='c'/>" +
            "         <attribute name='def' type='int' distribution='increment'/>" +
            "      </generate>" +
            "   </generate>" +
            "</generate>"
    );
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(8, consumer.startConsumingCount.get());
    assertComponents((Entity) consumer.getProducts().get(0), "slash", 1, "a", 1, "b", 1, "c", 1, "def", 1);
    assertComponents((Entity) consumer.getProducts().get(1), "slash", 2, "a", 2, "b", 2, "c", 2, "def", 2);
    assertComponents((Entity) consumer.getProducts().get(2), "slash", 3, "a", 3, "b", 1, "c", 1, "def", 1);
    assertComponents((Entity) consumer.getProducts().get(3), "slash", 4, "a", 4, "b", 2, "c", 2, "def", 2);
    assertComponents((Entity) consumer.getProducts().get(4), "slash", 5, "a", 1, "b", 1, "c", 1, "def", 1);
    assertComponents((Entity) consumer.getProducts().get(5), "slash", 6, "a", 2, "b", 2, "c", 2, "def", 2);
    assertComponents((Entity) consumer.getProducts().get(6), "slash", 7, "a", 3, "b", 1, "c", 1, "def", 1);
    assertComponents((Entity) consumer.getProducts().get(7), "slash", 8, "a", 4, "b", 2, "c", 2, "def", 2);
  }

  @Test
  public void testScopeWithVariables() {
    Statement statement = parse(
        "<generate name='a' count='2' consumer='NoConsumer'>" +
            "   <generate name='b' count='2' consumer='NoConsumer'>" +
            "      <generate name='c' count='2' consumer='ConsoleExporter,cons'>" +
            "         <variable name='slash'  type='int' distribution='increment' scope='/'/>" +
            "         <variable name='a'      type='int' distribution='increment' scope='a'/>" +
            // TODO it should be forbidden to use a variable name that shadows an outer entity/variable name
            "         <variable name='b'      type='int' distribution='increment' scope='b'/>" +
            "         <variable name='c'      type='int' distribution='increment' scope='c'/>" +
            "         <variable name='def'    type='int' distribution='increment'/>" +

            "         <attribute name='slash' type='int' script='slash'/>" +
            "         <attribute name='a'     type='int' script='a'/>" +
            "         <attribute name='b'     type='int' script='b'/>" +
            "         <attribute name='c'     type='int' script='c'/>" +
            "         <attribute name='def'   type='int' script='def'/>" +
            "      </generate>" +
            "   </generate>" +
            "</generate>"
    );
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(8, consumer.startConsumingCount.get());
    assertComponents((Entity) consumer.getProducts().get(0), "slash", 1, "a", 1, "b", 1, "c", 1, "def", 1);
    assertComponents((Entity) consumer.getProducts().get(1), "slash", 2, "a", 2, "b", 2, "c", 2, "def", 2);
    assertComponents((Entity) consumer.getProducts().get(2), "slash", 3, "a", 3, "b", 1, "c", 1, "def", 1);
    assertComponents((Entity) consumer.getProducts().get(3), "slash", 4, "a", 4, "b", 2, "c", 2, "def", 2);
    assertComponents((Entity) consumer.getProducts().get(4), "slash", 5, "a", 1, "b", 1, "c", 1, "def", 1);
    assertComponents((Entity) consumer.getProducts().get(5), "slash", 6, "a", 2, "b", 2, "c", 2, "def", 2);
    assertComponents((Entity) consumer.getProducts().get(6), "slash", 7, "a", 3, "b", 1, "c", 1, "def", 1);
    assertComponents((Entity) consumer.getProducts().get(7), "slash", 8, "a", 4, "b", 2, "c", 2, "def", 2);
  }

  @Test
  public void testIdIgnored() {
    Statement statement = parse(
        "<generate name='a' count='3' consumer='cons'>" +
            "   <id name='id' mode='ignored' />" +
            "</generate>"
    );
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(3, consumer.startConsumingCount.get());
    for (int i = 0; i < 3; i++) {
      assertNull(((Entity) consumer.getProducts().get(0)).get("id"));
    }
  }

  @Test
  public void testAttributeIgnored() {
    Statement statement = parse(
        "<generate name='a' count='3' consumer='cons'>" +
            "   <attribute name='att' mode='ignored' />" +
            "</generate>"
    );
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(3, consumer.startConsumingCount.get());
    for (int i = 0; i < 3; i++) {
      assertNull(((Entity) consumer.getProducts().get(0)).get("att"));
    }
  }

  @Test
  public void testReferenceIgnored() {
    Statement statement = parse(
        "<generate type='a' count='3' consumer='cons'>" +
            "   <reference name='ref' targetType='b' mode='ignored' />" +
            "</generate>"
    );
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    context.addLocalType(new ComplexTypeDescriptor("b", context.getLocalDescriptorProvider()));
    statement.execute(context);
    assertEquals(3, consumer.startConsumingCount.get());
    for (int i = 0; i < 3; i++) {
      assertNull(((Entity) consumer.getProducts().get(0)).get("ref"));
    }
  }

}
