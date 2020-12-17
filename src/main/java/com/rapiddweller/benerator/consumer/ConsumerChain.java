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

import java.util.List;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.IOUtil;

/**
 * Combines several Processors under one Processor interface.
 * Each call to the Processor is forwarded to all sub Processors.<br/>
 * <br/>
 * Created: 26.08.2007 14:50:29
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class ConsumerChain implements Consumer {

    private List<Consumer> components;

    // constructors ----------------------------------------------------------------------------------------------------

    public ConsumerChain(Consumer ... components) {
    	setComponents(components);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setComponents(Consumer... components) {
    	this.components = CollectionUtil.toList(components);
    }

    public void addComponent(Consumer component) {
        this.components.add(component);
    }
    
    public Consumer getComponent(int index) {
        return this.components.get(index);
    }
    
    public int componentCount() {
    	return components.size();
    }

    // Processor interface ---------------------------------------------------------------------------------------------

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void startConsuming(ProductWrapper<?> wrapper) {
		Object product = wrapper.unwrap();
        for (Consumer processor : components)
            processor.startConsuming(((ProductWrapper) wrapper).wrap(product));
    }

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void finishConsuming(ProductWrapper<?> wrapper) {
		Object product = wrapper.unwrap();
        for (Consumer processor : components)
            processor.finishConsuming(((ProductWrapper) wrapper).wrap(product));
    }

	@Override
	public void flush() {
        for (Consumer processor : components)
            processor.flush();
    }

	@Override
	public void close() {
        for (Consumer consumer : components)
            IOUtil.close(consumer);
    }

    public List<Consumer> getComponents() {
    	return components;
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + components;
    }
    
}
