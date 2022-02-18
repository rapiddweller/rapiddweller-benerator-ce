/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.attr;

import com.rapiddweller.format.xml.AttrInfo;

import static com.rapiddweller.benerator.engine.DescriptorConstants.ATT_SOURCE;

/**
 * {@link AttrInfo} implementation for a 'source' attribute of an XML element.<br/><br/>
 * Created: 18.02.2022 11:44:50
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class SourceAttribute extends AttrInfo<String>{
	public SourceAttribute(String errorId, boolean required) {
		super(ATT_SOURCE, required, errorId, null);
	}
}
