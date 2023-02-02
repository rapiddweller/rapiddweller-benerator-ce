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

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.ABCTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static com.rapiddweller.jdbacl.dialect.H2Util.*;
import static org.junit.Assert.*;

/**
 * Tests {@link DefaultDBSystem}.<br/><br/>
 * Created at 26.12.2008 03:40:44
 *
 * @author Volker Bergmann
 * @since 0.5.6
 */
public class DefaultDBSystemTest extends ABCTest {

  private DefaultDBSystem db;

  @Before
  public void setUp() throws Exception {
    Connection connection = null;
    try {
      db = new DefaultDBSystem("db", IN_MEMORY_URL_PREFIX + "benerator", DRIVER, DEFAULT_USER, DEFAULT_PASSWORD, new DataModel());
      db.setSchema("PUBLIC");
      db.getDialect();
      connection = db.createConnection();
      DBUtil.executeUpdate("drop table Test if exists", connection);
      DBUtil.executeUpdate("create table Test ( "
              + "ID   int,"
              + "NAME varchar(30) not null,"
              + "constraint T1_PK primary key (ID)"
              + ");",
          connection);
      db.invalidate();
      context.setContextUri("target/test-classes/" + BeanUtil.packageFolder(getClass()));
    } finally {
      DBUtil.close(connection);
    }
  }

  @After
  public void shutdown() {
    db.close();
  }

  @Test(expected = ConfigurationError.class)
  public void test_env_without_system() {
    new DefaultDBSystem("id", "local", null, context);
  }

  @Test(expected = ConfigurationError.class)
  public void test_not_a_database() {
    new DefaultDBSystem("id", "local", "kafka1", context);
  }

  @Test
  public void testGettersAndSetters() {
    assertEquals("sa", db.getEnvironment());
    assertEquals("h2", db.getSystem());
    assertEquals(DRIVER, db.getDriver());
    assertEquals("jdbc:h2:mem:benerator", db.getUrl());
    assertEquals("sa", db.getUser());
    assertNull(db.getCatalog());
    db.setCatalog("theCat");
    assertEquals("theCat", db.getCatalog());
  }

  @Test
  public void testDecimalFGranularity() {
    assertEquals("1", DefaultDBSystem.decimalGranularity(0));
    assertEquals("0.1", DefaultDBSystem.decimalGranularity(1));
    assertEquals("0.01", DefaultDBSystem.decimalGranularity(2));
    assertEquals("0.001", DefaultDBSystem.decimalGranularity(3));
  }

  @Test
  public void testReadWrite() {
    db.setReadOnly(false);

    // test insert w/o readOnly
    db.store(new Entity("Test", db, "ID", 1, "NAME", "Alice"));

    // test update w/o readOnly
    db.update(new Entity("Test", db, "ID", 1, "NAME", "Bob"));

    assertNotNull(db);
  }

  @Test
  public void testReadOnly() {
    db.setReadOnly(true);

    // test select w/ readOnly
    DataSource<?> result = db.query("select id from Test", true, null);
    result.iterator().close();

    // test insert w/ readOnly
    try {
      db.store(new Entity("Test", db, "ID", 2, "NAME", "Charly"));
      fail("Exception expected in store()");
    } catch (Exception e) {
      // That's the required behavior!
    }

    // test update w/ readOnly
    try {
      db.update(new Entity("Test", db, "ID", 2, "NAME", "Doris"));
      fail("Exception expected in update()");
    } catch (Exception e) {
      // That's the required behavior!
    }

    Connection connection = null;
    try {
      // test drop w/ readOnly in createStatement
      connection = db.createConnection();
      Statement statement = connection.createStatement();

      try {
        Objects.requireNonNull(statement).execute("drop table Test");
        fail("Exception expected in execute()");
      } catch (Exception e) {
        // That's the required behavior!
      } finally {
        DBUtil.close(statement);
      }

      // test drop w/ readOnly in prepareStatement
      try {
        connection = db.createConnection();
        connection.prepareStatement("drop table Test");
        fail("Exception expected in prepareStatement()");
      } catch (Exception e) {
        // That's the required behavior!
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      DBUtil.close(connection);
    }
  }

  @Test
  public void testSequence() throws Exception {
    String seq = getClass().getSimpleName();
    try {
      db.createSequence(seq);
      assertEquals(1, db.nextSequenceValue(seq));
      assertEquals(2, db.nextSequenceValue(seq));
      db.setSequenceValue(seq, 5);
      assertEquals(5, db.nextSequenceValue(seq));
    } finally {
      db.dropSequence(seq);
    }
  }

  @Test
  public void testQueryEntities_no_selector() {
    db.execute("insert into \"TEST\" (ID, NAME) values (1, 'Alice')");
    db.execute("insert into \"TEST\" (ID, NAME) values (2, 'Bob')");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DataContainer<Entity> container = new DataContainer<>();

    // test with plain selector
    DataIterator<Entity> iterator = db.queryEntities("TEST", null, context).iterator();
    assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"), iterator.next(container).getData());
    assertEquals(new Entity("TEST", db, "ID", 2, "NAME", "Bob"), iterator.next(container).getData());
    assertNull(iterator.next(container));
    iterator.close();
  }

  @Test
  public void testQueryEntities_static_selector() {
    db.execute("insert into \"TEST\" (ID, NAME) values (1, 'Alice')");
    db.execute("insert into \"TEST\" (ID, NAME) values (2, 'Bob')");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DataContainer<Entity> container = new DataContainer<>();

    // test with plain selector
    DataIterator<Entity> iterator = db.queryEntities("TEST", "ID = 1", context).iterator();
    assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"), iterator.next(container).getData());
    assertNull(iterator.next(container));
    iterator.close();
  }

  @Test
  public void testQueryEntities_ftl_selector() {
    db.execute("insert into \"TEST\" (ID, NAME) values (1, 'Alice')");
    db.execute("insert into \"TEST\" (ID, NAME) values (2, 'Bob')");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DataContainer<Entity> container = new DataContainer<>();
    // test with script selector
    DataIterator<Entity> iterator = db.queryEntities("TEST", "{ftl:ID = ${23/23}}", context).iterator();
    assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"), iterator.next(container).getData());
    assertNull(iterator.next(container));
    iterator.close();
  }

  @Test
  public void testQueryEntities_ben_selector() {
    db.execute("insert into \"TEST\" (ID, NAME) values (1, 'Alice')");
    db.execute("insert into \"TEST\" (ID, NAME) values (2, 'Bob')");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DataContainer<Entity> container = new DataContainer<>();
    DataIterator<Entity> iterator = db.queryEntities("TEST", "{ben:'ID = ' + 1}", context).iterator();
    assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"), iterator.next(container).getData());
    assertNull(iterator.next(container));
    iterator.close();
  }

  @Test
  public void testQueryEntityIds_no_selector() {
    db.execute("insert into \"TEST\" (ID, NAME) values (1, 'Alice')");
    db.execute("insert into \"TEST\" (ID, NAME) values (2, 'Bob')");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DataContainer container = new DataContainer<>();
    DataIterator<?> iterator3 = db.queryEntityIds("TEST", null, context).iterator();
    assertEquals(1, iterator3.next(container).getData());
    assertEquals(2, iterator3.next(container).getData());
    assertNull(iterator3.next(container));
    iterator3.close();
  }

  @Test
  public void testQueryEntityIds_static_selector() {
    db.execute("insert into \"TEST\" (ID, NAME) values (1, 'Alice')");
    db.execute("insert into \"TEST\" (ID, NAME) values (2, 'Bob')");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DataContainer container = new DataContainer<>();
    DataIterator<?> iterator = db.queryEntityIds("TEST", "ID = 1", context).iterator();
    assertEquals(1, iterator.next(container).getData());
    assertNull(iterator.next(container));
    iterator.close();
  }

  @Test
  public void testQueryEntityIds_ftl_selector() {
    db.execute("insert into \"TEST\" (ID, NAME) values (1, 'Alice')");
    db.execute("insert into \"TEST\" (ID, NAME) values (2, 'Bob')");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DataContainer container = new DataContainer<>();
    DataIterator<?> iterator = db.queryEntityIds("TEST", "{ftl:ID = 2}", context).iterator();
    assertEquals(2, iterator.next(container).getData());
    assertNull(iterator.next(container));
    iterator.close();
  }

  @Test
  public void testQueryEntityIds_ben_selector() {
    db.execute("insert into \"TEST\" (ID, NAME) values (1, 'Alice')");
    db.execute("insert into \"TEST\" (ID, NAME) values (2, 'Bob')");
    DefaultBeneratorContext context = new DefaultBeneratorContext();
    DataContainer container = new DataContainer<>();
    DataIterator<?> iterator2 = db.queryEntityIds("TEST", "{ben:'ID = ' + 2}", context).iterator();
    assertEquals(2, iterator2.next(container).getData());
    assertNull(iterator2.next(container));
    iterator2.close();
  }

  @Test
  public void testInserter() {
    Consumer inserter = db.inserter();
    Entity entity = new Entity("TEST", db, "ID", 1, "NAME", "Alice");
    inserter.startConsuming(new ProductWrapper<Entity>().wrap(entity));
    inserter.finishConsuming(new ProductWrapper<Entity>().wrap(entity));
    DataSource<Entity> entities = db.queryEntities("TEST", "ID = 1", new DefaultBeneratorContext());
    DataIterator<Entity> iterator = entities.iterator();
    assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"),
        iterator.next(new DataContainer<>()).getData());
  }

  @Test
  public void testInserter_table() {
    Consumer inserter = db.inserter("TEST");
    Entity entity = new Entity("Xyz", db, "ID", 1, "NAME", "Alice");
    inserter.startConsuming(new ProductWrapper<Entity>().wrap(entity));
    inserter.finishConsuming(new ProductWrapper<Entity>().wrap(entity));
    DataSource<Entity> entities = db.queryEntities("TEST", "ID = 1", new DefaultBeneratorContext());
    DataIterator<Entity> iterator = entities.iterator();
    assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"),
        iterator.next(new DataContainer<>()).getData());
  }

  @Test
  public void testUpdater() throws Exception {

    db.execute("insert into TEST (ID, NAME) values (1, 'Alice')");
    db.execute("insert into TEST (ID, NAME) values (2, 'Bob')");
    Consumer updater = db.updater();
    // update (1, Alice) to (1, Charly)
    assertEquals("DefaultDBSystem[sa@jdbc:h2:mem:benerator]", db.toString());
    assertEquals("h2", db.getDbType());
    assertEquals(1, db.invalidationCount());
    assertEquals("ConvertingDataSource[QueryDataSource[SELECT * FROM TEST] -> ResultSetConverter]",
        db.queryEntityIds("TEST", "SELECT * FROM TEST", null).toString());
    assertEquals(2, db.countEntities("TEST"));
    assertEquals("TEST[ID=1, NAME=Alice]", db.queryEntityById("TEST", 1).toString());
    assertNull(db.getCatalog());
    assertEquals("PUBLIC", db.getSchema());
    assertNull(db.getPassword());
    assertEquals(".*", db.getIncludeTables());
    assertNull(db.getExcludeTables());
    assertFalse(db.isMetaCache());
    assertEquals(100, db.getFetchSize());
    assertTrue(db.isLazy());
    db.getTypeDescriptors();
    db.setAcceptUnknownColumnTypes(true);
    db.queryEntityById("TEST", 1);
    db.getTable("TEST");
    Entity entity1 = new Entity("TEST", db, "ID", 1, "NAME", "Charly");
    ProductWrapper<Entity> wrapper = new ProductWrapper<>();
    updater.startConsuming(wrapper.wrap(entity1));
    updater.finishConsuming(wrapper.wrap(entity1));
    // update (2, Bob) to (2, Otto)
    Entity entity2 = new Entity("TEST", db, "ID", 2, "NAME", "Otto");
    updater.startConsuming(wrapper.wrap(entity2));
    updater.finishConsuming(wrapper.wrap(entity2));
    // check database content
    List<Object[]> storedData = DBUtil.query("select ID, NAME from TEST", db.getConnection());
    assertEquals(2, storedData.size());
    assertArrayEquals(new Object[] {1, "Charly"}, storedData.get(0));
    assertArrayEquals(new Object[] {2, "Otto"}, storedData.get(1));
  }

  @Test
  public void testTableExists() {
    assertTrue(db.tableExists("TEST"));
    assertFalse(db.tableExists("TEST_______"));
    assertFalse(db.tableExists(""));
    assertFalse(db.tableExists(null));
  }

}
