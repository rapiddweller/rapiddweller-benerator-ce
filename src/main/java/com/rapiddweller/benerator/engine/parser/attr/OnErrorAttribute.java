/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.Level;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ON_ERROR;

/**
 * Parses an XML element's 'onError' attribute.<br/><br/>
 * Created: 28.03.2022 14:02:06
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class OnErrorAttribute extends AttrInfo<Expression<String>> {

	public OnErrorAttribute(String errorId) {
		super(ATT_ON_ERROR, false, errorId, new LocalOnErrorParser(), new DefaultErrorHandlerExpression(), null);
	}

	static class DefaultErrorHandlerExpression implements Expression<String> {
		@Override
		public String evaluate(Context context) {
			return ((BeneratorContext) context).getDefaultErrorHandler();
		}
		@Override
		public boolean isConstant() {
			return false;
		}
	}

	static class LocalOnErrorParser extends ScriptableParser<String> {
		public LocalOnErrorParser() {
			super(new OnErrorParser(), String.class);
		}
	}

	static class OnErrorParser implements Parser<String> {

		@Override
		public String parse(String spec) {
			if (spec != null) {
				Level.valueOf(spec);
			}
			return spec;
		}

		@Override
		public String getDescription() {
			return "onError";
		}
	}
}
