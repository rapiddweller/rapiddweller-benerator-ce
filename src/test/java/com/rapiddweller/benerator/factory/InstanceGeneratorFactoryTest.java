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

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.model.data.IdDescriptor;
import com.rapiddweller.model.data.InstanceDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import com.rapiddweller.model.data.Uniqueness;
import org.junit.Test;

/**
 * Tests the {@link InstanceGeneratorFactory}.<br/>
 * <br/>
 * Created at 27.08.2008 13:55:03
 * @since 0.5.5
 * @author Volker Bergmann
 */
public class InstanceGeneratorFactoryTest extends GeneratorTest {
	
	/**
	 * Test unique generation based on random sequence.
	 * <attribute distribution="random" unique="true"/>
	 */
	@Test
	public void testUniqueRandom() {
		SimpleTypeDescriptor type = createSimpleType(null, "long").withMin("1").withMax("3").withDistribution("random");
		InstanceDescriptor instance = createInstance("n", type).withUnique(true);
		Generator<Long> generator = createInstanceGenerator(instance);
		generator.init(context);
		expectUniquelyGeneratedSet(generator, 1L, 2L, 3L).withCeasedAvailability();
	}
	
	@Test
	public void testDefaultId() {
		IdDescriptor descriptor = createId("id", "long");
		Generator<Long> generator = createInstanceGenerator(descriptor);
		generator.init(context);
		expectUniquelyGeneratedSet(generator, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L).withContinuedAvailability();
	}
	
	@SuppressWarnings("unchecked")
    private Generator<Long> createInstanceGenerator(InstanceDescriptor instance) {
		return (Generator<Long>) InstanceGeneratorFactory.createSingleInstanceGenerator(
				instance, Uniqueness.NONE, context);
	}

}
