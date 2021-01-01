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
import org.junit.Test;

/**
 * Tests the {@link LiteralSequence}.<br/><br/>
 * Created: 03.06.2010 09:08:56
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class LiteralSequenceTest extends GeneratorTest {
	
	@Test
	public void testCreateGenerator_empty() {
		LiteralSequence sequence = new LiteralSequence("");
		Generator<Integer> generator = sequence.createNumberGenerator(Integer.class, 1, 20, 1, false);
		generator.init(context);
		expectGeneratedSequence(generator);
	}

	@Test
	public void testCreateGenerator_int() {
		LiteralSequence sequence = new LiteralSequence("2, 3, 5, 7, 11");
		Generator<Integer> generator = sequence.createNumberGenerator(Integer.class, 1, 20, 1, false);
		generator.init(context);
		expectGeneratedSequence(generator, 2, 3, 5, 7, 11);
	}

	@Test
	public void testApply() {
		Generator<String> source = new SequenceTestGenerator<>("A", "B", "C", "D");
		LiteralSequence sequence = new LiteralSequence("1, 3");
		Generator<String> generator = sequence.applyTo(source, false);
		generator.init(context);
		expectGeneratedSequence(generator, "B", "D");
	}

}
