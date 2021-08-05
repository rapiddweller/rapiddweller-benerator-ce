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

package com.rapiddweller.task;

import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.ThreadAware;

import java.io.Closeable;

/**
 * Common Task interface based on the GoF 'Command' pattern with specific extensions,
 * see https://en.wikipedia.org/wiki/Command_pattern.
 * An implementation may require more than one call to the execute() method in order to complete its work.
 * The method's return value signals to the framework if the task is still UNAVAILABLE, RUNNING or FINISHED.
 * The framework will execute the task only as long as it returns the stats RUNNING
 * and may decide to finish invocations before the task returns FINISHED or UNAVAILABE.
 * The framework may impose page semantics (for eg. transactions) by calling the pageFinished()
 * method after a group of invocations of the execute() method. Implementors may ignore that
 * if it does not make sense for them.
 * When a task is completed, the framework calls the close() method.<br/>
 * <br/>
 * When implementing the Task interface, you should preferably inherit from
 * {@link AbstractTask}, this may compensate for future interface changes.
 * By implementing the {@link ThreadAware} interface the implementor can signal to a multithreaded execution framework
 * if it is thread safe, unsafe or may be cloned and executed by one thread per cloned instance.
 * may be cloned and
 * <br/>
 * <br/>
 * Created: 06.07.2007 06:30:22
 *
 * @author Volker Bergmann
 * @since 0.2
 */
public interface Task extends ThreadAware, Closeable {

	/** Provides a task name for logging and debugging purposes. */
	String getTaskName();

	/** Executes the task's work or at least one step of it.
	 *  @return UNAVAILABLE if the task is not able to run,
	 *     RUNNING if the invocation worked properly, but may require further invocations,
	 *     FINISHED if the task has definitely completed its work
	 *     or SKIPPED if it was called in a state in which it was not able to execute
	 *     (having either finished or being unavailable before).
	 *  @param context a Context object for retrieving variable values and storing results
	 *  @param errorHandler an {@link ErrorHandler} which decides how to deal with exceptions
	 */
	TaskResult execute(Context context, ErrorHandler errorHandler);

	/** Callback method for implementing paged execution (like for example paged transactions) */
	void pageFinished();

	/** Callback method which is called after the last call to the execute() method.
     *  Its implementation is required to release all heavyweight resources that may lead to
     *  eg. heap overflow or system resource shortage.*/
	@Override
	void close();

}
