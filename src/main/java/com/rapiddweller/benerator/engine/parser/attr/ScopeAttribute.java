/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SCOPE;

/**
 * {@link com.rapiddweller.format.xml.AttrInfo} implementation for the 'scope' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 15:46:44
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ScopeAttribute extends AttrInfo<String> {
	public ScopeAttribute(String errorId) {
		super(ATT_SCOPE, false, errorId, null);
	}
}
