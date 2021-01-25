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

import static org.junit.Assert.*;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

import com.rapiddweller.benerator.test.BeneratorIntegrationTest;
import com.rapiddweller.common.ConnectFailedException;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.jdbacl.DBUtil;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.db.DBSystem;
import org.junit.After;
import org.junit.Test;

/**
 * Integration test for Benerator's transcoding feature.<br/><br/>
 * Created: 12.01.2011 17:06:25
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodingIntegrationTest extends BeneratorIntegrationTest {

	private static final String PARENT_FOLDER = "src/test/resources/com/rapiddweller/benerator/engine/transcode";
	private static final String DESCRIPTOR1_FILE_NAME = PARENT_FOLDER + "/transcode_to_empty_target.ben.xml";
	private static final String DESCRIPTOR2_FILE_NAME = PARENT_FOLDER + "/transcode_to_target_with_countries.ben.xml";
	private static final String DESCRIPTOR3_FILE_NAME = PARENT_FOLDER + "/transcode_partially.ben.xml";
	private static final String DESCRIPTOR4_FILE_NAME = PARENT_FOLDER + "/transcode_partially_to_non_empty_target.ben.xml";
	private static final String DESCRIPTOR5_FILE_NAME = PARENT_FOLDER + "/transcode_partially_with_cascade.ben.xml";
	
	@After
	public void clearDB() throws ConnectFailedException, SQLException {
		dropTables(HSQLUtil.connectInMemoryDB("s"));
		dropTables(HSQLUtil.connectInMemoryDB("t"));
	}
	
	
	
	// tests -----------------------------------------------------------------------------------------------------------

	@Test
	public void testEmptyTarget() throws Exception {
		DescriptorRunner runner = null;
		try {
			// run descriptor file
			runner = new DescriptorRunner(DESCRIPTOR1_FILE_NAME, context);
			runner.run();
			DBSystem t = (DBSystem) context.get("t");
			// check countries
			DataSource<Entity> iterable = t.queryEntities("COUNTRY", null, context);
			DataIterator<Entity> iterator = iterable.iterator();
			assertNextCountry(1, "United States", iterator);
			assertNextCountry(2, "Germany", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
			// check states
			iterable = t.queryEntities("STATE", null, context);
			iterator = iterable.iterator();
			assertNextState(3, 1, "California", iterator);
			assertNextState(4, 1, "Florida", iterator);
			assertNextState(5, 2, "Bayern", iterator);
			assertNextState(6, 2, "Hamburg", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
		} finally {
			IOUtil.close(runner);
		}
	}

	@Test
	public void testTargetWithCountries() throws Exception {
		DescriptorRunner runner = null;
		try {
			// run descriptor file
			runner = new DescriptorRunner(DESCRIPTOR2_FILE_NAME, context);
			runner.run();
			DBSystem t = (DBSystem) context.get("t");
			// check countries
			DataSource<Entity> iterable = t.queryEntities("COUNTRY", null, context);
			DataIterator<Entity> iterator = iterable.iterator();
			assertNextCountry(1000, "United States", iterator);
			assertNextCountry(2000, "Germany", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
			// check states
			iterable = t.queryEntities("STATE", null, context);
			iterator = iterable.iterator();
			assertNextState(1, 1000, "California", iterator);
			assertNextState(2, 1000, "Florida", iterator);
			assertNextState(3, 2000, "Bayern", iterator);
			assertNextState(4, 2000, "Hamburg", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
		} finally {
			IOUtil.close(runner);
		}
	}
	
	@Test
	public void testPartialTranscode() throws Exception {
		DescriptorRunner runner = null;
		try {
			// run descriptor file
			runner = new DescriptorRunner(DESCRIPTOR3_FILE_NAME, context);
			runner.run();
			DBSystem t = (DBSystem) context.get("t");
			// check countries
			DataSource<Entity> iterable = t.queryEntities("COUNTRY", null, context);
			DataIterator<Entity> iterator = iterable.iterator();
			assertNextCountry(1, "Germany", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
			// check states
			iterable = t.queryEntities("STATE", null, context);
			iterator = iterable.iterator();
			assertNextState(2, 1, "Bayern", iterator);
			assertNextState(3, 1, "Hamburg", iterator);
			assertNextState(4, null, "No State", iterator); // checking transcoding of 'null' refs
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
		} finally {
			IOUtil.close(runner);
		}
	}

	@Test
	public void testPartialTranscodeToNonEmptyTarget() throws Exception {
		DescriptorRunner runner = null;
		try {
			// run descriptor file
			runner = new DescriptorRunner(DESCRIPTOR4_FILE_NAME, context);
			runner.run();
			DBSystem t = (DBSystem) context.get("t");
			// check countries
			DataSource<Entity> iterable = t.queryEntities("COUNTRY", null, context);
			DataIterator<Entity> iterator = iterable.iterator();
			assertNextCountry(1, "Germany", iterator);
			assertNextCountry(10, "United States", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
			// check states
			iterable = t.queryEntities("STATE", null, context);
			iterator = iterable.iterator();
			assertNextState(2, 1, "Bayern", iterator);
			assertNextState(3, 1, "Hamburg", iterator);
			assertNextState(110, 10, "California", iterator);
			assertNextState(120, 10, "Florida", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
		} finally {
			IOUtil.close(runner);
		}
	}

	@Test
	public void testPartialTranscodeWithCascade() throws Exception {
		DescriptorRunner runner = null;
		try {
			// run descriptor file
			runner = new DescriptorRunner(DESCRIPTOR5_FILE_NAME, context);
			runner.run();
			DBSystem t = (DBSystem) context.get("t");
			
			// check countries
			DataSource<Entity> iterable = t.queryEntities("COUNTRY", null, context);
			DataIterator<Entity> iterator = iterable.iterator();
			assertNextCountry(1, "Germany", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
			
			// check states
			iterable = t.queryEntities("STATE", null, context);
			iterator = iterable.iterator();
			assertNextState(2, 1, "Bayern", iterator);
			assertNextState(5, 1, "Hamburg", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
			
			// check cities
			iterable = t.queryEntities("CITY", null, context);
			iterator = iterable.iterator();
			assertNextCity(3, 2, "München", iterator);
			assertNextCity(4, 2, "Ingolstadt", iterator);
			assertNull(iterator.next(new DataContainer<>()));
			((Closeable) iterator).close();
		} finally {
			IOUtil.close(runner);
		}
	}

	
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private void assertNextCountry(int id, String name, DataIterator<Entity> iterator) {
		Entity expectedCountry = createEntity("COUNTRY", "ID", id, "NAME", name);
		Entity actualCountry = iterator.next(new DataContainer<>()).getData();
		assertEquals(expectedCountry, actualCountry);
	}
	
	private void assertNextState(int id, Integer countryId, String name, DataIterator<Entity> iterator) {
		Entity expectedState = createEntity("STATE", "ID", id, "COUNTRY_FK", countryId, "NAME", name);
		assertEquals(expectedState, iterator.next(new DataContainer<>()).getData());
	}
	
	private void assertNextCity(int id, Integer stateId, String name, DataIterator<Entity> iterator) {
		Entity expectedCity = createEntity("CITY", "ID", id, "STATE_FK", stateId, "NAME", name);
		assertEquals(expectedCity, iterator.next(new DataContainer<>()).getData());
	}
	
	private static void dropTables(Connection s) throws SQLException {
		DBUtil.executeUpdate("drop table user", s);
		DBUtil.executeUpdate("drop table role", s);
		DBUtil.executeUpdate("drop table city", s);
		DBUtil.executeUpdate("drop table state", s);
		DBUtil.executeUpdate("drop table country", s);
	}
	
}