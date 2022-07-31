/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.dbunit;

import com.rapiddweller.format.util.DataUtil;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.AbstractEntityIteratorTest;

import static org.junit.Assert.assertEquals;

/**
 * Abstract parent class for DbUnit tests.<br/><br/>
 * Created: 31.07.2022 11:45:18
 * @author Volker Bergmann
 * @since 3.0.0
 */
public abstract class AbstractDbUnitIteratorTest extends AbstractEntityIteratorTest {

	protected void check(AbstractDbUnitEntityIterator iterator) {
		assertEquals(createPerson("Alice", "23"), DataUtil.nextNotNullData(iterator));
		assertEquals(createPerson("Bob", "34"), DataUtil.nextNotNullData(iterator));
		assertEquals(createPerson("Charly", "45"), DataUtil.nextNotNullData(iterator));
		assertEquals(createRole("Admin"), DataUtil.nextNotNullData(iterator));
		assertEquals(createRole("User"), DataUtil.nextNotNullData(iterator));
		assertUnavailable(iterator);
		iterator.close();
	}

	protected Entity createPerson(String name, String age) {
		Entity person = new Entity(createComplexType("PERSON"));
		person.setComponent("name", name);
		person.setComponent("age", age);
		return person;
	}

	protected Entity createRole(String name) {
		Entity role = new Entity(createComplexType("ROLE"));
		role.setComponent("name", name);
		return role;
	}

}
