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

import java.util.List;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.ConstantTestGenerator;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.collection.ObjectCounter;
import org.junit.Test;

/**
 * Tests the {@link CollectionGenerator}.<br/><br/>
 * Created: 11.10.2006 23:12:21
 * @since 0.1
 * @author Volker Bergmann
 */
public class CollectionGeneratorTest extends GeneratorClassTest {

    public CollectionGeneratorTest() {
        super(CollectionGenerator.class);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testElements() {
        Generator<Integer> source = new ConstantTestGenerator<>(1);
        source.init(context);
		CollectionGenerator<List, Integer> generator 
        	= new CollectionGenerator<>(List.class, source, 1, 5, SequenceManager.RANDOM_SEQUENCE);
        generator.init(context);
        List<Integer> list = GeneratorUtil.generateNonNull(generator);
        checkEqualDistribution(list, 0., CollectionUtil.toSet(1));
    }

    @SuppressWarnings("rawtypes")
	@Test
    public void testSize() {
        Generator<Integer> source = new ConstantTestGenerator<>(1);
        source.init(context);
		CollectionGenerator<List, Integer> generator 
        	= new CollectionGenerator<>(List.class, source, 0, 3, SequenceManager.RANDOM_SEQUENCE);
        generator.init(context);
        ObjectCounter<Integer> counter = new ObjectCounter<>(4);
        for (int i = 0; i < 5000; i++)
            counter.count(GeneratorUtil.generateNonNull(generator).size());
        checkEqualDistribution(counter, 0.1, CollectionUtil.toSet(0, 1, 2, 3));
    }
    
}
