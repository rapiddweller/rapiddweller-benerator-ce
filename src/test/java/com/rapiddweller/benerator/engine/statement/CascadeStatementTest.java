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

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import com.rapiddweller.jdbacl.model.DBForeignKeyConstraint;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.db.DefaultDBSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link CascadeStatement}.<br/><br/>
 * Created: 28.08.2011 18:51:55
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public class CascadeStatementTest extends GeneratorTest {

  private DefaultDBSystem db;

  /**
   * Sets up database.
   */
  @Before
  public void setUpDatabase() {
    DBUtil.resetMonitors();
    ConsumerMock consumer = new ConsumerMock(true);
    context.setGlobal("cons", consumer);
    String dbUrl = HSQLUtil.getInMemoryURL(getClass().getSimpleName());
    db = new DefaultDBSystem("db", dbUrl, HSQLUtil.DRIVER,
        HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD, context.getDataModel());
    db.setSchema("PUBLIC");
    // drop tables if they already exist
    db.execute("drop table referer if exists");
    db.execute("drop table referee if exists");
    // create and populate referee table
    db.execute("create table referee (id int, n int, primary key (id))");
    db.execute("insert into referee (id, n) values (2, 2)");
    db.execute("insert into referee (id, n) values (3, 3)");
    // create and populate referer table
    db.execute(
        "create table referer ( " +
            "	id int," +
            "	referee_id int," +
            "	the_date date," +
            "	primary key (id)," +
            "   constraint referee_fk foreign key (referee_id) references referee (id))");
    db.execute("insert into referer (id, referee_id) values (4, 2)");
    db.execute("insert into referer (id, referee_id) values (5, 3)");
    context.setGlobal("db", db);
    context.getDataModel().addDescriptorProvider(db);
  }

  /**
   * Test resolve to many reference.
   */
  @Test
  public void testResolveToManyReference() {
    CascadeStatement.Reference ref = new CascadeStatement.Reference("referee", new String[] {"id"});
    DBForeignKeyConstraint fk = db.getDbMetaData().getTable("referer").getForeignKeyConstraint("referee_id");
    Entity fromEntity = createEntity("referee", "id", 2);
    DataIterator<Entity> iterator = ref.resolveToManyReference(fromEntity, fk, db, context);
    DataContainer<Entity> container = new DataContainer<>();
    DataContainer<Entity> next = iterator.next(container);
    assertNotNull("referee not found", next);
    assertEquals(createEntity("REFERER", "ID", 4, "REFEREE_ID", 2, "THE_DATE", null), next.getData());
    assertNull(iterator.next(container));
  }

  /**
   * Test resolve to one reference.
   */
  @Test
  public void testResolveToOneReference() {
    CascadeStatement.Reference ref = new CascadeStatement.Reference("referer", new String[] {"referee_id"});
    DBForeignKeyConstraint fk = db.getDbMetaData().getTable("referer").getForeignKeyConstraint("referee_id");
    Entity fromEntity = createEntity("referer", "id", 4, "referee_id", 2);
    DataIterator<Entity> iterator = ref.resolveToOneReference(fromEntity, fk, db, context);
    DataContainer<Entity> container = new DataContainer<>();
    DataContainer<Entity> next = iterator.next(container);
    assertNotNull("referee not found", next);
    assertEquals(createEntity("REFEREE", "ID", 2, "N", 2), next.getData());
    assertNull(iterator.next(container));
  }

}
