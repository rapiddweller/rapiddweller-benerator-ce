/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
import com.rapiddweller.common.MessageHolder;

/**
 * Wraps a Task and forwards invocations.<br/><br/>
 * Created: 06.07.2007 06:36:22
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.2
 */
public abstract class TaskProxy<E extends Task> extends AbstractTask implements Cloneable, MessageHolder {

  protected E realTask;

  protected TaskProxy(E realTask) {
    setRealTask(realTask);
  }

  public E getRealTask() {
    return realTask;
  }

  public void setRealTask(E realTask) {
    this.realTask = realTask;
    setTaskName(realTask != null ? realTask.getClass().getSimpleName() :
        "undefined");
  }

  @Override
  public TaskResult execute(Context context, ErrorHandler errorHandler) {
    return realTask.execute(context, errorHandler);
  }

  @Override
  public void pageFinished() {
    realTask.pageFinished();
  }

  @Override
  public boolean isThreadSafe() {
    return realTask.isThreadSafe();
  }

  @Override
  public boolean isParallelizable() {
    return realTask.isParallelizable();
  }

  @Override
  public String getMessage() {
    return (realTask instanceof MessageHolder ?
        ((MessageHolder) realTask).getMessage() : null);
  }

  @Override
  public void close() {
    realTask.close();
  }

  @Override
  public abstract Object clone();

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + realTask.toString() + ']';
  }

}
