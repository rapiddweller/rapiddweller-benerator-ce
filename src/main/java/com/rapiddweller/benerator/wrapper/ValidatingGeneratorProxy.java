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
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.util.ValidatingGenerator;
import com.rapiddweller.commons.Validator;

/**
 * Generator proxy that uses another generator for creating values and filters out invalid ones.
 * <br/>
 * Created: 29.08.2006 08:27:11
 * @see ValidatingGenerator
 */
public class ValidatingGeneratorProxy<E> extends ValidatingGenerator<E> {

    /** The source generator to use */
    private Generator<E> source;

    /** Constructor with the source generator and the validator to use */
    public ValidatingGeneratorProxy(Generator<E> source, Validator<E> validator) {
        super(validator);
        this.source = source;
    }

    // Generator & ValidatingGenerator implementation ------------------------------------------------------------------

    @Override
	public Class<E> getGeneratedType() {
        return source.getGeneratedType();
    }

    /**
     * Callback method implementation from ValidatingGenerator.
     * This calls the source's generate() method and returns its result.
     */
    @Override
    protected ProductWrapper<E> doGenerate(ProductWrapper<E> wrapper) {
        return source.generate(wrapper);
    }

    @Override
    public void init(GeneratorContext context) {
        source.init(context);
        super.init(context);
    }

    /** Calls the reset() method on the source generator */
    @Override
    public void reset() {
        source.reset();
        super.reset();
    }

    /** Calls the close() method on the source generator */
    @Override
    public void close() {
        source.close();
        super.close();
    }

	@Override
	public boolean isThreadSafe() {
	    return source.isThreadSafe();
    }
    
	@Override
	public boolean isParallelizable() {
	    return source.isParallelizable();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + source + ']';
    }

}
