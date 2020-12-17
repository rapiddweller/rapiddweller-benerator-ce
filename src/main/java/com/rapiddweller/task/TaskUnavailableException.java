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

import com.rapiddweller.commons.MessageHolder;
import com.rapiddweller.commons.StringUtil;

/**
 * Exception which indicates that a required Task is unavailable.<br/><br/>
 * Created: 20.10.2009 10:07:05
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class TaskUnavailableException extends TaskException {

    private static final long serialVersionUID = -2073389311048962081L;

    private final Task task;
    private final long requiredCount;
    private final long actualCount;

    public TaskUnavailableException(Task task, long requiredCount, long actualCount) {
        super(renderMessage(task, requiredCount, actualCount));
        this.task = task;
        this.requiredCount = requiredCount;
        this.actualCount = actualCount;
    }

    private static String renderMessage(Task task, long requiredCount, long actualCount) {
        StringBuilder builder = new StringBuilder("Task ").append(task);
        if (actualCount == 0)
            builder.append(" not available");
        else
            builder.append(" could be executed only ").append(actualCount)
                    .append(" times, required minimum: ").append(requiredCount);
        if (task instanceof MessageHolder) {
            String message = ((MessageHolder) task).getMessage();
            if (!StringUtil.isEmpty(message))
                builder.append(". ").append(message);
        }
        return builder.toString();
    }

    public long getRequiredCount() {
        return requiredCount;
    }

    public long getActualCount() {
        return actualCount;
    }

    public Task getTask() {
        return task;
    }

}
