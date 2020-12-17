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

package com.rapiddweller.domain.math;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link PadovanSequence}.<br/>
 * <br/>
 * Created at 03.07.2009 13:25:56
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class PadovanSequenceTest extends GeneratorTest {

	@Test
	public void testLimitedRange() {
		PadovanSequence padovanSequence = new PadovanSequence();
		Generator<Integer> generator = padovanSequence.createNumberGenerator(Integer.class, 0, 10, 1, false);
		generator.init(context);
		expectGeneratedSequence(generator, 1, 1, 1, 2, 2, 3, 4, 5, 7, 9).withCeasedAvailability();
	}
	
	@Test
	public void testUnlimitedRange() {
		PadovanSequence padovanSequence = new PadovanSequence();
		Generator<Integer> generator = padovanSequence.createNumberGenerator(Integer.class, 0, null, 1, false);
		generator.init(context);
		expectGeneratedSequence(generator, 1, 1, 1, 2, 2, 3, 4, 5, 7, 9, 12, 16).withContinuedAvailability();
	}
	
	@Test
	public void testUniqueness() {
		PadovanSequence padovanSequence = new PadovanSequence();
		Generator<Integer> generator = padovanSequence.createNumberGenerator(Integer.class, 0, 10, 1, true);
		generator.init(context);
		expectGeneratedSequence(generator, 1, 2, 2, 3, 4, 5, 7, 9).withCeasedAvailability();
	}
	
}
