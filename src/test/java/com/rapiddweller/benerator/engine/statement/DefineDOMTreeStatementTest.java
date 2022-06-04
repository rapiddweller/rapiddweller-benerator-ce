/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.statement;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Expression;
import com.rapiddweller.platform.xml.DOMTree;
import com.rapiddweller.script.expression.ConstantExpression;
import org.junit.Test;

import java.io.Closeable;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link DefineDOMTreeStatement}.<br/><br/>
 * Created: 04.06.2022 16:33:51
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class DefineDOMTreeStatementTest {

	@Test
	public void test_regular() {
		Expression<String> id = new ConstantExpression<>("theId");
		Expression<String> inputUri = new ConstantExpression<>("in");
		Expression<String> outputUri = new ConstantExpression<>("out");
		Expression<Boolean> namespaceAware = new ConstantExpression<>(false);
		ResourceManager resourceManager = createResourceManager();
		DefineDOMTreeStatement s = new DefineDOMTreeStatement(id, inputUri, outputUri, namespaceAware, resourceManager);
		DefaultBeneratorContext context = new DefaultBeneratorContext();
		s.execute(context);
		DOMTree tree = (DOMTree) context.get("theId");
		assertEquals("in", tree.getInputUri());
		assertEquals("out", tree.getOutputUri());
	}

	@Test(expected = ConfigurationError.class)
	public void test_no_id() {
		Expression<String> id = null;
		ResourceManager resourceManager = createResourceManager();
		new DefineDOMTreeStatement(id, new ConstantExpression<String>("in"), new ConstantExpression<String>("out"),
			new ConstantExpression<Boolean>(false), resourceManager);
	}

	private ResourceManager createResourceManager() {
		return new ResourceManager() {
			@Override
			public boolean addResource(Closeable resource) {
				return true;
			}

			@Override
			public void close() {

			}
		};
	}

}
