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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import com.rapiddweller.model.data.DataModel;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link QueryGenerator}.<br/><br/>
 * Created: 09.08.2010 13:05:02
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class QueryGeneratorTest extends GeneratorTest {

    static DefaultDBSystem db;

    @BeforeClass
    public static void setupDB() {
        db = new DefaultDBSystem("db", HSQLUtil.getInMemoryURL(QueryGeneratorTest.class.getSimpleName()), HSQLUtil.DRIVER, "sa", null, new DataModel());
        db.execute("create table TT ( id int, value int )");
        db.execute("insert into TT (id, value) values (1, 1000)");
    }

    @Before
    public void setupTable() {
        db.execute("update TT set value = 1000 where id = 1");
    }

    @AfterClass
    public static void closeDB() {
        db.execute("drop table TT");
        IOUtil.close(db);
    }

    @Test
    public void testConstructor() {
        QueryGenerator<Object> actualQueryGenerator = new QueryGenerator<Object>();
        assertEquals("QueryGenerator[null]", actualQueryGenerator.toString());
        Class<?> expectedGeneratedType = Object.class;
        assertSame(expectedGeneratedType, actualQueryGenerator.getGeneratedType());
        assertNull(actualQueryGenerator.getSource());
    }

    @Test
    public void testSimple() {
        QueryGenerator<Integer> generator = null;
        try {
            generator = new QueryGenerator<>("select value from TT", db, true);
            generator.init(context);
            assertEquals(1000, GeneratorUtil.generateNonNull(generator).intValue());
            assertUnavailable(generator);

            db.execute("update TT set value = 1001 where id = 1");
            generator.reset();
            assertEquals(1001, GeneratorUtil.generateNonNull(generator).intValue());
            assertUnavailable(generator);
        } finally {
            IOUtil.close(generator);
        }
    }

}
