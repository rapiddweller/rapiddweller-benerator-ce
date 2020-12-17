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

package com.rapiddweller.benerator.composite;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.engine.AbstractScopedLifeCycleHolder;
import com.rapiddweller.benerator.engine.BeneratorContext;

/**
 * Abstract implementation of the GeneratorComponent interface which manages a source Generator
 * and a Context reference.<br/><br/>
 * Created: 31.08.2011 12:56:22
 * @since 0.7.0
 * @author Volker Bergmann
 */
public abstract class AbstractGeneratorComponent<E> extends AbstractScopedLifeCycleHolder implements GeneratorComponent<E> {

	protected Generator<?> source;
	protected GeneratorContext context;
	protected String message;

	public AbstractGeneratorComponent(Generator<?> source, String scope) {
		super(scope);
		this.source = source;
	}

    public Generator<?> getSource() {
    	return source;
    }
    
	@Override
	public String getMessage() {
		return message;
	}
	
	protected void assertInitialized() {
		if (!source.wasInitialized())
			throw new IllegalGeneratorStateException("Generator component was not initialized: " + this);
	}

    // GeneratorComponent interface implementation ---------------------------------------------------------------------

	@Override
	public void init(BeneratorContext context) {
		this.context = context;
		source.init(context);
	}

	@Override
	public void reset() {
		source.reset();
	}
	
	@Override
	public void close() {
    	source.close();
	}

	@Override
	public boolean isParallelizable() {
	    return source.isParallelizable();
    }

	@Override
	public boolean isThreadSafe() {
	    return source.isThreadSafe();
    }
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return getClass().getSimpleName() + '{' + source + '}';
	}

}
