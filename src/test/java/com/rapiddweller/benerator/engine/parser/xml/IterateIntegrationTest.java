/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.benerator.test.PersonSource;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.util.DataIteratorTestCase;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.platform.db.DefaultDBSystem;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Integration test for &gt;iterate&gt;.<br/><br/>
 * Created: 09.12.2021 19:25:30
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class IterateIntegrationTest extends AbstractBeneratorIntegrationTest {

  /** Tests iterating an {@link EntitySource} */
  @Test
  public void testIterate() {
    Statement statement = parseXmlString("<iterate type='Person' source='personSource' consumer='cons' />");
    PersonSource source = new PersonSource();
    source.setContext(context);
    context.setGlobal("personSource", source);
    ConsumerMock consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(2, consumer.getProducts().size());
    assertEquals(source.createPersons(), consumer.getProducts());
  }

  @Test
  public void testDBUpdate() {
    // create DB
    DefaultDBSystem db = new DefaultDBSystem("db", HSQLUtil.getInMemoryURL("benetest"),
        HSQLUtil.DRIVER, HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD, context.getDataModel());
    db.setCatalog("PUBLIC");
    db.setSchema("PUBLIC");
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
      Statement statement = parseXmlString(
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
  public void testIterateWithOffset() {
    Generator<Integer[]> source = new SequenceTestGenerator<>(
        new Integer[] {1},
        new Integer[] {2},
        new Integer[] {3},
        new Integer[] {4},
        new Integer[] {5});
    context.setGlobal("source", source);
    Statement statement = parseXmlString("<iterate source='source' offset='2' type='array' count='3' consumer='cons' />");
    ConsumerMock consumer = new ConsumerMock(true, 1);
    context.setGlobal("cons", consumer);
    statement.execute(context);
    assertEquals(3, consumer.startConsumingCount.get());
    assertArrayEquals(new Object[] {3}, (Object[]) consumer.getProducts().get(0));
    assertArrayEquals(new Object[] {4}, (Object[]) consumer.getProducts().get(1));
    assertArrayEquals(new Object[] {5}, (Object[]) consumer.getProducts().get(2));
  }

}
