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

import com.rapiddweller.platform.contiperf.PerfTrackingTaskProxy;
import com.rapiddweller.commons.Context;
import com.rapiddweller.commons.ErrorHandler;
import com.rapiddweller.contiperf.PerformanceTracker;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.PrintWriter;
import java.util.List;

/**
 * Single-threaded non-locking {@link Task} executor.<br/><br/>
 * Created: 19.12.2012 09:54:56
 *
 * @author Volker Bergmann
 * @since 0.8.0
 */
public class TaskExecutor {

    private static final Logger LOGGER = LogManager.getLogger(TaskExecutor.class);

    private final Task target;
    private final Context context;
    private final ErrorHandler errorHandler;
    private final List<PageListener> pageListeners;
    private final long pageSize;
    private final boolean infoLog;
    private PerformanceTracker tracker;

    private TaskExecutor(Task target, List<PageListener> pageListeners, long pageSize,
                         boolean stats, Context context, ErrorHandler errorHandler, boolean infoLog) {
        this.context = context;
        this.errorHandler = errorHandler;
        if (stats) {
            target = new PerfTrackingTaskProxy(target);
            this.tracker = ((PerfTrackingTaskProxy) target).getOrCreateTracker();
        }
        this.target = new StateTrackingTaskProxy<Task>(target);
        this.pageListeners = pageListeners;
        this.pageSize = pageSize;
        this.infoLog = infoLog;
    }

    public static void execute(Task task, Context context, Long requestedInvocations, Long minInvocations,
                               List<PageListener> pageListeners, long pageSize, boolean stats,
                               ErrorHandler errorHandler, boolean infoLog) {
        TaskExecutor runner = new TaskExecutor(task, pageListeners,
                pageSize, stats, context, errorHandler, infoLog);
        runner.run(requestedInvocations, minInvocations);
    }

    private static long runWithoutPage(Task target, Long invocationCount, Context context, ErrorHandler errorHandler) {
        long actualCount = 0;
        for (int i = 0; invocationCount == null || i < invocationCount; i++) {
            TaskResult stepResult = target.execute(context, errorHandler);
            if (stepResult != TaskResult.UNAVAILABLE)
                actualCount++;
            if (stepResult != TaskResult.EXECUTING)
                break;
        }
        return actualCount;
    }

    private static void logExecutionInfo(Task task, Long minInvocations, Long maxInvocations, long pageSize, boolean infoLog) {
        if (infoLog) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info(executionInfo(task, minInvocations, maxInvocations, pageSize));
        } else if (LOGGER.isDebugEnabled())
            LOGGER.debug(executionInfo(task, minInvocations, maxInvocations, pageSize));
    }

    private static String executionInfo(Task task, Long minInvocations, Long maxInvocations, long pageSize) {
        String invocationInfo = (maxInvocations == null ? "as long as available" :
                (maxInvocations > 1 ? maxInvocations + " times" : ""));
        if (minInvocations != null && minInvocations > 0 && (maxInvocations == null || maxInvocations > minInvocations))
            invocationInfo += " requiring at least " + minInvocations + " generations";
        if (invocationInfo.length() > 0)
            invocationInfo += " with page size " + pageSize + " in a single thread";
        return "Running task " + task + " " + invocationInfo;
    }

    private long run(Long requestedInvocations, Long minInvocations) {
        logExecutionInfo(target, requestedInvocations, minInvocations, pageSize, infoLog);
        // first run without verification
        long countValue = run(requestedInvocations);
        // afterwards verify execution count
        if (minInvocations != null && countValue < minInvocations)
            throw new TaskUnavailableException(target, minInvocations, countValue);
        if (tracker != null)
            tracker.getCounters()[0].printSummary(new PrintWriter(System.out), 90, 95);
        return countValue;
    }

    private long run(Long requestedInvocations) {
        if (requestedInvocations != null && requestedInvocations == 0)
            return 0;
        long queuedInvocations = 0;
        long actualCount = 0;
        if (requestedInvocations != null)
            queuedInvocations = requestedInvocations;
        LOGGER.debug("Starting task {}", getTaskName());
        int currentPageNo = 0;
        do {
            try {
                if (pageSize > 0)
                    pageStarting(currentPageNo);
                long currentPageSize = currentPageSize(requestedInvocations, queuedInvocations);
                queuedInvocations -= currentPageSize;
                actualCount += runPage(currentPageSize, (pageSize > 0));
                if (pageSize > 0)
                    pageFinished(currentPageNo, context);
                currentPageNo++;
            } catch (Exception e) {
                errorHandler.handleError("Error in execution of task " + getTaskName(), e);
            }
        } while (workPending(requestedInvocations, queuedInvocations));
        LOGGER.debug("Finished task {}", getTaskName());
        return actualCount;
    }

    protected long currentPageSize(Long requestedInvocations, long queuedInvocations) {
        if (pageSize > 0)
            return (requestedInvocations == null ? pageSize : Math.min(pageSize, queuedInvocations));
        else
            return (requestedInvocations == null ? 1 : Math.min(requestedInvocations, queuedInvocations));
    }

    private String getTaskName() {
        return target.getTaskName();
    }

    private long runPage(Long invocationCount, boolean finishPage) {
        try {
            return runWithoutPage(target, invocationCount, context, errorHandler);
        } finally {
            if (finishPage)
                target.pageFinished();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean workPending(Long maxInvocationCount, long queuedInvocations) {
        if (!((StateTrackingTaskProxy<? extends Task>) target).isAvailable())
            return false;
        if (maxInvocationCount == null)
            return true;
        return (queuedInvocations > 0);
    }

    private void pageStarting(int currentPageNo) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting page " + (currentPageNo + 1) + " of " + getTaskName() + " with pageSize=" + pageSize);
        if (pageListeners != null)
            for (PageListener listener : pageListeners)
                listener.pageStarting();
    }

    private void pageFinished(int currentPageNo, Context context) {
        LOGGER.debug("Page {} of {} finished", currentPageNo + 1, getTaskName());
        if (pageListeners != null)
            for (PageListener listener : pageListeners)
                listener.pageFinished();
    }


    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
