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
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link HeadSequence}.<br/><br/>
 * Created: 25.07.2010 11:19:57
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class HeadSequenceTest extends GeneratorTest {

	@Test
    public void testLongGenerator() {
        expectGeneratedSequence(longGenerator(1),  0L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(2),  0L, 1L).withCeasedAvailability();
    }

	@Test
    public void testDoubleGenerator() {
        expectGeneratedSequence(doubleGenerator(1),  0.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(2),  0., 1.).withCeasedAvailability();
    }

	@Test
    public void testApply() {
        expectGeneratedSequence(charGenerator(1),  'A').withCeasedAvailability();
        expectGeneratedSequence(charGenerator(2),  'A', 'B').withCeasedAvailability();
    }
	
	// test helpers ----------------------------------------------------------------------------------------------------

    private Generator<Long> longGenerator(long n) {
		Sequence sequence = new HeadSequence(n);
        return initialize(sequence.createNumberGenerator(Long.class, 0L, 1000L, 1L, false));
    }

    private Generator<Double> doubleGenerator(long n) {
		Sequence sequence = new HeadSequence(n);
        return initialize(sequence.createNumberGenerator(Double.class, 0., 1000., 1., false));
    }

    private Generator<Character> charGenerator(long n) {
		Sequence sequence = new HeadSequence(n);
		Generator<Character> source = new SequenceTestGenerator<>('A', 'B', 'C', 'D');
        return initialize(sequence.applyTo(source, false));
    }

}
