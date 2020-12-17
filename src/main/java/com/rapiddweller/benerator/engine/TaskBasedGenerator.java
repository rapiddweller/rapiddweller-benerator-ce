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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.engine.statement.GenerateAndConsumeTask;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.commons.ErrorHandler;

/**
 * Wraps a {@link GenerateAndConsumeTask} with a {@link Generator} interface.<br/><br/>
 * Created: 01.09.2011 15:33:34
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class TaskBasedGenerator implements Generator<Object> {
	
	private GenerateAndConsumeTask task;
	private GeneratorContext context;
	private ErrorHandler errorHandler;
	private boolean initialized;

	public TaskBasedGenerator(GenerateAndConsumeTask task) {
		this.task = task;
		this.errorHandler = ErrorHandler.getDefault();
	}

	@Override
	public boolean isParallelizable() {
		return task.isParallelizable();
	}

	@Override
	public boolean isThreadSafe() {
		return task.isThreadSafe();
	}

	@Override
	public Class<Object> getGeneratedType() {
		return Object.class;
	}

	@Override
	public void init(GeneratorContext context) {
		task.init((BeneratorContext) context);
		this.context = context;
		this.initialized = true;
	}

	@Override
	public boolean wasInitialized() {
		return initialized;
	}

	@Override
	public ProductWrapper<Object> generate(ProductWrapper<Object> wrapper) {
		task.execute(context, errorHandler);
		ProductWrapper<?> currentProduct = task.getRecentProduct();
		if (currentProduct == null)
			return null;
		return new ProductWrapper<Object>().wrap(currentProduct.unwrap());
	}

	@Override
	public void reset() {
		task.reset();
	}

	@Override
	public void close() {
		task.close();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + task + ']';
	}
	
}
