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

package com.rapiddweller.benerator.consumer;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.wrapper.ProductWrapper;


/**
 * {@link Consumer} proxy that forwards data to 'real consumer' and if the real consumer 
 * raises an error, forwards the data to a 'bad data consumer'.<br/><br/>
 * Created: 23.01.2011 08:04:17
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class BadDataConsumer extends ConsumerProxy {
	
	Consumer badDataTarget;
	
	public BadDataConsumer(Consumer badDataTarget, Consumer realTarget) {
		super(realTarget);
		this.badDataTarget = badDataTarget;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void startConsuming(ProductWrapper<?> wrapper) {
		Object product = wrapper.unwrap();
		try {
			target.startConsuming(((ProductWrapper) wrapper).wrap(product));
		} catch (Exception e) {
			badDataTarget.startConsuming(((ProductWrapper) wrapper).wrap(product));
			badDataTarget.finishConsuming(((ProductWrapper) wrapper).wrap(product));
		}
	}

	@Override
	public void flush() {
		super.flush();
		badDataTarget.flush();
	}
	
	@Override
	public void close() {
		super.close();
		badDataTarget.close();
	}
	
}
