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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.commons.ArrayFormat;
import com.rapiddweller.commons.ArrayUtil;
import com.rapiddweller.commons.ConversionException;
import com.rapiddweller.commons.Converter;

/**
 * Reads products from a source Generator and applies a Converter to transform them into the target products.<br/>
 * <br/>
 * Created: 12.06.2006 19:02:30
 * @since 0.1
 * @author Volker Bergmann
 */
public class ConvertingGenerator<S, T> extends GeneratorWrapper<S, T> {

    /** The converter to apply to the source's products */
    protected Converter<?, ?>[] converters;

    /** Initializes all attributes */
    public ConvertingGenerator(Generator<S> source, Converter<?, ?>... converters) {
        super(source);
        this.converters = converters;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public void init(GeneratorContext context) {
        if (ArrayUtil.isEmpty(converters))
            throw new InvalidGeneratorSetupException("converters", "is empty");
        super.init(context);
    }

    @Override
	@SuppressWarnings("unchecked")
    public Class<T> getGeneratedType() {
    	if (converters.length > 0)
    		return (Class<T>) converters[converters.length - 1].getTargetType();
    	else
    		return (Class<T>) getSource().getGeneratedType();
    }

    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ProductWrapper<T> generate(ProductWrapper<T> wrapper) {
        try {
        	ProductWrapper<S> sourceWrapper = generateFromSource();
            if (sourceWrapper == null)
            	return null;
            Object tmp = sourceWrapper.unwrap();
            for (Converter converter : converters)
            	tmp = converter.convert(tmp);
            return wrapper.wrap((T) tmp);
        } catch (ConversionException e) {
            throw new IllegalGeneratorStateException(e);
        }
    }

    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[source=" + getSource() + ", " +
    			"converters=[" + ArrayFormat.format(converters) + "]]";
    }

}
