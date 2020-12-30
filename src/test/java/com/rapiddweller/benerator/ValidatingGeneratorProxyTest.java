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

package com.rapiddweller.benerator;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import org.junit.Test;
import static org.junit.Assert.*;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.benerator.wrapper.ValidatingGeneratorProxy;
import com.rapiddweller.common.Validator;

/**
 * Tests the {@link ValidatingGeneratorProxy}.<br/><br/>
 * Created: 29.09.2006 17:01:59
 * @since 0.1
 * @author Volker Bergmann
 */
public class ValidatingGeneratorProxyTest {

	@Test
    public void testValid() {
        Generator<?> generator = new ValidatingGeneratorProxy<Integer>(
                new ConstantGenerator<Integer>(1),
                new MockValidator(true));
        for (int i = 0; i < 10; i++)
        	GeneratorUtil.generateNullable(generator);
    }

	@Test
    public void testInvalid() {
        Generator<?> generator = new ValidatingGeneratorProxy<Integer>(
                new ConstantGenerator<Integer>(1),
                new MockValidator(false));
        try {
            GeneratorUtil.generateNullable(generator);
            fail("Exception expercted");
        } catch (IllegalGeneratorStateException e) {
            // This is the expected behavior
        }
    }

    private static final class MockValidator implements Validator<Integer> {

        private boolean result;

        public MockValidator(boolean result) {
            this.result = result;
        }

        @Override
		public boolean valid(Integer object) {
            return result;
        }
    }
    
}
