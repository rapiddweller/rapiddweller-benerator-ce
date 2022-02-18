/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_CONSTANT;

/**
 * {@link AttrInfo} implementation for a 'constant' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 11:53:02
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ConstantAttribute extends AttrInfo<String> {
	public ConstantAttribute(String errorId) {
		super(ATT_CONSTANT, false, errorId, null);
	}
}
