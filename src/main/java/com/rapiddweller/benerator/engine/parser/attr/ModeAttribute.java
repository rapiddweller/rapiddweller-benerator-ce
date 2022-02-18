/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.common.parser.ValuesParser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_MODE;

/**
 * {@link AttrInfo} implementation for 'mode' attributes of XML elements.<br/><br/>
 * Created: 18.02.2022 15:22:26
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ModeAttribute extends AttrInfo<String> {

	public static final String NORMAL = "normal";
	public static final String IGNORED = "ignored";

	public ModeAttribute(String errorId) {
		super(ATT_MODE, false, errorId, parser(), NORMAL, null);
	}

	private static Parser<String> parser() {
		return new ValuesParser("mode", NORMAL, IGNORED);
	}

}
