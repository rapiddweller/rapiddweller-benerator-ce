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

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.commons.Context;
import com.rapiddweller.commons.Converter;
import com.rapiddweller.commons.context.ContextAware;
import com.rapiddweller.commons.converter.ThreadSafeConverter;

/**
 * {@link Converter} implementation which makes use of a {@link Generator}.<br/><br/>
 * Created: 27.07.2011 08:44:40
 * @since 0.7.0
 * @author Volker Bergmann
 */
public abstract class GeneratingConverter<S, G, T> extends ThreadSafeConverter<S, T> implements ContextAware {
	
	protected Generator<G> generator;
	protected GeneratorContext context;
	private WrapperProvider<G> wrapperProvider;
	private boolean initialized;
	
	public GeneratingConverter(Class<S> sourceType, Class<T> targetType, Generator<G> generator) {
		super(sourceType, targetType);
		this.wrapperProvider = new WrapperProvider<G>();
	    this.generator = generator;
	    this.initialized = false;
    }
	
	// ContextAware interface implementation ---------------------------------------------------------------------------

	@Override
	public void setContext(Context context) {
	    this.context = (GeneratorContext) context;
    }

	// Converter interface implementation ------------------------------------------------------------------------------
	
	@Override
	public final T convert(S sourceValue) {
		if (sourceValue == null)
			return null;
		if (!initialized) {
			initialize(sourceValue);
			initialized = true;
		}
	    return doConvert(sourceValue);
    }

    protected abstract T doConvert(S sourceValue);

	protected void initialize(S sourceValue) {
		if (context == null)
			throw new IllegalStateException("Context has not been injected in " + this);
		generator.init(context);
	}
	
	protected ProductWrapper<G> generate() {
		return generator.generate(wrapperProvider.get());
	}
}
