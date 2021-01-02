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

package com.rapiddweller.platform.contiperf;

import static org.junit.Assert.*;

import java.io.PrintWriter;

import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.stat.LatencyCounter;
import org.junit.Test;

/**
 * Tests the {@link PerfTrackingConsumer}.<br/><br/>
 * Created: 14.03.2010 11:53:48
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTrackingConsumerTest {

	private static final int MAX_LATENCY = 80;
	private static final int MIN_LATENCY = 40;

	@Test
	public void test() {
		ConsumerMock mock = new ConsumerMock(false, 1, MIN_LATENCY, MAX_LATENCY);
		PerfTrackingConsumer tracker = new PerfTrackingConsumer();
		try {
			tracker.setTarget(mock);
			for (int i = 0; i < 10; i++) {
				tracker.startConsuming(new ProductWrapper<>().wrap(null));
				tracker.finishConsuming(new ProductWrapper<>().wrap(null));
			}
			LatencyCounter counter = tracker.getOrCreateTracker().getCounters()[0];
			counter.printSummary(new PrintWriter(System.out), 90, 95);
			assertTrue(counter.minLatency() >= MIN_LATENCY - 10);
			assertTrue(counter.averageLatency() > MIN_LATENCY - 10);
			assertTrue(counter.averageLatency() < MAX_LATENCY + 10);
		} finally {
			IOUtil.close(tracker);
		}
	}
	
}
