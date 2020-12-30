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
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.distribution.WeightFunction;
import com.rapiddweller.benerator.distribution.WeightedDoubleGenerator;
import com.rapiddweller.benerator.distribution.function.ConstantFunction;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.CollectionUtil;
import org.junit.Test;

/**
 * Tests the {@link WeightedDoubleGenerator}.<br/><br/>
 * Created: 18.06.2006 15:04:17
 * @since 0.1
 * @author Volker Bergmann
 */
public class WeightedDoubleGeneratorTest extends GeneratorClassTest {

    public WeightedDoubleGeneratorTest() {
        super(WeightedDoubleGenerator.class);
    }

    @Test
    public void testSingleValueGeneration() throws IllegalGeneratorStateException {
        checkProductSet(
                create(0, 0, 1, new ConstantFunction(1)), 300, CollectionUtil.toSet(0.));
        checkProductSet(
        		create( 1,  1, 0.5, new ConstantFunction(1)), 300, CollectionUtil.toSet(1.));
        checkProductSet(
        		create(-1, -1, 1, new ConstantFunction(1)), 300, CollectionUtil.toSet(-1.));
    }

    @Test
    public void testDiscreteRangeGeneration() throws IllegalGeneratorStateException {
        checkProductSet(
        		create( -1,  0, 0.5, new ConstantFunction(1)), 300, CollectionUtil.toSet(-1., -0.5, 0.));
        checkProductSet(
        		create(-1, 1, 0.5, new ConstantFunction(1)), 300, CollectionUtil.toSet(-1., -0.5, 0., 0.5, 1.));
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testNegativeGranularity() throws IllegalGeneratorStateException {
    	create( 0,  1, -1, new ConstantFunction(1)); // negative granularity
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testZeroGranularity() throws IllegalGeneratorStateException {
    	create( 0,  1,  0, new ConstantFunction(1)); // granularity == 0
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testInvalidRange() throws IllegalGeneratorStateException {
    	create( 2,  1,  1, new ConstantFunction(1)); // min > max
    }
    
	private WeightedDoubleGenerator create(double min, double max, double granularity, WeightFunction distribution) {
	    WeightedDoubleGenerator generator = new WeightedDoubleGenerator(min, max, granularity, distribution);
	    generator.init(context);
		return generator;
    }

}
