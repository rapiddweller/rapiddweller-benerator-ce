/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.benerator.engine.parser.string.WeightedLiteralListParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.script.WeightedSample;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_VALUES;

/**
 * {@link AttrInfo} implementation for a 'values' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 11:55:47
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ValuesAttribute extends AttrInfo<WeightedSample<?>[]> {
	public ValuesAttribute(String errorId) {
		super(ATT_VALUES, false, errorId, new WeightedLiteralListParser());
	}
}
