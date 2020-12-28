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

package com.rapiddweller.benerator.sample;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.util.ThreadSafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Returns a value only once and then becomes unavailable immediately.<br/>
 * <br/>
 * Created at 23.09.2009 00:20:03
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class OneShotGenerator<E> extends ThreadSafeGenerator<E> {

	private E value;
	private final Class<E> generatedType;
	private boolean used;
	
    @SuppressWarnings("unchecked")
	public OneShotGenerator(E value) {
	    this(value, (Class<E>) value.getClass());
    }

    public OneShotGenerator(E value, Class<E> generatedType) {
	    this.value = value;
	    this.generatedType = generatedType;
	    this.used = false;
    }

    @Override
    public void close() {
    	used = true;
	    value = null;
	    super.close();
    }

	@Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
	    if (used)
	    	return null;
	    used = true;
	    return wrapper.wrap(value);
    }

    @Override
	public Class<E> getGeneratedType() {
	    return generatedType;
    }

    @Override
    public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
	    super.init(context);
    }

    @Override
    public void reset() {
	    used = false;
	    super.reset();
    }

    @Override
    public String toString() {
    	return getClass().getSimpleName() + '[' + value + ']';
    }

}
