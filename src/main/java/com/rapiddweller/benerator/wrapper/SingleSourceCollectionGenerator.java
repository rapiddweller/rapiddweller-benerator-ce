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

import java.util.Collection;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.common.BeanUtil;

/**
 * {@link Generator} which takes one or more products from a source generator and wraps them with a {@link Collection}.
 * <br/><br/>
 * Created: 24.02.2012 19:52:24
 * @since 0.7.6
 * @author Volker Bergmann
 */
public class SingleSourceCollectionGenerator<I, C extends Collection<I>> extends CardinalGenerator<I, C> implements NonNullGenerator<C> {

    private final Class<C> collectionType;

	public SingleSourceCollectionGenerator(Generator<I> source, Class<C> collectionType, 
			NonNullGenerator<Integer> lengthGenerator) {
        super(source, false, lengthGenerator);
        this.collectionType = collectionType;
    }

    // configuration properties ----------------------------------------------------------------------------------------

    @Override
	public Class<C> getGeneratedType() {
        return collectionType;
    }

	@Override
	public ProductWrapper<C> generate(ProductWrapper<C> wrapper) {
        return wrapper.wrap(generate());
    }

	@Override
	public C generate() {
    	ProductWrapper<Integer> sizeWrapper = generateCardinalWrapper();
    	if (sizeWrapper == null)
    		return null;
    	Integer size = sizeWrapper.unwrap();
    	// the following works for primitive types as well as for objects
		C collection;
		if (size != null)
			collection = BeanUtil.newInstance(collectionType, new Object[] {size});
		else
			collection = BeanUtil.newInstance(collectionType);
        for (int i = 0; size == null || i < size; i++) {
            ProductWrapper<I> component = generateFromSource();
            if (component == null) {
            	getSource().reset();
            	break;
            }
			collection.add(component.unwrap());
        } 
        return collection;
	}

}
