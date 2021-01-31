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
import com.rapiddweller.benerator.GeneratorState;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.BeanUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract {@link Generator} implementation which holds a state and state management methods.<br/><br/>
 * Created: 24.02.2010 12:28:05
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class AbstractGenerator<E> implements Generator<E> {

	protected final Logger logger = LogManager.getLogger(getClass());
	
	protected GeneratorState state;

	protected GeneratorContext context;
	
	private final WrapperProvider<E> resultWrapperProvider;

	public AbstractGenerator() {
	    this.state = GeneratorState.CREATED;
	    this.resultWrapperProvider = new WrapperProvider<>();
    }

	@Override
	public synchronized void init(GeneratorContext context) {
		this.context = context;
		this.state = GeneratorState.RUNNING;
    }
	
	@Override
	public boolean wasInitialized() {
	    return (state != GeneratorState.CREATED);
	}

	@Override
	public void reset() {
	    this.state = GeneratorState.RUNNING;
	}
	
	@Override
	public void close() {
	    this.state = GeneratorState.CLOSED;
	}
	
	// internal helpers ------------------------------------------------------------------------------------------------
    
    protected final void assertNotInitialized() {
	    if (state != GeneratorState.CREATED)
	    	if (state == GeneratorState.RUNNING)
	    		throw new IllegalGeneratorStateException("Trying to initialize generator a 2nd time: " + this);
	    	else
	    		throw new IllegalGeneratorStateException("Trying to initialize generator in '" + state + "' state: " + this);
    }

    protected final void assertInitialized() {
    	if (state == GeneratorState.CREATED)
    		throw new IllegalGeneratorStateException("Generator has not been initialized: " + this);
    	if (state == GeneratorState.CLOSED)
    		throw new IllegalGeneratorStateException("Generator has already been closed: " + this);
    }
    
    protected ProductWrapper<E> getResultWrapper() {
    	return resultWrapperProvider.get();
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return (state == GeneratorState.RUNNING ? BeanUtil.toString(this) : getClass().getSimpleName());
    }
    
}
