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

package com.rapiddweller.benerator.engine.statement;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.commons.ErrorHandler;
import com.rapiddweller.script.Expression;
import com.rapiddweller.task.PageListener;
import com.rapiddweller.task.TaskExecutor;
import com.rapiddweller.task.Task;

/**
 * {@link Statement} that executes a {@link Task} supporting paging and multithreading.<br/><br/>
 * Created: 27.10.2009 20:29:47
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class RunTaskStatement extends AbstractStatement implements Closeable {
	
	protected final Expression<? extends Task> taskProvider;
	protected Task task;
	protected final Expression<Long> count;
	protected final Expression<Long> pageSize;
	protected final Expression<Integer> threads;
	protected final Expression<PageListener> pageListener;
	protected final Expression<Boolean> stats;
	protected final boolean infoLog;

	public RunTaskStatement(Expression<? extends Task> taskProvider, 
			Expression<Long> count, Expression<Long> pageSize, 
			Expression<PageListener> pageListener, Expression<Integer> threads, 
			Expression<Boolean> stats, Expression<ErrorHandler> errorHandler,
			boolean infoLog) {
		super(errorHandler);
	    this.taskProvider = taskProvider;
	    this.count = count;
	    this.pageSize = pageSize;
	    this.threads = threads;
	    this.pageListener = pageListener;
	    this.stats = stats;
	    this.infoLog = infoLog;
    }

	public Expression<Long> getCount() {
    	return count;
    }

	public Expression<Long> getPageSize() {
    	return pageSize;
    }

	public Expression<Integer> getThreads() {
    	return threads;
    }

	public Expression<PageListener> getPager() {
    	return pageListener;
    }

	@Override
	public boolean execute(BeneratorContext context) {
	    Long invocations = count.evaluate(context);
		TaskExecutor.execute(
	    		getTask(context), context, 
	    		invocations,
	    		invocations,
	    		getPageListeners(context), 
	    		pageSize.evaluate(context), 
	    		stats.evaluate(context),
	    		getErrorHandler(context),
	    		infoLog);
    	return true;
	}

	public synchronized Task getTask(BeneratorContext context) {
		if (task == null)
			task = taskProvider.evaluate(context);
	    return task;
    }

	@Override
	public void close() {
		task.close();
	}
	
	private List<PageListener> getPageListeners(BeneratorContext context) {
		List<PageListener> listeners = new ArrayList<>();
	    if (pageListener != null)
	    	listeners.add(pageListener.evaluate(context));
	    return listeners;
    }

}
