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

package com.rapiddweller.benerator.primitive;

import java.util.HashMap;
import java.util.Map;

import com.rapiddweller.common.BeanUtil;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.WeightedTransition;

/**
 * Converter implementation that maps input values in a 'Map' style.<br/><br/>
 * Created: 24.10.2009 09:05:58
 * @since 0.6.0
 * @author Volker Bergmann
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ValueMapper implements Converter {

	private Map mappings;
	private boolean lenient;
	private Class<?> targetType;
	private Class<?> sourceType;
	
	public ValueMapper() {
	    init(null, false);
	}

	public ValueMapper(String mappingSpec) {
		this(mappingSpec, false);
	}

	public ValueMapper(String mappingSpec, boolean lenient) {
		init(mappingSpec, lenient);
	}

	private void init(String mappingSpec, boolean lenient) {
	    this.mappings = new HashMap<>();
		setMappings(mappingSpec);
		this.lenient = lenient;
    }

	public void setMappings(String mappingSpec) {
		if (mappingSpec != null) {
			WeightedTransition[] tl = DatabeneScriptParser.parseTransitionList(mappingSpec);
			for (WeightedTransition t : tl)
				mappings.put(t.getFrom(), t.getTo());
			sourceType = BeanUtil.commonSubType(mappings.keySet());
			targetType = BeanUtil.commonSuperType(mappings.values());
		} else {
			sourceType = Object.class;
			mappings.clear();
		}
	}
	
	@Override
	public Class getSourceType() {
	    return sourceType;
	}
	
	@Override
	public Class<?> getTargetType() {
	    return targetType;
    }
	
	@Override
	public Object convert(Object sourceValue) throws ConversionException {
		sourceValue = AnyConverter.convert(sourceValue, sourceType);
		if (!mappings.containsKey(sourceValue))
			if (lenient)
				return sourceValue;
			else
				throw new IllegalArgumentException("Cannot convert value: " + sourceValue);
		else 
			return mappings.get(sourceValue);
    }

	@Override
	public String toString() {
	    return getClass().getSimpleName() + mappings;
	}

	@Override
	public boolean isParallelizable() {
	    return true;
    }

	@Override
	public boolean isThreadSafe() {
	    return true;
    }
	
	@Override
    public Object clone() {
		try {
	        return super.clone();
        } catch (CloneNotSupportedException e) {
        	throw new RuntimeException(e);
        }
	}
	
}
