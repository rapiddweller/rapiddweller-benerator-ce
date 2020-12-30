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
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.CollectionUtil;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link RandomWalkLongGenerator}.<br/><br/>
 * Created: 18.06.2006 09:11:19
 * @since 0.1
 * @author Volker Bergmann
 */
public class RandomWalkDoubleGeneratorTest extends GeneratorClassTest {

    public RandomWalkDoubleGeneratorTest() {
        super(RandomWalkDoubleGenerator.class);
    }

    @Test
    public void testGreaterSimple() {
        RandomWalkDoubleGenerator simpleGenerator = initialize(new RandomWalkDoubleGenerator(1, 5, 1, 1, 1));
        expectGeneratedSequence(simpleGenerator, 1., 2., 3.);
    }

    @Test
    public void testGreaterOdd() {
        RandomWalkDoubleGenerator oddGenerator = initialize(new RandomWalkDoubleGenerator(1, 5, 2, 2, 2));
        expectGeneratedSequence(oddGenerator, 1., 3., 5.);
    }

    @Test
    public void testGreaterOrEquals() {
        RandomWalkDoubleGenerator generator = initialize(new RandomWalkDoubleGenerator(1, 5, 2, 0, 2));
        Set<Double> space = CollectionUtil.toSet(1., 3., 5.);
        assertProductSpace(space, generator);
    }

    @Test
    public void testEquals() {
        RandomWalkDoubleGenerator generator = initialize(new RandomWalkDoubleGenerator(1, 5, 2, 0, 0));
        expectGeneratedSequence(generator, 3., 3., 3.);
    }

    @Test
    public void testLessOrEquals() {
        RandomWalkDoubleGenerator generator = initialize(new RandomWalkDoubleGenerator(1, 5, 2, -2, 0));
        Set<Double> space = CollectionUtil.toSet(1., 3., 5.);
        assertProductSpace(space, generator);
    }

    @Test
    public void testLess() {
        RandomWalkDoubleGenerator generator = initialize(new RandomWalkDoubleGenerator(1, 5, 2, -2, -2));
        expectGeneratedSequence(generator, 5., 3., 1.);
    }

    @Test
    public void testLessOrGreater() {
        RandomWalkDoubleGenerator generator = initialize(new RandomWalkDoubleGenerator(1, 5, 2, -2, 2));
        Set<Double> space = CollectionUtil.toSet(1., 3., 5.);
        assertProductSpace(space, generator);
    }

    private static void assertProductSpace(Set<Double> space, Generator<Double> generator) {
    	Double product = generator.generate(new ProductWrapper<Double>()).unwrap();
		assertTrue("Expected one of " + space + ", but found " + product, space.contains(product));
    }

}
