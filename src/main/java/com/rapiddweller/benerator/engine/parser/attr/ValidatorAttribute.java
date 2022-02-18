/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_VALIDATOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_VALIDATOR;

/**
 * {@link AttrInfo} implementation for the 'validator' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 17:10:36
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ValidatorAttribute extends AttrInfo<String> {
	public ValidatorAttribute(String errorId) {
		super(ATT_VALIDATOR, false, errorId, null);
	}
}
