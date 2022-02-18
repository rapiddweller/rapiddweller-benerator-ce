/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.BooleanParser;
import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_UNIQUE;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_UNIQUE;

/**
 * {@link AttrInfo} implementation for the 'unique' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 16:18:44
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class UniqueAttribute extends AttrInfo<Boolean> {
	public UniqueAttribute(String errorId) {
		super(ATT_UNIQUE, false, errorId, new BooleanParser(), "false");
	}
}
