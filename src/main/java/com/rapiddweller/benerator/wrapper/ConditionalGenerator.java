/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.common.Expression;

/**
 * {@link Generator} wrapper with a condition. If the condition is true, the method generate() forwards
 * the wrapped generator's product, otherwise it returns the value null.<br/><br/>
 * Created: 13.03.2022 20:00:59
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ConditionalGenerator <E> extends GeneratorProxy<E> {

	private final Expression<Boolean> condition;

	public ConditionalGenerator(Generator<E> source, Expression<Boolean> condition) {
		super(source);
		this.condition = condition;
	}

	@Override
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
		boolean conditionResult = (condition == null || condition.evaluate(context));
		if (conditionResult) {
			return super.generate(wrapper);
		} else {
			return wrapper.wrap(null);
		}
	}

}
