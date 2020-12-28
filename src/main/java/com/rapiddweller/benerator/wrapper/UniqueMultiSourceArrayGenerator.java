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

import java.util.List;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.commons.ArrayUtil;
import com.rapiddweller.commons.ArrayFormat;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Creates arrays of unique combinations of the output of other generators.
 * Each array element is filled from an own generator,
 * each used generator is supposed to generate unique values itself.<br/>
 * <br/>
 * Created: 17.11.2007 13:37:37
 * @author Volker Bergmann
 */
public class UniqueMultiSourceArrayGenerator<S> extends MultiGeneratorWrapper<S, S[]> {

    private static final Logger logger = LogManager.getLogger(UniqueMultiSourceArrayGenerator.class);

    private final Class<S> componentType;
    private Object[] buffer;

    // constructors ----------------------------------------------------------------------------------------------------

    /**
     * Initializes the generator to an array of source generators
     */
    @SuppressWarnings("unchecked")
	public UniqueMultiSourceArrayGenerator(Class<S> componentType, Generator<? extends S> ... sources) {
        super(ArrayUtil.arrayType(componentType), sources);
        this.componentType = componentType;
    }

    @SuppressWarnings("unchecked")
	public UniqueMultiSourceArrayGenerator(Class<S> componentType, List<Generator<? extends S>> sources) {
        super(ArrayUtil.arrayType(componentType), sources);
        this.componentType = componentType;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    @Override
    public void init(GeneratorContext context) {
        super.init(context);
        init();
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void init() {
	    if (sources.size() == 0)
            throw new InvalidGeneratorSetupException("source", "is null");
        buffer = ArrayUtil.newInstance(componentType, sources.size());
        for (int i = 0; i < buffer.length; i++) {
        	Generator<? extends S> source = sources.get(i);
        	if (source != null) {
				ProductWrapper<?> wrapper = source.generate((ProductWrapper) getSourceWrapper());
	            if (wrapper == null)
	                throw new InvalidGeneratorSetupException("Sub generator not available: " + source);
	        	buffer[i] = wrapper.unwrap();
        	}
        }
    }

    @Override
	@SuppressWarnings("cast")
	public ProductWrapper<S[]> generate(ProductWrapper<S[]> wrapper) {
    	assertInitialized();
    	if (buffer == null)
    		return null;
        S[] result = (S[]) ArrayUtil.copyOfRange(buffer, 0, buffer.length, componentType);
        fetchNextArrayItem(buffer.length - 1);
        if (logger.isDebugEnabled())
            logger.debug("generated: " + ArrayFormat.format(result));
        return wrapper.wrap(result);
    }

    @Override
    public void reset() {
    	assertInitialized();
        super.reset();
        init();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void fetchNextArrayItem(int index) {
    	// skip ignored elements
    	while (index >= 0 && sources.get(index) == null)
    		index--;
    	
        // check for overrun
        if (buffer == null || index < 0 || index >= sources.size()) {
            buffer = null;
            return;
        }
        // if available, fetch the digit's next value
        Generator<? extends S> elementGenerator = sources.get(index);
        ProductWrapper elementWrapper = elementGenerator.generate((ProductWrapper) getSourceWrapper());
        if (elementWrapper != null) {
            buffer[index] = elementWrapper.unwrap();
            return;
        }
        // sources[index] was not available, move on to the next index
        fetchNextArrayItem(index - 1);
        if (buffer != null) {
            elementGenerator.reset();
            elementWrapper = elementGenerator.generate((ProductWrapper) getSourceWrapper());
            buffer[index] = elementWrapper.unwrap();
        }
    }

}
