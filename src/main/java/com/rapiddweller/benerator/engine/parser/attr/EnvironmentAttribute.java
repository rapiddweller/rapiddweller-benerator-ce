/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_ENVIRONMENT;

/**
 * {@link AttrInfo} for an 'environment' pointing to an environment definition file.
 * When used, it needs to be combined with a 'system' attribute<br/><br/>
 * Created: 13.02.2022 15:46:19
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class EnvironmentAttribute extends AttrInfo<Expression<String>> {

	public EnvironmentAttribute(String errorId, boolean required) {
		super(ATT_ENVIRONMENT, required, errorId, new ScriptableParser<>(new IdParser()));
	}

}
