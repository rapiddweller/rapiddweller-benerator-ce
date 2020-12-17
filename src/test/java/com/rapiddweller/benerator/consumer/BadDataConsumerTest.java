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

package com.rapiddweller.benerator.consumer;

import static org.junit.Assert.*;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import org.junit.Test;

/**
 * Tests the {@link BadDataConsumer}.<br/><br/>
 * Created: 23.01.2011 08:15:24
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class BadDataConsumerTest {
	
	@Test
	public void test() {
		// the real consumer throws an exception on every second invocation
		Consumer realTarget = new AbstractConsumer() {
			@Override
			public void startProductConsumption(Object object) {
				if (((Integer) object) % 2 == 1)
					throw new RuntimeException();
			}
		};
		
		// the bad data consumer stores error data in a list
		ListConsumer badTarget = new ListConsumer();
		
		BadDataConsumer consumer = new BadDataConsumer(badTarget, realTarget);

		for (int i = 1; i <= 5; i++) {
			consumer.startConsuming(new ProductWrapper<Integer>().wrap(i));
			consumer.finishConsuming(new ProductWrapper<Integer>().wrap(i));
		}
		consumer.close();
		
		assertEquals(3, badTarget.getConsumedData().size());
		assertEquals(1, badTarget.getConsumedData().get(0));
		assertEquals(3, badTarget.getConsumedData().get(1));
	}

}
