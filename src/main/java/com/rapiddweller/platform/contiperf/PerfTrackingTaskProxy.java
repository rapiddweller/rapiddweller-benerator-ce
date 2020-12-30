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

import com.rapiddweller.task.Task;
import com.rapiddweller.task.TaskResult;
import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.contiperf.PerformanceTracker;

/**
 * Proxies a {@link Task} and tracks its execution times.<br/><br/>
 * Created: 25.02.2010 09:08:48
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class PerfTrackingTaskProxy extends PerfTrackingWrapper implements Task {

    private final Task realTask;

    public PerfTrackingTaskProxy(Task realTask) {
        this(realTask, null);
    }

    public PerfTrackingTaskProxy(Task realTask, PerformanceTracker tracker) {
        super(tracker);
        this.realTask = realTask;
    }

    @Override
    public TaskResult execute(Context context, ErrorHandler errorHandler) {
        try {
            return (TaskResult) getOrCreateTracker().invoke(new Object[]{context, errorHandler});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pageFinished() {
        // nothing special to do here
    }

    @Override
    public void close() {
        super.close();
        realTask.close();
    }

    @Override
    public Object clone() {
        return new PerfTrackingTaskProxy(BeanUtil.clone(realTask), getOrCreateTracker());
    }

    @Override
    public String getTaskName() {
        return realTask.getTaskName();
    }

    @Override
    public boolean isParallelizable() {
        return realTask.isParallelizable();
    }

    @Override
    public boolean isThreadSafe() {
        return realTask.isThreadSafe();
    }

    @Override
    protected TaskInvoker getInvoker() {
        return new TaskInvoker(realTask);
    }

}
