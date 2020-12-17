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

package com.rapiddweller.benerator.parser;

import java.util.Map;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.commons.ConversionException;
import com.rapiddweller.commons.Converter;
import com.rapiddweller.commons.collection.MapEntry;
import com.rapiddweller.commons.converter.AbstractConverter;
import com.rapiddweller.commons.converter.LiteralParser;
import com.rapiddweller.commons.converter.NoOpConverter;
import com.rapiddweller.commons.mutator.AnyMutator;

/**
 * Converts Map entries by first applying a preprocessor to the value, 
 * then (if possible) converting the result to a number or boolean.<br/><br/>
 * Created: 01.02.2008 14:40:43
 * @since 0.4.0
 * @author Volker Bergmann
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultEntryConverter extends AbstractConverter<Map.Entry, Map.Entry> {
    
    private BeneratorContext context;
    private Converter<?, ?> preprocessor;
    private LiteralParser stringParser;
    private boolean putEntriesToContext;

    public DefaultEntryConverter(BeneratorContext context) {
        this(new NoOpConverter(), context, false);
    }

    public DefaultEntryConverter(Converter<?, ?> preprocessor, BeneratorContext context, boolean putEntriesToContext) {
    	super(Map.Entry.class, Map.Entry.class);
        this.preprocessor = preprocessor;
        this.context = context;
        this.putEntriesToContext = putEntriesToContext;
        this.stringParser = new LiteralParser();
    }

    @Override
	public Map.Entry convert(Map.Entry entry) throws ConversionException {
        String key = String.valueOf(entry.getKey());
        String sourceValue = String.valueOf(entry.getValue());
        sourceValue = String.valueOf(((Converter) preprocessor).convert(sourceValue));
        Object result = stringParser.convert(sourceValue);
        if (putEntriesToContext) {
    		if (key.startsWith("benerator."))
    			AnyMutator.setValue(context, key, result, true, false);
    		else
    			context.setGlobal(key, result);
        }
        return new MapEntry<String, Object>(key, result);
    }

	@Override
	public boolean isParallelizable() {
	    return preprocessor.isParallelizable();
    }

	@Override
	public boolean isThreadSafe() {
	    return preprocessor.isThreadSafe();
    }

}
