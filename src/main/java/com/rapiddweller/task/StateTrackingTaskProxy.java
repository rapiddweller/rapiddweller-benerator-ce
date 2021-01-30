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

import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;

/**
 * Task proxy that remembers the result of the last execution step and provides it as
 * property <code>available</code>.<br/><br/>
 * Created: 05.02.2010 10:41:55
 *
 * @author Volker Bergmann
 * @since 0.6
 */
public class StateTrackingTaskProxy<E extends Task> extends TaskProxy<E> {

    protected volatile TaskResult state;

    public StateTrackingTaskProxy(E realTask) {
        super(realTask);
        this.state = TaskResult.EXECUTING;
    }

    public boolean isAvailable() {
        return (state != TaskResult.EXECUTING);
    }

    @Override
    public TaskResult execute(Context context, ErrorHandler errorHandler) {
        if (isAvailable()) {
            return TaskResult.UNAVAILABLE;
        }
        TaskResult result = super.execute(context, errorHandler);
        state = result;
        return result; // avoiding synchronization issues using a local variable instead of 'state' attribute
    }

    @Override
    public StateTrackingTaskProxy<E> clone() {
        return new StateTrackingTaskProxy<>(BeanUtil.clone(realTask));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + realTask.toString() + ']';
    }

}
