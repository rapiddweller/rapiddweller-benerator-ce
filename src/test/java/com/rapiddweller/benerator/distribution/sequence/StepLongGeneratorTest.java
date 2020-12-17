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
import org.junit.Test;

/**
 * Tests the {@link StepLongGenerator}.<br/><br/>
 * Created: 26.07.2007 18:11:19
 * @author Volker Bergmann
 */
public class StepLongGeneratorTest extends GeneratorClassTest {

    public StepLongGeneratorTest() {
        super(StepLongGenerator.class);
    }

    @Test
    public void testIncrement() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5, 1);
        expectGeneratedSequence(simpleGenerator, 1L, 2L, 3L, 4L, 5L).withCeasedAvailability();
        StepLongGenerator oddGenerator = new StepLongGenerator(1, 5, 2);
        expectGeneratedSequence(oddGenerator, 1L, 3L, 5L).withCeasedAvailability();
    }

    @Test
    public void testDecrement() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5, -1);
        expectGeneratedSequence(simpleGenerator, 5L, 4L, 3L, 2L, 1L).withCeasedAvailability();
        StepLongGenerator oddGenerator = new StepLongGenerator(1, 5, -2);
        expectGeneratedSequence(oddGenerator, 5L, 3L, 1L).withCeasedAvailability();
    }

    @Test
    public void testGranularity() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5);
        simpleGenerator.setGranularity(2L);
        expectGeneratedSequence(simpleGenerator, 1L, 3L, 5L).withCeasedAvailability();
    }

}
