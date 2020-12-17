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

import java.util.Stack;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.model.data.ComponentNameMapper;
import com.rapiddweller.model.data.Entity;

/**
 * Proxy to a {@link Consumer} which maps attribute names of the entities.<br/><br/>
 * Created: 22.02.2010 19:42:16
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class MappingEntityConsumer extends ConsumerProxy {
	
	private ComponentNameMapper mapper;
	private Stack<Entity> stack;

	public MappingEntityConsumer() {
	    this(null, null);
    }

	public MappingEntityConsumer(Consumer target, String mappingSpec) {
		super(target);
	    this.mapper = new ComponentNameMapper(mappingSpec);
	    stack = new Stack<Entity>();
    }

	public void setMappings(String mappingSpec) {
		this.mapper.setMappings(mappingSpec);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void startConsuming(ProductWrapper<?> wrapper) {
		Object object = wrapper.unwrap();
		if (!(object instanceof Entity))
			throw new IllegalArgumentException("Expected Entity");
		Entity entity = (Entity) object;
		Entity output = mapper.convert(entity);
		stack.push(output);
		target.startConsuming(((ProductWrapper) wrapper).wrap(output));
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void finishConsuming(ProductWrapper<?> wrapper) {
		super.finishConsuming(((ProductWrapper) wrapper).wrap(stack.pop()));
	}
	
}
