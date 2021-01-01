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
import com.rapiddweller.benerator.distribution.WeightedLongGenerator;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;

import java.util.Set;
import java.util.HashSet;

/**
 * Tests the {@link WeightedLongGenerator}.<br/><br/>
 * Created: 18.06.2006 15:04:17
 * @since 0.1
 * @author Volker Bergmann
 */
public class WeightedLongGeneratorTest extends GeneratorClassTest {

    public WeightedLongGeneratorTest() {
        super(WeightedLongGenerator.class);
    }

    @Test
    public void testRandomSequence() throws IllegalGeneratorStateException {
        checkUniformDistribution(-2,  2, 1, 10000, 0.1, -2, -1, 0, 1, 2);
        checkUniformDistribution(-2,  2, 2, 10000, 0.1, -2, 0, 2);
        checkUniformDistribution( 1,  5, 2, 10000, 0.1, 1, 3, 5);
        checkUniformDistribution(-5, -1, 2, 10000, 0.1, -5, -3, -1);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void checkUniformDistribution(int min, int max, int granularity,
                                          int iterations, double tolerance, int ... expectedValuesAsInt) {
        Set<Long> expectedValues = new HashSet<>(expectedValuesAsInt.length);
        for (int i : expectedValuesAsInt)
            expectedValues.add((long)i);
        WeightedLongGenerator generator = new WeightedLongGenerator(min, max, granularity);
        generator.init(context);
        checkEqualDistribution(generator, iterations, tolerance, expectedValues);
    }

}
