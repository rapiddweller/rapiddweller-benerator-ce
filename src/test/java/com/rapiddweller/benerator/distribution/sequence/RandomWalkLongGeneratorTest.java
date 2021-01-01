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

import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.CollectionUtil;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the RandomWalkLongGenerator
 * Created: 18.06.2006 09:11:19
 * @since 0.1
 * @author Volker Bergmann
 */
public class RandomWalkLongGeneratorTest extends GeneratorClassTest {

    public RandomWalkLongGeneratorTest() {
        super(RandomWalkLongGenerator.class);
    }

    @Test
    public void testGreater() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator simpleGenerator = new RandomWalkLongGenerator(1, 5, 1, 1, 1, 1);
        simpleGenerator.init(context);
        expectGeneratedSequence(simpleGenerator, 1L, 2L, 3L);

        RandomWalkLongGenerator oddGenerator = new RandomWalkLongGenerator(1, 5, 2, 1, 2, 2);
        oddGenerator.init(context);
        expectGeneratedSequence(oddGenerator, 1L, 3L, 5L);
    }

    @Test
    public void testGreaterOrEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 1, 0, 2);
        generator.init(context);
        Set<Long> space = CollectionUtil.toSet(1L, 3L, 5L);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
    }

    @Test
	public void testEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 1, 0, 0);
        generator.init(context);
        expectGeneratedSequence(generator, 3L, 3L, 3L);
    }

    @Test
    public void testLessOrEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 5, -2, 0);
        generator.init(context);
        Set<Long> space = CollectionUtil.toSet(1L, 3L, 5L);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
    }

    @Test
    public void testLess() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 1, -2, -2);
        generator.init(context);
        expectGeneratedSequence(generator, 5L, 3L, 1L);
    }

    @Test
    public void testLessOrGreater() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 1, -2, 2);
        generator.init(context);
        Set<Long> space = CollectionUtil.toSet(1L, 3L, 5L);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
    }

    private static void assertProductSpace(Set<Long> space, RandomWalkLongGenerator generator) {
        Long product = generator.generate(new ProductWrapper<>()).unwrap();
		assertTrue("Expected one of " + space + ", but found " + product, space.contains(product));
    }

}
