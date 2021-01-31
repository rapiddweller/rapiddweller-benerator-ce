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

package com.rapiddweller;

import static org.junit.Assert.*;

import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.statement.GenerateOrIterateStatement;
import com.rapiddweller.benerator.engine.statement.StatementProxy;
import com.rapiddweller.benerator.engine.statement.TimedGeneratorStatement;
import com.rapiddweller.benerator.test.BeneratorIntegrationTest;
import com.rapiddweller.benerator.test.ConsumerMock;
import com.rapiddweller.platform.contiperf.PerfTrackingConsumer;
import com.rapiddweller.stat.LatencyCounter;
import org.junit.Test;

/**
 * Integration test of the {@link PerfTrackingConsumer}.<br/><br/>
 * Created: 14.03.2010 12:17:07
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PerfTrackingConsumerIntegrationTest extends BeneratorIntegrationTest {

	@Test
	public void testNesting() {
		TimedGeneratorStatement statement = (TimedGeneratorStatement) parse(
				"<generate type='bla' count='10'>" +
				"	<consumer class='com.rapiddweller.platform.contiperf.PerfTrackingConsumer'>" +
				"		<property name='target'>" +
				"			<bean id='c' spec='new com.rapiddweller.benerator.test.ConsumerMock(false, 0, 50, 100)' />" +
				"		</property>" +
				"	</consumer>" +
				"</generate>");
		statement.execute(context);
		ConsumerMock consumerMock = ConsumerMock.instances.get(0);
		assertEquals(10, consumerMock.startConsumingCount.get());
		checkStats(statement);
	}

	@Test
	public void testScript() {
		TimedGeneratorStatement statement = (TimedGeneratorStatement) parse(
				"<generate type='bla' count='10'>" +
				"	<consumer spec='new com.rapiddweller.platform.contiperf.PerfTrackingConsumer(new com.rapiddweller.benerator.test.ConsumerMock(false, 0, 50, 100))'/>" +
				"</generate>");
		statement.execute(context);
		ConsumerMock consumerMock = ConsumerMock.instances.get(0);
		assertEquals(10, consumerMock.startConsumingCount.get());
		checkStats(statement);
	}

	private void checkStats(TimedGeneratorStatement statement) {
		Statement tmp = statement;
		while (tmp instanceof StatementProxy)
			tmp = ((StatementProxy) tmp).getRealStatement(context);
	    GenerateOrIterateStatement realStatement = (GenerateOrIterateStatement) tmp;
		PerfTrackingConsumer tracker = (PerfTrackingConsumer) (realStatement.getTask()).getConsumer();
		LatencyCounter counter = tracker.getOrCreateTracker().getCounters()[0];
		assertEquals(10, counter.sampleCount());
		assertTrue("Expected latency greater than 29 ms, but measured " + counter.minLatency() + " ms", counter.minLatency() > 29);
		assertTrue(counter.averageLatency() > 29);
		assertTrue(counter.minLatency() < counter.maxLatency());
    }
	
}
