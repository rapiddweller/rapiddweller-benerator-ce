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
import com.rapiddweller.benerator.wrapper.GeneratorProxy;

/**
 * Generates values defined by a weighted or non-weighted value list literal, like "'A'^3,'B'^2",
 * supporting weighted random generation and uniqueness.<br/><br/>
 * Created: 28.07.2010 17:56:44
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class WeigthedLiteralGenerator<E> extends GeneratorProxy<E> {
	
	private boolean unique;
	private String valueSpec;
	
	public WeigthedLiteralGenerator(Class<E> targetType) {
	    this(targetType, null);
    }

	public WeigthedLiteralGenerator(Class<E> targetType, String valueSpec) {
	    this(targetType, valueSpec, false);
    }

	public WeigthedLiteralGenerator(Class<E> targetType, String valueSpec, boolean unique) {
		super(targetType);
		this.valueSpec = valueSpec;
	    this.unique = unique;
    }

	public void setValueSpec(String valueSpec) {
		this.valueSpec = valueSpec;
	}

	public void setUnique(boolean unique) {
    	this.unique = unique;
    }
	
	@Override
	public synchronized void init(GeneratorContext context) {
		if (valueSpec == null)
			throw new InvalidGeneratorSetupException("'codes' is null");
	    super.setSource(context.getGeneratorFactory().createFromWeightedLiteralList(
	    		valueSpec, getGeneratedType(), null, unique));
	    super.init(context);
    }

}
