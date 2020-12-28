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

import com.rapiddweller.commons.Assert;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.IOUtil;

import java.util.List;

/**
 * Parent class for a {@link Task} that wrap several other Tasks.<br/>
 * <br/>
 * Created at 24.07.2009 06:26:36
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */

public abstract class CompositeTask extends AbstractTask {

    protected final List<Task> subTasks;

    public CompositeTask(Task... subTasks) {
        super(); // parent class default constructor chooses name
        this.subTasks = CollectionUtil.toList(subTasks);
    }

    public void addSubTask(Task task) {
        Assert.notNull(task, "task");
        subTasks.add(task);
    }

    public Task[] getSubTasks() {
        return CollectionUtil.toArray(subTasks);
    }

    @Override
    public void close() {
        for (Task subTask : subTasks)
            IOUtil.close(subTask);
    }

}
