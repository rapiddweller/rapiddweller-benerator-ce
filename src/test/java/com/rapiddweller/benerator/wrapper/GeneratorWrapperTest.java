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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.util.UnsafeGenerator;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link GeneratorWrapper}.<br/>
 * <br/>
 * Created at 18.03.2009 07:55:31
 * @since 0.5.9
 * @author Volker Bergmann
 */

public class GeneratorWrapperTest extends GeneratorTest {
	
	@Test
	public void testReset() {
		MyWrapper wrapper = initialize(new MyWrapper(new Source12()));
		expect12(wrapper);
		wrapper.reset();
		expect12(wrapper);
		wrapper.close();
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private static void expect12(MyWrapper wrapper) {
		assertEquals(1, (int) GeneratorUtil.generateNonNull(wrapper));
		assertEquals(2, (int) GeneratorUtil.generateNonNull(wrapper));
		assertUnavailable(wrapper);
    }
	
	public static class MyWrapper extends GeneratorWrapper<Integer, Integer> {

        public MyWrapper(Generator<Integer> source) {
	        super(source);
        }

        @Override
		public Class<Integer> getGeneratedType() {
	        return getSource().getGeneratedType();
        }
		
		@Override
		public ProductWrapper<Integer> generate(ProductWrapper<Integer> wrapper) {
	        return getSource().generate(wrapper);
        }

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
