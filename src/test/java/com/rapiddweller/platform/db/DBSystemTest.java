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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.formats.DataContainer;
import com.rapiddweller.formats.DataIterator;
import com.rapiddweller.formats.DataSource;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;

import org.junit.Before;
import org.junit.Test;
import static com.rapiddweller.jdbacl.dialect.HSQLUtil.*;
import static org.junit.Assert.*;

/**
 * Tests {@link DefaultDBSystem}.<br/>
 * <br/>
 * Created at 26.12.2008 03:40:44
 * @since 0.5.6
 * @author Volker Bergmann
 */

public class DBSystemTest {

	@Test
	public void testReadWrite() {
		db.setReadOnly(false);
		
		// test insert w/o readOnly
		db.store(new Entity("Test", db, "ID", 1, "NAME", "Alice"));
		
		// test update w/o readOnly
		db.update(new Entity("Test", db, "ID", 1, "NAME", "Bob"));
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
			Statement statement = null;
			try {
				connection = db.createConnection();
				statement = connection.createStatement();
				statement.execute("drop table Test");
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
	public void testQueryEntities() throws Exception {
		db.execute("insert into TEST (ID, NAME) values (1, 'Alice')");
		db.execute("insert into TEST (ID, NAME) values (2, 'Bob')");
		DefaultBeneratorContext context = new DefaultBeneratorContext();
        DataContainer<Entity> container = new DataContainer<Entity>();
        
		// test without selector
		DataIterator<Entity> iterator = db.queryEntities("TEST", "ID = 1", context).iterator();
		assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"), iterator.next(container).getData());
		assertNull(iterator.next(container));
        iterator.close();
        
		// test with selector
		DataIterator<Entity> iterator2 = db.queryEntities("TEST", null, context).iterator();
        assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"), iterator2.next(container).getData());
        assertEquals(new Entity("TEST", db, "ID", 2, "NAME", "Bob"), iterator2.next(container).getData());
		assertNull(iterator2.next(container));
        iterator2.close();
	}
	
	@Test
	public void testInserter() throws Exception {
        Consumer inserter = db.inserter();
        Entity entity = new Entity("TEST", db, "ID", 1, "NAME", "Alice");
        inserter.startConsuming(new ProductWrapper<Entity>().wrap(entity));
        inserter.finishConsuming(new ProductWrapper<Entity>().wrap(entity));
        DataSource<Entity> entities = db.queryEntities("TEST", "ID = 1", new DefaultBeneratorContext());
        DataIterator<Entity> iterator = entities.iterator();
        assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"), 
        		iterator.next(new DataContainer<Entity>()).getData());
	}
	
	@Test
	public void testInserter_table() throws Exception {
        Consumer inserter = db.inserter("TEST");
        Entity entity = new Entity("Xyz", db, "ID", 1, "NAME", "Alice");
        inserter.startConsuming(new ProductWrapper<Entity>().wrap(entity));
        inserter.finishConsuming(new ProductWrapper<Entity>().wrap(entity));
        DataSource<Entity> entities = db.queryEntities("TEST", "ID = 1", new DefaultBeneratorContext());
        DataIterator<Entity> iterator = entities.iterator();
        assertEquals(new Entity("TEST", db, "ID", 1, "NAME", "Alice"), 
        		iterator.next(new DataContainer<Entity>()).getData());
	}
	
	@Test
	public void testUpdater() throws Exception {
		db.execute("insert into TEST (ID, NAME) values (1, 'Alice')");
		db.execute("insert into TEST (ID, NAME) values (2, 'Bob')");
		Consumer updater = db.updater();
		// update (1, Alice) to (1, Charly)
        Entity entity1 = new Entity("TEST", db, "ID", 1, "NAME", "Charly");
        ProductWrapper<Entity> wrapper = new ProductWrapper<Entity>();
		updater.startConsuming(wrapper.wrap(entity1));
        updater.finishConsuming(wrapper.wrap(entity1));
		// update (2, Bob) to (2, Otto)
        Entity entity2 = new Entity("TEST", db, "ID", 2, "NAME", "Otto");
		updater.startConsuming(wrapper.wrap(entity2));
        updater.finishConsuming(wrapper.wrap(entity2));
        // check database content
		List<Object[]> storedData = DBUtil.query("select ID, NAME from TEST", db.getConnection());
		assertEquals(2, storedData.size());
		assertArrayEquals(new Object[] { 1, "Charly" }, storedData.get(0));
		assertArrayEquals(new Object[] { 2, "Otto"   }, storedData.get(1));
	}
	
	@Test
	public void testTableExists() throws Exception {
        assertTrue(db.tableExists("TEST"));
        assertFalse(db.tableExists("TEST_______"));
        assertFalse(db.tableExists(""));
        assertFalse(db.tableExists(null));
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------
	
	private DefaultDBSystem db;
	
	@Before
	public void setUp() throws Exception {
		Connection connection = null;
		try {
			db = new DefaultDBSystem("db", IN_MEMORY_URL_PREFIX + "benerator", DRIVER, DEFAULT_USER, DEFAULT_PASSWORD, new DataModel());
			db.setSchema("public");
			connection = db.createConnection();
			try {
				DBUtil.executeUpdate("drop table Test", connection);
			} catch (SQLException e) {
				// ignore
			}
			DBUtil.executeUpdate("create table Test ( "
					+ "ID   int,"
					+ "NAME varchar(30) not null,"
					+ "constraint T1_PK primary key (ID)"
					+ ");", 
					connection);
			db.invalidate();
		} finally {
			DBUtil.close(connection);
		}
	}
	
}
