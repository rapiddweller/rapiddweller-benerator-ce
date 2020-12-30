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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.rapiddweller.benerator.util.ThreadSafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.CollectionUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Creates a predefined sequence of objects.<br/>
 * <br/>
 * Created: 19.11.2007 15:21:24
 * @author Volker Bergmann
 */
public class SequenceGenerator<E> extends ThreadSafeGenerator<E> {
    
    private static final Logger logger = LogManager.getLogger(SequenceGenerator.class);

    private final Class<E> productType;
    private List<E> values;
    private int cursor;

    @SafeVarargs
    public SequenceGenerator(Class<E> productType, E... values) {
        this(productType, (values != null ? CollectionUtil.toList(values) : null));
    }
    
    public SequenceGenerator(Class<E> productType, Collection<? extends E> values) {
        this.productType = productType;
        this.values = (values != null ? new ArrayList<>(values) : new ArrayList<>());
        this.cursor = 0;
    }
    
    public void addValue(E value) {
    	this.values.add(value);
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
	public Class<E> getGeneratedType() {
        return productType;
    }

	@Override
	public synchronized ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
        if (cursor < 0)
            return null;
        E result = values.get(cursor);
        if (cursor < values.size() - 1)
            cursor++;
        else
            cursor = -1;
        if (logger.isDebugEnabled())
            logger.debug("created: " + result);
        return wrapper.wrap(result);
    }

    @Override
    public synchronized void reset() {
        cursor = 0;
        super.reset();
    }

    @Override
    public synchronized void close() {
        values = null;
        cursor = -1;
        super.close();
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + values;
    }

}
