/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.test.AbstractBeneratorIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for DB issues.<br/><br/>
 * Created: 18.02.2022 20:23:07
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class DBIssuesTest extends AbstractBeneratorIntegrationTest {

	@Test
	public void test_issue_heap_overflow_on_unlimited_varchar() {
		// the heap overflow is caused by the edition's VarLengthStringGenerator, so this test needs to be performed on each edition
		String xml = "<setup>\n" +
				"    <database id='db' url='jdbc:h2:mem:target' driver='org.h2.Driver' user='sa' schema='public'/>\n" +
				"    <execute target='db'>\n" +
				"        drop table customer if exists;\n" +
				"        create table customer ( name VARCHAR(1000) not null );\n" +
				"    </execute>\n" +
				"    <generate type='customer' count='10' consumer='db'/>\n" +
				"    <execute target='db'>drop table customer if exists;</execute>\n" +
			"</setup>";
		parseAndExecuteXmlString(xml);
	}

}
