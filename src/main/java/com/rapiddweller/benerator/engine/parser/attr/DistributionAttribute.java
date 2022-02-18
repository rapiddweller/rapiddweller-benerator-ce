/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.common.parser.Parser;
import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.BeneratorErrorIds.SYN_ATTR_DISTRIBUTION;
import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_DISTRIBUTION;

/**
 * {@link AttrInfo} implementation for the 'distribution' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 15:51:57
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class DistributionAttribute extends AttrInfo<String> {
	public DistributionAttribute(String errorId) {
		super(ATT_DISTRIBUTION, false, errorId, null);
	}
}
