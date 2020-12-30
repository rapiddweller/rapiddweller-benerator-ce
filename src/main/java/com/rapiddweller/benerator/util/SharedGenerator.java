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
import com.rapiddweller.benerator.wrapper.GeneratorProxy;
import com.rapiddweller.common.Assert;

/**
 * Proxy for generators that are used by several clients. It forwards all calls to 
 * the real generator except calls to {@link #reset()} and {@link #close()} to assure
 * that clients do not interfere with each other. This class may only be applied to 
 * {@link Generator}s of which behaviour does not change in case of a reset.<br/>
 * <br/>
 * Created: 20.01.2012 16:28:14
 * @since 0.7.6
 * @author Volker Bergmann
 */
public class SharedGenerator<E> extends GeneratorProxy<E> {

	public SharedGenerator(Generator<E> source) {
		super(source);
		Assert.notNull(source, "source");
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		if (state == GeneratorState.CREATED)
			super.init(context);
	}
	
	@Override
	public void reset() {
		// ignore
	}
	
	@Override
	public void close() {
		// ignore
	}
	
}
