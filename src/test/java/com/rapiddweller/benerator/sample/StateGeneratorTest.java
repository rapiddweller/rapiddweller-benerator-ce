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

package com.rapiddweller.benerator.sample;

import java.util.List;

import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link StateGenerator}.<br/>
 * <br/>
 * Created at 17.07.2009 05:56:28
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class StateGeneratorTest extends GeneratorTest {

	@Test
	public void testDeterministicSequence() {
		StateGenerator<Integer> generator = new StateGenerator<>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 2, 1.);
		generator.addTransition(2, null, 1.);
		generator.init(context);
		expectGeneratedSequence(generator, 1, 2).withCeasedAvailability();
	}
	
	@Test
	public void testDeterministicIntSequenceByStringSpec() {
		StateGenerator<Integer> generator = new StateGenerator<>("null->1,1->2,2->null");
		generator.init(context);
		expectGeneratedSequence(generator, 1, 2).withCeasedAvailability();
	}
	
	@Test
	public void testDeterministicStringSequenceByStringSpec() {
		StateGenerator<String> generator = new StateGenerator<>("null->'1', '1'->'2', '2'->null");
		generator.init(context);
		expectGeneratedSequence(generator, "1", "2").withCeasedAvailability();
	}
	
	/** Tests a setup that generates Sequences (1, 2)*, e.g. (1, 2), (1, 2, 1, 2), (1, 2, 1, 2, 1, 2), ... */
	@Test
	public void testRandomSequence() {
		StateGenerator<Integer> generator = new StateGenerator<>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 2, 1.);
		generator.addTransition(2, 1, 0.5);
		generator.addTransition(2, null, 0.5);
		generator.init(context);
		for (int n = 0; n < 10; n++) {
			List<Integer> products = GeneratorUtil.allProducts(generator);
			assertEquals(0, products.size() % 2);
			for (int i = 0; i < products.size(); i++)

				assertEquals((1 + (i % 2)), products.get(i).intValue());
			generator.reset();
		}
	}
	
	/** Tests a setup that generates Sequences (1, 2)*, e.g. (1, 2), (1, 2, 1, 2), (1, 2, 1, 2, 1, 2), ... */
	@Test
	public void testRandomSequenceByStringSpec() {
		StateGenerator<String> generator = new StateGenerator<>("null->1,1->2,2->1^0.5,2->null^0.5");
		generator.init(context);
		for (int n = 0; n < 10; n++) {
			List<String> products = GeneratorUtil.allProducts(generator);
			assertEquals(0, products.size() % 2);
			for (int i = 0; i < products.size(); i++) {
				assertEquals(1 + (i % 2), products.get(i));
			}
			generator.reset();
		}
	}
	
	/** Tests a setup that generates Sequences 1*, e.g. (1), (1, 1), (1, 1, 1), ... */
	@Test
	public void testRecursion() {
		StateGenerator<Integer> generator = new StateGenerator<>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 1, 0.5);
		generator.addTransition(1, null, 0.5);
		generator.init(context);
		for (int n = 0; n < 10; n++) {
			List<Integer> products = GeneratorUtil.allProducts(generator);
			for (Integer product : products) assertEquals(1, product.intValue());
			generator.reset();
		}
	}
	
	@Test(expected = InvalidGeneratorSetupException.class)
	public void testNoInitialState() {
		StateGenerator<Integer> generator = new StateGenerator<>(Integer.class);
		generator.addTransition(1, 2, 0.6);
		generator.init(context);
	}
	
	@Test(expected = InvalidGeneratorSetupException.class)
	public void testNoFinalState() {
		StateGenerator<Integer> generator = new StateGenerator<>(Integer.class);
		generator.addTransition(null, 1, 0.6);
		generator.init(context);
	}
	
}
