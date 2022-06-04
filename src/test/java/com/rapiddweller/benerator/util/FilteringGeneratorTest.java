/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.util;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.primitive.IncrementGenerator;
import com.rapiddweller.benerator.sample.ConstantGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.DynamicExpression;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link FilteringGenerator}.<br/><br/>
 * Created: 03.06.2022 10:59:44
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class FilteringGeneratorTest extends GeneratorTest {

	@Test
	public void test_normal() {
		Generator<Long> g = new IncrementGenerator();
		Expression<Boolean> f = new DynamicExpression<>() {
			@Override
			public Boolean evaluate(Context context) {
				return (((Long) context.get("_candidate")) % 2) == 0;
			}
		};
		FilteringGenerator<Long> fg = new FilteringGenerator<Long>(g, f);
		fg.init(context);
		ProductWrapper<Long> wrapper = new ProductWrapper<>();
		assertEquals(2L, fg.generate(wrapper).unwrap().longValue());
		assertEquals(4L, fg.generate(wrapper).unwrap().longValue());
		assertEquals(6L, fg.generate(wrapper).unwrap().longValue());
	}

	@Test(expected = SyntaxError.class)
	public void test_on_nulls() {
		Generator<Long> g = new ConstantGenerator<>(null);
		Expression<Boolean> f = new ConstantExpression<>(null);
		FilteringGenerator<Long> fg = new FilteringGenerator<>(g, f);
		fg.init(context);
		fg.generate(new ProductWrapper<>());
	}

	@Test
	public void test_depletion() {
		Generator<Long> g = new IncrementGenerator(0, 1, 1);
		Expression<Boolean> f = new ConstantExpression<>(Boolean.TRUE);
		FilteringGenerator<Long> fg = new FilteringGenerator<>(g, f);
		fg.init(context);
		ProductWrapper<Long> wrapper = new ProductWrapper<>();
		assertEquals(0L, fg.generate(wrapper).unwrap().longValue());
		assertEquals(1L, fg.generate(wrapper).unwrap().longValue());
		assertNull(fg.generate(wrapper));
	}

}
