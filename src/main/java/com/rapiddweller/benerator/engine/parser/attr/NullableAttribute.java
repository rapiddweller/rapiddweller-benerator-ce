/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_NULLABLE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_NULLABLE;

/**
 * {@link AttrInfo} implementation for the 'nullable' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 16:21:29
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class NullableAttribute extends AttrInfo<Boolean> {
	public NullableAttribute(String errorId) {
		super(ATT_NULLABLE, false, errorId, new BooleanParser(), "false");
	}
}
