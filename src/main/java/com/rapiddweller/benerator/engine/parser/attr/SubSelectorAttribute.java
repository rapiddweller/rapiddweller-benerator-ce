/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SELECTOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SUB_SELECTOR;

/**
 * {@link AttrInfo} implementation for the 'subSelector' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 16:14:32
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SubSelectorAttribute extends AttrInfo<String> {
	public SubSelectorAttribute(String errorId) {
		super(ATT_SUB_SELECTOR, false, errorId, null);
	}
}
