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

package com.rapiddweller.benerator.engine.statement;

import static org.junit.Assert.*;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.DescriptorBasedGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.SystemInfo;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;

/**
 * Tests the {@link DescriptorBasedGenerator}.<br/><br/>
 * Created: 23.02.2010 12:17:27
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DescriptorBasedGeneratorTest extends GeneratorTest {

	@Test
	public void testGetGenerator() throws Exception {
		String lf = SystemInfo.getLineSeparator();
		String uri = "string://<setup>" + lf +
				"	<generate name='perGen' type='Person' count='3'>" + lf +
				"		<id name='id' type='int'/>" + lf +
				"		<attribute name='name' constant='Alice'/>" + lf +
				"	</generate>" + lf +
				"</setup>";
		context.setValidate(false);
		Generator<?> generator = new DescriptorBasedGenerator(uri, "perGen", context);
		assertEquals(Object.class, generator.getGeneratedType());
		assertNotNull(generator);
		generator.init(context);
		for (int i = 0; i < 3; i++)
			checkGeneration((Entity) GeneratorUtil.generateNonNull(generator), i + 1);
		assertUnavailable(generator);
		generator.close();
	}

	private void checkGeneration(Entity entity, int id) {
	    assertEquals(createEntity("Person", "id", id, "name", "Alice"), entity);
    }
	
}
