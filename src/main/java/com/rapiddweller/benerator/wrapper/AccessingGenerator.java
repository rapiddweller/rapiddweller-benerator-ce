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

import com.rapiddweller.benerator.util.UnsafeGenerator;
import com.rapiddweller.commons.Accessor;

/**
 * Returns the results of an accessor that is applied on a constant provider object.<br/>
 * <br/>
 * Created: 22.08.2007 19:05:40
 * @author Volker Bergmann
 */
public class AccessingGenerator<S, P> extends UnsafeGenerator<P> {

	private final Class<P> targetType;
    private final Accessor<S, P> accessor;
    private final S provider;

    public AccessingGenerator(Class<P> targetType, Accessor<S, P> accessor, S provider) {
        this.targetType = targetType;
        this.accessor = accessor;
        this.provider = provider;
    }

	@Override
	public ProductWrapper<P> generate(ProductWrapper<P> wrapper) {
        return wrapper.wrap(accessor.getValue(provider));
    }
    
	@Override
	public Class<P> getGeneratedType() {
		return targetType;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[accessor=" + accessor + ']';
    }

}
