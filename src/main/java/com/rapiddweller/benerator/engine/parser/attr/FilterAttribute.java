/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_FILTER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_FILTER;

/**
 * {@link AttrInfo} implementation for the 'filter' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 15:49:36
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class FilterAttribute extends AttrInfo<Expression<Boolean>> {
	public FilterAttribute(String errorId) {
		super(ATT_FILTER, false, errorId, new ScriptParser<>(Boolean.class));
	}
}
