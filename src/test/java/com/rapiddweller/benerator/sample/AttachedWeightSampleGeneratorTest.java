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

package com.rapiddweller.benerator.sample;

import java.util.List;

import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.script.WeightedSample;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link AttachedWeightSampleGenerator}.<br/>
 * <br/>
 * Created: 07.06.2006 21:59:02
 * @author Volker Bergmann
 */
public class AttachedWeightSampleGeneratorTest extends GeneratorTest {

    private static Logger logger = LogManager.getLogger(AttachedWeightSampleGeneratorTest.class);

    @Test
    public void testInstantiation() throws Exception {
        new AttachedWeightSampleGenerator<Integer>(Integer.class);
        new AttachedWeightSampleGenerator<String>(String.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDistribution() throws Exception {
    	// prepare
        WeightedSample<Integer>[] samples = new WeightedSample[] {
            new WeightedSample<Integer>(0, 0.1),
            new WeightedSample<Integer>(1, 0.3),
            new WeightedSample<Integer>(2, 0.6)
        };
        AttachedWeightSampleGenerator<Integer> g = new AttachedWeightSampleGenerator<Integer>(Integer.class);
        g.setSamples(samples);
        g.init(context);
        // execute
        int n = 10000;
        int[] sampleCount = new int[3];
        for (int i = 0; i < n; i++) {
            sampleCount[g.generate(new ProductWrapper<Integer>()).unwrap()] ++;
        }
        List<WeightedSample<? extends Integer>> samples2 = g.getSamples();
        for (int i = 0; i < sampleCount.length; i++) {
            int count = sampleCount[i];
            double measuredProbability = (float)count / n;
            double expectedProbability = samples2.get(i).getWeight();
            double ratio = measuredProbability / expectedProbability;
            logger.debug(i + " " + count + " " + ratio);
            assertTrue(ratio > 0.9 && ratio < 1.1);
        }
    }
    
}
