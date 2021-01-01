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
import com.rapiddweller.benerator.SequenceTestGenerator;
import com.rapiddweller.benerator.sample.OneShotGenerator;
import com.rapiddweller.benerator.sample.SequenceGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link UniqueMultiSourceArrayGenerator}.<br/>
 * <br/>
 * Created: 17.11.2007 13:39:04
 * @author Volker Bergmann
 */
public class UniqueCompositeArrayGeneratorTest extends GeneratorTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testInteger() {
        Generator<Integer>[] sources = new Generator [] {
				new SequenceTestGenerator<>(0, 1),
				new SequenceTestGenerator<>(0, 1),
				new SequenceTestGenerator<>(0, 1)
        };
        UniqueMultiSourceArrayGenerator<Integer> generator = new UniqueMultiSourceArrayGenerator<>(Integer.class, sources);
        generator.init(context);
		expectUniqueProducts(generator,  8).withCeasedAvailability();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testString() {
        Generator<String>[] sources = new Generator [] {
				new OneShotGenerator<>("x"),
				new SequenceTestGenerator<>("a", "b"),
				new OneShotGenerator<>("x")
        };
        UniqueMultiSourceArrayGenerator<String> generator = new UniqueMultiSourceArrayGenerator<>(String.class, sources);
        generator.init(context);
		expectUniqueProducts(generator,  2).withCeasedAvailability();
    }

	@SuppressWarnings("unchecked")
    @Test
	public void testNotNull() {
        Generator<Integer>[] sources = new Generator [] {
				new SequenceGenerator<>(Integer.class, 1, 2),
				new SequenceGenerator<>(Integer.class, 3, 4)
        };
        UniqueMultiSourceArrayGenerator<Integer> generator = new UniqueMultiSourceArrayGenerator<>(Integer.class, sources);
		generator.init(context);
		expectGeneratedSequence(generator, 
			new Integer[] { 1, 3 },
			new Integer[] { 1, 4 },
			new Integer[] { 2, 3 },
			new Integer[] { 2, 4 }
		);
	}
	
	@SuppressWarnings("unchecked")
    @Test
	public void testNull() {
        Generator<Integer>[] sources = new Generator [] {
				new SequenceGenerator<>(Integer.class, null, 1),
				new SequenceGenerator<>(Integer.class, null, 2)
        };
		UniqueMultiSourceArrayGenerator<Integer> generator = new UniqueMultiSourceArrayGenerator<>(
				Integer.class, sources);
		generator.init(context);
		expectGeneratedSequence(generator, 
			new Integer[] { null, null },
			new Integer[] { null,    2 },
			new Integer[] {    1, null },
			new Integer[] {    1,    2 }
		);
	}
	
    @SuppressWarnings("unchecked")
	@Test
	public void testThreeDigits() {
        Generator<Integer>[] sources = new Generator [] {
				new SequenceGenerator<>(Integer.class, 1, 2),
				new SequenceGenerator<>(Integer.class, 3, 4),
				new SequenceGenerator<>(Integer.class, 5, 6)
        };
		UniqueMultiSourceArrayGenerator<Integer> generator = new UniqueMultiSourceArrayGenerator<>(Integer.class, sources);
		generator.init(context);
		expectGeneratedSequence(generator, 
			new Integer[] { 1, 3, 5 },
			new Integer[] { 1, 3, 6 },
			new Integer[] { 1, 4, 5 },
			new Integer[] { 1, 4, 6 },
			new Integer[] { 2, 3, 5 },
			new Integer[] { 2, 3, 6 },
			new Integer[] { 2, 4, 5 },
			new Integer[] { 2, 4, 6 }
		);
	}
	
}
