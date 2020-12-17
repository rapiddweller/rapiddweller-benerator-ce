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

import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link ShuffleDoubleGenerator}.<br/><br/>
 * Created: 07.06.2006 20:23:39
 * @since 0.1
 * @author Volker Bergmann
 */
public class ShuffleDoubleGeneratorTest extends GeneratorClassTest {

    public ShuffleDoubleGeneratorTest() {
        super(ShuffleDoubleGenerator.class);
    }

    @Test
    public void testIncrementOne() throws Exception {
        check( 0, 2, 1, 1,   0,  1, 2);
        check(-2, 0, 1, 1,  -2, -1, 0);
    }

    @Test
    public void testIncrementTwo() throws Exception {
        check( 0, 2, 1, 2,   0,  2, 1);
        check(-2, 0, 1, 2,  -2, 0, -1);
    }

    @Test
    public void testFractionalGranularity() throws Exception {
        check( 0, 1, 0.5, 1,   0, 1,  0.5);
        check(-1, 0, 0.5, 1,  -1, 0, -0.5);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testMinGreaterMax() {
        new ShuffleDoubleGenerator(1, 0,  1, 1).init(context);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testZeroIncrement() {
        new ShuffleDoubleGenerator(0, 1,  1, 0).init(context);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testNegativeIncrement() {
        new ShuffleDoubleGenerator(0, 1, 1, -1).init(context);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testZeroGranularity() {
        new ShuffleDoubleGenerator(0, 1, 0, 1).init(context);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testNegativeGranularity() {
        new ShuffleDoubleGenerator(0, 1, -1, 1).init(context);
    }

    @Test
    public void testReset() throws Exception {
    	ShuffleDoubleGenerator generator = new ShuffleDoubleGenerator(0., 3., 1., 2.);
    	generator.init(context);
        expectGeneratedSequence(generator, 0., 2., 1., 3.).withCeasedAvailability();
    }
    
    // helper methods --------------------------------------------------------------------------------------------------

    private void check(double min, double max, double granularity, double increment, double ... expectedProducts) {
    	ShuffleDoubleGenerator generator = new ShuffleDoubleGenerator(min, max, granularity, increment);
        generator.init(context);
        for (double expected : expectedProducts)
			assertEquals(expected, generator.generate(), 0);
        assertUnavailable(generator);
    }

}
