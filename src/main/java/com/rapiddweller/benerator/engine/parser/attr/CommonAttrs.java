/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.Expression;
import com.rapiddweller.format.xml.AttrInfo;

/**
 * Factory for common {@link com.rapiddweller.format.xml.AttrInfo}s.<br/><br/>
 * Created: 13.02.2022 15:43:56
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CommonAttrs {

	private CommonAttrs() {
		// private constructor to prevent instantiation of this utility class
	}

	public static AttrInfo<String> id(String errorId, boolean required) {
		return new IdAttribute(errorId, required);
	}

	public static AttrInfo<String> consumer(String errorId) {
		return new ConsumerAttribute(errorId);
	}

	public static AttrInfo<Expression<String>> environment(String errorId, boolean required) {
		return new EnvironmentAttribute(errorId, required);
	}

	public static AttrInfo<Expression<String>> system(String errorId, boolean required) {
		return new SystemAttribute(errorId, required);
	}

}
