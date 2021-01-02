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

package com.rapiddweller.benerator.wrapper;

import org.junit.Test;
import static org.junit.Assert.*;

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.util.UnsafeGenerator;

/**
 * Tests the {@link CyclicGeneratorProxy}.<br/>
 * <br/>
 * Created at 18.03.2009 09:17:10
 * @since 0.5.9
 * @author Volker Bergmann
 */

public class CyclicGeneratorProxyTest extends GeneratorTest {
	
	@Test
	public void testSingleIteration() {
		CyclicGeneratorProxy<Integer> proxy = new CyclicGeneratorProxy<>(new Source12());
		proxy.init(context);
		expect12(proxy);
		proxy.close();
		assertUnavailable(proxy);
	}

	@Test
	public void testCyclicIteration() {
		CyclicGeneratorProxy<Integer> proxy = new CyclicGeneratorProxy<>(new Source12());
		proxy.init(context);
		expect12(proxy);
		expect12(proxy);
		proxy.close();
		assertUnavailable(proxy);
	}

	@Test
	public void testReset() {
		CyclicGeneratorProxy<Integer> proxy = new CyclicGeneratorProxy<>(new Source12());
		proxy.init(context);
		assertAvailable(proxy);
		proxy.reset();
		expect12(proxy);
		proxy.close();
		assertUnavailable(proxy);
	}
	
	// helper methods --------------------------------------------------------------------------------------------------

	private static void expect12(CyclicGeneratorProxy<Integer> wrapper) {
		assertEquals(1, (int) GeneratorUtil.generateNonNull(wrapper));
		assertEquals(2, (int) GeneratorUtil.generateNonNull(wrapper));
    }

	public static class Source12 extends UnsafeGenerator<Integer> {
		
		private int n = 0;
		
		@Override
		public Class<Integer> getGeneratedType() {
	        return Integer.class;
        }
		
		@Override
		public ProductWrapper<Integer> generate(ProductWrapper<Integer> wrapper) {
	        return (n < 2 ? wrapper.wrap(++n) : null);
        }

        @Override
        public void reset() throws IllegalGeneratorStateException {
	        n = 0;
        }

        @Override
        public void close() {
	        n = 3;
        }

	}

}
