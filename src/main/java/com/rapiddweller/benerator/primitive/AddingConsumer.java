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

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.consumer.AbstractConsumer;
import com.rapiddweller.commons.Accessor;
import com.rapiddweller.commons.Converter;
import com.rapiddweller.commons.StringUtil;
import com.rapiddweller.commons.accessor.FeatureAccessor;
import com.rapiddweller.commons.converter.NumberToNumberConverter;
import com.rapiddweller.script.PrimitiveType;
import com.rapiddweller.script.math.ArithmeticEngine;

/**
 * {@link Consumer} implementation which sums up the values of a 'feature' of all objects it consumes
 * and return the sum as 'sum' property of type 'type'.<br/><br/>
 * Created: 03.04.2010 07:41:42
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class AddingConsumer extends AbstractConsumer {

	private Accessor<Object, Number> accessor;

	private Converter<Number, ? extends Number> converter;
	
	private Number sum;
	
	public AddingConsumer() {
		this(null, null);
	}
	
	public AddingConsumer(String feature, String type) {
	    setFeature(feature);
	    setType(type);
    }

	public void setFeature(String feature) {
		this.accessor = (feature != null ? new FeatureAccessor<>(feature, true) : null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void setType(String typeName) {
		if (StringUtil.isEmpty(typeName))
			typeName = "double";
		Class<? extends Number> numberType = (Class<? extends Number>) PrimitiveType.getInstance(typeName).getJavaType();
		this.converter = new NumberToNumberConverter(Number.class, numberType);
		this.sum = converter.convert(0);
	}
	
	public Number getSum() {
		return this.sum;
	}
	
	@Override
	public void startProductConsumption(Object object) {
	    Number addend = converter.convert(accessor.getValue(object));
	    this.sum = (Number) ArithmeticEngine.defaultInstance().add(sum, addend);
    }

}
