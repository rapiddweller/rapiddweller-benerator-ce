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

package com.rapiddweller.benerator.engine.task;

import java.util.Iterator;
import java.util.List;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.consumer.ListConsumer;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.CurrentProductGeneration;
import com.rapiddweller.benerator.engine.statement.GenerateAndConsumeTask;
import com.rapiddweller.benerator.wrapper.IteratingGenerator;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.TypedIterable;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.task.Task;
import com.rapiddweller.task.test.AbstractTaskTest;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link GenerateAndConsumeTask}.<br/>
 * <br/>
 * Created at 25.07.2009 12:42:25
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class GenerateAndConsumeTaskTest extends AbstractTaskTest {
	
	protected Entity ALICE;
	protected Entity BOB;

    @Before
    public void createPersons() {
        ALICE = createEntity("Person", "name", "Alice");
        BOB = createEntity("Person", "name", "Bob");
    }
    
    // tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void testFlat() throws Exception {
    	// setup
		final ListConsumer consumer = new ListConsumer();
		Expression<Consumer> consumerExpr = new ConstantExpression<Consumer>(consumer);
		GenerateAndConsumeTask task = new GenerateAndConsumeTask("tn", "tn");
		Generator<Entity> source = new IteratingGenerator<Entity>(new AB());
		task.addStatement(new CurrentProductGeneration("in", source));
		task.setConsumer(consumerExpr);
		// test initial behavior
		checkIteration(task, consumer);
		consumer.clear();
		// test reset()
		task.reset();
		checkIteration(task, consumer);
		// close
		task.close();
		assertEquals("tn", task.getTaskName());
	}

    // test helpers ----------------------------------------------------------------------------------------------------

	private void checkIteration(Task task, final ListConsumer consumer) {
		BeneratorContext childContext = context.createSubContext("sub");
		// check life cycle
	    executeStepAndAssertAvailability(task, childContext);
	    executeStepAndAssertAvailability(task, childContext);
	    executeStepAndAssertUnavailability(task, childContext);
		// check output
		assertEquals(2, consumer.getConsumedData().size());
		assertEquals(ALICE, consumer.getConsumedData().get(0));
		assertEquals(BOB, consumer.getConsumedData().get(1));
    }
	
	class AB implements TypedIterable<Entity> {
		private final List<Entity> ab = CollectionUtil.toList(ALICE, BOB);
		
        @Override
		public Class<Entity> getType() {
	        return Entity.class;
        }
        
        @Override
		public Iterator<Entity> iterator() {
	        return ab.iterator();
        }
        
        @Override
        public String toString() {
        	return "AB";
        }
	}

}
