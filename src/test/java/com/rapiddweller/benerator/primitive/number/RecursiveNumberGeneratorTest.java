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

package com.rapiddweller.benerator.primitive.number;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link RecurrenceRelationNumberGenerator}.<br/><br/>
 * Created: 13.10.2009 19:54:23
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RecursiveNumberGeneratorTest extends GeneratorTest {

	@Test
	public void testDepthOne() {
		Generator<Integer> generator = new RecurrenceRelationNumberGenerator<Integer>(Integer.class, 1, 0, 5) {

			@Override
            protected Integer a0(int n) {
	            return 0;
            }

			@Override
            protected Integer aN() {
	            return aN(-1) + 1;
            }
			
		};
		generator.init(context);
		expectGeneratedSequence(generator, 0, 1, 2, 3, 4, 5).withCeasedAvailability();
	}

	@Test
	public void testDepthTwo() {
		Generator<Integer> generator = new RecurrenceRelationNumberGenerator<Integer>(Integer.class, 2, 0, 5) {

			@Override
            protected Integer a0(int n) {
				if (n == 0)
					return 0;
				else
					return 1;
            }

			@Override
            protected Integer aN() {
	            return 2 * aN(-2) + aN(-1);
            }
			
		};
		generator.init(context);
		expectGeneratedSequence(generator, 0, 1, 1, 3, 5).withCeasedAvailability();
	}
	
}
