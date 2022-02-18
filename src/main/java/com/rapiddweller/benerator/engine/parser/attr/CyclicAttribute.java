/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.Expression;
import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_CYCLIC;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CYCLIC;

/**
 * {@link AttrInfo} implementation for the 'cyclic' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 16:09:35
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CyclicAttribute extends AttrInfo<Boolean> {
	public CyclicAttribute(String errorId) {
		super(ATT_CYCLIC, false, errorId, new BooleanParser());
	}
}
