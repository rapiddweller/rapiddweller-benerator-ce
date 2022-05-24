/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.parser.AbstractTypedParser;
import com.rapiddweller.format.script.ScriptUtil;

/**
 * Parses a string and checks if it is the id of a script engine registered with Benerator.<br/><br/>
 * Created: 23.05.2022 16:16:25
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ScriptEngineIdParser extends AbstractTypedParser<String> {

	public ScriptEngineIdParser() {
		super("Script Engine Id", String.class);
	}

	@Override
	protected String parseImpl(String spec) {
		Assert.notEmpty(spec, "Script Engine Id is missing");
		if (!ScriptUtil.getFactoryIds().contains(spec)) {
			throw BeneratorExceptionFactory.getInstance().illegalArgument("Not a script engine id", null, null);
		}
		return spec;
	}

}
