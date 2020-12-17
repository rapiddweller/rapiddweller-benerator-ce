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

package com.rapiddweller.benerator.primitive;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the {@link IncrementGenerator}.<br/><br/>
 * Created: 14.11.2009 06:38:34
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IncrementGeneratorTest extends GeneratorClassTest {

	public IncrementGeneratorTest() {
	    super(IncrementGenerator.class);
    }

	@Test
	public void testIncrementAndMax() {
		IncrementGenerator generator = new IncrementGenerator(10, 20, 60);
		expectGeneratedSequence(generator, 10L, 30L, 50L).withCeasedAvailability();
		generator = new IncrementGenerator(10, 20, 50);
		expectGeneratedSequence(generator, 10L, 30L, 50L).withCeasedAvailability();
	}
	
	public void testDefaultMax() {
		IncrementGenerator generator = new IncrementGenerator();
		expectGeneratedSequence(generator, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L).withContinuedAvailability();
	}
	
	@Test
	public void testMultiThreading() throws Exception {
		final IncrementGenerator generator = new IncrementGenerator(0);
		ExecutorService service = Executors.newCachedThreadPool();
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 500; i++)
					generator.generate();
            }
		};
		for (int i = 0; i < 20; i++)
			service.execute(runner);
		service.shutdown();
		service.awaitTermination(2, TimeUnit.SECONDS);
		assertEquals(10000L, generator.cursor.get());
	}
	
}
