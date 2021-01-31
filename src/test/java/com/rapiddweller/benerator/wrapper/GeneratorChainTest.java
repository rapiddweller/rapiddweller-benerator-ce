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

import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.sample.OneShotGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link GeneratorChain} class.<br/><br/>
 * Created: 22.07.2011 15:02:07
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class GeneratorChainTest extends GeneratorTest {

	@Test
	public void testUnique() {
		GeneratorChain<Integer> chain = new GeneratorChain<>(Integer.class, true,
                new SequenceTestGenerator<>(2, 3),
                new SequenceTestGenerator<>(1, 2));
		chain.init(context);
		expectGeneratedSequence(chain, 2, 3, 1).withCeasedAvailability();
	}
	
	@Test
	public void testNonUnique() {
		GeneratorChain<Integer> chain = new GeneratorChain<>(Integer.class, false,
                new OneShotGenerator<>(2),
                new OneShotGenerator<>(1));
		chain.init(context);
		expectGeneratedSequence(chain, 2, 1).withCeasedAvailability();
	}
	
}
