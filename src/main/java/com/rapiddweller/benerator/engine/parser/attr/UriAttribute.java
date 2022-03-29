/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.parser.string.UriParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_URI;

/**
 * {@link AttrInfo} for a URI.<br/><br/>
 * Created: 28.03.2022 13:00:50
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class UriAttribute extends AttrInfo<Expression<String>> {
	public UriAttribute(String errorId, boolean required) {
		super(ATT_URI, required, errorId, new ScriptableParser<>(new UriParser()));
	}
}
