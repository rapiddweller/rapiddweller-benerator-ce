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

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.commons.ConfigurationError;
import org.junit.Test;

/**
 * Tests the {@link WeightedNumbers} sequence.<br/><br/>
 * Created: 02.06.2010 08:10:28
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class WeightedNumbersTest extends GeneratorTest {

	WeightedNumbers<Integer> intDist = new WeightedNumbers<Integer>("0^0,1^3,2^2,3^1");

	@Test(expected = ConfigurationError.class)
	public void testCreateGenerator_unique() {
		intDist.createNumberGenerator(Integer.class, 0, 3, 1, true);
	}
	
	@Test
	public void testCreateGenerator_nonUnique() {
		Generator<Integer> generator = intDist.createNumberGenerator(Integer.class, 0, 3, 1, false);
		generator.init(context);
		expectRelativeWeights(generator, 3000, 0, 0, 1, 3, 2, 2, 3, 1);
	}
	
	@Test(expected = ConfigurationError.class)
	public void testApply_null() {
		intDist.applyTo(null, true);
	}

	@Test(expected = ConfigurationError.class)
	public void testApply_unique() {
		intDist.applyTo(new SequenceTestGenerator<String>("X", "A", "B", "C"), true);
	}

	@Test
	public void testApply_nonUnique() {
		Generator<String> generator = intDist.applyTo(new SequenceTestGenerator<String>("X", "A", "B", "C"), false);
		generator.init(context);
		expectRelativeWeights(generator, 3000, "X", 0, "A", 3, "B", 2, "C", 1);
	}

}
