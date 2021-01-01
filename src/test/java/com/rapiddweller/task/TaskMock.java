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

import java.util.concurrent.atomic.AtomicInteger;

import com.rapiddweller.benerator.util.RandomUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.context.ContextAware;

/**
 * Mock implementation of the {@link Task} interface.<br/><br/>
 * Created: 26.10.2009 07:09:12
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class TaskMock extends AbstractTask implements ContextAware {

	public static final AtomicInteger count = new AtomicInteger();
	public int intProp;
	public Context context;
	
	public TaskMock() {
	    this(0, null);
    }

	public TaskMock(int intProp, Context context) {
	    this.intProp = intProp;
	    this.context = context;
    }

	@Override
	public void setContext(Context context) {
		this.context = context;
    }

	public void setIntProp(int intProp) {
    	this.intProp = intProp;
    }
	
	@Override
	public boolean isParallelizable() {
	    return true;
	}
	
	@Override
    public Object clone() {
		return new TaskMock(intProp, context);
	}

	@Override
	public TaskResult execute(Context context, ErrorHandler errorHandler) {
		if (this.context == null)
			throw new IllegalStateException("Context has not been injected");
	    count.incrementAndGet();
	    try {
	        Thread.sleep(RandomUtil.randomLong(1, 10));
        } catch (InterruptedException e) {
	        throw new RuntimeException(e);
        }
	    return TaskResult.EXECUTING;
    }
	
	@Override
	public void close() {
	    super.close();
	}

}
