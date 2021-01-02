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
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.script.Transition;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link StateTransitionGenerator}.<br/>
 * <br/>
 * Created at 17.07.2009 08:15:03
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class StateTransitionGeneratorTest extends GeneratorTest {

	@Test
    public void testDeterministicSequence() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 2, 1.);
		generator.addTransition(2, null, 1.);
		generator.init(context);
		expectGeneratedSequence(generator, 
				new Transition(null,    1), 
				new Transition(   1,    2),
				new Transition(   2,    null)
			).withCeasedAvailability();
	}
	
	/** Tests a setup that generates Sequences null->1, (1->2, 2->1)* */
	@Test
    public void testRandomSequence() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 2, 1.);
		generator.addTransition(2, 1, 0.5);
		generator.addTransition(2, null, 0.5);
		generator.init(context);
		for (int n = 0; n < 10; n++) {
			List<Transition> products = GeneratorUtil.allProducts(generator);
			assertTrue("Expected an odd number of products, but found: " + products.size(), products.size() % 2 == 1);
			assertEquals(new Transition(null, 1), products.get(0));
			for (int i = 1; i < products.size() - 1; i++) {
				int oldState = 1 + ((i - 1) % 2);
				int newState = 1 + (i % 2);
				assertEquals(new Transition(oldState, newState), products.get(i));
			}
			assertEquals(new Transition(2, null), CollectionUtil.lastElement(products));
			generator.reset();
		}
	}
	
	/** Tests a setup that generates Sequences 1*, e.g. (1), (1, 1), (1, 1, 1), ... */
	@Test
    public void testRecursion() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 1, 0.5);
		generator.addTransition(1, null, 0.5);
		generator.init(context);
		checkRecursion(generator);
	}

	/** Tests the textual specification of transitions */
	@Test
    public void testTextualSpec() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<>(
                Integer.class, "null->1, 1->1^0.5, 1->null^0.5");
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 1, 0.5);
		generator.addTransition(1, null, 0.5);
		generator.init(context);
		checkRecursion(generator);
	}
	
	@Test(expected = InvalidGeneratorSetupException.class)
	public void testNoInitialState() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<>(Integer.class);
		generator.addTransition(1, 2, 0.6);
		generator.init(context);
	}
	
	@Test(expected = InvalidGeneratorSetupException.class)
	public void testNoFinalState() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<>(Integer.class);
		generator.addTransition(null, 1, 0.6);
		generator.init(context);
	}
	
	// private helpers -------------------------------------------------------------------------------------------------
	
	private static void checkRecursion(StateTransitionGenerator<Integer> generator) {
	    for (int n = 0; n < 10; n++) {
			List<Transition> products = GeneratorUtil.allProducts(generator);
			assertEquals(new Transition(null, 1), products.get(0));
			for (int i = 1; i < products.size() - 1; i++)
				assertEquals(new Transition(1, 1), products.get(i));
			assertEquals(new Transition(1, null), CollectionUtil.lastElement(products));
			generator.reset();
		}
    }
	
}
