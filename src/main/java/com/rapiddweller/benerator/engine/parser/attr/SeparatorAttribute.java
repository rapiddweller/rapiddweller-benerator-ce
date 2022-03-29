/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.AbstractParser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SEPARATOR;

/**
 * {@link AttrInfo} implementation that parses a 'separator' character.<br/><br/>
 * Created: 28.03.2022 16:42:06
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SeparatorAttribute extends AttrInfo<Expression<Character>> {

	public SeparatorAttribute(String errorId, Character defaultValue) {
		super(ATT_SEPARATOR, false, errorId, new SeparatorParser(defaultValue));
	}

	static class SeparatorParser extends AbstractParser<Expression<Character>> {

		private final Character defaultValue;

		protected SeparatorParser(Character defaultValue) {
			super("separator");
			this.defaultValue = defaultValue;
		}

		@Override
		protected Expression<Character> parseImpl(String spec) {
			if (spec != null && spec.length() != 1) {
				throw BeneratorExceptionFactory.getInstance().syntaxErrorForText("Illegal separator", spec);
			} else {
				return new SeparatorExpression(spec, defaultValue);
			}
		}
	}

	static class SeparatorExpression implements Expression<Character> {

		private final String spec;
		private final Character defaultValue;

		public SeparatorExpression(String spec, Character defaultValue) {
			this.spec = spec;
			this.defaultValue = defaultValue;
		}

		@Override
		public boolean isConstant() {
			return true;
		}

		@Override
		public Character evaluate(Context context) {
			if (spec != null) {
				return spec.charAt(0);
			} else if (defaultValue != null) {
				return defaultValue;
			} else {
				return ((BeneratorContext) context).getDefaultSeparator();
			}
		}
	}
}
