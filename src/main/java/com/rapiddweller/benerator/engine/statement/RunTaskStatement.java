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

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.script.Expression;
import com.rapiddweller.task.PageListener;
import com.rapiddweller.task.Task;
import com.rapiddweller.task.TaskExecutor;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Statement} that executes a {@link Task} supporting paging and multithreading.<br/><br/>
 * Created: 27.10.2009 20:29:47
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class RunTaskStatement extends AbstractStatement implements Closeable {

  /**
   * The Task provider.
   */
  protected final Expression<? extends Task> taskProvider;
  /**
   * The Task.
   */
  protected Task task;
  /**
   * The Count.
   */
  protected final Expression<Long> count;
  /**
   * The Page size.
   */
  protected final Expression<Long> pageSize;
  /**
   * The Threads.
   */
  protected final Expression<Integer> threads;
  /**
   * The Page listener.
   */
  protected final Expression<PageListener> pageListener;
  /**
   * The Stats.
   */
  protected final Expression<Boolean> stats;
  /**
   * The Info log.
   */
  protected final boolean infoLog;

  /**
   * Instantiates a new Run task statement.
   *
   * @param taskProvider the task provider
   * @param count        the count
   * @param pageSize     the page size
   * @param pageListener the page listener
   * @param threads      the threads
   * @param stats        the stats
   * @param errorHandler the error handler
   * @param infoLog      the info log
   */
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

  /**
   * Gets count.
   *
   * @return the count
   */
  public Expression<Long> getCount() {
    return count;
  }

  /**
   * Gets page size.
   *
   * @return the page size
   */
  public Expression<Long> getPageSize() {
    return pageSize;
  }

  /**
   * Gets threads.
   *
   * @return the threads
   */
  public Expression<Integer> getThreads() {
    return threads;
  }

  /**
   * Gets pager.
   *
   * @return the pager
   */
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

  /**
   * Gets task.
   *
   * @param context the context
   * @return the task
   */
  public synchronized Task getTask(BeneratorContext context) {
    if (task == null) {
      task = taskProvider.evaluate(context);
    }
    return task;
  }

  @Override
  public void close() {
    task.close();
  }

  private List<PageListener> getPageListeners(BeneratorContext context) {
    List<PageListener> listeners = new ArrayList<>();
    if (pageListener != null) {
      listeners.add(pageListener.evaluate(context));
    }
    return listeners;
  }

}
