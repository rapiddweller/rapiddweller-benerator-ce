/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.NonNegativeLongParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_OFFSET;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_OFFSET;

/**
 * {@link AttrInfo} implementation for the 'offset' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 16:33:42
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class OffsetAttribute extends AttrInfo<Expression<Long>> {
	public OffsetAttribute(String errorId) {
		super(ATT_OFFSET, false, errorId, new ScriptableParser<>(new NonNegativeLongParser()));
	}
}
