/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_SELECTOR;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SELECTOR;

/**
 * {@link AttrInfo} implementation for the 'selector' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 16:13:26
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SelectorAttribute extends AttrInfo<String> {
	public SelectorAttribute(String errorId) {
		super(ATT_SELECTOR, false, errorId, null);
	}
}
