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

import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link CumulatedLongGenerator}
 * Created: 07.06.2006 20:23:39
 * @since 0.1
 * @author Volker Bergmann
 */
public class CumulatedLongGeneratorTest extends GeneratorClassTest {

    private static Logger logger = LogManager.getLogger(CumulatedLongGeneratorTest.class);

    public CumulatedLongGeneratorTest() {
        super(CumulatedLongGenerator.class);
    }

    // tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void testInstantiation() throws Exception {
        new CumulatedLongGenerator(0, 10);
    }

    @Test
    public void testAverage() throws Exception {
        checkAverage(0, 1, 0.5);
        checkAverage(1, 2, 1.5);
        checkAverage(0, 2, 1);
        checkAverage(0, 50, 25);
    }

    @Test
    public void testDistribution() throws Exception {
        checkDistribution(0, 1, 1000);
        checkDistribution(0, 5, 10000);
    }

    @Test
    public void testRange() throws Exception {
    	long min = -10;
        long max = CumulatedLongGenerator.DEFAULT_MAX;
		CumulatedLongGenerator generator = new CumulatedLongGenerator(min, max);
        generator.init(context);
        expectRange(generator, 1000, min, max);
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    private void checkAverage(int min, int max, double average) {
        CumulatedLongGenerator g = new CumulatedLongGenerator(min, max);
        g.init(context);
        assertEquals(average, g.average(), 0.1);
    }

    private void checkDistribution(int min, int max, int n) {
        logger.debug("checkDistribution(" + min + ", " + max + ", " + n + ")");
        CumulatedLongGenerator g = new CumulatedLongGenerator(min, max);
        g.init(context);
        int[] sampleCount = new int[max - min + 1];
        for (int i = 0; i < n; i++) {
            int sample = g.generate().intValue();
            sampleCount[sample - min]++;
        }
        assert(sampleCount[0] > 0);
        assert(sampleCount[sampleCount.length - 1] > 0);
        for (int i = 0; i <= sampleCount.length / 2; i++) {
            int c1 = sampleCount[i];
            int c2 = sampleCount[sampleCount.length - 1 - i];
            int threshold = n * 2 / sampleCount.length / sampleCount.length;
            if (c1 > threshold && c2 > threshold) {
                float ratio = (float)c1/c2;
                boolean check = (ratio > 0.8 && ratio < 1.2);
                logger.debug((i + " " + c1 + " " + ratio + " " + check));
                assertTrue("Distribution expected to be symmetric", check);
            }
        }
    }
    
}
