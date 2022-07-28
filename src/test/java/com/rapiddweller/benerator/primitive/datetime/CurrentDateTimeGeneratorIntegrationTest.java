/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.BeanStatement;
import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.memstore.MemStore;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Integration tests for the {@link CurrentZonedDateTimeGenerator}.<br/><br/>
 * Created: 05.07.2022 17:18:42
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CurrentDateTimeGeneratorIntegrationTest extends AbstractBeneratorIntegrationTest {

	@Test
	public void test_undefined() {
		// create DateTimeGenerator from XML descriptor
		String xml = "<setup xmlns='https://www.benerator.de/schema/3.0.0'>\n" +
				"    <memstore id='mem'/>" +
				"    <generate type='event' count='3' consumer='mem'>\n" +
				"        <attribute name='dateTime' type='zoneddatetime' generator=\"new CurrentZonedDateTimeGenerator('America/Chicago')\"/>\n" +
				"    </generate>\n" +
				"</setup>";
		parseXmlString(xml).execute(context);

		MemStore mem = (MemStore) context.get("mem");
		List<Entity> events = mem.getEntities("event");
		assertEquals(3, events.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(ZoneId.of("America/Chicago"), ((ZonedDateTime) events.get(i).getComponent("dateTime")).getZone());
		}
	}


}
