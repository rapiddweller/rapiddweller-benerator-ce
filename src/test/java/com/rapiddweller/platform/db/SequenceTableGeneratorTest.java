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

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.rapiddweller.benerator.engine.DescriptorRunner;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.commons.IOUtil;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.Entity;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link SequenceTableGenerator}.<br/><br/>
 * Created: 09.08.2010 14:51:40
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class SequenceTableGeneratorTest extends GeneratorTest {
	
	static DefaultDBSystem db;

	@BeforeClass
	public static void setupDB() {
	    db = new DefaultDBSystem("db", HSQLUtil.getInMemoryURL(SequenceTableGeneratorTest.class.getSimpleName()), HSQLUtil.DRIVER, "sa", null, new DataModel());
		db.execute("create table TT ( id1 int, id2 int, value int )");
		db.execute("insert into TT (id1, id2, value) values (1, 2, 1000)");
		db.execute("insert into TT (id1, id2, value) values (2, 3, 2000)");
    }
	
	@Before
	public void setupTable() {
		db.execute("update TT set value = 1000 where id1 = 1 and id2 = 2");
		db.execute("update TT set value = 2000 where id1 = 2 and id2 = 3");
	}

	@AfterClass
	public static void closeDB() {
		db.execute("drop table TT");
		IOUtil.close(db);
	}
	
	@Test
	public void testStatic() {
		SequenceTableGenerator<Integer> generator = null;
		try {
	        generator = new SequenceTableGenerator<Integer>("TT", "value", db);
	        generator.setSelector("id1 = 1 and id2 = 2");
	        generator.init(context);
	        for (int i = 0; i < 100; i++)
	        	assertEquals(1000 + i, generator.generate().intValue());
	        assertAvailable(generator);
        } finally {
	        IOUtil.close(generator);
        }
	}

	@Test
	public void testDynamicSelector() {
		SequenceTableGenerator<Integer> generator = null;
		try {
	        generator = new SequenceTableGenerator<Integer>("TT", "value", db);
	        // the selector makes the generator use row #1 and #2 after each other for generating id values
	        generator.setSelector("{'id1 = ' + (1 + (num % 2)) + ' and id2 = ' + (2 + (num % 2))}");
	        generator.init(context);
	        for (int i = 0; i < 100;) {
		        context.set("num", i);
	        	assertEquals(1000 + i/2, generator.generate().intValue());
	        	i++;
		        context.set("num", i);
	        	assertEquals(2000 + i/2, generator.generate().intValue());
	        	i++;
	        }
	        assertAvailable(generator);
        } finally {
	        IOUtil.close(generator);
        }
	}

	@Test
	public void testParameterizedSelector() {
		SequenceTableGenerator<Integer> generator = null;
		try {
	        generator = new SequenceTableGenerator<Integer>("TT", "value", db, "id1 = ? and id2 = ?");
	        generator.init(context);
        	assertEquals(1000, generator.generateWithParams(1, 2).intValue());
        	assertEquals(2000, generator.generateWithParams(2, 3).intValue());
        	assertEquals(1001, generator.generateWithParams(1, 2).intValue());
        	assertEquals(2001, generator.generateWithParams(2, 3).intValue());
        } finally {
	        IOUtil.close(generator);
        }
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIntegration() throws Exception {
		ConsumerMock consumer = new ConsumerMock(true);
		context.setGlobal("cons", consumer);
		DescriptorRunner runner = new DescriptorRunner("com/rapiddweller/platform/db/SequenceTableIntegrationTest.ben.xml", context);
		try {
			runner.run();
			List<Entity> products = (List<Entity>) consumer.getProducts();
			assertEquals(2, products.size());
			assertEquals(createEntity("x", "id", 2000), products.get(0));
			assertEquals(createEntity("x", "id", 2001), products.get(1));
		} finally {
			IOUtil.close(runner);
		}
	}
	
}
