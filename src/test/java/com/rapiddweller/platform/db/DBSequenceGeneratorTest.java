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

import java.sql.SQLException;

import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.jdbacl.dialect.HSQLUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link DBSequenceGenerator}.<br/><br/>
 * Created: 11.11.2009 18:50:58
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DBSequenceGeneratorTest extends GeneratorTest {

	private DefaultDBSystem db;
	private String seq = getClass().getSimpleName();
	
	@Before
	public void setUpDB() throws SQLException {
		db = new DefaultDBSystem("db", 
				HSQLUtil.IN_MEMORY_URL_PREFIX + "benerator", 
				HSQLUtil.DRIVER, HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD, context.getDataModel());
		// create sequence and read its value
		db.createSequence(seq);
	}
	
	@Test
	public void testUncached() throws Exception {
		check(false);
	}

	@Test
	public void testCached() throws Exception {
		check(true);
	}

	protected void check(boolean cached) {
		long first = db.nextSequenceValue(seq);
		try {
			// assure that the generated values are like if they stem from the DB sequence
			DBSequenceGenerator generator = new DBSequenceGenerator(seq, db, cached);
			generator.init(context);
			long n = first;
			for (int i = 0; i < 10; i++) {
				Long product = generator.generate();
				assertNotNull(product);
				assertEquals(++n, product.longValue());
			}
			// verify that the original sequence value has not been changed if cache is active
			if (cached)
				assertEquals(first + 2, db.nextSequenceValue(seq));
			// assure that after closing the generator, the DB sequence continues as if it had been used itself
			generator.close();
			assertEquals(n + 1, db.nextSequenceValue(seq));
		} finally {
			db.dropSequence(seq);
		}
	}

}
