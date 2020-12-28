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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorState;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.util.AbstractGenerator;
import com.rapiddweller.benerator.util.WrapperProvider;
import com.rapiddweller.commons.BeanUtil;
import com.rapiddweller.commons.IOUtil;
import com.rapiddweller.commons.NullSafeComparator;

/**
 * Abstract generator class that wraps another generator object (in a <i>source</i> property)
 * and delegates life cycle control to it.<br/>
 * <br/>
 * Created: 12.12.2006 19:13:55
 * @since 0.1
 * @author Volker Bergmann
 */
public abstract class GeneratorWrapper<S, P> extends AbstractGenerator<P> {

    private Generator<S> source;
    private final WrapperProvider<S> sourceWrapperProvider = new WrapperProvider<>();

    public GeneratorWrapper(Generator<S> source) {
        this.source = source;
    }

    // config properties -----------------------------------------------------------------------------------------------

    /** Returns the source generator */
    public Generator<S> getSource() {
        return source;
    }

    /** Sets the source generator */
    public void setSource(Generator<S> source) {
        this.source = source;
    }
    
    protected ProductWrapper<S> generateFromSource() {
		return source.generate(getSourceWrapper());
    }

    protected ProductWrapper<S> getSourceWrapper() {
    	return sourceWrapperProvider.get();
    }

    // Generator interface implementation ------------------------------------------------------------------------------

	@Override
	public boolean isThreadSafe() {
	    return source.isThreadSafe();
    }

    @Override
	public boolean isParallelizable() {
	    return source.isParallelizable();
    }

    @Override
    public synchronized void init(GeneratorContext context) {
    	assertNotInitialized();
        if (source == null)
            throw new InvalidGeneratorSetupException("source", "is null");
        synchronized (source) {
        	if (!source.wasInitialized())
        		source.init(context);
        }
        super.init(context);
    }

	@Override
    public void reset() {
		super.reset();
        source.reset();
    }

    @Override
    public void close() {
    	super.close();
        IOUtil.close(source);
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object other) {
	    if (this == other)
		    return true;
	    if (other == null || getClass() != other.getClass())
		    return false;
	    GeneratorWrapper<?,?> that = (GeneratorWrapper<?,?>) other;
	    return NullSafeComparator.equals(this.source, that.source);
    }
    
	@Override
    public int hashCode() {
	    return source.hashCode();
    }

	@Override
	public String toString() {
	    return (state != GeneratorState.CREATED ? BeanUtil.toString(this) : getClass().getSimpleName());
	}

}
