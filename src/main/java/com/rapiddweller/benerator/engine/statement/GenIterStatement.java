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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorMonitor;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.HF;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.time.ElapsedTimeFormatter;
import com.rapiddweller.contiperf.StopWatch;
import com.rapiddweller.profile.Profiler;
import com.rapiddweller.profile.Profiling;
import com.rapiddweller.common.Expression;
import com.rapiddweller.stat.CounterRepository;
import com.rapiddweller.task.PageListener;
import com.rapiddweller.task.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Creates a number of entities in multithreaded execution and a given page size.<br/><br/>
 * Created: 01.02.2008 14:43:15
 * @since 1.0
 * @author Volker Bergmann
 */
public class GenIterStatement extends AbstractStatement implements Closeable, PageListener {

  protected Logger logger = LoggerFactory.getLogger(GenIterStatement.class);

  // constant attributes -----------------------------------------------------------------------------------------------

  protected final boolean iterate;
  protected final String productName;
  protected final Generator<Long> countGenerator;
  protected final Expression<Long> minCount;
  protected final Expression<Integer> threads;
  protected final Expression<Long> pageSize;
  protected final Expression<PageListener> pageListenerEx;
  protected final Expression<Boolean> stats;
  protected final boolean customSensor;
  protected final String sensor;
  protected final boolean infoLog;
  protected final boolean isSubCreator;
  protected final BeneratorContext context;
  protected final BeneratorContext childContext;
  private final ElapsedTimeFormatter elapsedTimeFormatter;
  private final List<String> profilerPath;


  // mutable attributes ------------------------------------------------------------------------------------------------

  protected GenIterTask task;
  protected PageListener pageListener;

  // constructor -------------------------------------------------------------------------------------------------------

  public GenIterStatement(
      Statement[] parentPath, boolean iterate, String productName, Generator<Long> countGenerator, Expression<Long> minCount, Expression<Integer> threads,
      Expression<Long> pageSize, Expression<PageListener> pageListenerEx, Expression<Boolean> stats, String sensor,
      Expression<ErrorHandler> errorHandler, boolean infoLog, boolean isSubCreator,
      BeneratorContext context, BeneratorContext childContext) {
    super(errorHandler);
    this.iterate = iterate;
    this.productName = productName;
    this.countGenerator = countGenerator;
    this.minCount = minCount;
    this.threads = threads;
    this.pageSize = pageSize;
    this.pageListenerEx = pageListenerEx;
    this.stats = stats;
    if (!StringUtil.isEmpty(sensor)) {
      this.customSensor = true;
      this.sensor = sensor;
    } else {
      this.customSensor = false;
      this.sensor = (iterate ? "iterate" : "generate") + '.' + productName;
    }
    this.infoLog = infoLog;
    this.isSubCreator = isSubCreator;
    this.context = context;
    this.childContext = childContext;
    this.task = null;
    this.pageListener = null;
    this.elapsedTimeFormatter = new ElapsedTimeFormatter(Locale.US, " ", false);
    this.profilerPath = createProfilerPath(parentPath, this);
  }


  // properties --------------------------------------------------------------------------------------------------------


  public boolean isIterate() {
    return iterate;
  }

  public void setTask(GenIterTask task) {
    this.task = task;
  }

  public GenIterTask getTask() {
    return task;
  }

  public BeneratorContext getContext() {
    return context;
  }

  public BeneratorContext getChildContext() {
    return childContext;
  }


  // Statement interface ---------------------------------------------------------------------------------------------

  @Override
  public boolean execute(BeneratorContext context) {
    long c0 = BeneratorMonitor.INSTANCE.getTotalGenerationCount();
    StopWatch stopWatch = new StopWatch(sensor);
    if (!beInitialized(context)) {
      task.reset();
    }
    Long requestedCount = generateCount(childContext);
    executeTask(requestedCount, minCount.evaluate(childContext), pageSize.evaluate(childContext),
        evaluatePageListeners(childContext), getErrorHandler(childContext));
    if (!isSubCreator) {
      close();
    }
    int dt = stopWatch.stop();
    long dc = BeneratorMonitor.INSTANCE.getTotalGenerationCount() - c0;
    CounterRepository.getInstance().getCounter(sensor).setSampleCount(dc);
    if (Profiling.isEnabled()) {
      Profiler.defaultInstance().addSample(profilerPath, dt);
    }
    if (!isSubCreator) {
      logPerformance(dt, dc);
    }
    return true;
  }

  public Long generateCount(BeneratorContext context) {
    beInitialized(context);
    ProductWrapper<Long> count = countGenerator.generate(new ProductWrapper<>());
    return (count != null ? count.unwrap() : null);
  }

  @Override
  public void close() {
    task.close();
    countGenerator.close();
    if (pageListener instanceof Closeable) {
      IOUtil.close((Closeable) pageListener);
    }
  }

  // PageListener interface implementation ---------------------------------------------------------------------------

  @Override
  public void pageStarting() {
    getTask().pageStarting();
  }

  @Override
  public void pageFinished() {
    getTask().pageFinished();
  }


  // internal helpers ------------------------------------------------------------------------------------------------

  private static List<String> createProfilerPath(Statement[] parentPath, Statement currentElement) {
    List<String> path = new ArrayList<>(parentPath != null ? parentPath.length + 1 : 1);
    if (parentPath != null) {
      for (Statement statement : parentPath) {
        path.add(statement.toString());
      }
    }
    path.add(currentElement.toString());
    return path;
  }

  protected List<PageListener> evaluatePageListeners(Context context) {
    List<PageListener> listeners = new ArrayList<>();
    if (pageListener != null) {
      pageListener = pageListenerEx.evaluate(context);
      if (pageListener != null) {
        listeners.add(pageListener);
      }
    }
    return listeners;
  }

  protected boolean beInitialized(BeneratorContext context) {
    if (!countGenerator.wasInitialized()) {
      countGenerator.init(childContext);
      task.init(childContext);
      return true;
    }
    return false;
  }

  protected void executeTask(Long reqExecutions, Long minExecutions, Long pageSizeValue,
                             List<PageListener> pageListeners, ErrorHandler errorHandler) {
    TaskExecutor.execute(task, childContext, reqExecutions, minExecutions,
        pageListeners, pageSizeValue, false, errorHandler, infoLog);
  }

  private void logPerformance(int dt, long dc) {
    String operation = (iterate ? "iterated" : "generated");
    if (dc == 0) {
      logger.info("No data {} for '{}' setup", operation, sensor);
    } else if (dt > 0) {
      if (logger.isInfoEnabled()) {
        StringBuilder message = new StringBuilder(operation).append(" ").append(HF.pluralize(dc, productName));
        if (customSensor) {
          message.append(" [").append(sensor).append(']');
        }
        message.append(" in ").append(elapsedTimeFormatter.convert((long) dt));
        message.append(" (").append(HF.format(dc * 1000 / dt)).append("/s)");
        logger.info("{}", message);
      }
    } else {
      logger.info("{} {} '{}' data set(s)", operation, dc, sensor);
    }
  }

}
