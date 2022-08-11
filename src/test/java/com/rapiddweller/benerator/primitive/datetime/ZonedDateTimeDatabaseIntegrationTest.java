/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.db.DefaultDBSystem;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the database integration of Benerator's primitive data type 'zoneddatetime'.<br/><br/>
 * Created: 07.08.2022 18:30:49
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ZonedDateTimeDatabaseIntegrationTest extends AbstractBeneratorIntegrationTest {

	private static final String XML = "<setup>\n" +
		"  <database id='db' url='jdbc:h2:mem:zdtit' driver='org.h2.Driver' schema='PUBLIC' user='sa' password='' />\n" +
		"  <execute target='db'>CREATE TABLE ZDTIT ( ZDT TIMESTAMP WITH TIME ZONE NOT NULL );</execute>\n" +
		"  <generate type='ZDTIT' count='5' consumer='db'>\n" +
		"    <attribute name='ZDT' type='zoneddatetime' generator='CurrentZonedDateTimeGenerator' />\n" +
		"  </generate>\n" +
		"  <memstore id='mem'/>\n" +
		"  <iterate source='db' type='ZDTIT' consumer='mem'/>\n" +
		"</setup>";

	@Test
	public void testZonedDateTimeSupport() {
		BeneratorContext context = parseAndExecuteXmlString(XML);
		// check mem
		MemStore mem = (MemStore) context.get("mem");
		List<Entity> timestamps = mem.getEntities("ZDTIT");
		assertEquals(5, timestamps.size());
		for (Entity timestamp : timestamps) {
			assertTrue(timestamp.get("zdt") instanceof ZonedDateTime);
		}
		// check database
		DefaultDBSystem db = (DefaultDBSystem) context.get("db");
		assertNotNull(db);
		DataSource<Entity> timestampDs = db.queryEntities("ZDTIT", null, context);
		DataIterator<Entity> dsIterator = timestampDs.iterator();
		int count = 0;
		DataContainer<Entity> container = new DataContainer<>();
		while ((container = dsIterator.next(container)) != null) {
			Entity zdtit = container.getData();
			assertTrue(zdtit.get("zdt") instanceof ZonedDateTime);
			count++;
		}
		assertEquals(5, count);
	}

}
