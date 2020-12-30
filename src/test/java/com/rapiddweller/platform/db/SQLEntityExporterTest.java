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

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;

import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.ReaderLineIterator;
import com.rapiddweller.common.TimeUtil;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;

/**
 * Tests the {@link SQLEntityExporter}.<br/><br/>
 * Created: 18.02.2010 15:24:05
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SQLEntityExporterTest extends ModelTest {
	
	private static final String FILENAME = "target" + File.separator 
		+ SQLEntityExporterTest.class.getSimpleName() + ".sql";

	@Test(expected = ConfigurationError.class)
	public void testWithoutDialect() throws Exception {
		SQLEntityExporter exporter = new SQLEntityExporter(FILENAME);
		try {
			Entity alice = createEntity("Person", "name", "Alice", "birthDate", TimeUtil.date(1987, 11, 31), "score", 23);
			exporter.startProductConsumption(alice);
		} finally {
			exporter.close();
			FileUtil.deleteIfExists(new File(FILENAME));
		}
	}
	
	@Test
	public void testNormal() throws Exception {
		try {
			Entity alice = createEntity("Person", "name", "Alice", "birthDate", TimeUtil.date(1987, 11, 31), "score", 23);
			Entity bob = createEntity("Person", "name", "Bob", "birthDate", TimeUtil.date(1977, 11, 31), "score", 34);
			SQLEntityExporter exporter = new SQLEntityExporter(FILENAME);
			exporter.setDialect("hsql");
			exporter.startProductConsumption(alice);
			exporter.startProductConsumption(bob);
			exporter.close();
			BufferedReader reader = IOUtil.getReaderForURI(FILENAME);
			ReaderLineIterator iterator = new ReaderLineIterator(reader);
			assertTrue(iterator.hasNext());
			assertEquals("insert into \"Person\" (name, birthDate, score) values ('Alice', '1987-12-31', 23);", iterator.next());
			assertTrue(iterator.hasNext());
			assertEquals("insert into \"Person\" (name, birthDate, score) values ('Bob', '1977-12-31', 34);", iterator.next());
			assertFalse(iterator.hasNext());
		} finally {
			FileUtil.deleteIfExists(new File(FILENAME));
		}
	}
	
}
