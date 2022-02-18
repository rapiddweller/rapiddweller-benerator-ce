/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_CONVERTER;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONVERTER;

/**
 * {@link AttrInfo} implementation for the 'converter' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 17:07:32
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ConverterAttribute extends AttrInfo<String> {
	public ConverterAttribute(String errorId) {
		super(ATT_CONVERTER, false, errorId, null);
	}
}
