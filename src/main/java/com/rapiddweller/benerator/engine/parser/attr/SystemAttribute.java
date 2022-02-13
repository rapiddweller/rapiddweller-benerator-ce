/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ENVIRONMENT;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SYSTEM;

/**
 * {@link AttrInfo} for a 'system' defined in an environment file.
 * When used, it needs to be combined with an 'environment' attribute.<br/><br/>
 * Created: 13.02.2022 15:54:01
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SystemAttribute extends AttrInfo<Expression<String>> {

	public SystemAttribute(String errorId, boolean required) {
		super(ATT_SYSTEM, required, errorId, new ScriptableParser<>(new IdParser()));
	}

}
