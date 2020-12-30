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

package com.rapiddweller.benerator.csv;

import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.common.Encodings;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the DatasetCSVGenerator.<br/><br/>
 * Created: 21.03.2008 16:58:20
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class WeightedDatasetCSVGeneratorTest extends GeneratorTest {

    private static final String FAMILY_NAME = "com/rapiddweller/domain/person/familyName";
    private static final String REGION = "com/rapiddweller/dataset/region";

    @Test
    public void testDE() {
        WeightedDatasetCSVGenerator<String> source = new WeightedDatasetCSVGenerator<String>(
        		String.class, FAMILY_NAME + "_{0}.csv", "DE", REGION, false, Encodings.UTF_8);
        NonNullGenerator<String> generator = WrapperFactory.asNonNullGenerator(source);
        generator.init(context);
        boolean mueller = false;
        for (int i = 0; i < 1000; i++) {
            if ("Müller".equals(generator.generate()))
                mueller = true;
        }
        assertTrue(mueller);
    }

    @Test
    public void testEurope() {
        WeightedDatasetCSVGenerator<String> source = new WeightedDatasetCSVGenerator<String>(
        		String.class, FAMILY_NAME + "_{0}.csv", "europe", REGION, false, Encodings.UTF_8);
        NonNullGenerator<String> generator = WrapperFactory.asNonNullGenerator(source);
        generator.init(context);
        boolean mueller = false; // German name
        boolean garcia = false;  // Spanish name
        for (int i = 0; i < 100000 && (!mueller || !garcia); i++) {
            String name = generator.generate();
            if ("Müller".equals(name))
                mueller = true;
            if ("García".equals(name))
                garcia = true;
        }
        assertTrue(mueller);
        assertTrue(garcia);
    }
    
}
