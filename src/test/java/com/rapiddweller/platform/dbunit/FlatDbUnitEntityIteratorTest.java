/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.platform.dbunit;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import org.junit.Test;

/**
 * Tests the {@link FlatDbUnitEntityIterator}.<br/><br/>
 * Created: 05.08.2007 08:05:10
 * @author Volker Bergmann
 */
public class FlatDbUnitEntityIteratorTest extends AbstractDbUnitIteratorTest {

	@Test
	public void testFlatDataset() {
		FlatDbUnitEntityIterator iterator = new FlatDbUnitEntityIterator(
			"com/rapiddweller/platform/importer/dbunit/person+role-dbunit.flat.xml",
			new DefaultBeneratorContext());
		check(iterator);
	}

}
